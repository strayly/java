package com.xiaozhao.datahandler;

import com.xiaozhao.conf.Setting;
import com.xiaozhao.util.HttpConnectionUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VersionHandler {


	private String newVersion;
	private String downloadUrl;
	private int resultCode = 0;
	public VersionHandler(){

	}

	public String getNewVersion(){
		return newVersion;
	}
	public String getDownloadUrl(){
		return downloadUrl;
	}
	public int getResultCode(){
		return resultCode;
	}

	public void handlerData(){
		JSONArray jsonArray;
		Map<String, String> map = new HashMap<String, String>();
		map.put("module","androidversion");
		map.put("version", Setting.APP_VERSION);
		HttpConnectionUtil conn = new HttpConnectionUtil();
		String data = conn.requestGet(Setting.API_ROOT_URL, map);
		try {
			JSONObject jsonObject = new JSONObject(data);
			String jsonStatus = jsonObject.getString("result");
			resultCode =  Integer.valueOf(jsonStatus);
			if(resultCode==200) {
				JSONObject resultbody = jsonObject.getJSONObject("resultbody");
				newVersion = resultbody.getString("version");
				downloadUrl = resultbody.getString("url");
			}

		} catch (Exception e) {
			resultCode = -2;//返回的json内容解析失败
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}