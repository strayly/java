package com.weixinpublic.entity.dto.weixin;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.weixinpublic.enums.weixin.MsgType;

/**
 * 回复文本消息
 */
public class TextResponseDTO extends ResponseDTO {

    private String content; // 文本消息内容

    public TextResponseDTO() {
        this.msgType = MsgType.text;
    }

    @JacksonXmlProperty(localName = "Content")
    @JacksonXmlCData
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
