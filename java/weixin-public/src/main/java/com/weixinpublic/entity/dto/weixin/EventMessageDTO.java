package com.weixinpublic.entity.dto.weixin;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * 关事件消息 (关注 取消 模版消息确认)
 */
public class EventMessageDTO extends MessageDTO {

    private String event; // 事件类型

    @JacksonXmlProperty(localName = "Event")
    public String getEvent() {
        return event;
    }

}
