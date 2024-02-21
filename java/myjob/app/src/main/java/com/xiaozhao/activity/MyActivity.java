package com.xiaozhao.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaozhao.R;
import com.xiaozhao.util.HttpConnectionUtil;
import com.xiaozhao.util.MyApplication;

import java.util.ArrayList;
import java.util.HashMap;




public class MyActivity extends Activity implements OnClickListener,OnItemClickListener{

	private static final String TAG = "MyActivity";
	//private TextView toolbar;
	private TextView btnBottomRecommend,btnBottomJoblist,btnBottomXjh,btnBottomDeadline, btnBottomMore; //菜单栏按钮定义
	private TextView resume_btn;

	private String sessid = "";
	private String accountid = "";
	private Button homebtn,loginBtn,logoutBtn;
	//private Button backbtn;
	private final static int REQ_CODE = 1111;
	private LayoutInflater loginInflater;
	private RelativeLayout loginLayout,unloginLayout;
	private View loginView,unloginView;
	private Context mContext = MyActivity.this;
	//private MainAdapter mainAdapter;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.my);


		//backbtn = (Button) findViewById(R.id.back_btn);
		//backbtn.setOnClickListener(this);

		btnBottomRecommend = (TextView) findViewById(R.id.btn_bottom_recommend);
		btnBottomRecommend.setOnClickListener(this);

		//logoutBtn = (Button) findViewById(R.id.outbtn);
		//logoutBtn.setOnClickListener(this);

		btnBottomJoblist = (TextView) findViewById(R.id.btn_bottom_joblist);
		btnBottomJoblist.setOnClickListener(this);

		btnBottomXjh = (TextView) findViewById(R.id.btn_bottom_xjh);
		btnBottomXjh.setOnClickListener(this);

		btnBottomDeadline = (TextView) findViewById(R.id.btn_bottom_deadline);
		btnBottomDeadline.setOnClickListener(this);

		btnBottomMore = (TextView) findViewById(R.id.btn_bottom_more);
		btnBottomMore.setOnClickListener(this);
		btnBottomMore.setSelected(true);



		//backbtn = (Button) findViewById(R.id.back_btn);
		//backbtn.setOnClickListener(this);

		GridView gridview = (GridView) findViewById(R.id.gridview);

		gridview.setAdapter(getMenuAdapter(new String[]{"我的申请", "职位收藏","宣讲会收藏","浏览历史"},
				new int[]{R.mipmap.icon_mylist_resume_normal, R.mipmap.icon_mylist_duty_normal, R.mipmap.icon_mylist_campus_normal, R.mipmap.icon_mylist_history_normal}));


		//添加消息处理
		gridview.setOnItemClickListener(new MyActivity.ItemClickListener());


		SharedPreferences logindata = getSharedPreferences("logindata", 0);
		sessid = logindata.getString("sessid", "");
		accountid = logindata.getString("accountid", "");
		//sessid = null;

		//sessid ="";
		//登录信息显示模块
		loginInflater = getLayoutInflater();
		loginLayout = (RelativeLayout)findViewById(R.id.login_info);
		unloginLayout = (RelativeLayout)findViewById(R.id.unlogin_info);


		loginView = loginInflater.inflate(R.layout.block_logined, null);

		unloginView = loginInflater.inflate(R.layout.block_unlogin, null);

		loginBtn = (Button) unloginView.findViewById(R.id.login_btn);

		logoutBtn = (Button) loginView.findViewById(R.id.outbtn);

		loginLayout.addView(loginView);
		unloginLayout.addView(unloginView);

		resume_btn = (TextView) loginView.findViewById(R.id.resume_btn);
		resume_btn.setOnClickListener(this);


