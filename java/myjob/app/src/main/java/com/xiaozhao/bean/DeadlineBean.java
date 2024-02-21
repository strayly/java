package com.xiaozhao.bean;

import java.io.Serializable;

public class DeadlineBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private int indexid;
	private int linkid;
	private String linktype;
	private String title;
	private String jobplace;
	private String deaddate;
	private String leftday;
	private String linkurl;

	public int getIndexid(){
		return indexid;
	}
	public void setIndexid(int indexid){
		this.indexid = indexid;
	}

	public int getLinkid(){
		return linkid;
	}
	public void setLinkid(int linkid){
		this.linkid = linkid;
	}

	public String getLinkurl(){
		return linkurl;
	}
	public void setLinkurl(String linkurl){
		this.linkurl = linkurl;
	}

	public String getTitle(){
		return title;
	}
	public void setTitle(String title){
		this.title = title;
	}
	
	
	public String getLinktype(){
		return linktype;
	}
	public void setLinktype(String linktype){
		this.linktype = linktype;
	}

	public String getJobplace(){
		return jobplace;
	}
	public void setJobplace(String jobplace){
		this.jobplace = jobplace;
	}
	
	public String getDeaddate(){
		return deaddate;
	}
	public void setDeaddate(String deaddate)	{
		this.deaddate = deaddate;
	}
	
	public String getLeftday()	{
		return leftday;
	}
	public void setLeftday(String leftday){
		this.leftday = leftday;
	}


	
}
