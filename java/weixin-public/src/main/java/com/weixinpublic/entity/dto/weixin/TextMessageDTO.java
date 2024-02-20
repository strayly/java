package com.weixinpublic.entity.dto.weixin;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * 文本消息
 */
public class TextMessageDTO extends IdMessageDTO {

    private String content; // 文本消息内容

    @JacksonXmlProperty(localName = "Content")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
