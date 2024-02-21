package com.xiaozhao.datahandler;

import com.xiaozhao.bean.DeadlineBean;
import com.xiaozhao.conf.Setting;
import com.xiaozhao.util.HttpConnectionUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeadlineHandler  {
	private int total;
	private DeadlineBean deadlineBean;
	private List<DeadlineBean> deadlineList;
	private int resultCode;

	public DeadlineHandler() {
		this.deadlineList = new ArrayList<DeadlineBean>();
		total = 0;

	}


	public int getResultCode() {
		return resultCode;
	}

	public List<DeadlineBean> getDeadlineList() {
		return this.deadlineList;
	}

	public int getTotal() {
		return total;
	}


	public void handlerListData(Map<String, String> map) {
		JSONArray jsonArray;
		map.put("module", "deadline");
		map.put("version", Setting.APP_VERSION);


		HttpConnectionUtil conn = new HttpConnectionUtil();
		String data = conn.requestGet(Setting.API_ROOT_URL, map);
		if (conn.getStatusCode() == 200) {
			try {
				JSONObject jsonObject = new JSONObject(data);
				String jsonStatus = jsonObject.getString("result");
				resultCode = Integer.valueOf(jsonStatus);
				if (resultCode == 200) {
					JSONObject resultbody = jsonObject.getJSONObject("resultbody");
					jsonArray = resultbody.getJSONArray("items");
					total = resultbody.getInt("totalcount");
					//System.out.println(jsonArray);
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject itemObject = jsonArray.getJSONObject(i);
						deadlineBean = new DeadlineBean();
						String title = itemObject.getString("title");
						String linktype = itemObject.getString("linktype");
						String linkurl = itemObject.getString("linkurl");
						String jobplace = itemObject.getString("jobplace");
						String deaddate = itemObject.getString("deaddate");
						String leftday = itemObject.getString("leftday");

						int linkid = itemObject.getInt("linkid");


						deadlineBean.setLinkid(linkid);


						deadlineBean.setTitle(title);
						deadlineBean.setLinktype(linktype == null ? "" : linktype);
						deadlineBean.setJobplace(jobplace == null ? "" : jobplace);
						deadlineBean.setDeaddate(deaddate == null ? "" : deaddate);
						deadlineBean.setLeftday(leftday == null ? "" : leftday);
						deadlineBean.setLinkurl(linkurl == null ? "" : linkurl);
						this.deadlineList.add(deadlineBean);

					}
				}
			} catch (Exception e) {
				resultCode = -2;//返回的json内容解析失败
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			resultCode = -1;//url 链接 请求失败返回-1
		}

	}
}