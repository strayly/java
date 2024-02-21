package com.xiaozhao.datahandler;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

import com.xiaozhao.bean.LoginInfoBean;
import com.xiaozhao.conf.Setting;
import com.xiaozhao.util.HttpConnectionUtil;
import com.xiaozhao.util.MD5;

public class UserLoginHandler{	
	
	private long expiretime = 3600*24*30;//
	private LoginInfoBean loginInfoBean;
	
	public UserLoginHandler(){

	}
	
	public long login(String uname,String upass){
		
		Map<String, String> map = new HashMap<String, String>();
		HttpConnectionUtil conn = new HttpConnectionUtil();
		map.put("key",MD5.MD5Encode(MD5.MD5Encode(Setting.APP_SECRET_KEY)));
		map.put("uname",uname);
		map.put("upass",upass);
		
		return 1;

	}
	public LoginInfoBean getLoginInfo(Context context){
		
		SharedPreferences logindata = context.getSharedPreferences("logindata", 0);
		long uid = logindata.getLong("uid",0);
		if(uid>0){
			long logintime = logindata.getLong("logintime",0);
			if(((System.currentTimeMillis()/1000)-logintime)>expiretime) uid = 0;
		}
		
		//测试用
		uid = 1;
		
		this.loginInfoBean = new LoginInfoBean();
		this.loginInfoBean.setUname(logindata.getString("uname",""));
		this.loginInfoBean.setUpass(logindata.getString("pw",""));
		this.loginInfoBean.setUid(logindata.getLong("uid",0));
		this.loginInfoBean.setLogintime(logindata.getLong("logintime",0));
		
		return this.loginInfoBean;
	}
	
	//public void setLoginInfo(){
	//}
	public LoginInfoBean getLoginInfoBean(){
		return this.loginInfoBean;
	}

}