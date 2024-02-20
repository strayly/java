package com.weixinpublic.entity.dto.weixin;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
响应的加密消息格式
<xml>
  <Encrypt></Encrypt>
  <MsgSignature></MsgSignature>
  <TimeStamp></TimeStamp>
  <Nonce></Nonce>
</xml>
 */
public class EncryptResponseDTO {

    private String encrypt;// 加密后的消息体
    private String msgSignature; // 消息签名
    private String timeStamp;   // 时间戳
    private String nonce; // 随机字符串

    @JacksonXmlProperty(localName = "Encrypt")
    @JacksonXmlCData
    public String getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(String encrypt) {
        this.encrypt = encrypt;
    }

    @JacksonXmlProperty(localName = "MsgSignature")
    @JacksonXmlCData
    public String getMsgSignature() {
        return msgSignature;
    }

    public void setMsgSignature(String msgSignature) {
        this.msgSignature = msgSignature;
    }

    @JacksonXmlProperty(localName = "TimeStamp")
    @JacksonXmlCData
    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @JacksonXmlProperty(localName = "Nonce")
    @JacksonXmlCData
    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
}
