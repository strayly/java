package com.weixinpublic.controller;

import com.weixinpublic.service.WeixinMenuService;
import org.apache.commons.io.IOUtils;
import com.weixinpublic.enums.ErrorEnum;
import com.weixinpublic.service.WeixinMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;



//接收微信消息接口
@RestController
@RequestMapping("/weixin")
@Slf4j
public class WeixinRecieverController {
    @Resource
    private WeixinMessageService weixinMessageService;
    @Resource
    private WeixinMenuService weixinMenuService;
    /**
     * 验证签名
     * @param request 接收GET参数 signature,timestamp,nonce,echostr用于验证签名)
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/message")
    public String checkSignature(HttpServletRequest request) throws Exception {
        // 微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
        String signature = request.getParameter("signature");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");
        // 随机字符串
        String echostr = request.getParameter("echostr");

        if (weixinMessageService.checkSignature(signature, timestamp, nonce)) {
            return echostr;
        }
        return ErrorEnum.WX_CHECK_SIGNATURE_ERROR.getMsg();

    }

    /**
     * 接收微信消息接口 POST
     * @param request (接收GET参数 signature,timestamp,nonce,msg_signature,encrypt_type,用于验证签名和消息解密)
     * @return 默认返回 空字符串""给微信，有回复消息时，返回xml格式的回复消息
     * @throws Exception
     */
    @RequestMapping(value = "/message", method = {RequestMethod.POST})
    public String receiveMessage(HttpServletRequest request) throws Exception {
        String result = "";
        request.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        String url = request.getRequestURL().toString();
        String queryString = request.getQueryString();
        String fullUrl = url + (queryString != null ? "?" + queryString : "");
        String msgSignature = "";String timestamp = "";String nonce = "";
        log.info("request:{}",fullUrl);
        ServletInputStream inputStream = request.getInputStream();
        msgSignature = request.getParameter("msg_signature");
        timestamp = request.getParameter("timestamp");
        nonce = request.getParameter("nonce");
        String signature = request.getParameter("signature");
        String encryptType = request.getParameter("encrypt_type");
        String xml  = IOUtils.toString(inputStream,StandardCharsets.UTF_8.toString());
        log.info("原消息：{}", xml);
        //saveMassageLog(xml);
        //验证签名
        if (weixinMessageService.checkSignature(signature, timestamp, nonce)==false) {
            return ErrorEnum.WX_CHECK_SIGNATURE_ERROR.getMsg();
        }
        if(xml.toLowerCase().contains("<xml>")) {
            result = weixinMessageService.processMessage( xml,encryptType, msgSignature, timestamp, nonce);
        }
        return result;
    }
    /**
     * 检测与微信接口的网络通信状况
     * @return
     * @throws Exception
     */
    /*
    @GetMapping(value = "/checkNet")
    public String checkNet() throws Exception {
        return weixinMenuService.checkNet();
    }
    */

}
