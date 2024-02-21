package com.xiaozhao.bean;

import java.io.Serializable;

public class ScheduleBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String subject;
	private String noticetype;
	private String infodate;
	private String company;
	private String infofrom;
	private String fromurl;
	private String message;

	public int getId(){
		return id;
	}
	public void setId(int id){
		this.id = id;
	}


	public String getSubject(){
		return subject;
	}
	public void setSubject(String subject){
		this.subject = subject;
	}
	
	
	public String getNoticetype(){
		return noticetype;
	}
	public void setNoticetype(String noticetype){
		this.noticetype = noticetype;
	}

	public String getInfodate(){
		return infodate;
	}
	public void setInfodate(String infodate){
		this.infodate = infodate;
	}
	
	public String getCompany(){
		return company;
	}
	public void setCompany(String company)	{
		this.company = company;
	}
	
	public String getInfofrom()	{
		return infofrom;
	}
	public void setInfofrom(String infofrom){
		this.infofrom = infofrom;
	}

	public String getFromurl()	{
		return fromurl;
	}
	public void setFromurl(String fromurl){
		this.fromurl = fromurl;
	}

	public String getMessage()	{
		return infofrom;
	}
	public void setMessage(String message){
		this.message = message;
	}


	
	
}
