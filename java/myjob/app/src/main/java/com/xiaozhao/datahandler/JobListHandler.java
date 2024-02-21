package com.xiaozhao.datahandler;

import android.content.Context;

import com.xiaozhao.bean.JobDescBean;
import com.xiaozhao.bean.JobItemInfoBean;
import com.xiaozhao.conf.Setting;
import com.xiaozhao.util.Html2Text;
import com.xiaozhao.util.HttpConnectionUtil;
import com.xiaozhao.util.MD5;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JobListHandler {
	
	
	private int total;
	private JobDescBean jobDescBean;
	private List<JobItemInfoBean> jobitemInfoBeans;
	private JobItemInfoBean jobitemInfoBean;
	private int resultCode;
	private Context context;
	public JobListHandler(Context context){
		this.context = context;
		this.jobitemInfoBeans = new ArrayList<JobItemInfoBean>();
		total = 0;
		
	}

	public List<JobItemInfoBean> getListData(){
		return this.jobitemInfoBeans;
	}
	
	public int getTotal(){
		return total;
	}
	
	public JobDescBean getJobDescBean(){
		return jobDescBean;
	}
	public int getResultCode() {
		return resultCode;
	}
	public void handlerData(Map<String, String> map,boolean fromCache){
		JSONArray jsonArray;
		map.put("module","joblist");
		map.put("version", Setting.APP_VERSION);
		int page = Integer.parseInt(map.get("page"));
		String data = "";
		String cacheKey = "joblist_" + MD5.MD5Encode(map.get("locationname")) + "_" + String.valueOf(page) + "_" + map.get("jobterm") ;
		DataCache dataCache = new DataCache(context, cacheKey);
		if(page==1 && fromCache==true) {
			data = dataCache.getCacheData();

		}
		if(data==null || data.length()==0) {
			HttpConnectionUtil conn = new HttpConnectionUtil();
			data = conn.requestGetJson(Setting.API_ROOT_URL, map);
			System.out.println(data);
			if (conn.getStatusCode() != 200) {
				data = "";
				resultCode = -1;
			}
			else{
				dataCache.setCacheData(data);
			}

		}
		if(data!=null && data.length()>0) {
			try {
				JSONObject jsonObject = new JSONObject(data);
				String jsonStatus = jsonObject.getString("result");
				resultCode = Integer.valueOf(jsonStatus);
				if(resultCode==200) {
					JSONObject resultbody = jsonObject.getJSONObject("resultbody");
					jsonArray = resultbody.getJSONArray("items");
					total = resultbody.getInt("totalcount");
					//System.out.println(jsonArray);
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject itemObject = jsonArray.getJSONObject(i);
						jobitemInfoBean = new JobItemInfoBean();
						String title = itemObject.getString("title");
						//String intro = jsonObject.getString("intro");
						String date = itemObject.getString("date");
						String jobterm = itemObject.getString("jobterm");
						String city = itemObject.getString("city");//FullTime,PartTime
						String linkurl = "";
						//String source = jsonObject.getString("source");
						title = title==null ? "" : java.net.URLDecoder.decode(title,"UTF-8");
						Html2Text h2t = new Html2Text();
						title = h2t.html2Text2(title);

						linkurl = linkurl==null ? "" : java.net.URLDecoder.decode(linkurl,"UTF-8");

						String linktype = itemObject.getString("linktype");
						int linkid = itemObject.getInt("linkid");
						int top = itemObject.getInt("istop");

						jobitemInfoBean.setLinkid(linkid);
						//if(linktype.equals("51jobid")) title = title+"(51job)";
						jobitemInfoBean.setTitle(title);
						jobitemInfoBean.setDate(date==null ? "" : java.net.URLDecoder.decode(date,"UTF-8"));
						jobitemInfoBean.setCity(city==null ? "" : java.net.URLDecoder.decode(city,"UTF-8"));
						jobitemInfoBean.setJobterm(jobterm==null ? "" : java.net.URLDecoder.decode(jobterm,"UTF-8"));
						jobitemInfoBean.setLinkurl(linkurl);
						jobitemInfoBean.setLinktype(linktype);
						jobitemInfoBean.setTop(top);
						this.jobitemInfoBeans.add(jobitemInfoBean);
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

}