package com.weixinpublic.entity.dto.weixin;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.weixinpublic.enums.weixin.MsgType;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 微信用户发送给公众号的基础消息
 */
public class MessageDTO {

    protected String toUserName; // 目标方帐号
    protected String fromUserName; // 发送方帐号
    protected String createTime; // 消息创建时间 （整型）
    protected MsgType msgType; // 消息类型

    @JacksonXmlProperty(localName = "ToUserName")
    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    @JacksonXmlProperty(localName = "FromUserName")
    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    @JacksonXmlProperty(localName = "CreateTime")
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @JacksonXmlProperty(localName = "MsgType")
    public MsgType getMsgType() {
        return msgType;
    }

    public void setMsgType(MsgType msgType) {
        this.msgType = msgType;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
