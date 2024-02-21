package com.xiaozhao.datahandler;

import com.xiaozhao.conf.Setting;
import com.xiaozhao.util.HttpConnectionUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;



public class ZzJobViewCache {
	

	private static final String cacheDir = Setting.APP_SD_DIR+"/cache";

	private long cachetime = 600000; //600000
	private String data = null; 
	
	public ZzJobViewCache(){
		File froot = new File(Setting.APP_SD_DIR);
		if(!froot.exists()){
			froot.mkdir();
		}
		File fcache = new File(cacheDir);
		if(!fcache.exists()){
			fcache.mkdir();
		}
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

	public String getZzJobIntro(int id){		
		Map<String, String> map = new HashMap<String, String>();
		HttpConnectionUtil conn = new HttpConnectionUtil();
		map.put("module","zzjobview");
		map.put("jobid",String.valueOf(id));
		map.put("rtime",Setting.getRtime());
		map.put("rsign",Setting.getRsign(String.valueOf(id)));

		data = conn.requestGetJson(Setting.API_ROOT_URL, map);

		return this.data;
	}

}