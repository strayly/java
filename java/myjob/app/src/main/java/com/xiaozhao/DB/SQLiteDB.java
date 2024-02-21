package com.xiaozhao.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDB extends SQLiteOpenHelper
{
	private final static String DATA_BASE = "yjs.sqlite"; // 数据库名称
	private final static int DATA_VERSION = 1; // 数据库版本
	//private final static String CREATE_TABLE = "create table zhaopin (_id integer primary key autoincrement,job_url text, job text, company text, date varchar(30) , addr varchar(20))";
	
	private final static String CREATE_TABLE_XJH = "create table xjhinfo (id integer primary key autoincrement,cname varchar(150), cityname varchar(50), xjhdate varchar(50), xjhtime varchar(50) , school varchar(100), address varchar(200))";
	
	private final static String CREATE_TABLE_JOB = "create table jobinfo (id integer primary key autoincrement,linkid integer(15),linktype varchar(50), city varchar(50), title varchar(250) , jobterm varchar(100), date varchar(50), linkurl varchar(250), source varchar(80))";

	private final static String CREATE_TABLE_SEARCH = "create table search (id integer primary key autoincrement,title varchar(250) , searchtype varchar(20),  searchtime integer(15))";

    public SQLiteDB(Context context)
	{
		super(context, DATA_BASE, null, DATA_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		//db.execSQL(CREATE_TABLE);
		if(tabbleIsExist("xjhinfo",db)==false){
		    db.execSQL(CREATE_TABLE_XJH);
        }
		if(tabbleIsExist("jobinfo",db)==false){
		    db.execSQL(CREATE_TABLE_JOB);
        }
        if(tabbleIsExist("search",db)==false){
            db.execSQL(CREATE_TABLE_SEARCH);
        }
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// TODO Auto-generated method stub
		
	}
	
	/**
     * 判断某张表是否存在
     * @param tabName 表名
     * @return
     */
    public boolean tabbleIsExist(String tableName,SQLiteDatabase db){
            boolean result = false;
            if(tableName == null){
            	return false;
            }
            //SQLiteDatabase db = null;
            Cursor cursor = null;
            try {
                    //db = this.getReadableDatabase();
                    String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='"+tableName.trim()+"' ";
                    cursor = db.rawQuery(sql, null);
                    if(cursor.moveToNext()){
                            int count = cursor.getInt(0);
                            if(count>0){
                                    result = true;
                            }
                    }
                    cursor.close();
            		//db.close();
                    
            } catch (Exception e) {
                    // TODO: handle exception
            }                
            return result;
    }
}
