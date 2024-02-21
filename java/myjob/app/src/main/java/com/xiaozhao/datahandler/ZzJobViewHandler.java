package com.xiaozhao.datahandler;

import com.xiaozhao.bean.JobDescBean;
import com.xiaozhao.bean.JobItemInfoBean;
import com.xiaozhao.conf.Setting;
import com.xiaozhao.util.HttpConnectionUtil;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class ZzJobViewHandler {
	

	private int total;
	private JobDescBean jobDescBean;
	private int resultCode;
	public ZzJobViewHandler(){
		//jobItemInfos = new ArrayList<JobItemInfo>();
		total = 0;
		
	}
	public int getResultCode(){
		return resultCode;
	}

	
	public int getTotal(){
		return total;
	}
	
	public JobDescBean getJobDescBean(){
		return jobDescBean;
	}
	
	
	public void handlerDataDetail(int zzid){
		JSONArray jsonArray;
		ZzJobViewCache dataCache = new ZzJobViewCache();
		String s = dataCache.getZzJobIntro(zzid);
			try {
				JSONObject jsonObject = new JSONObject(s);
				String jsonStatus = jsonObject.getString("result");
				resultCode =  Integer.valueOf(jsonStatus);
				if(resultCode==200) {
					JSONObject resultbody = jsonObject.getJSONObject("resultbody");
					String jobintro = resultbody.getString("jobdesc");
					int jid = resultbody.getInt("jobid");

					jobDescBean = new JobDescBean();
					jobDescBean.setJid(jid);

					jobDescBean.setJobintro(jobintro );
					jobDescBean.setComintro("");
					//System.out.println(jobDesc.getJobintro());
					//基本信息
					JobItemInfoBean jinfo = new JobItemInfoBean();
					String jobcity = resultbody.getString("jobplacestr");
					String jobname = resultbody.getString("jobname");
					String cname = resultbody.getString("companyname");
					String jobdate = resultbody.getString("postdate");
					String jobtype = resultbody.getString("jobtermstr");//FullTime,PartTime
					String joburl = resultbody.getString("mobileurl");
					String jobid51job = resultbody.getString("jobid51job");

					if(jobdate.contains(" ")) {
						String[] jobdateArr = jobdate.split(" ");
						jobdate = jobdateArr[0];
					}

					if(jobid51job==null)  jobid51job = "";
					String linktype = "zzjobid";int linkid = zzid;
					if(!jobid51job.equals("") && !jobid51job.equals("0")){
						linktype = "51jobid";
						linkid = Integer.parseInt(jobid51job);
					}
					jinfo.setLinkid(linkid);
					jinfo.setTitle(cname+" "+jobname);
					jinfo.setCity(jobcity==null ? "" : jobcity);
					jinfo.setJname(jobname==null ? "" : jobname);
					jinfo.setCname(cname==null ? "" : cname);
					jinfo.setDate(jobdate==null ? "" : jobdate);
					jinfo.setJobterm(jobtype==null ? "" : jobtype);
					jinfo.setLinkurl(joburl==null ? "" : joburl);
					jinfo.setLinktype(linktype);
					jinfo.setJobid51job(jobid51job);
					jinfo.setZzid(zzid);
					jinfo.setJsid(0);
					jobDescBean.setJobItemInfoBean(jinfo);
				}

			} catch (Exception e) {
				resultCode = -2;//返回的json内容解析失败
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	//
	public int handlerIdFromjob51(int id51){
		JSONArray jsonArray;
		int zzid = 0;
		Map<String, String> map = new HashMap<String, String>();
		HttpConnectionUtil conn = new HttpConnectionUtil();
		map.put("module","zzjobview");
		map.put("jobid51",String.valueOf(id51));
		//map.put("key",getExpCode());
		String data = conn.requestGetJson(Setting.API_ROOT_URL, map);
		try {
			JSONObject jsonObject = new JSONObject(data);
			String jsonStatus = jsonObject.getString("result");
			if(jsonStatus.equals("200")) {
				JSONObject resultbody = jsonObject.getJSONObject("resultbody");
				zzid = resultbody.getInt("ID");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			zzid = 0;
			e.printStackTrace();
		}
		return zzid;
	}

}