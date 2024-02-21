package com.xiaozhao.datahandler;

import com.xiaozhao.bean.DalibaoBean;
import com.xiaozhao.conf.Setting;
import com.xiaozhao.util.HttpConnectionUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DalibaoHandler {
	
	
	private int total;
	private DalibaoBean dalibaoBean;
	private List<DalibaoBean> dalibaoList;
	private int resultCode;
	public DalibaoHandler(){
		this.dalibaoList = new ArrayList<DalibaoBean>();
		total = 0;
		
	}

	public int getResultCode(){
		return resultCode;
	}
	public List<DalibaoBean> getDalibaoList(){
		return this.dalibaoList;
	}
	
	public int getTotal(){
		return total;
	}

	
	public void handlerListData(Map<String, String> map){
		JSONArray jsonArray;
		map.put("module","dalibao");
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
						dalibaoBean = new DalibaoBean();
						String title = itemObject.getString("title");
						String pubdate = itemObject.getString("pubdate");
						String showdate = itemObject.getString("showdate");
						String classname = itemObject.getString("classname");
						String source = itemObject.getString("source");
						//Html2Text h2t = new Html2Text();
						//title = h2t.html2Text2(title);
						int dlbid = itemObject.getInt("dlbid");
						int classid = itemObject.getInt("classid");

						dalibaoBean.setDlbid(dlbid);
						dalibaoBean.setClassid(classid);
						//if(linktype.equals("51jobid")) title = title+"(51job)";
						dalibaoBean.setTitle(title);
						dalibaoBean.setPubdate(pubdate == null ? "" : pubdate);
						dalibaoBean.setShowdate(showdate == null ? "" : showdate);
						dalibaoBean.setClassname(classname == null ? "" : classname);
						dalibaoBean.setSource(source == null ? "" : source);
						this.dalibaoList.add(dalibaoBean);
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

	public DalibaoBean handlerDetail(int id){
		JSONArray jsonArray;
		Map<String, String> map = new HashMap<String, String>();
		map.put("dlbid",String.valueOf(id));
		map.put("module","dalibaoview");
		map.put("version", Setting.APP_VERSION);
		HttpConnectionUtil conn = new HttpConnectionUtil();
		String data = conn.requestGet(Setting.API_ROOT_URL, map);
		DalibaoBean dalibaoBean = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			String jsonStatus = jsonObject.getString("result");
			resultCode =  Integer.valueOf(jsonStatus);

			if(resultCode==200) {
				JSONObject resultbody = jsonObject.getJSONObject("resultbody");

				int dlbid = resultbody.getInt("dlbid");
				String title = resultbody.getString("title");
				String classname = resultbody.getString("classname");
				String pubdate = resultbody.getString("pubdate");
				String attachment = resultbody.getString("attachment");
				String message = resultbody.getString("message");

				dalibaoBean = new DalibaoBean();
				dalibaoBean.setDlbid(dlbid);
				dalibaoBean.setTitle(title);
				dalibaoBean.setClassname(classname==null ? "" :classname );
				dalibaoBean.setPubdate(pubdate==null ? "" :pubdate );
				dalibaoBean.setAttachment(attachment==null ? "" :attachment );
				dalibaoBean.setMessage(message==null ? "" :message );

			}

		} catch (Exception e) {
			resultCode = -2;//返回的json内容解析失败
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dalibaoBean;
	}

}