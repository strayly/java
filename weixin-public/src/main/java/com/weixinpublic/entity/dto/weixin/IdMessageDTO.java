package com.weixinpublic.entity.dto.weixin;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * 包含消息id的消息
 */
public class IdMessageDTO extends MessageDTO {

    protected String msgId; // 消息id，64位整型

    @JacksonXmlProperty(localName = "MsgId")
    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
