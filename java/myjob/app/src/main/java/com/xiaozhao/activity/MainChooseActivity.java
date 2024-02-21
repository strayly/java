package com.xiaozhao.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.xiaozhao.R;
import com.xiaozhao.util.MyApplication;

import java.util.ArrayList;
import java.util.HashMap;


public class MainChooseActivity extends Activity implements OnClickListener,OnItemClickListener{

	private static final String TAG = "MainChooseActivity";
	private Button backbtn;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.mainchoose);



		backbtn = (Button) findViewById(R.id.back_btn);
		backbtn.setOnClickListener(this);

	    GridView gridview = (GridView) findViewById(R.id.gridview);
	    
	    gridview.setAdapter(getMenuAdapter(new String[]{"职位列表","招聘推荐","职位搜索","宣讲会","网申截止","我的"},
				new int[]{R.drawable.nav_ft,R.drawable.nav_tj, R.drawable.nav_search, R.drawable.nav_xjh, R.drawable.nav_xjh, R.drawable.nav_userlogin}));
	    //,"大礼包"  R.drawable.nav_xjh,
	    
	    //添加消息处理
	    gridview.setOnItemClickListener(new ItemClickListener());

	    //检测登录信息
		SharedPreferences logindata = getSharedPreferences("logindata", 0);
		String sessid = logindata.getString("sessid", "");
		String accountid = logindata.getString("accountid", "");
		String usertoken = logindata.getString("usertoken", "");
		String uid = logindata.getString("uid", "");
		String username = logindata.getString("username", "");
		String mobile = logindata.getString("mobile", "");
		Long applogintime = logindata.getLong("applogintime", 0);


	}
	
	
	public SimpleAdapter getMenuAdapter(String[] menuNameArray,int[] imageResourceArray) {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < menuNameArray.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", imageResourceArray[i]);
			map.put("itemText", menuNameArray[i]);
			data.add(map);
		}
		SimpleAdapter simperAdapter = new SimpleAdapter(MainChooseActivity.this, data,R.layout.main_item,
				new String[] { "itemImage", "itemText" },new int[] { R.id.ItemImage, R.id.ItemText });
		return simperAdapter;
	}
	
	
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onClick(View v) {
		if(v == backbtn){
			finish();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	     //按下键盘上返回按钮

		return super.onKeyDown(keyCode, event);
	}
	 class  ItemClickListener implements OnItemClickListener{      
	 	public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3) {      
	 		Intent intent = new Intent();
	 		switch(arg2){
	 			
	 			case 0://全职
	 				intent.putExtra("jobtype", "fulltime");
	 				intent.setClass(MainChooseActivity.this, JobListActivity.class);
	 				startActivity(intent);
	 				break;

	 				
	 			case 1://推荐
	 				
	 				intent.setClass(MainChooseActivity.this, TjJobActivity.class);
	 				startActivity(intent);
	 				break;
	 				
	 			case 2://搜索
	 				
	 				intent.setClass(MainChooseActivity.this, JobSearchActivity.class);
	 				startActivity(intent);
	 				break;
	 				
	 			case 3://宣讲会
	 				
	 				intent.setClass(MainChooseActivity.this, XjhActivity.class);
	 				startActivity(intent);
	 				break;
				case 4://网申

					intent.setClass(MainChooseActivity.this, DeadlineActivity.class);
					startActivity(intent);
					break;


	 			case 5://我的
	 				
	 				intent.setClass(MainChooseActivity.this, MyActivity.class);
	 				startActivity(intent);
	 				break;
	 		}
	 	}   
	 }

		private void mainActivityFinish(){
			AlertDialog.Builder dialog = new Builder(this);
			dialog.setTitle(getResources().getString(R.string.dialog_title));
			dialog.setMessage(getResources().getString(R.string.dialog_exit_message));
			dialog.setPositiveButton("确定", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which){
					//finish();
					MyApplication.getInstance().exit();
				}
			});
			dialog.setNegativeButton("取消", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which){
					dialog.dismiss();
				}
			});
			dialog.show();
		}
}






