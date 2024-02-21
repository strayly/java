package com.xiaozhao.datahandler;

import android.content.Context;

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

public class TjJobHandler{
	
	
	private List<JobItemInfoBean> jobitemInfoList;
	//private JobItemInfoBean jobitemInfo;
	private int total;
	private int resultCode;
	private Context context;
	public TjJobHandler(Context context){
		this.context = context;
		jobitemInfoList = new ArrayList<JobItemInfoBean>();
		total = 0;

	}
	public void setListData(List<JobItemInfoBean> jlist){
		jobitemInfoList = jlist;
	}
	public List<JobItemInfoBean> getListData(){
		return jobitemInfoList;
	}
	
	public int getTotal(){
		return total;
	}

	public int getResultCode() {
		return resultCode;
	}

	public void handlerData(Map<String, String> map,boolean fromCache){
		JSONArray jsonArray;
		map.put("module","recommedlist");
		map.put("version", Setting.APP_VERSION);
		String data = "";
		int page = Integer.parseInt(map.get("page"));
		String cacheKey = "tjjob_" + MD5.MD5Encode(map.get("locationname")) + "_" + String.valueOf(page) + "_" + map.get("jobterm") + "_" + map.get("pernum");
		DataCache dataCache = new DataCache(context, cacheKey);
		if(page==1 && fromCache==true) {
			data = dataCache.getCacheData();

		}
		if(data==null || data.length()==0) {
			HttpConnectionUtil conn = new HttpConnectionUtil();
			data = conn.requestGetJson(Setting.API_ROOT_URL, map);
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
				if (resultCode == 200) {
					JSONObject resultbody = jsonObject.getJSONObject("resultbody");
					jsonArray = resultbody.getJSONArray("items");
					total = resultbody.getInt("totalcount");
					List<JobItemInfoBean> jList = new ArrayList<JobItemInfoBean>();
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject itemObject = jsonArray.getJSONObject(i);
						JobItemInfoBean jobitemInfo = new JobItemInfoBean();
						String title = itemObject.getString("title");
						//String intro = jsonObject.getString("intro");
						String date = itemObject.getString("date");
						String jobterm = itemObject.getString("jobterm");
						String city = itemObject.getString("city");//FullTime,PartTime
						String linkurl = itemObject.getString("linkurl");
						//String source = jsonObject.getString("source");
						title = title == null ? "" : java.net.URLDecoder.decode(title, "UTF-8");
						Html2Text h2t = new Html2Text();
						title = h2t.html2Text2(title);

						linkurl = linkurl == null ? "" : java.net.URLDecoder.decode(linkurl, "UTF-8");

						String linktype = itemObject.getString("linktype");
						int linkid = itemObject.getInt("linkid");
						int top = itemObject.getInt("istop");

						jobitemInfo.setLinkid(linkid);
						if (linktype.equals("51jobid")) title = title + "(51job)";
						jobitemInfo.setTitle(title);
						//jobsearch.setIntro(intro==null ? "" : java.net.URLDecoder.decode(intro,"UTF-8"));
						jobitemInfo.setDate(date == null ? "" : date);
						jobitemInfo.setCity(city == null ? "" : city);
						jobitemInfo.setJobterm(jobterm == null ? "" : jobterm);
						jobitemInfo.setLinkurl(linkurl);
						jobitemInfo.setLinktype(linktype);
						jobitemInfo.setTop(top);
						//jobsearch.setSource(source==null ? "" : java.net.URLDecoder.decode(source,"UTF-8"));
						jList.add(jobitemInfo);

						setListData(jList);
					}

				}
			}catch(Exception e){
				resultCode = -2;//返回的json内容解析失败
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}

	}
}