package com.xiaozhao.bean;

import java.io.Serializable;

public class JobItemInfoBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private int linkid;
	private String linktype;
	private String linkurl;
	private String city;
	private String title;
	private String intro;
	private String jobterm;
	private String date;
	private String source;
	private int indexId;
	private String jname;
	private String cname;
	private String jobid51job;
	private int jsid;
	private int zzid;
	private int top;

	public String getCity(){
		return this.city;
	}
	public void setCity(String city){
		this.city = city;
	}
	
	public int getLinkid(){
		return this.linkid;
	}
	public void setLinkid(int linkid){
		this.linkid = linkid;
	}
	
	public String getLinktype(){
		return this.linktype;
	}
	public void setLinktype(String linktype){
		this.linktype = linktype;
	}
	
	public String getLinkurl(){
		return this.linkurl;
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
	
	public String getJobterm(){
		return this.jobterm;
	}
	public void setJobterm(String jobterm)	{
		this.jobterm = jobterm;
	}
	
	public String getIntro(){
		return intro;
	}
	public void setIntro(String intro)	{
		this.intro = intro;
	}
	
	public String getDate()	{
		return date;
	}
	public void setDate(String date){
		this.date = date;
	}
	
	public String getSource()	{
		return source;
	}
	public void setSource(String source){
		this.source = source;
	}

	public int getIndexId(){
		return this.indexId;
	}
	public void setIndexId(int indexId){
		this.indexId = indexId;
	}

	public String getJname(){
		return jname;
	}
	public void setJname(String jname){
		this.jname = jname;
	}

	public String getCname(){
		return cname;
	}
	public void setCname(String cname){
		this.cname = cname;
	}

	public String getJobid51job(){
		return jobid51job;
	}
	public void setJobid51job(String jobid51job){
		this.jobid51job = jobid51job;
	}

	public int getJsid(){
		return this.jsid;
	}
	public void setJsid(int jsid){
		this.jsid = jsid;
	}

	public int getZzid(){
		return this.zzid;
	}
	public void setZzid(int zzid){
		this.zzid = zzid;
	}

	public int getTop(){
		return this.top;
	}
	public void setTop(int top){
		this.top = top;
	}

}
