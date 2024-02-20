package com.weixinpublic.service;


import com.weixinpublic.entity.bo.WxPublicUser;


/**
 * @description
 * @author ruiguo.dong
 * @date 2023/12/21 15:27
 */
public interface WeixinMessageService {

    /**
     * 处理微信消息
     * @param xml POST的消息体
     * @param encryptType 加密方式 aes
     * @param msgSignature 签名
     * @param timestamp 时间戳参数
     * @param nonce 随机字符串
     * @return
     */
    String processMessage(String xml,String encryptType,String msgSignature,String timestamp,String nonce)  ;

    /**
     * 签名验证
     * @param signature 微信传参
     * @param timestamp 微信传参
     * @param nonce 微信传参
     * @return 签名验证结果
     */
    boolean checkSignature(String signature,String timestamp,String nonce) ;

    /**
     * 保存用户openid和关注状态到数据表
     * @param wxPublicUser 对应数据表对象
     * @return 保存结果
     */
     int saveWxUser(WxPublicUser wxPublicUser);
}
