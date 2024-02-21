package com.xiaozhao.datahandler;

import com.xiaozhao.bean.CompanyInfoBean;
import com.xiaozhao.bean.JobDescBean;
import com.xiaozhao.bean.JobItemInfoBean;
import com.xiaozhao.conf.Setting;
import com.xiaozhao.util.HttpConnectionUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsJobViewHandler {
	
	
	private List<JobItemInfoBean> jobItemInfoBeans;
	private JobItemInfoBean jobItemInfoBean;
	private int total;
	private JobDescBean jobDescBean;
    private CompanyInfoBean companyInfoBean;
	private int resultCode;

	public JsJobViewHandler(){
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
	
	public JobDescBean getJobDescBean(){
		return jobDescBean;
	}

    public CompanyInfoBean getCompanyInfoBean(){
        return companyInfoBean;
    }
	
	public void handlerDataDetail(int id){
		JSONArray jsonArray;
		JsJobViewCache dataCache = new JsJobViewCache();
		String s = dataCache.getJobIntro(id);

			try {
				JSONObject jsonObject = new JSONObject(s);
				String jsonStatus = jsonObject.getString("result");
				resultCode =  Integer.valueOf(jsonStatus);
				//System.out.println("eeeee:"+s);
				//System.out.println(jsonStatus);
				if(resultCode==200) {
					JSONObject resultbody = jsonObject.getJSONObject("resultbody");
					String jobintro = resultbody.getString("jobintro");

					int jid = resultbody.getInt("jsjobid");
					int cid = resultbody.getInt("companyid");
					//String comintro = null;
					String comintro = resultbody.getString("comintro");
					jobDescBean = new JobDescBean();
					jobDescBean.setJid(jid);
					jobDescBean.setCid(cid);
					//jobDesc.setJobintro(jobintro==null||jobintro=="null" ? "" : java.net.URLDecoder.decode(jobintro,"UTF-8"));
					//jobDesc.setComintro(comintro==null||comintro=="null" ? "" : java.net.URLDecoder.decode(comintro,"UTF-8"));
					jobDescBean.setBumen(resultbody.getString("jigou") );
					jobDescBean.setRenshu(resultbody.getString("people") );
					jobDescBean.setRemoteurl(resultbody.getString("remoteurl") );
					jobDescBean.setJobintro(jobintro );
					jobDescBean.setComintro(comintro);
					//System.out.println(jobDesc.getJobintro());
					//基本信息
					JobItemInfoBean jinfo = new JobItemInfoBean();
					String jobcity = resultbody.getString("mulplace");
					String jobname = resultbody.getString("jname");
					String cname = resultbody.getString("cname");
					String jobdate = resultbody.getString("postdate");
					String jobtype = resultbody.getString("jobskinds");//FullTime,PartTime
					String joburl = resultbody.getString("mobileurl");
					String title = resultbody.getString("title");
					if(jobdate.contains("年")) jobdate = jobdate.replace( "年","-" );
					if(jobdate.contains("月")) jobdate = jobdate.replace( "月","-" );
					if(jobdate.contains("日")) jobdate = jobdate.replace( "日","" );
					jinfo.setLinkid(id);
					jinfo.setCity(jobcity==null ? "" : jobcity);
					jinfo.setJname(jobname==null ? "" : jobname);
					jinfo.setCname(cname==null ? "" : cname);
					jinfo.setDate(jobdate==null ? "" : jobdate);
					jinfo.setJobterm(jobtype==null ? "" : jobtype);
					jinfo.setLinkurl(joburl==null ? "" : joburl);
					jinfo.setTitle(title==null ? "" : title);
					jinfo.setLinktype("jsjobid");
					jinfo.setJsid(id);
					jinfo.setZzid(0);
					jinfo.setJobid51job("0");
					jobDescBean.setJobItemInfoBean(jinfo);
				}

			} catch (Exception e) {
				resultCode = -2;//返回的json内容解析失败
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	public void handlerCompanyDetail(int id){
		JSONArray jsonArray;
		JsJobViewCache dataCache = new JsJobViewCache();
		String s = dataCache.getCompanyIntro(id);

			try {
				JSONObject jsonObject = new JSONObject(s);
				String jsonStatus = jsonObject.getString("result");
				resultCode =  Integer.valueOf(jsonStatus);
				//System.out.println("eeeee:"+s);
				//System.out.println(jsonStatus);
				if(resultCode==200) {
					JSONObject resultbody = jsonObject.getJSONObject("resultbody");
					int companyid = resultbody.getInt("companyid");
					//String comintro = null;
					String comintro = resultbody.getString("context");
					String cname = resultbody.getString("cname");

                    companyInfoBean = new CompanyInfoBean();
                    companyInfoBean.setCid(companyid);
					//jobDesc.setJobintro(jobintro==null||jobintro=="null" ? "" : java.net.URLDecoder.decode(jobintro,"UTF-8"));
					//jobDesc.setComintro(comintro==null||comintro=="null" ? "" : java.net.URLDecoder.decode(comintro,"UTF-8"));
                    companyInfoBean.setCname(cname);
                    companyInfoBean.setComintro(comintro);
					//System.out.println(jobDesc.getJobintro());

				}

			} catch (Exception e) {
				resultCode = -2;//返回的json内容解析失败
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	public void handlerCompanyJobs(Map<String, String> map){
		JSONArray jsonArray;
		map.put("module","jscomjobs");
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
						jobItemInfoBean = new JobItemInfoBean();

						//String intro = jsonObject.getString("intro");
						String date = itemObject.getString("jobdate");
						//String jobterm = itemObject.getString("jobterm");
						String city = itemObject.getString("mulplace");//FullTime,PartTime
						//String linkurl = "";
						String jname = itemObject.getString("jname");
						//String cname = itemObject.getString("cname");
						//String source = jsonObject.getString("source");
						String title = jname;
						title = title==null ? "" : java.net.URLDecoder.decode(title,"UTF-8");

						//linkurl = linkurl==null ? "" : java.net.URLDecoder.decode(linkurl,"UTF-8");

						//String linktype = itemObject.getString("linktype");
						int jsjobid = itemObject.getInt("jsjobid");


						jobItemInfoBean.setLinkid(jsjobid);

						//if(linktype.equals("51jobid")) title = title+"(51job)";
						jobItemInfoBean.setTitle(title);
						jobItemInfoBean.setDate(date==null ? "" : java.net.URLDecoder.decode(date,"UTF-8"));
						jobItemInfoBean.setCity(city==null ? "" : java.net.URLDecoder.decode(city,"UTF-8"));
						jobItemInfoBean.setJobterm("");
						//jobitemInfoBean.setLinkurl(linkurl);
						jobItemInfoBean.setLinktype("jsjobid");
						jobItemInfoBean.setTop(0);

						this.jobItemInfoBeans.add(jobItemInfoBean);

					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}