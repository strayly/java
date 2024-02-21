package com.xiaozhao.datahandler;

import com.xiaozhao.bean.JobItemInfoBean;
import com.xiaozhao.conf.Setting;
import com.xiaozhao.util.Html2Text;
import com.xiaozhao.util.HttpConnectionUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchJobHandler{
	
	
	private List<JobItemInfoBean> jobsearchs;
	private JobItemInfoBean jobsearch;
	private int total;
	private int resultCode;
	
	public SearchJobHandler(){
		jobsearchs = new ArrayList<JobItemInfoBean>();
		total = 0;
		
	}

	public List<JobItemInfoBean> getListData(){
		return jobsearchs;
	}
	
	public int getTotal(){
		return total;
	}

	public int getResultCode() {
		return resultCode;
	}
	public void handlerData(Map<String, String> map){
		JSONArray jsonArray;

		map.put("module","jobsearch");
		map.put("version", Setting.APP_VERSION);
		HttpConnectionUtil conn = new HttpConnectionUtil();
		String data = conn.requestGetJson(Setting.API_ROOT_URL, map);
		if (conn.getStatusCode() == 200) {
			try {
				JSONObject jsonObject = new JSONObject(data);
				String jsonStatus = jsonObject.getString("result");
				resultCode = Integer.valueOf(jsonStatus);
				if(resultCode==200) {
					JSONObject resultbody = jsonObject.getJSONObject("resultbody");
					jsonArray = resultbody.getJSONArray("items");
					total = resultbody.getInt("totalcount");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject itemObject = jsonArray.getJSONObject(i);

						jobsearch = new JobItemInfoBean();
						String title = itemObject.getString("title");
						//String intro = jsonObject.getString("intro");
						String date = itemObject.getString("date");
						String jobterm = itemObject.getString("jobterm");
						String city = itemObject.getString("city");//FullTime,PartTime
						String source = itemObject.getString("source");

						int linkid = itemObject.getInt("linkid");
						String linktype = itemObject.getString("linktype");

						Html2Text html2text = new Html2Text();
						title = html2text.html2Text2(title);

						jobsearch.setLinkid(linkid);
						jobsearch.setLinktype(linktype);
						jobsearch.setTitle(title == null ? "" : title);
						//jobsearch.setIntro(intro == null ? "" : java.net.URLDecoder.decode(intro, "UTF-8"));
						jobsearch.setDate(date == null ? "" : date);
						jobsearch.setCity(city == null ? "" : city);
						jobsearch.setJobterm(jobterm == null ? "" : jobterm);
						//jobsearch.setJoburl(joburl == null ? "" : java.net.URLDecoder.decode(joburl, "UTF-8"));
						jobsearch.setSource(source == null ? "" : source);
						jobsearchs.add(jobsearch);

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