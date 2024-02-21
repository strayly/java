package com.xiaozhao.datahandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xiaozhao.bean.XjhInfoBean;
import com.xiaozhao.conf.Setting;
import com.xiaozhao.util.HttpConnectionUtil;

public class XjhCollectHandler{
	

	
	private List<XjhInfoBean> xjhInfoBeans;
	private XjhInfoBean xjhInfoBean;
	private int total;
	private int resultCode;
	public XjhCollectHandler(){
		xjhInfoBeans = new ArrayList<XjhInfoBean>();
		total = 0;
		
	}


	public int getResultCode(){
		return resultCode;
	}

	public List<XjhInfoBean> getListXjh(){
		return xjhInfoBeans;
	}
	
	public int getTotal(){
		return total;
	}
	public void handlerData(Map<String, String> map){
		JSONArray jsonArray;
		map.put("module","xjhcollection");
		map.put("version", Setting.APP_VERSION);
		HttpConnectionUtil conn = new HttpConnectionUtil();
		String data = conn.requestGetJson(Setting.API_ROOT_URL, map);

		if(conn.getStatusCode()==200){
			try {
				//jsonArray = new JSONArray(s);
				JSONObject jsonObject = new JSONObject(data);
				String jsonStatus = jsonObject.getString("result");
				resultCode =  Integer.valueOf(jsonStatus);
				if(resultCode==200){
					JSONObject resultbody = jsonObject.getJSONObject("resultbody");
					jsonArray = resultbody.getJSONArray("items");
					total = resultbody.getInt("totalcount");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject itemObject = jsonArray.getJSONObject(i);
						xjhInfoBean = new XjhInfoBean();
						String cname = itemObject.getString("cname");
						String logourl = itemObject.getString("logourl");
						String cityname = itemObject.getString("cityname");
						String address = itemObject.getString("address");
						String school = itemObject.getString("school");

						String provinceid = itemObject.getString("provinceid");
						String schoolid = itemObject.getString("schoolid");
						String cityid = itemObject.getString("cityid");

						String old = itemObject.getString("old");
						String updatetime = itemObject.getString("updatetime");

						String cid = itemObject.getString("cid");
						String industryname = itemObject.getString("industryname");
						String uid = itemObject.getString("uid");
						String startdate = itemObject.getString("xjhdate");
						String starttime = itemObject.getString("xjhtime");


						Long indexId = itemObject.getLong("id");
						int id = itemObject.getInt("xjhid");

						xjhInfoBean.setId(id);
						xjhInfoBean.setCityname(cityname == null ? "" : cityname);
						xjhInfoBean.setSchool(school == null ? "" : school);
						xjhInfoBean.setAddress(address == null ? "" : address);
						xjhInfoBean.setCompany(cname == null ? "" : cname);
						xjhInfoBean.setXjhdate(startdate == null ? "" : startdate);
						xjhInfoBean.setXjhtime(starttime == null ? "" : starttime);
						xjhInfoBean.setLogourl(logourl == null ? "" : logourl);
						xjhInfoBean.setIndexId(indexId);

						xjhInfoBeans.add(xjhInfoBean);

					}
				}
			} catch (Exception e) {
				resultCode = -2;//返回的json内容解析失败
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			resultCode = -1;//url 链接 请求失败返回-1
		}
	}
	//收藏某条宣讲会
	public int subCollect(Map<String, String> map){
		int collectid = 0;
		JSONArray jsonArray;
		map.put("module","subxjhcollection");
		map.put("version", Setting.APP_VERSION);
		map.put("action","sub"); //收藏
		//map.put("action","unsub"); //取消收藏

		Map<String, String> postMap = new HashMap<String, String>();
		postMap.put("subinfo",map.get("subinfo"));
		map.remove("subinfo");
		HttpConnectionUtil conn = new HttpConnectionUtil();
		String data = conn.requestPost(Setting.API_ROOT_URL, map,postMap);

		if(conn.getStatusCode()==200){
			try {
				//jsonArray = new JSONArray(s);
				JSONObject jsonObject = new JSONObject(data);
				String jsonStatus = jsonObject.getString("result");
				resultCode =  Integer.valueOf(jsonStatus);
				//{"result":"200","resultbody":{"id":"954886","collected":"1"}}
				if(resultCode==200){
					//{"result":"200","resultbody":{"id":"954886","collected":"1"}}
					JSONObject resultbody = jsonObject.getJSONObject("resultbody");
					collectid = resultbody.getInt("id");
				}
			} catch (Exception e) {
				resultCode = -2;//返回的json内容解析失败
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			resultCode = -1;//url 链接 请求失败返回-1
		}
		return collectid;
	}
	//取消收藏
	public void unsubCollect(Map<String, String> map){
		JSONArray jsonArray;
		map.put("module","subxjhcollection");
		map.put("version", Setting.APP_VERSION);
		map.put("action","unsub"); //取消收藏

		HttpConnectionUtil conn = new HttpConnectionUtil();
		String data = conn.requestGetJson(Setting.API_ROOT_URL, map);

		if(conn.getStatusCode()==200){
			try {
				//jsonArray = new JSONArray(s);
				JSONObject jsonObject = new JSONObject(data);
				String jsonStatus = jsonObject.getString("result");
				resultCode =  Integer.valueOf(jsonStatus);

				if(resultCode==200){
					//success
				}
			} catch (Exception e) {
				resultCode = -2;//返回的json内容解析失败
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			resultCode = -1;//url 链接 请求失败返回-1
		}
	}

	public int isCollected(Map<String, String> map){
		JSONArray jsonArray;
		map.put("module","checkcollection");
		map.put("version", Setting.APP_VERSION);
		HttpConnectionUtil conn = new HttpConnectionUtil();
		String data = conn.requestGetJson(Setting.API_ROOT_URL, map);
		int collectid = 0;
		if(conn.getStatusCode()==200){
			try {
				//jsonArray = new JSONArray(s);
				JSONObject jsonObject = new JSONObject(data);
				String jsonStatus = jsonObject.getString("result");
				resultCode =  Integer.valueOf(jsonStatus);
				if(resultCode==200){
					//{"result":"200","resultbody":{"id":"954886","collected":"1"}}
					JSONObject resultbody = jsonObject.getJSONObject("resultbody");
					String collected = resultbody.getString("collected");
					try {
						collectid = resultbody.getInt("id");
					}
					catch (Exception e){
						collectid = 0;
					}
				}
			} catch (Exception e) {
				resultCode = -2;//返回的json内容解析失败
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			resultCode = -1;//url 链接 请求失败返回-1
		}
		return collectid;
	}
}