package com.xiaozhao.bean;

import java.io.Serializable;

public class DalibaoBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private int dlbid;
	private int classid;
	private String title;
	private String pubdate;
	private String showdate;
	private String classname;
	private String source;
	private String attachment;
	private String message;

	public int getDlbid(){
		return dlbid;
	}
	public void setDlbid(int dlbid){
		this.dlbid = dlbid;
	}

	public int getClassid(){
		return classid;
	}
	public void setClassid(int classid){
		this.classid = classid;
	}


	public String getTitle(){
		return title;
	}
	public void setTitle(String title){
		this.title = title;
	}
	
	
	public String getPubdate(){
		return pubdate;
	}
	public void setPubdate(String pubdate){
		this.pubdate = pubdate;
	}

	public String getShowdate(){
		return showdate;
	}
	public void setShowdate(String showdate){
		this.showdate = showdate;
	}
	
	public String getClassname(){
		return classname;
	}
	public void setClassname(String classname)	{
		this.classname = classname;
	}
	
	public String getSource()	{
		return source;
	}
	public void setSource(String source){
		this.source = source;
	}

	public String getAttachment()	{
		return attachment;
	}
	public void setAttachment(String attachment){
		this.attachment = attachment;
	}

	public String getMessage()	{
		return message;
	}
	public void setMessage(String message){
		this.message = message;
	}


	
	
}
