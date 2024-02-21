package com.xiaozhao.datahandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xiaozhao.bean.JobItemInfoBean;
import com.xiaozhao.conf.Setting;
import com.xiaozhao.util.HttpConnectionUtil;

public class JobCollectHandler{
	

	
	private List<JobItemInfoBean> jobItemInfoBeans;
	private JobItemInfoBean jobItemInfoBean;
	private int total;
	private int resultCode;
	public JobCollectHandler(){
		jobItemInfoBeans = new ArrayList<JobItemInfoBean>();
		total = 0;
		
	}


	public int getResultCode(){
		return resultCode;
	}

	public List<JobItemInfoBean> getListData(){
		return jobItemInfoBeans;
	}
	
	public int getTotal(){
		return total;
	}
	public void handlerData(Map<String, String> map){
		JSONArray jsonArray;
		map.put("module","jobcollection");
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
						jobItemInfoBean = new JobItemInfoBean();
						String city = itemObject.getString("city");
						String title = itemObject.getString("title");
						String companyname = itemObject.getString("companyname");
						String jobname = itemObject.getString("jobname");
						String date = itemObject.getString("pubdate");
						//String jobterm = itemObject.getString("jobterm");
						String jobid51job = itemObject.getString("jobid51job");


						String linktype = itemObject.getString("linktype");
						int indexId = itemObject.getInt("id");
						int linkid = itemObject.getInt("linkid");

						jobItemInfoBean.setCity(city == null ? "" : city);
						jobItemInfoBean.setTitle(title == null ? "" : title);
						jobItemInfoBean.setDate(date == null ? "" : date);
						//jobItemInfo.setJobterm(jobterm == null ? "" : jobterm);
						//jobItemInfo.setLinkurl(linkurl == null ? "" : linkurl);
						jobItemInfoBean.setLinkid(linkid == 0 ? 0 : linkid);
						jobItemInfoBean.setLinktype(linktype == "" ? "" : linktype);
						jobItemInfoBean.setIndexId(indexId == 0 ? 0 : indexId);


						jobItemInfoBeans.add(jobItemInfoBean);

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
		map.put("module","subjobcollection");
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
		map.put("module","subjobcollection");
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