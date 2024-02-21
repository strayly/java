package com.xiaozhao.bean;

import java.io.Serializable;

public class LoginInfoBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private long uid;
	private long logintime;
	private String uname;
	private String upass;
	
	public long getUid(){
		return uid;
	}
	public void setUid(long uid){
		this.uid = uid;
	}
	
	public long getLogintime(){
		return logintime;
	}
	public void setLogintime(long logintime){
		this.logintime = logintime;
	}
	
	public String getUname(){
		return uname;
	}
	public void setUname(String uname){
		this.uname = uname;
	}	
	
	public String getUpass(){
		return upass;
	}
	public void setUpass(String upass){
		this.upass = upass;
	}
	
}
