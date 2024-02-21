package com.xiaozhao.datahandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.xiaozhao.bean.JobItemInfoBean;
import com.xiaozhao.conf.Setting;
import com.xiaozhao.util.HttpConnectionUtil;

import org.json.JSONArray;
import org.json.JSONObject;

public class ApplyJobHandler{
	public static final String TAG = "ApplyHandler";
	private int resultCode;
	private String resultMsg = "";
	private List<JobItemInfoBean> jobItemInfoBeans;
	private JobItemInfoBean jobItemInfoBean;
	private int total;
	public ApplyJobHandler(){
		jobItemInfoBeans = new ArrayList<JobItemInfoBean>();
		total = 0;
	}
	public int getResultCode(){
		return resultCode;
	}
	public String getResultMsg(){
		return resultMsg;
	}
	public List<JobItemInfoBean> getApplyList(){
		return jobItemInfoBeans;
	}

	public int getTotal(){
		return total;
	}
	public void myApplyList(Map<String, String> map){
		JSONArray jsonArray;
		map.put("module","myapply");
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
					//System.out.println(jsonArray);

					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject itemObject = jsonArray.getJSONObject(i);
						jobItemInfoBean = new JobItemInfoBean();
						String city = "";//itemObject.getString("city");

						String title = itemObject.getString("title");
						String cname = itemObject.getString("cname");
						String jname = itemObject.getString("jname");
						//String date = itemObject.getString("pubdate");
						String applydate = itemObject.getString("applydate");
						String jobterm = "";//itemObject.getString("jobterm");
						String jobid51job = itemObject.getString("jobid51job");


						String linktype = itemObject.getString("linktype");

						int linkid = itemObject.getInt("linkid");

						jobItemInfoBean.setCity(city == null ? "" : city);
						jobItemInfoBean.setTitle(title == null ? "" : title);
						jobItemInfoBean.setDate(applydate == null ? "" : applydate);
						jobItemInfoBean.setJobterm(jobterm == null ? "" : jobterm);
						//jobItemInfo.setLinkurl(linkurl == null ? "" : linkurl);
						jobItemInfoBean.setLinkid(linkid == 0 ? 0 : linkid);
						jobItemInfoBean.setLinktype(linktype == "" ? "" : linktype);



						jobItemInfoBeans.add(jobItemInfoBean);
						//Log.e(TAG, itemObject.getString("title"));

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
	public int subApply(Map<String, String> map){
		JSONArray jsonArray;
		//投递接口
		map.put("module","applysub");
		map.put("version", Setting.APP_VERSION);
		HttpConnectionUtil conn = new HttpConnectionUtil();
		String data = conn.requestGetJson(Setting.API_ROOT_URL, map);
		int collectid = 0;
		if(conn.getStatusCode()==200){
			try {
				//jsonArray = new JSONArray(s);
				JSONObject jsonObject = new JSONObject(data);
				String jsonStatus = jsonObject.getString("result");
				resultMsg = jsonObject.getString("message");
				resultCode =  Integer.valueOf(jsonStatus);
				if(resultCode==200){
					//{"result":"200","resultbody":{"id":"954886","collected":"1"}}
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