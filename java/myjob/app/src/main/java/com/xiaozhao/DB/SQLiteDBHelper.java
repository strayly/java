package com.xiaozhao.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xiaozhao.bean.JobItemInfoBean;
import com.xiaozhao.bean.XjhInfoBean;

import java.util.ArrayList;
import java.util.List;

public class SQLiteDBHelper
{
	private SQLiteDB data;

	public SQLiteDBHelper(Context context)
	{
		data = new SQLiteDB(context);
		isExistTable();
	}
	//没有表 就创建
	public void isExistTable() {
		SQLiteDatabase db = data.getWritableDatabase();
		data.onCreate(db);
		db.close();
	}
	public void saveXjh(XjhInfoBean xjhinfo){

		SQLiteDatabase db = data.getWritableDatabase(); // 得到用于数据库的实例		
		String sql = "replace into xjhinfo (id, cname, cityname, xjhdate, xjhtime,school,address) values(?,?, ?, ?, ?, ?,?)";

		db.execSQL(sql,
				new Object[] {xjhinfo.getId(), xjhinfo.getCompany(), xjhinfo.getCityname(),xjhinfo.getXjhdate(), xjhinfo.getXjhtime(), xjhinfo.getSchool(), xjhinfo.getAddress()});

		db.close();

	}
	public int getXjhCount(){
		SQLiteDatabase db = data.getReadableDatabase(); // 得到用于数据库的实例
		int count = 0 ;
		String sql = "select count(*) as c from xjhinfo";
		Cursor cursor = db.rawQuery(sql,null);
		while(cursor.moveToNext()){
			count = cursor.getInt(cursor.getColumnIndex("c"));
		}
		cursor.close();
		db.close();
		return count;
	}
	
	public List<XjhInfoBean> getListXjh(int offset, int maxResult){
		SQLiteDatabase db = data.getReadableDatabase(); // 得到用于数据库的实例
		List<XjhInfoBean> lists = new ArrayList<XjhInfoBean>();
		String sql = "select * from xjhinfo order by id desc limit ? , ?";
		Cursor cursor = db.rawQuery(sql, new String[] { String.valueOf(offset),String.valueOf(maxResult)});
		while(cursor.moveToNext()){
			XjhInfoBean xjhinfo = new XjhInfoBean();
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			xjhinfo.setId(id);
			String cname = cursor.getString(cursor.getColumnIndex("cname"));
			xjhinfo.setCompany(cname);
			String address = cursor.getString(cursor.getColumnIndex("address"));
			xjhinfo.setAddress(address);
			String cityname = cursor.getString(cursor.getColumnIndex("cityname"));
			xjhinfo.setCityname(cityname);
			String xjhdate = cursor.getString(cursor.getColumnIndex("xjhdate"));
			xjhinfo.setXjhdate(xjhdate);
			String xjhtime = cursor.getString(cursor.getColumnIndex("xjhtime"));
			xjhinfo.setXjhtime(xjhtime);
			String school = cursor.getString(cursor.getColumnIndex("school"));
			xjhinfo.setSchool(school);
			lists.add(xjhinfo);
		}
		cursor.close();
		db.close();
		return lists;
	}
	
	
	public XjhInfoBean geXjhRow(int id){
		SQLiteDatabase db = data.getReadableDatabase(); // 得到用于数据库的实例
		String sql = "select * from xjhinfo where id=?";
		Cursor cursor = db.rawQuery(sql,new String[]{String.valueOf(id)});
		XjhInfoBean xjhinfo = new XjhInfoBean();
		while(cursor.moveToNext()){
			xjhinfo.setId(id);
			String cname = cursor.getString(cursor.getColumnIndex("cname"));
			xjhinfo.setCompany(cname);
			String address = cursor.getString(cursor.getColumnIndex("address"));
			xjhinfo.setAddress(address);
			String cityname = cursor.getString(cursor.getColumnIndex("cityname"));
			xjhinfo.setCityname(cityname);
			String xjhdate = cursor.getString(cursor.getColumnIndex("xjhdate"));
			xjhinfo.setXjhdate(xjhdate);
			String xjhtime = cursor.getString(cursor.getColumnIndex("xjhtime"));
			xjhinfo.setXjhtime(xjhtime);
			String school = cursor.getString(cursor.getColumnIndex("school"));
			xjhinfo.setSchool(school);
		}
		cursor.close();
		db.close();
		return xjhinfo;
	}
	
	public void deleteXjh(int id){
		SQLiteDatabase db = data.getWritableDatabase(); // 得到用于数据的数据库实例
		db.execSQL("delete from xjhinfo where id = ?",new Object[] { String.valueOf(id) });
		db.close();
	}
	
