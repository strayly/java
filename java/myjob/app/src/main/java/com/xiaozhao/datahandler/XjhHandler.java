package com.xiaozhao.datahandler;

import com.xiaozhao.bean.XjhInfoBean;
import com.xiaozhao.conf.Setting;
import com.xiaozhao.util.HttpConnectionUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XjhHandler{
	
	private String jobDes_str;
	private String job_yaoqiu = "";
	private String jobContact_str;
	private String Company_str;
	
	private List<XjhInfoBean> xjhInfoBeans;
	private XjhInfoBean xjhInfoBean;
	private int total;
	private int resultCode;
	public XjhHandler(){
		xjhInfoBeans = new ArrayList<XjhInfoBean>();
		total = 0;
		
	}
	public String getCompany_str(){
		return Company_str;
	}

	public String getJobContact_str(){
		return jobContact_str;
	}

	public String getJob_yaoqiu(){
		return job_yaoqiu;
	}

	public String getJobDes_str(){
		return jobDes_str;
	}

	public int getResultCode() {
		return resultCode;
	}

	public List<XjhInfoBean> getListXjh(){
		return xjhInfoBeans;
	}
	
	public int getTotal(){
		return total;
	}
	public void handlerXjhData(Map<String, String> map){
		JSONArray jsonArray;
		map.put("module","xjh");
		map.put("version", Setting.APP_VERSION);
		map.put("rtime",Setting.getRtime());
		map.put("rsign",Setting.getRsign(map.get("page")));
		HttpConnectionUtil conn = new HttpConnectionUtil();
		String data = conn.requestGetJson(Setting.API_ROOT_URL, map);
		if (conn.getStatusCode() == 200) {
			try {
				//jsonArray = new JSONArray(s);
				JSONObject jsonObject = new JSONObject(data);
				String jsonStatus = jsonObject.getString("result");
				resultCode = Integer.valueOf(jsonStatus);
				if(resultCode==200){
					JSONObject resultbody = jsonObject.getJSONObject("resultbody");
					jsonArray = resultbody.getJSONArray("items");
					total = resultbody.getInt("totalcount");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject itemObject = jsonArray.getJSONObject(i);
						xjhInfoBean = new XjhInfoBean();
						String cityname = itemObject.getString("cityname");
						String school = itemObject.getString("school");
						String address = itemObject.getString("address");
						String cname = itemObject.getString("cname");
						String startdate = itemObject.getString("xjhdate");
						String starttime = itemObject.getString("xjhtime");
						String mark = "";
						int id = itemObject.getInt("xjhid");

						xjhInfoBean.setId(id);
						xjhInfoBean.setCityname(cityname == null ? "" : cityname);
						xjhInfoBean.setSchool(school == null ? "" : school);
						xjhInfoBean.setAddress(address == null ? "" : address);
						xjhInfoBean.setCompany(cname == null ? "" : cname);
						xjhInfoBean.setXjhdate(startdate == null ? "" : startdate);
						xjhInfoBean.setXjhtime(starttime == null ? "" : starttime);

						xjhInfoBean.setMark(mark == null ? "" : mark);

						xjhInfoBeans.add(xjhInfoBean);

					}
				}
			} catch (Exception e) {
				resultCode = -2;
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		}
		else{
			resultCode = -1;//url 链接 请求失败返回-1
		}
	}

}