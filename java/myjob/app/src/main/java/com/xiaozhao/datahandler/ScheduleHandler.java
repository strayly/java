package com.xiaozhao.datahandler;

import com.xiaozhao.bean.ScheduleBean;
import com.xiaozhao.conf.Setting;
import com.xiaozhao.util.HttpConnectionUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleHandler {
	
	private int total;
	private ScheduleBean scheduleBean;
	private List<ScheduleBean> scheduleList;
	private int resultCode;
	public ScheduleHandler(){
		this.scheduleList = new ArrayList<ScheduleBean>();
		total = 0;
		
	}

	public int getResultCode(){
		return resultCode;
	}
	public List<ScheduleBean> getScheduleList(){
		return this.scheduleList;
	}
	
	public int getTotal(){
		return total;
	}


	public void handlerListData(Map<String, String> map){
		JSONArray jsonArray;
		map.put("module","schedule");
		map.put("version", Setting.APP_VERSION);


		HttpConnectionUtil conn = new HttpConnectionUtil();
		String data = conn.requestGet(Setting.API_ROOT_URL, map);
		if(conn.getStatusCode()==200) {
			try {
				JSONObject jsonObject = new JSONObject(data);
				String jsonStatus = jsonObject.getString("result");
				resultCode = Integer.valueOf(jsonStatus);
				if (resultCode == 200) {
					JSONObject resultbody = jsonObject.getJSONObject("resultbody");
					jsonArray = resultbody.getJSONArray("items");
					total = resultbody.getInt("totalcount");

					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject itemObject = jsonArray.getJSONObject(i);
						scheduleBean = new ScheduleBean();
						String subject = itemObject.getString("subject");
						String noticetype = itemObject.getString("noticetype");
						String infodate = itemObject.getString("infodate");
						String company = itemObject.getString("company");
						String infofrom = itemObject.getString("infofrom");

						int id = itemObject.getInt("tid");


						scheduleBean.setId(id);

						scheduleBean.setSubject(subject);
						scheduleBean.setNoticetype(noticetype == null ? "" : noticetype);
						scheduleBean.setInfodate(infodate == null ? "" : infodate);
						scheduleBean.setCompany(company == null ? "" : company);
						scheduleBean.setInfofrom(infofrom == null ? "" : infofrom);
						//scheduleBean.setFromurl(fromurl == null ? "" : fromurl);
						//scheduleBean.setMessage(message == null ? "" : message);
						this.scheduleList.add(scheduleBean);
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
	public ScheduleBean handlerDetail(int id){
		JSONArray jsonArray;
		Map<String, String> map = new HashMap<String, String>();
		map.put("tid",String.valueOf(id));
		map.put("module","scheduleview");
		map.put("version", Setting.APP_VERSION);

		HttpConnectionUtil conn = new HttpConnectionUtil();
		String data = conn.requestGet(Setting.API_ROOT_URL, map);
		ScheduleBean scheduleBean = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			String jsonStatus = jsonObject.getString("result");
			resultCode =  Integer.valueOf(jsonStatus);

			if(resultCode==200) {
				JSONObject resultbody = jsonObject.getJSONObject("resultbody");
				String message = resultbody.getString("message");

				int tid = resultbody.getInt("tid");
				String subject = resultbody.getString("subject");
				String noticetype = resultbody.getString("noticetype");
				String infodate = resultbody.getString("infodate");
				String company = resultbody.getString("company");
				String infofrom = resultbody.getString("infofrom");
				String fromurl = resultbody.getString("fromurl");
				scheduleBean = new ScheduleBean();
				scheduleBean.setId(tid);
				scheduleBean.setSubject(subject);
				scheduleBean.setNoticetype(noticetype==null ? "" :noticetype );
				scheduleBean.setInfodate(infodate==null ? "" :infodate );
				scheduleBean.setCompany(company==null ? "" :company );
				scheduleBean.setInfofrom(infofrom==null ? "" :infofrom );
				scheduleBean.setFromurl(fromurl==null ? "" :fromurl );
				scheduleBean.setMessage(message==null ? "" :message );


			}

		} catch (Exception e) {
			resultCode = -2;//返回的json内容解析失败
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return scheduleBean;
	}
}