	/**
	 * 删除所有数据
	 */
	public void deleteAllXjhData(){
		SQLiteDatabase db = data.getWritableDatabase(); // 得到用于数据的数据库实例
		db.execSQL("delete from xjhinfo");
		db.close();
	}
	
	
	public void saveJob(JobItemInfoBean jinfo) throws Exception{
		SQLiteDatabase db = data.getWritableDatabase();
		int id = 0 ;
		String sql = "";
		String ltype = jinfo.getLinktype();int lid = jinfo.getLinkid();
		if(!ltype.equals("") && lid>0) {
			sql = "select id from jobinfo where linkid=? AND linktype=?";
			Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(lid), ltype});
			while (cursor.moveToNext()) {
				id = cursor.getInt(cursor.getColumnIndex("id"));
			}
			cursor.close();
		}
		if(id>0) {
			db.execSQL("delete from jobinfo where id = ?",new Object[] { String.valueOf(id) });

		}
		sql = "insert into jobinfo (linkid,linktype, city,title, jobterm, date,linkurl) values(?,?, ?, ?, ?, ?,?)";
		db.execSQL(sql,new Object[] {jinfo.getLinkid(), jinfo.getLinktype(), jinfo.getCity(),jinfo.getTitle(), jinfo.getJobterm(), jinfo.getDate(), jinfo.getLinkurl()});
		int count = 0 ;
		sql = "select count(*) as c from jobinfo";
		Cursor cursor = db.rawQuery(sql,null);
		while(cursor.moveToNext()){
			count = cursor.getInt(cursor.getColumnIndex("c"));
		}
		if(count>200){
			db.execSQL("DELETE FROM  jobinfo  where  id in (SELECT id FROM jobinfo order by id asc limit 2)");
		}
		db.close();
	}
	
	public List<JobItemInfoBean> getListJob(int offset, int maxResult){
		SQLiteDatabase db = data.getReadableDatabase(); // 得到用于数据库的实例
		List<JobItemInfoBean> lists = new ArrayList<JobItemInfoBean>();
		String sql = "select * from jobinfo order by id desc limit ? , ?";
		Cursor cursor = db.rawQuery(sql, new String[] { String.valueOf(offset),String.valueOf(maxResult)});
		while(cursor.moveToNext()){
			JobItemInfoBean jinfo = new JobItemInfoBean();
			int indexId = cursor.getInt(cursor.getColumnIndex("id"));
			jinfo.setIndexId(Integer.valueOf(indexId));
			int linkid = cursor.getInt(cursor.getColumnIndex("linkid"));
			jinfo.setLinkid(linkid);
			
			String city = cursor.getString(cursor.getColumnIndex("city"));
			jinfo.setCity(city);

			String title = cursor.getString(cursor.getColumnIndex("title"));
			jinfo.setTitle(title);
			
			String jobterm = cursor.getString(cursor.getColumnIndex("jobterm"));
			jinfo.setJobterm(jobterm);
			
			String date = cursor.getString(cursor.getColumnIndex("date"));
			jinfo.setDate(date);
			
			String linkurl = cursor.getString(cursor.getColumnIndex("linkurl"));
			jinfo.setLinkurl(linkurl);

			String linktype = cursor.getString(cursor.getColumnIndex("linktype"));
			jinfo.setLinktype(linktype);

			lists.add(jinfo);
		}
		cursor.close();
		db.close();
		return lists;
	}
	

	
	public void deleteJob(int lid,String ltype){
		SQLiteDatabase db = data.getWritableDatabase(); // 得到用于数据的数据库实例
		if(!ltype.equals("") && lid>0) {
			db.execSQL("delete from jobinfo where linkid = ? and linktype=?",new Object[] { String.valueOf(lid),ltype });
		}
		db.close();
	}
	
	/**
	 * 删除所有数据
	 */
	public void deleteAllJobData(){
		SQLiteDatabase db = data.getWritableDatabase(); // 得到用于数据的数据库实例
		db.execSQL("delete from jobinfo");
		db.close();
	}


	public void saveSearchWord(String word,String type){
		int timestamp = Integer.valueOf(String.valueOf(System.currentTimeMillis()/1000));

		SQLiteDatabase db = data.getWritableDatabase(); // 得到用于数据库的实例
		db.execSQL("delete from search where title = ? AND searchtype=?",new Object[] { word,type });
		String sql = "insert into search (title, searchtype,searchtime) values(?,?,?)";
		db.execSQL(sql,new Object[] {word, type, timestamp});
		db.close();

	}
	public int getSearchWordCount(){
		SQLiteDatabase db = data.getReadableDatabase(); // 得到用于数据库的实例
		int count = 0 ;
		String sql = "select count(*) as c from search";
		Cursor cursor = db.rawQuery(sql,null);
		while(cursor.moveToNext()){
			count = cursor.getInt(cursor.getColumnIndex("c"));
		}
		cursor.close();
		db.close();
		return count;
	}

	public List<String> getListSearchWord(String type,int offset, int maxResult){
		SQLiteDatabase db = data.getReadableDatabase(); // 得到用于数据库的实例
		List<String> lists = new ArrayList<String>();
		String sql = "select * from search where searchtype=? order by id desc limit ? , ?";
		Cursor cursor = db.rawQuery(sql, new String[] { type,String.valueOf(offset),String.valueOf(maxResult)});
		while(cursor.moveToNext()){
			System.out.println(cursor.getColumnIndex("title"));
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String title = cursor.getString(cursor.getColumnIndex("title"));
			lists.add(title);
		}
		cursor.close();
		db.close();
		return lists;
	}

	public void deleteSearchWord(String word,String type){
		SQLiteDatabase db = data.getWritableDatabase(); // 得到用于数据的数据库实例
		db.execSQL("delete from search where title = ? AND searchtype=?",new Object[] { word,type });
		db.close();
	}

	/**
	 * 删除所有数据
	 */
	public void deleteAllSearchWord(String type){
		SQLiteDatabase db = data.getWritableDatabase(); // 得到用于数据的数据库实例
		db.execSQL("delete from search where searchtype=?",new Object[] {type });
		db.close();
	}


}
