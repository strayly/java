package com.xiaozhao.bean;

import java.io.Serializable;

public class CompanyInfoBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	

	private int cid;
	private String hangye;
	private String guimo;
	private String xingzhi;
	private String email;
	private String comintro;
	private String cname;
	
	public int getCid(){
		return cid;
	}
	public void setCid(int cid){
		this.cid = cid;
	}

	public String getCname(){
		return cname;
	}
	public void setCname(String cname){
		this.cname = cname;
	}

	public String getHangye(){
		return hangye;
	}
	public void setHangye(String hangye){
		this.hangye = hangye;
	}
	
	public String getGuimo(){
		return guimo;
	}
	public void setGuimo(String guimo){
		this.guimo = guimo;
	}
	
	public String getXingzhi(){
		return xingzhi;
	}
	public void setXingzhi(String xingzhi)	{
		this.xingzhi = xingzhi;
	}
	
	public String getEmail(){
		return email;
	}
	public void setEmail(String email)	{
		this.email = email;
	}

	public String getComintro()	{
		return comintro;
	}
	public void setComintro(String comintro){
		this.comintro = comintro;
	}

}
