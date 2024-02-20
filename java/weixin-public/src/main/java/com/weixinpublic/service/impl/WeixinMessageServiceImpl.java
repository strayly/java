package com.weixinpublic.service.impl;


import com.weixinpublic.config.weixin.WeixinConfig;
import com.weixinpublic.entity.bo.WxPublicUser;
import com.weixinpublic.entity.dto.weixin.TemplateMessageDTO;
import com.weixinpublic.enums.weixin.ReplyMessage;
import com.weixinpublic.enums.TableEnum;
import com.weixinpublic.mapper.mysql.WxPublicUserMapper;
import com.weixinpublic.entity.dto.weixin.EncryptMessageDTO;
import com.weixinpublic.entity.dto.weixin.TextMessageDTO;
import com.weixinpublic.entity.dto.weixin.TextResponseDTO;
import com.weixinpublic.entity.dto.weixin.MessageDTO;
import com.weixinpublic.enums.weixin.MsgType;
import com.weixinpublic.service.WeixinMessageService;
import com.weixinpublic.util.DataConvert;
import com.weixinpublic.util.WeixinUtils;
import com.weixinpublic.constant.WeixinAutoMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.time.LocalDateTime;


/**
 * @description
 */
@Service
@Slf4j
public class WeixinMessageServiceImpl implements WeixinMessageService {
    //微信相关配置
    @Resource
    private WeixinConfig weixinConfig;
    //用户信息表映射
    @Autowired
    private WxPublicUserMapper wxPublicUserMapper;

    private WxPublicUser wxPublicUser = new WxPublicUser();
    private String nonce = null;

    /**
     * 处理微信消息
     * @param xml POST的消息体
     * @param encryptType 加密方式 aes
     * @param msgSignature 签名
     * @param timestamp 时间戳参数
     * @param nonce 随机字符串
     * @return
     */
    @Override
    public String processMessage(String xml,String encryptType,String msgSignature,String timestamp,String nonce )   {
        this.nonce = nonce;
        String result = "";
        //如果为加密消息 先解密
        if("aes".equals(encryptType) && xml.toLowerCase().contains("<encrypt>")){
            EncryptMessageDTO encrypt = DataConvert.fromXML(xml, EncryptMessageDTO.class);
            String encryptMsg = encrypt.getEncrypt();
            if(encryptMsg!=null) {
                xml = WeixinUtils.msgDecrypt(weixinConfig,  msgSignature, timestamp, nonce,encryptMsg);
                log.info("解密消息：{}",xml);
            }
        }
        MessageDTO message = DataConvert.fromXML(xml, MessageDTO.class);
        String openId =  message.getFromUserName();

        if(openId!=null){
            switch (message.getMsgType()) {
                case text://处理文本消息
                    return handlerTextAutoReply(xml);
                case event://处理事件消息
                     return handlerEvent(xml);            }
        }
        return result;
    }
    //处理自动回复文本消息
    //xml 消息体字
    private String handlerTextAutoReply(String xml){
        TextMessageDTO textMessage = DataConvert.fromXML(xml, TextMessageDTO.class);
        String reply = ReplyMessage.getReply(textMessage.getContent());
        if(reply!=null) {
            return handlerTextReply(reply,textMessage.getToUserName(),textMessage.getFromUserName());
        }
        return "";
    }

    /**
     * 处理回复文本消息
     * @param content 回复内容
     * @param setFromUserName 发送这，即公众号名称
     * @param setToUserName 接收者，用户openid
     * @return
     */
    private String handlerTextReply(String content,String setFromUserName,String setToUserName){
        String result = "";
        if(content!=null) {
            String timestamp = System.currentTimeMillis() / 1000 + "";
            TextResponseDTO textResponseDTO = new TextResponseDTO();
            textResponseDTO.setContent(content);
            textResponseDTO.setCreateTime(timestamp);
            textResponseDTO.setFromUserName(setFromUserName);
            textResponseDTO.setMsgType(MsgType.text);
            textResponseDTO.setToUserName(setToUserName);
            log.info("回复消息：{}",DataConvert.toXML(textResponseDTO));
            //回复加密消息
            result = handlerEncrypt(DataConvert.toXML(textResponseDTO), timestamp);
        }
        return result;
    }
    //事件消息处理
    //xml 消息体字符串
    private String handlerEvent(String xml){
        //String result ="success";
        TemplateMessageDTO eventMessage = DataConvert.fromXML(xml, TemplateMessageDTO.class);
        String openId = eventMessage.getFromUserName();
        switch (eventMessage.getEvent().toUpperCase()) {
            case "SUBSCRIBE"://关注
                saveUser(openId, true);
                //关注后 自送发送消息
                return handlerSubscribeReply(xml, WeixinAutoMessage.WEIXIN_SUBSCRIBE_SUCCESS);
            case "UNSUBSCRIBE"://取消关注
                saveUser(openId, false);
                break;

        }
        return "";
    }
    //关注公众号后 自动回复文本消息
    private String handlerSubscribeReply(String content,String replyMessage){
        TextMessageDTO textMessage = DataConvert.fromXML(content, TextMessageDTO.class);
        return handlerTextReply(replyMessage,textMessage.getToUserName(),textMessage.getFromUserName());
    }
    private String handlerEncrypt(String content,String timestamp){
        //String timestamp = new Date().getTime()+"";
        //加密并返回xml格式
        String encrypted = WeixinUtils.msgCrypt(weixinConfig,content,timestamp,nonce);
        return encrypted;
    }

    //关注、取消事件通知 发送给 接口
    private void saveUser(String openId,Boolean status){
        try {
            //保存到数据表
            wxPublicUser.setOpenid(openId);
            wxPublicUser.setCreateTime(LocalDateTime.now());
            wxPublicUser.setUpdateTime(LocalDateTime.now());
            wxPublicUser.setStatus(status==true? TableEnum.WX_USER_SUBSCRIBE.getCodeStr():TableEnum.WX_USER_UNSUBSCRIBE.getCodeStr());
            wxPublicUser.setProduct("yjs");
            saveWxUser(wxPublicUser);


        } catch (Exception e) {
            log.error("关注/取消 异常 exception={}", e.getMessage());
        }
    }
    /**
     * 检验签名
     * @param signature 签名字符串
     * @param timestamp 时间戳
     * @param nonce 随机字符串
     * @return
     */
    @Override
    public boolean checkSignature(String signature,String timestamp,String nonce)  {
        if(signature==null || timestamp==null || nonce==null){
            return false;
        }
        return WeixinUtils.checkSignature(weixinConfig.getWxToken(),signature,timestamp,nonce);
    }


    //将openid 和 关注状态写入mysql数据表
    @Override
    public int saveWxUser(WxPublicUser wxPublicUser) {
        int id = 0;
        //查询是否已存在
        if(wxPublicUserMapper.getUserInfo(wxPublicUser.getOpenid(),wxPublicUser.getProduct()) == null) {
            wxPublicUserMapper.insertUser(wxPublicUser);
        }
        else{
            wxPublicUserMapper.updateUser(wxPublicUser);
        }
        return id;
    }
}
