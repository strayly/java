package com.weixinpublic.entity.dto.weixin;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * 接收加密的xml
 <xml>
 <ToUserName><![CDATA[ToUserName]]></ToUserName>
 <Encrypt><![CDATA[加密后的内容]]></Encrypt>
 </xml>

 */
public class EncryptMessageDTO {

    private String toUserName; //接收方（开发者微信号）
    private String encrypt; //加密消息

    @JacksonXmlProperty(localName = "ToUserName")
    @JacksonXmlCData
    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    @JacksonXmlProperty(localName = "Encrypt")
    @JacksonXmlCData
    public String getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(String encrypt) {
        this.encrypt = encrypt;
    }
}
