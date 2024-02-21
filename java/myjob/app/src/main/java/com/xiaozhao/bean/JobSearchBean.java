package com.xiaozhao.bean;

import java.io.Serializable;

public class JobSearchBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private int jsid;
	private int zzid;
	private String jobplace;
	private String title;
	private String intro;
	private String jobtype;
	private String date;
	private String joburl;
	private String source;
	
	public String getJobplace(){
		return jobplace;
	}
	public void setJobplace(String jobplace){
		this.jobplace = jobplace;
	}
	
	public int getJsid(){
		return jsid;
	}
	public void setJsid(int jsid){
		this.jsid = jsid;
	}
	
	public int getZzid(){
		return zzid;
	}
	public void setZzid(int zzid){
		this.zzid = zzid;
	}
	
	public String getTitle(){
		return title;
	}
	public void setTitle(String title){
		this.title = title;
	}
	
	public String getJobtype(){
		return jobtype;
	}
	public void setJobtype(String jobtype)	{
		this.jobtype = jobtype;
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
	
	public String getJoburl()	{
		return joburl;
	}
	public void setJoburl(String joburl){
		this.joburl = joburl;
	}
	
	public String getSource()	{
		return source;
	}
	public void setSource(String source){
		this.source = source;
	}
	

}
