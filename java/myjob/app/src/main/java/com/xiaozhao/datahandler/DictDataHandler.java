package com.xiaozhao.datahandler;


import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.xiaozhao.conf.Setting;
import com.xiaozhao.util.HttpConnectionUtil;
import com.xiaozhao.util.MyApplication;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class DictDataHandler {

	private  String cacheDir ;
	private static final String TAG = "DictDataHandler";
	private long cachetime = 6;//600000; //600000
	private String dataString ;

	private Context context;
	public DictDataHandler(Context context){
		this.context = context;
		makeCacheDir();
	}
	public void setDataString(String dataString){
		this.dataString = dataString;
	}
	public String getDataString(){
		return this.dataString;
	}

	public void makeCacheDir(){

		cacheDir = context.getCacheDir().getPath();
		//Log.e(TAG, "APP file:"+cacheDir);
		File fcache = new File(cacheDir);
		if(!fcache.exists()){
			fcache.mkdir();
		}
	}

	public String getCachePath( Context context ){
		String cachePath ;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
			//外部存储可用
			cachePath = context.getExternalCacheDir().getPath() ;
		}else {
			//外部存储不可用
			cachePath = context.getCacheDir().getPath() ;
		}
		return cachePath ;
	}

	private boolean isCache(File f,long cachetime){
		long timestamp = System.currentTimeMillis();
		if (!f.exists()) {
			return false;
		}
		else if((timestamp - f.lastModified())>cachetime){
			return false;
		}
		return true;
	}
	public boolean isAccessUpdate(){
		return true;
	}
	public String getCacheData(String cacheKey,boolean cacheOnly) {
		String data  = null;
		final File cacheFile = new File(cacheDir+"/"+cacheKey+".cache");
		boolean iscache;
		if(cacheOnly==true && cacheFile.exists())  iscache = true;
		else iscache = isCache(cacheFile,cachetime);
		if(iscache){
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(new FileInputStream(cacheFile));
				data = (String)ois.readObject();
				ois.close();
			}
			catch (Exception e) {
				data  = null;
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//System.out.println("eeeee:"+data);
		return data;
	}
	public List<HashMap<String, String>> getXjhLocation(boolean cacheOnly){

		JSONArray jsonArray;
		String data = getCacheData("xjh",cacheOnly);
		if(data==null && cacheOnly==false) {
			setXjhHttpCache();
		}
		List<HashMap<String, String>> dataList = getXjhDictList(data);
		return dataList;
	}
	public void setXjhHttpCache(){
		try{
			HttpConnectionUtil conn = new HttpConnectionUtil();
			Map<String, String> paramsMap = new HashMap<String, String>();
			paramsMap.put("module","xjhlocation");
			paramsMap.put("version", Setting.APP_VERSION);
			String data = conn.requestGet(Setting.API_ROOT_URL+"datadict/", paramsMap);
			if(data!=null && data.indexOf("resultbody")>=0) dataString = data;
			File cacheFile = new File(cacheDir+"/xjh.cache");
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(cacheFile));
			oos.writeObject(dataString);
			oos.close();
		}
		catch(IOException e){
			//
			e.printStackTrace();
		}
	}


	public List<HashMap<String, String>> getXjhDictList(String data){
		List<HashMap<String, String>> dataList = new ArrayList< HashMap<String, String>>();
		if(data!=null) {
			JSONArray jsonArray;
			try {
				JSONObject jsonObject = new JSONObject(data);
				String jsonStatus = jsonObject.getString("result");
				if (jsonStatus.equals("200")) {
					JSONObject resultbody = jsonObject.getJSONObject("resultbody");
					jsonArray = resultbody.getJSONArray("items");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject itemObject = jsonArray.getJSONObject(i);
						HashMap<String, String> itemMap = new HashMap<String, String>();
						String name = itemObject.getString("value");
						String code = itemObject.getString("code");
						itemMap.put("name", name);
						itemMap.put("code", code);
						dataList.add(itemMap);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return dataList;
	}


	public List<HashMap<String, String>> getJobLocation(boolean cacheOnly){

		JSONArray jsonArray;

		String data = getCacheData("job",cacheOnly);
		if(data==null && cacheOnly==false) {
			setJobHttpCache();
		}
		List<HashMap<String, String>> dataList = getJobDictList(data);
		return dataList;
	}
	public void setJobHttpCache(){
		try{
			HttpConnectionUtil conn = new HttpConnectionUtil();
			Map<String, String> paramsMap = new HashMap<String, String>();
			paramsMap.put("module","joblocation");
			paramsMap.put("version", Setting.APP_VERSION);
			String data = conn.requestGet(Setting.API_ROOT_URL+"datadict/", paramsMap);
			if(data!=null && data.indexOf("resultbody")>=0) dataString = data;
			File cacheFile = new File(cacheDir+"/job.cache");
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(cacheFile));
			oos.writeObject(dataString);
			oos.close();
		}
		catch(IOException e){
			//
			e.printStackTrace();
		}
	}

	public List<HashMap<String, String>> getJobDictList(String data){
		List<HashMap<String, String>> dataList = new ArrayList< HashMap<String, String>>();
		if(data!=null) {
			JSONArray jsonArray;
			try {
				JSONObject jsonObject = new JSONObject(data);
				String jsonStatus = jsonObject.getString("result");
				if (jsonStatus.equals("200")) {
					JSONObject resultbody = jsonObject.getJSONObject("resultbody");
					jsonArray = resultbody.getJSONArray("items");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject itemObject = jsonArray.getJSONObject(i);
						HashMap<String, String> itemMap = new HashMap<String, String>();
						String name = itemObject.getString("value");
						String code = itemObject.getString("code");
						itemMap.put("name", name);
						itemMap.put("code", code);
						String ishot = itemObject.getString("ishot");
						itemMap.put("ishot", ishot);
						dataList.add(itemMap);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return dataList;
	}
}