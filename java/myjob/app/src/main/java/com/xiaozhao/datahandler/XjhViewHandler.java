package com.xiaozhao.datahandler;

import com.xiaozhao.bean.XjhInfoBean;
import com.xiaozhao.conf.Setting;
import com.xiaozhao.util.HttpConnectionUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class XjhViewHandler {


	private XjhInfoBean xjhInfoBean;
	private int resultCode;
	public int getResultCode() {
		return resultCode;
	}
	public XjhViewHandler(){
	}

	public XjhInfoBean getXjhInfoBean(){
		return xjhInfoBean;
	}


	public void handlerDetail(int id){
		JSONArray jsonArray;
		Map<String, String> map = new HashMap<String, String>();
		HttpConnectionUtil conn = new HttpConnectionUtil();
		map.put("module","xjhview");
		map.put("xjhid",String.valueOf(id));
		map.put("rtime",Setting.getRtime());
		map.put("rsign",Setting.getRsign(String.valueOf(id)));
		//map.put("key",getExpCode());
		String data = conn.requestGetJson(Setting.API_ROOT_URL, map);
		if (conn.getStatusCode() == 200) {
			try {
				JSONObject jsonObject = new JSONObject(data);
				String jsonStatus = jsonObject.getString("result");
				resultCode = Integer.valueOf(jsonStatus);
				if (resultCode==200) {
					JSONObject resultbody = jsonObject.getJSONObject("resultbody");
					String xjhdetail = resultbody.getString("xjhdetail");
					int xjhid = resultbody.getInt("xjhid");
					xjhInfoBean = new XjhInfoBean();

					String logourl = resultbody.getString("logourl");
					String mark = resultbody.getString("mark");
					String xjhdate = resultbody.getString("xjhdate");
					String xjhtime = resultbody.getString("xjhtime");
					String address = resultbody.getString("address");
					String school = resultbody.getString("school");
					String cityname = resultbody.getString("cityname");
					String company = resultbody.getString("cname");

					String provinceid = resultbody.getString("provinceid");
					String cityid = resultbody.getString("cityid");
					String schoolid = resultbody.getString("schoolid");
					String industryname = resultbody.getString("industryname");
					String cid = resultbody.getString("cid");

					xjhInfoBean.setId(xjhid);
					xjhInfoBean.setCityname(cityname == null ? "" : cityname);
					xjhInfoBean.setSchool(school == null ? "" : school);
					xjhInfoBean.setAddress(address == null ? "" : address);
					xjhInfoBean.setCompany(company == null ? "" : company);
					xjhInfoBean.setXjhtime(xjhtime == null ? "" : xjhtime);
					xjhInfoBean.setXjhdate(xjhdate == null ? "" : xjhdate);
					xjhInfoBean.setMark(mark == null ? "" : mark);
					xjhInfoBean.setProvinceid(provinceid == null ? "0" : provinceid);
					xjhInfoBean.setCityid(cityid == null ? "0" : cityid);
					xjhInfoBean.setSchoolid(schoolid == null ? "0" : schoolid);
					xjhInfoBean.setIndustryname(industryname == null ? "" : industryname);
					xjhInfoBean.setCid(cid == null ? "0" : cid);
					xjhInfoBean.setLogourl(logourl == null ? "" : logourl);
					xjhInfoBean.setDetail(xjhdetail == null ? "" : xjhdetail);
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