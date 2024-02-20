package com.weixinpublic.enums.weixin;


import java.util.ArrayList;
import java.util.Arrays;
//设置关键词 自动回复
public enum ReplyMessage {
    //设置的关键词 ，以及回复内容
	REPLY_WORDS1(new ArrayList<>(Arrays.asList("关键词1", "关键词2", "关键词3")),"回复内容1");

	private ArrayList words;
	private String reply;
	ReplyMessage(ArrayList words, String reply) {
		this.words = words;
        this.reply = reply;
	}
    /**
     * 根据关键词查询回复内容
     * @param message 关键词
     * @return 返回的 回复内容
     */
    public static String getReply(String message){
        for(ReplyMessage replyMessage : ReplyMessage.values()){
            if(message!=null){
                for (Object word : replyMessage.words) {
                   if(message.contains(word.toString())) return replyMessage.reply;
                }
            }
        }
        return null;
    }

}