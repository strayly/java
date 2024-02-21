package com.xiaozhao.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaozhao.R;
import com.xiaozhao.bean.XjhInfoBean;
import com.xiaozhao.datahandler.XjhCollectHandler;
import com.xiaozhao.datahandler.XjhViewHandler;
import com.xiaozhao.util.ErrorViewManager;
import com.xiaozhao.util.Html2Text;
import com.xiaozhao.util.MyApplication;
import com.xiaozhao.util.MyImageFix;
import com.xiaozhao.util.MyUrlClick;
import com.zzhoujay.richtext.RichText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class XjhViewActivity extends Activity implements OnClickListener{

	private static final String TAG = "XjhViewActivity";

	private InputMethodManager imm;	
	
	
	private ProgressDialog Dialog;
	private TextView fieldXjhdetail;
	private TextView fieldCompany,fieldMark,fieldXjhdate,fieldXjhtime,fieldAddress,fieldSchool,fieldCityname;
	protected String xjhdetail;

	private TextView fieldLogourl;

	private XjhInfoBean xjhInfoBean;
	protected String company;
	private Button return_back;
	private ImageButton toolbar_back, collectjobbtn;
	private int xjhid;
	private int collectid = 0;
	private String sessid;
	private String accountid;

	private Map<String, String> subCollectMap = new HashMap<String, String>();
	private int subCollectResultCode ;
	private final static int REQ_CODE = 1113;
	private MyImageFix myImageFix ;
	private MyUrlClick myUrlClick ;
	private Context mContext = XjhViewActivity.this;
	private int resultCode = 0;
	private ErrorViewManager errorView;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.xjhshow);

		LinearLayout mainLayout = findViewById(R.id.mainLayout);
		errorView = ErrorViewManager.getInstance().init(mainLayout);
		errorView.setOnRetryChildClickListener(onRetryClickListener);

		xjhid = (Integer) getIntent().getSerializableExtra("xjhid");


		fieldCompany = (TextView) findViewById(R.id.fieldCompany);
		fieldMark = (TextView) findViewById(R.id.fieldMark);
		fieldXjhdate = (TextView) findViewById(R.id.fieldXjhdate);
		fieldXjhtime = (TextView) findViewById(R.id.fieldXjhtime);
		fieldAddress = (TextView) findViewById(R.id.fieldAddress);
		fieldSchool = (TextView) findViewById(R.id.fieldSchool);
		fieldCityname = (TextView) findViewById(R.id.fieldCityname);
		
		collectjobbtn = (ImageButton) findViewById(R.id.collectjobbtn);
		collectjobbtn.setOnClickListener(this);
		
		toolbar_back = (ImageButton) findViewById(R.id.toolbar_back);
		toolbar_back.setOnClickListener(this);

		fieldXjhdetail = (TextView) findViewById(R.id.fieldXjhdetail);
		myImageFix = new MyImageFix(getScreenWidth()) ;
		myUrlClick = new MyUrlClick(mContext) ;


		htmlHandler();
		checkCollected();



	}
	private ErrorViewManager.OnLoadingAndRetryListener onRetryClickListener = new ErrorViewManager.OnLoadingAndRetryListener(){
		public void onClick() {
			htmlHandler();
			checkCollected();
		}
	};
	private void htmlHandler(){
		errorView.showLoading();
		final Handler mHandler = new Handler()
		{

			// 处理来自线程的消息,并将线程中的数据设置入
			@Override
			public void handleMessage(Message msg)
			{
				if (msg.what == 1)
				{
					if(resultCode==200) {
						errorView.showContent();

						RichText.from(xjhdetail).autoFix(false).fix(myImageFix).urlClick(myUrlClick).error(R.drawable.nophoto).into(fieldXjhdetail);

						//基本信息
						fieldCompany.setText(xjhInfoBean.getCompany());
						fieldMark.setText(xjhInfoBean.getMark());
						fieldXjhdate.setText(xjhInfoBean.getXjhdate());
						fieldXjhtime.setText(xjhInfoBean.getXjhtime());
						fieldAddress.setText(xjhInfoBean.getAddress());
						fieldSchool.setText(xjhInfoBean.getSchool());
						fieldCityname.setText(xjhInfoBean.getCityname());
						//companyTV.setText(xjhInfo.getDetail());
					}
					else{
						errorView.showRetry();
					}
					
				}
			}
		};

		new Thread()
		{

			public void run()
			{
				// 子线程的循环标志位
				Looper.prepare();
				try{
					Thread.sleep(5);

				}
				catch (InterruptedException e){
					e.printStackTrace();
				}
				try{
                    XjhViewHandler xjhViewHandler = new XjhViewHandler();
                    xjhViewHandler.handlerDetail(xjhid);
					resultCode = xjhViewHandler.getResultCode();
					if(resultCode==200) {
						xjhInfoBean = xjhViewHandler.getXjhInfoBean();
						xjhdetail = xjhInfoBean.getDetail();
						Html2Text html2text = new Html2Text();
						xjhdetail = html2text.html2Text(xjhdetail);
						xjhdetail = xjhdetail.replaceAll("(<br />\\s*)+", "<br />");
						//System.out.println("bbb:"+jobDetail);
					}
				}
				catch (Exception e){
					Log.e(TAG, e.toString());
				}
				// 给handle发送的消息
				Message m = new Message();
				m.what = 1;
				mHandler.sendMessage(m);
				Looper.loop();
			};

		}.start();
	}
	public void checkCollected() {//检查该信息是否已收藏
		//Map<String, String> map = new HashMap<String, String>();
		//int handlerResult = 0;
		final Handler mHandler = new Handler()
		{

			// 处理来自线程的消息,并将线程中的数据设置入
			@Override
			public void handleMessage(Message msg)
			{
				if (msg.what == 1){
					super.handleMessage(msg);
					Bundle data = msg.getData();
					collectid = data.getInt("checkResult");
					if(collectid>0){
						collectjobbtn.setImageResource(R.mipmap.icon_genaral_fav_selected);
						//collectjobbtn.setText("已收藏");
						//collectjobbtn.setClickable(false);
					}
				}
			}
		};
		new Thread(new Runnable(){
			@Override
			public void run() {
				XjhCollectHandler xjhCollectHandler = new XjhCollectHandler();
				int checkResult = 0;
				Map<String, String> map = new HashMap<String, String>();
				SharedPreferences logindata = getSharedPreferences("logindata", 0);
				sessid = logindata.getString("sessid", "");
				accountid = logindata.getString("accountid", "");
				if(sessid!=null && accountid!=null){//未登录
					map.put("sessid", sessid);
					map.put("userid", accountid);
					map.put("type", "xjh");
					map.put("xjhid", String.valueOf(xjhid));
					checkResult  = xjhCollectHandler.isCollected(map);
				}
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putInt("checkResult",checkResult);
				msg.setData(data);
				msg.what = 1;
				mHandler.sendMessage(msg);
			}
		}).start();
		//return handlerResult;
	}
	//取消收藏
	public void unsubCollectData() {
		//Map<String, String> map = new HashMap<String, String>();
		//int handlerResult = 0;
		final Handler mHandler = new Handler()
		{

			// 处理来自线程的消息,并将线程中的数据设置入
			@Override
			public void handleMessage(Message msg)
			{
				if (msg.what == 1){
					super.handleMessage(msg);
					Bundle data = msg.getData();
					String resultCode = data.getString("resultCode");
					subCollectResultCode = Integer.parseInt(resultCode);
					//Log.i(TAG,"请求结果:" + resultCode);
					switch(subCollectResultCode){
						case -1 :
							Toast.makeText(XjhViewActivity.this, "读取远程服务器数据失败！", Toast.LENGTH_LONG).show();
							break; //可选
						case -2 :
							Toast.makeText(XjhViewActivity.this, "远程数据错误，请稍后尝试！\r\n", Toast.LENGTH_LONG).show();
							break; //可选
						case 401 :
							Toast.makeText(XjhViewActivity.this,"您的登录信息已失效，请重新登录！",Toast.LENGTH_SHORT).show();
							Intent intent = new Intent();
							intent.setClass(XjhViewActivity.this, UserLoginActivity.class);
							startActivityForResult(intent,REQ_CODE);
							//startActivity(intent);
							//return;
							break; //可选
						case 200 :
							Toast.makeText(XjhViewActivity.this, "收藏已取消", Toast.LENGTH_LONG).show();
							collectjobbtn.setImageResource(R.mipmap.icon_genaral_fav_normal);
							//collectjobbtn.setText("收藏");
							collectid = 0;
							break; //可选
						//你可以有任意数量的case语句
						default : //可选
							Toast.makeText(XjhViewActivity.this, subCollectResultCode+"服务请求失败！", Toast.LENGTH_LONG).show();
					}
				}
			}
		};
		new Thread(new Runnable(){
			@Override
			public void run() {
				XjhCollectHandler xjhCollectHandler = new XjhCollectHandler();
				Map<String, String> map = new HashMap<String, String>();
				map.put("uid", accountid);
				map.put("id", String.valueOf(collectid));
				map.put("sessid", sessid);
				map.put("userid", accountid);
				xjhCollectHandler.unsubCollect(map);
				int subResult = xjhCollectHandler.getResultCode();

				Message msg = new Message();
				Bundle data = new Bundle();
				data.putString("resultCode",String.valueOf(subResult));
				msg.setData(data);
				msg.what = 1;
				mHandler.sendMessage(msg);
			}
		}).start();
		//return handlerResult;
	}
	//收藏
	public void subCollectData() {
		//Map<String, String> map = new HashMap<String, String>();
		//int handlerResult = 0;
		final Handler mHandler = new Handler()
		{

			// 处理来自线程的消息,并将线程中的数据设置入
			@Override
			public void handleMessage(Message msg)
			{
				if (msg.what == 1){
					super.handleMessage(msg);
					Bundle data = msg.getData();
					String resultCode = data.getString("resultCode");
					subCollectResultCode = Integer.parseInt(resultCode);
					//Log.i(TAG,"请求结果:" + resultCode);
					switch(subCollectResultCode){
						case -1 :
							Toast.makeText(XjhViewActivity.this, "读取远程服务器数据失败！", Toast.LENGTH_LONG).show();
							break; //可选
						case -2 :
							Toast.makeText(XjhViewActivity.this, "远程数据错误，请稍后尝试\r\n或者通过应届生网站访问！", Toast.LENGTH_LONG).show();
							break; //可选
						case 401 :
							Toast.makeText(XjhViewActivity.this,"您的登录信息已失效，请重新登录！",Toast.LENGTH_SHORT).show();
							Intent intent = new Intent();
							intent.setClass(XjhViewActivity.this, UserLoginActivity.class);
							startActivityForResult(intent,REQ_CODE);
							//startActivity(intent);
							//return;
							break; //可选
						case 200 :
							Toast.makeText(XjhViewActivity.this, "信息已收藏", Toast.LENGTH_LONG).show();
							collectjobbtn.setImageResource(R.mipmap.icon_genaral_fav_selected);
							//collectjobbtn.setText("已收藏");
							//iscollect = 0;
							break; //可选
						//你可以有任意数量的case语句
						default : //可选
							Toast.makeText(XjhViewActivity.this, subCollectResultCode+"服务请求失败！", Toast.LENGTH_LONG).show();
					}
				}
			}
		};
		new Thread(new Runnable(){
			@Override
			public void run() {
				XjhCollectHandler xjhCollectHandler = new XjhCollectHandler();
				String subinfo = "";
				try {
					JSONObject xjhJson = new JSONObject();
					xjhJson.put("xjhid",String.valueOf(xjhInfoBean.getId()));
					xjhJson.put("logourl", xjhInfoBean.getLogourl());
					xjhJson.put("provinceid", xjhInfoBean.getProvinceid());
					xjhJson.put("schoolid", xjhInfoBean.getSchoolid());
					xjhJson.put("xjhdate", xjhInfoBean.getXjhdate());
					xjhJson.put("xjhtime", xjhInfoBean.getXjhtime());
					xjhJson.put("cname", xjhInfoBean.getCompany());
					xjhJson.put("address", xjhInfoBean.getAddress());
					xjhJson.put("cityid", xjhInfoBean.getCityid());
					xjhJson.put("school", xjhInfoBean.getSchool());
					xjhJson.put("industryname", xjhInfoBean.getIndustryname());
					xjhJson.put("cid", xjhInfoBean.getCid());

					JSONArray xjhArray = new JSONArray();
					xjhArray.put(xjhJson);
					subinfo = xjhArray.toString();

				} catch (Exception e) {
					e.printStackTrace();
				}
				//System.out.println(subinfo);

				subCollectMap.put("subinfo", subinfo);
				collectid = xjhCollectHandler.subCollect(subCollectMap);
				int subResult = xjhCollectHandler.getResultCode();

				Message msg = new Message();
				Bundle data = new Bundle();
				data.putString("resultCode",String.valueOf(subResult));
				msg.setData(data);
				msg.what = 1;
				mHandler.sendMessage(msg);
			}
		}).start();
		//return handlerResult;
	}

	@Override
	public void onClick(View v){
		
		if (v == toolbar_back||v == return_back){
			this.finish();
		}
		else if(v == collectjobbtn){ //宣讲会收藏
			SharedPreferences logindata = getSharedPreferences("logindata", 0);
			sessid = logindata.getString("sessid", "");
			accountid = logindata.getString("accountid", "");

			if(sessid==null||sessid.equals("")){//未登录
				Intent intent = new Intent();
				intent.setClass(this, UserLoginActivity.class);
				//startActivity(intent);
				startActivityForResult(intent,REQ_CODE);
			}

			subCollectMap.put("sessid", sessid);
			subCollectMap.put("userid", accountid);
			if(collectid>0) {//已收藏，取消收藏
				unsubCollectData();
			}
			else{ //提交 收藏
				subCollectData();
			}

		}
	
	}
	//登录跳转回来后 刷新状态和数据
	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data){

		switch(requestCode){
			case REQ_CODE:
				if(resultCode==RESULT_OK){
					//dataHandler(curpage);
					SharedPreferences logindata = getSharedPreferences("logindata", 0);
					String sessid = logindata.getString("sessid", "");
					String accountid = logindata.getString("accountid", "");

					subCollectMap = new HashMap<String, String>();
					subCollectMap.put("sessid", sessid);
					subCollectMap.put("userid", accountid);
					if(collectid>0) {//已收藏，取消收藏
						unsubCollectData();
					}
					else{ //提交 收藏
						subCollectData();
					}

				}
				break;
			default:
		}
	}
	/**
	 * 其他页退出Activity
	 */
	private void activityFinish()
	{
		AlertDialog.Builder dialog = new Builder(this);
		dialog.setTitle(getResources().getString(R.string.dialog_title));
		dialog.setMessage(getResources()
				.getString(R.string.dialog_exit_message));
		dialog.setPositiveButton("确定", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which){

				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);

			}
		});
		dialog.setNegativeButton("取消", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Intent intent ;
		// return super.onOptionsItemSelected(item);
		switch (item.getItemId()){
			case R.id.homepage:
				// TODO Auto-generated method stub
				// 关于
				intent = new Intent();
				intent.setClass(XjhViewActivity.this, MainChooseActivity.class);
 				startActivity(intent);
				break;
			case R.id.jobcollect:
				intent = new Intent();
				intent.setClass(XjhViewActivity.this, JobCollectActivity.class);
				startActivity(intent);
				break;
			case R.id.eixt:
				//mainActivityFinish();
				break;
		}
		return true;
	}
	public void onDestroy(){
		super.onDestroy();
		RichText.recycle();
	}
	public int getScreenWidth(){
		WindowManager wm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}
}