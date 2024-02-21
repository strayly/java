package com.xiaozhao.conf;

import com.xiaozhao.util.MD5;

public class Setting {
	public final static String APP_VERSION = "240";
	public final static String API_ROOT_URL = "https://";


	public final static String MY_ROOT_URL = "http://";

	
	public final static String APP_VERSION_URL = MY_ROOT_URL+"mobile_api/m_version.html";
	public final static String APP_DOWN_URL = "http://";
	public final static String APP_SD_DIR = "/sdcard/xiaozhao/";
	public final static String APP_SECRET_KEY = "";
	//public final static String WEIBO_APP_KEY = "";
	//public final static String WEIBO_APP_SECRET = "";
	public final static String APP_RSIGN_KEY = "";
	public final static int  APP_RSIGN_KEY_EXPIRE = 300;



	public final static String  getRsign(String id) {
		return MD5.MD5Encode(MD5.MD5Encode(APP_RSIGN_KEY+"|"+id+"|"+getRtime()));
	}
	public final static String  getRtime() {
		return String.valueOf((int)Math.ceil(System.currentTimeMillis()/1000));
	}
}
