package com.xiaozhao.datahandler;

import com.xiaozhao.bean.JobDescBean;
import com.xiaozhao.bean.JobItemInfoBean;
import com.xiaozhao.conf.Setting;
import com.xiaozhao.util.HttpConnectionUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecJobsHandler {
	
	
	private int total;
	private JobDescBean jobDescBean;
	private List<JobItemInfoBean> jobitemInfoBeans;
	private JobItemInfoBean jobitemInfoBean;
	public RecJobsHandler(){
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
	
	public void handlerData(Map<String, String> map){
		JSONArray jsonArray;
		map.put("module","recjobs");
		map.put("version", Setting.APP_VERSION);

		HttpConnectionUtil conn = new HttpConnectionUtil();
		//map.put("key",getExpCode());
		String data = conn.requestGet(Setting.API_ROOT_URL, map);


		if(data==null){
			this.total = -1;
		}
		else if(data.indexOf("resultbody")==-1){
			this.total = -2;
		}
		else{
			try {
				JSONObject jsonObject = new JSONObject(data);
				String jsonStatus = jsonObject.getString("result");
				if(jsonStatus.equals("200")) {
					JSONObject resultbody = jsonObject.getJSONObject("resultbody");
					jsonArray = resultbody.getJSONArray("items");
					total = resultbody.getInt("totalcount");

					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject itemObject = jsonArray.getJSONObject(i);
						jobitemInfoBean = new JobItemInfoBean();

						//String intro = jsonObject.getString("intro");
						String date = itemObject.getString("date");
						//String jobterm = itemObject.getString("jobterm");
						String city = itemObject.getString("city");//FullTime,PartTime
						//String linkurl = "";
						String jname = itemObject.getString("jname");
						String cname = itemObject.getString("cname");
						//String source = jsonObject.getString("source");
						String title = cname+" "+jname;
						title = title==null ? "" : java.net.URLDecoder.decode(title,"UTF-8");

						//linkurl = linkurl==null ? "" : java.net.URLDecoder.decode(linkurl,"UTF-8");

						String linktype = itemObject.getString("linktype");
						int linkid = itemObject.getInt("linkid");


						jobitemInfoBean.setLinkid(linkid);

						//if(linktype.equals("51jobid")) title = title+"(51job)";
						jobitemInfoBean.setTitle(title);
						jobitemInfoBean.setDate(date==null ? "" : java.net.URLDecoder.decode(date,"UTF-8"));
						jobitemInfoBean.setCity(city==null ? "" : java.net.URLDecoder.decode(city,"UTF-8"));
						jobitemInfoBean.setJobterm("");
						//jobitemInfoBean.setLinkurl(linkurl);
						jobitemInfoBean.setLinktype(linktype);
						jobitemInfoBean.setTop(0);

						this.jobitemInfoBeans.add(jobitemInfoBean);

					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}