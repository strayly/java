package com.xiaozhao.datahandler;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class DataCache{
	



	private String cacheKey = null;
	private  String cacheDir ;
	private static final String TAG = "DataCache";
	private long cachetime = 60000;//600000; //600000
	private Context context;
	public DataCache(Context context,String cacheKey){
		this.context = context;
		this.cacheKey = cacheKey;
		makeCacheDir();
	}
	public void makeCacheDir(){
		cacheDir = getCachePath(context);
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
	public String getCacheData() {
		String data  = "";
		File cacheFile = new File(cacheDir+"/"+cacheKey+".cache");
		boolean  iscache = isCache(cacheFile,cachetime);
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
		return data;
	}
	public void setCacheData(String data){
		try{
			File cacheFile = new File(cacheDir+"/"+cacheKey+".cache");
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(cacheFile));

			oos.writeObject(data);
			oos.close();
		}
		catch(IOException e){
			//

			e.printStackTrace();
		}
	}


}