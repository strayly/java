package com.weixinpublic.entity.dto.weixin;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * 关事件消息 (关注 取消 模版消息确认)
 */
public class TemplateMessageDTO extends EventMessageDTO {

    private String status; // 消息到达状态
    private String msgId; // 消息ID

    @JacksonXmlProperty(localName = "Status")
    public String getStatus() {
        return status;
    }
    @JacksonXmlProperty(localName = "MsgID")
    public String getMsgId() {
        return msgId;
    }
}
