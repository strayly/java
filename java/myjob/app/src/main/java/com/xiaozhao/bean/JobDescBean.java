package com.xiaozhao.bean;

import java.io.Serializable;

public class JobDescBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private int jid;
	private int cid;
	private String hangye;
	private String guimo;
	private String xingzhi;
	private String email;
	private String renshu;
	private String jobintro;
	private String bumen;
	private String comintro;
	private String remoteurl;
	private JobItemInfoBean jobItemInfoBean;
	
	public int getCid(){
		return cid;
	}
	public void setCid(int cid){
		this.cid = cid;
	}
	
	public int getJid(){
		return jid;
	}
	public void setJid(int jid){
		this.jid = jid;
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
	
	public String getRenshu()	{
		return renshu;
	}
	public void setRenshu(String renshu){
		this.renshu = renshu;
	}
	
	public String getJobintro()	{
		return jobintro;
	}
	public void setJobintro(String jobintro){
		this.jobintro = jobintro;
	}
	public String getBumen()	{
		return bumen;
	}
	public void setBumen(String bumen){
		this.renshu = bumen;
	}
	public String getComintro()	{
		return comintro;
	}
	public void setComintro(String comintro){
		this.comintro = comintro;
	}
	
	public JobItemInfoBean getJobItemInfoBean()	{
		return jobItemInfoBean;
	}
	public void setJobItemInfoBean(JobItemInfoBean jobItemInfoBean){
		this.jobItemInfoBean = jobItemInfoBean;
	}
	
	public String getRemoteurl(){
		return remoteurl;
	}
	public void setRemoteurl(String remoteurl){
		this.remoteurl = remoteurl;
	}
}