		if(sessid==null||sessid.equals("")){
			//未登录
			showUnLoginView();

		}
		else{
			showLoginedView();
		}
		//扫二维码


	}
	class  ItemClickListener implements OnItemClickListener{
		public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3) {
			Intent intent = new Intent();
			switch(arg2){


				case 0://我的申请
					intent.setClass(MyActivity.this, MyApplyActivity.class);
					startActivity(intent);
					break;

				case 1://职位收藏

					intent.setClass(MyActivity.this, JobCollectActivity.class);
					startActivity(intent);
					break;
				case 2://宣讲会收藏

					intent.setClass(MyActivity.this, XjhCollectActivity.class);
					startActivity(intent);
					break;

				case 3://浏览历史

					intent.setClass(MyActivity.this, MyHistoryActivity.class);
					startActivity(intent);
					break;


			}
		}
	}
	public SimpleAdapter getMenuAdapter(String[] menuNameArray,int[] imageResourceArray) {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < menuNameArray.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", imageResourceArray[i]);
			map.put("itemText", menuNameArray[i]);
			map.put("itemLine", "");
			data.add(map);
		}
		SimpleAdapter simperAdapter = new SimpleAdapter(MyActivity.this, data,R.layout.main_item,
				new String[] { "itemImage", "itemText", "itemLine" },new int[] { R.id.ItemImage, R.id.ItemText, R.id.ItemLine });
		return simperAdapter;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,long arg3){
		

	}

	public void showLoginedView(){
		//loginBtn.setText("退出");
		//loginBtn.setOnClickListener(this);
		TextView loginname = (TextView) loginView.findViewById(R.id.loginname);
		SharedPreferences logindata = getSharedPreferences("logindata", 0);
		loginname.setText(logindata.getString("username", ""));
		//loginView.setVisibility(View.VISIBLE);
		//unloginView.setVisibility(View.GONE);
		unloginLayout.setVisibility(View.GONE);
		loginLayout.setVisibility(View.VISIBLE);

	}
	public void showUnLoginView(){


		//unloginView.setVisibility(View.VISIBLE);
		//loginView.setVisibility(View.GONE);

		unloginLayout.setVisibility(View.VISIBLE);
		loginLayout.setVisibility(View.GONE);

	}
	public void loginClick(View v){

		Intent intent = new Intent();
		intent.setClass(mContext, UserLoginActivity.class);
		//startActivity(intent);
		startActivityForResult(intent,REQ_CODE);
	}
	public void logoutClick(View v){
		logout();
		Intent intent = new Intent();
		intent.setClass(mContext, UserLoginActivity.class);
		startActivity(intent);
	}
	public void rsmClick(View v){
		Intent intent = new Intent();
		intent.setClass(mContext, MyResumeActivity.class);
		startActivity(intent);
	}
	@Override
	public void onClick(View v){
		//keyword = search.getText()==null ? "" : search.getText().toString();
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		if(v == homebtn){
			Intent intent = new Intent();
			intent.setClass(this, MyActivity.class);
			startActivity(intent);
		} else if(v == loginBtn){




		}

		else if(v == logoutBtn){

			logout();
			Intent intent = new Intent();
			intent.setClass(this, UserLoginActivity.class);
			startActivity(intent);
		} else if (v == btnBottomRecommend) {
			Intent intent = new Intent();
			intent.setClass(this, TjJobActivity.class);
			startActivity(intent);
		} else if (v == btnBottomJoblist) {
			Intent intent = new Intent();
			intent.setClass(this, JobListActivity.class);
			startActivity(intent);
		} else if (v == btnBottomXjh) {
			Intent intent = new Intent();
			intent.setClass(this, XjhActivity.class);
			startActivity(intent);
		} else if (v == btnBottomDeadline) {
			Intent intent = new Intent();
			intent.setClass(this, DeadlineActivity.class);
			startActivity(intent);
		} else if (v == resume_btn) {
			Intent intent = new Intent();
			intent.setClass(this, MyResumeActivity.class);
			startActivity(intent);
		}
		
	}


	//登录跳转回来后 刷新状态和数据
	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data){

		switch(requestCode){
			case REQ_CODE:
				if(resultCode==RESULT_OK){
					//dataHandler(curpage);
					String loginResult = (String) data.getSerializableExtra("LoginResult");
					if(loginResult.equals("SUCCESS")) {
						SharedPreferences logindata = getSharedPreferences("logindata", 0);
						sessid = logindata.getString("sessid", "");
						accountid = logindata.getString("accountid", "");
						showLoginedView();
					}
					else if(loginResult.equals("FAIL")){
						Toast.makeText(mContext, "登录失败！", Toast.LENGTH_SHORT).show();
						showUnLoginView();
					}
					else{
						finish();
					}
				}
				break;
			default:
		}
	}
	//退出登录
	private void logout(){
		SharedPreferences logindata = getSharedPreferences("logindata", 0);
		SharedPreferences.Editor edit = logindata.edit();
		edit.clear();
		edit.commit();
		sessid = null;
		accountid = null;
		//
		HttpConnectionUtil conn = new HttpConnectionUtil();
		conn.requestGet("https://H5", null);
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
	

	public void onResume() {
		//Log.w(Conf.TAG, "Activity2.OnResume()");
		super.onResume();
		//StatService.onResume(this);
	}

	public void onPause() {
		//Log.w(Conf.TAG, "Activity2.onPause()");
		super.onPause();
		//StatService.onPause(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		// return super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.jobmenu, menu);
		return true;
	}

}






