package com.xiaozhao.bean;

import java.io.Serializable;

public class XjhInfoBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String cityname;
	private String school;
	private String address;
	private String company;
	private String xjhtime;
	private String xjhdate;
	private String mark;
	private Long indexId;
	private String detail;
	private String provinceid;
	private String cityid;
	private String schoolid;
	private String cid;
	private String industryname;
	private String logourl;
	public String getCityname(){
		return cityname;
	}
	public void setCityname(String city){
		this.cityname = city;
	}
	
	public int getId(){
		return id;
	}
	public void setId(int id){
		this.id = id;
	}

	public Long getIndexId(){
		return indexId;
	}
	public void setIndexId(Long indexId){
		this.indexId = indexId;
	}
	
	public String getSchool(){
		return school;
	}
	public void setSchool(String school){
		this.school = school;
	}
	
	
	public String getAddress(){
		return address;
	}
	public void setAddress(String address){
		this.address = address;
	}
	
	public String getCompany(){
		return company;
	}
	public void setCompany(String company)	{
		this.company = company;
	}
	
	public String getXjhdate()	{
		return xjhdate;
	}
	public void setXjhdate(String date){
		this.xjhdate = date;
	}
	
	public String getXjhtime()	{
		return xjhtime;
	}
	public void setXjhtime(String time){
		this.xjhtime = time;
	}
	
	public String getMark()	{
		return mark;
	}
	public void setMark(String mark){
		this.mark = mark;
	}

	public String getDetail()	{
		return detail;
	}
	public void setDetail(String detail){
		this.detail = detail;
	}

	public String getProvinceid()	{
		return provinceid;
	}
	public void setProvinceid(String provinceid){
		this.provinceid = provinceid;
	}

	public String getSchoolid()	{
		return schoolid;
	}
	public void setSchoolid(String schoolid){
		this.schoolid = schoolid;
	}

	public String getCityid()	{
		return cityid;
	}
	public void setCityid(String cityid){
		this.detail = cityid;
	}

	public String getCid()	{
		return cid;
	}
	public void setCid(String cid){
		this.detail = cid;
	}

	public String getIndustryname()	{
		return industryname;
	}
	public void setIndustryname(String industryname){
		this.detail = industryname;
	}

	public String getLogourl()	{
		return logourl;
	}
	public void setLogourl(String logourl){
		this.logourl = logourl;
	}

	
	
}
