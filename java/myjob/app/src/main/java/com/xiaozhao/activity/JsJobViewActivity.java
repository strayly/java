package com.xiaozhao.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaozhao.DB.SQLiteDBHelper;
import com.xiaozhao.R;
import com.xiaozhao.bean.JobDescBean;
import com.xiaozhao.bean.JobItemInfoBean;
import com.xiaozhao.datahandler.ApplyJobHandler;
import com.xiaozhao.datahandler.JobCollectHandler;
import com.xiaozhao.datahandler.JsJobViewHandler;
import com.xiaozhao.datahandler.RecJobsHandler;

import com.xiaozhao.util.ErrorViewManager;
import com.xiaozhao.util.MyApplication;
import com.xiaozhao.util.ScrollViewWithListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.leefeng.promptlibrary.PromptDialog;

//import com.baidu.mobstat.StatService;


public class JsJobViewActivity extends Activity implements OnClickListener{

	private static final String TAG = "JsJobViewActivity";
	
	//private List<XjhInfo> xjhdata = new ArrayList<XjhInfo>(); 
	private List<Map<String, String>> jobdata = new ArrayList<Map<String, String>>();
	
	private InputMethodManager imm;	
	
	
	private ProgressDialog Dialog;
	private TextView jobdescTV,comdescTV,bumenTV,hangyeTV,guimoTV,xingzhiTV,renshuTV,emailTV,remoteurlTV,recjobtitTV;
	private TextView jobnameTV,jobtypeTV,jobcityTV,companyTV;
	protected String response_str;
	protected String jobDetail,comDetail;

	private Button showcompany;
	private JobItemInfoBean jobItemInfoBean;
	protected String company;

	private Button return_back,applybtn,companybtn;
	private ImageButton toolbar_back, collectjobbtn;
	private JobDescBean jobDescBean;
	private int jsjid;
	private int cid;
	private int collectid = 0;
	private String sessid = "";
	private String accountid = "";
	private int subCollectResultCode,subApplyResultCode ;
	private final static int REQ_CODE = 1116;
	private String remoteurl;
	private PromptDialog promptDialog;
	private Context mContext = JsJobViewActivity.this;
	private List<JobItemInfoBean> recList = new ArrayList<JobItemInfoBean>();
	private ScrollViewWithListView recjobsView = null;
	private SimpleAdapter recjobsAdapter;
	private List<Map<String, String>> recjobList = new ArrayList<Map<String, String>>();
	private ErrorViewManager errorView;
	private int resultCode = 0;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.jsjobshow);

		LinearLayout mainLayout = findViewById(R.id.mainLayout);
		errorView = ErrorViewManager.getInstance().init(mainLayout);
		errorView.setOnRetryChildClickListener(onRetryClickListener);

		jsjid = (Integer) getIntent().getSerializableExtra("jobid");
		
		//System.out.println(jid);
		//jid = jobinfo.getId();
		
		jobnameTV = (TextView) findViewById(R.id.jobnameTV);
		//jobnameTV.setText(jobinfo.getJobname());
		
		jobtypeTV = (TextView) findViewById(R.id.jobtypeTV);
		//jobtypeTV.setText(jobinfo.getJobtype());
		jobcityTV = (TextView) findViewById(R.id.jobcityTV);
		//jobcityTV.setText(jobinfo.getJobcity());
		
		companyTV = (TextView) findViewById(R.id.companyTV);
		//companyTV.setText(jobinfo.getJobcom());

		
		bumenTV = (TextView) findViewById(R.id.bumenTV);
		//hangyeTV = (TextView) findViewById(R.id.hangyeTV);
		//guimoTV = (TextView) findViewById(R.id.guimoTV);
		//xingzhiTV = (TextView) findViewById(R.id.xingzhiTV);
		renshuTV = (TextView) findViewById(R.id.renshuTV);
		//emailTV = (TextView) findViewById(R.id.emailTV);
		
		remoteurlTV = (TextView) findViewById(R.id.remoteurlTV);
		remoteurlTV.setOnClickListener(this);
		
		toolbar_back = (ImageButton) findViewById(R.id.toolbar_back);
		toolbar_back.setOnClickListener(this);
		
		//return_back = (Button) findViewById(R.id.return_back);
		//return_back.setOnClickListener(this);
		recjobtitTV = (TextView) findViewById(R.id.rec_job_tit);
		
		collectjobbtn = (ImageButton) findViewById(R.id.collectjobbtn);
		collectjobbtn.setOnClickListener(this);
		
		applybtn = (Button) findViewById(R.id.applybtn);
		applybtn.setOnClickListener(this);

		companybtn = (Button) findViewById(R.id.companybtn);
		companybtn.setOnClickListener(this);

		
		jobdescTV = (TextView) findViewById(R.id.jobdescTV);
		comdescTV = (TextView) findViewById(R.id.comdescTV);
		

		//mycollect = (Button) findViewById(R.id.mycollect);
		//mycollect.setOnClickListener(this);

		promptDialog = new PromptDialog(this);
		recjobsView = (ScrollViewWithListView) findViewById(R.id.listview);
		recjobsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				Map<String, String> recMap = new HashMap<String, String>();
				recMap =recjobList.get(i);
				String linkid = recMap.get("linkid");
				String linktype = recMap.get("linktype");
				Intent intent = new Intent();
				if(linktype.equals("jsjobid")){
					intent.putExtra("jobid", Integer.parseInt(linkid));
					intent.setClass(mContext, JsJobViewActivity.class);
					mContext.startActivity(intent);
				}
				else if(linktype.equals("zzjobid")){//转载
					intent.putExtra("jobid", Integer.parseInt(linkid));
					intent.setClass(mContext, ZzJobViewActivity.class);
					mContext.startActivity(intent);
				}

			}
		});

		htmlHandler();
		checkCollected();
		recdataHandler();


	}
	private ErrorViewManager.OnLoadingAndRetryListener onRetryClickListener = new ErrorViewManager.OnLoadingAndRetryListener(){
		public void onClick() {
			htmlHandler();
			checkCollected();
			recdataHandler();
		}
	};
	public void addHistory(){
		if(jobItemInfoBean.getLinkid()>0) {
			try {
				SQLiteDBHelper dbHelper = new SQLiteDBHelper(this);
				dbHelper.saveJob(jobItemInfoBean);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	//检查该信息是否已收藏
	public void checkCollected() {
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
				JobCollectHandler jobCollectHandler = new JobCollectHandler();
				int checkResult = 0;
				Map<String, String> map = new HashMap<String, String>();
				SharedPreferences logindata = getSharedPreferences("logindata", 0);
				sessid = logindata.getString("sessid", "");
				accountid = logindata.getString("accountid", "");
				if(sessid!=null && accountid!=null){//未登录
					map.put("sessid", sessid);
					map.put("userid", accountid);
					map.put("type", "job");
					map.put("linkid", String.valueOf(jsjid));
					map.put("linktype", "jsjobid");
					checkResult  = jobCollectHandler.isCollected(map);
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
							Toast.makeText(JsJobViewActivity.this, "读取远程服务器数据失败！", Toast.LENGTH_LONG).show();
							break; //可选
						case -2 :
							Toast.makeText(JsJobViewActivity.this, "远程数据错误，请稍后尝试！\r\n", Toast.LENGTH_LONG).show();
							break; //可选
						case 401 :
							Toast.makeText(JsJobViewActivity.this,"您的登录信息已失效，请重新登录！",Toast.LENGTH_SHORT).show();
							Intent intent = new Intent();
							intent.setClass(JsJobViewActivity.this, UserLoginActivity.class);
							startActivityForResult(intent,REQ_CODE);
							//startActivity(intent);
							//return;
							break; //可选
						case 200 :
							Toast.makeText(JsJobViewActivity.this, "收藏已取消", Toast.LENGTH_LONG).show();
							collectjobbtn.setImageResource(R.mipmap.icon_genaral_fav_normal);
							collectid = 0;
							break; //可选
						//你可以有任意数量的case语句
						default : //可选
							Toast.makeText(JsJobViewActivity.this, subCollectResultCode+"服务请求失败！", Toast.LENGTH_LONG).show();
					}
				}
			}
		};
		new Thread(new Runnable(){
			@Override
			public void run() {
				JobCollectHandler jobCollectHandler = new JobCollectHandler();
				Map<String, String> map = new HashMap<String, String>();
				map.put("uid", accountid);
				map.put("id", String.valueOf(collectid));
				map.put("sessid", sessid);
				map.put("userid", accountid);
				jobCollectHandler.unsubCollect(map);
				int subResult = jobCollectHandler.getResultCode();

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
							Toast.makeText(JsJobViewActivity.this, "读取远程服务器数据失败！", Toast.LENGTH_LONG).show();
							break; //可选
						case -2 :
							Toast.makeText(JsJobViewActivity.this, "远程数据错误，请稍后尝试\r\n或者通过应届生网站访问！", Toast.LENGTH_LONG).show();
							break; //可选
						case 401 :
							Toast.makeText(JsJobViewActivity.this,"您的登录信息已失效，请重新登录！",Toast.LENGTH_SHORT).show();
							Intent intent = new Intent();
							intent.setClass(JsJobViewActivity.this, UserLoginActivity.class);
							startActivityForResult(intent,REQ_CODE+101);
							//startActivity(intent);
							//return;
							break; //可选
						case 200 :
							Toast.makeText(JsJobViewActivity.this, "信息已收藏", Toast.LENGTH_LONG).show();
							collectjobbtn.setImageResource(R.mipmap.icon_genaral_fav_selected);
							// collectjobbtn.setText("已收藏");
							//iscollect = 0;
							break; //可选
						//你可以有任意数量的case语句
						default : //可选
							Toast.makeText(JsJobViewActivity.this, subCollectResultCode+"服务请求失败！", Toast.LENGTH_LONG).show();
					}
				}
			}
		};
		new Thread(new Runnable(){
			@Override
			public void run() {
				JobCollectHandler jobCollectHandler = new JobCollectHandler();
				String subinfo = "";
				try {
					JSONObject infoJson = new JSONObject();
					infoJson.put("zzid","0");
					infoJson.put("linktype","jsjobid");
					infoJson.put("linkid",String.valueOf(jobItemInfoBean.getLinkid()));
					infoJson.put("title", jobItemInfoBean.getCname()+" "+ jobItemInfoBean.getJname());
					infoJson.put("companyname", jobItemInfoBean.getCname());
					infoJson.put("jobname", jobItemInfoBean.getJname());
					infoJson.put("city", jobItemInfoBean.getCity());
					infoJson.put("pubdate", jobItemInfoBean.getDate());
					infoJson.put("jobid51job", jobItemInfoBean.getJobid51job());

					JSONArray infoArray = new JSONArray();
					infoArray.put(infoJson);
					subinfo = infoArray.toString();

				} catch (Exception e) {
					e.printStackTrace();
				}
				//System.out.println(subinfo);
				Map<String, String> subCollectMap = new HashMap<String, String>();
				subCollectMap.put("sessid", sessid);
				subCollectMap.put("userid", accountid);
				subCollectMap.put("subinfo", subinfo);
				collectid = jobCollectHandler.subCollect(subCollectMap);
				int subResult = jobCollectHandler.getResultCode();

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

	//申请
	public void subApplyData() {
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
					String resultMsg = data.getString("resultMsg");
					subApplyResultCode = Integer.parseInt(resultCode);
					//Log.i(TAG,"请求结果:" + resultCode);
					switch(subApplyResultCode){
						case -1 :
							Toast.makeText(JsJobViewActivity.this, "读取远程服务器数据失败！", Toast.LENGTH_LONG).show();
							break; //可选
						case -2 :
							Toast.makeText(JsJobViewActivity.this, "远程数据错误，请稍后尝试\r\n或者通过应届生网站访问！", Toast.LENGTH_LONG).show();
							break; //可选
						case 401 :
							Toast.makeText(JsJobViewActivity.this,"您的登录信息已失效，请重新登录！",Toast.LENGTH_SHORT).show();
							Intent intent = new Intent();
							intent.setClass(JsJobViewActivity.this, UserLoginActivity.class);
							startActivityForResult(intent,REQ_CODE+100);
							//startActivity(intent);
							//return;
							break; //可选
						case 200 :
							Toast.makeText(JsJobViewActivity.this, "申请成功!", Toast.LENGTH_LONG).show();

							break; //可选
						case 601 ://跳转链接
							final String url = resultMsg;
							AlertDialog.Builder dialog = new Builder(JsJobViewActivity.this);
							dialog.setTitle("信息提示");
							dialog.setMessage("该链接为外部链接，是否启用本地浏览器访问？");
							dialog.setPositiveButton("确定", new DialogInterface.OnClickListener(){
								@Override
								public void onClick(DialogInterface dialog, int which){
									Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
									startActivity(browserIntent);
								}
							});
							dialog.setNegativeButton("取消", new DialogInterface.OnClickListener(){
								@Override
								public void onClick(DialogInterface dialog, int which){
									dialog.dismiss();
								}
							});
							dialog.show();
							break; //
						case 423 ://
							Toast.makeText(JsJobViewActivity.this, "没有找到对应的职位", Toast.LENGTH_LONG).show();
							break; //
						case 445 ://
							Toast.makeText(JsJobViewActivity.this, "已投递过该职位", Toast.LENGTH_LONG).show();
							break; //
						case 448 ://
							Toast.makeText(JsJobViewActivity.this, "该职位7天内不可重复申请", Toast.LENGTH_LONG).show();
							break; //
						case 421 ://
							Toast.makeText(JsJobViewActivity.this, "职位不存在或已删除", Toast.LENGTH_LONG).show();
							break; //
						case 443 ://
							Toast.makeText(JsJobViewActivity.this, "该职位已过期", Toast.LENGTH_LONG).show();
							break; //
						case 446 ://
							Toast.makeText(JsJobViewActivity.this, "英文简历不完整，请完善后再投递心仪的职位", Toast.LENGTH_LONG).show();
							break; //
						case 442 ://
							Toast.makeText(JsJobViewActivity.this, "中文简历不完整，请完善后再投递心仪的职位", Toast.LENGTH_LONG).show();
							break; //
						//你可以有任意数量的case语句
						default : //
							if(resultMsg!=null && resultMsg.length()>1) Toast.makeText(JsJobViewActivity.this, resultMsg, Toast.LENGTH_LONG).show();
							else Toast.makeText(JsJobViewActivity.this, subApplyResultCode+"服务请求失败！", Toast.LENGTH_LONG).show();
					}
				}
			}
		};


		new Thread(new Runnable(){
			@Override
			public void run() {
				ApplyJobHandler applyJobHandler = new ApplyJobHandler();
				Map<String, String> subApplyMap = new HashMap<String, String>();
				subApplyMap.put("sessid", sessid);
				subApplyMap.put("userid", accountid);
				subApplyMap.put("jobyjsid", String.valueOf(jsjid));
				subApplyMap.put("rsm51id", "0");
				subApplyMap.put("jobzzid", "0");
				subApplyMap.put("jobid51", "0");
				applyJobHandler.subApply(subApplyMap);
				int subResultCode = applyJobHandler.getResultCode();
				String subResultMsg = applyJobHandler.getResultMsg();
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putString("resultCode",String.valueOf(subResultCode));
				data.putString("resultMsg",subResultMsg);
				msg.setData(data);
				msg.what = 1;
				mHandler.sendMessage(msg);
			}
		}).start();
		//return handlerResult;
	}
	private void htmlHandler()
	{
		errorView.showLoading();
		final Handler mHandler = new Handler()
		{

			// 处理来自线程的消息,并将线程中的数据设置入
			@Override
			public void handleMessage(Message msg)
			{
				if (msg.what == 1)
				{
					promptDialog.dismiss();
					Bundle data = msg.getData();
					int resultCode = data.getInt("resultCode");
					errorView.showContent();
					if(resultCode==200) {
						jobDetail = jobDescBean.getJobintro();

						comDetail = jobDescBean.getComintro();
						remoteurl = jobDescBean.getRemoteurl();
						if(remoteurl.length()>0)remoteurlTV.setText("申请地址："+remoteurl);
						else remoteurlTV.setText("");


						jobDetail = jobDetail.replaceAll("\n", "<br />");
						jobDetail = jobDetail.replaceAll("(<br />\\s*)+", "<br />");

						comDetail = comDetail.replaceAll("\n", "<br />");
						comDetail = comDetail.replaceAll("(<br />\\s*)+", "<br />");

						jobdescTV.setText(Html.fromHtml(jobDetail));
						comdescTV.setText(Html.fromHtml(comDetail));
						bumenTV.setText(jobDescBean.getBumen());

						String zprs = jobDescBean.getRenshu();
						if(zprs.equals("0"))zprs = "";
						renshuTV.setText(zprs);

						//基本信息部分

						jobnameTV.setText(jobDescBean.getJobItemInfoBean().getJname());
						jobtypeTV.setText(jobDescBean.getJobItemInfoBean().getJobterm());
						jobcityTV.setText(jobDescBean.getJobItemInfoBean().getCity());
						companyTV.setText(jobDescBean.getJobItemInfoBean().getCname());

						addHistory();
					}
					else{
						jobdescTV.setText("该招聘信息不存在或已过期");
						Toast.makeText(JsJobViewActivity.this, "该招聘信息不存在或已过期", 1).show();
						return;
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

				try
				{
					Thread.sleep(5);

				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}

				try
				{
					//htmlConnect(url);
					JsJobViewHandler jsJobHandler = new JsJobViewHandler();
					jsJobHandler.handlerDataDetail(jsjid);
					resultCode = jsJobHandler.getResultCode();
					if(resultCode==200) {
						jobDescBean = jsJobHandler.getJobDescBean();
						jobItemInfoBean = jobDescBean.getJobItemInfoBean();

					}

				} catch (Exception e)
				{
					//Toast.makeText(JsJobViewActivity.this, "该招聘信息不存在或已过期", 1).show();
					Log.e(TAG, ""+e.toString());
				}

				// 给handle发送的消息
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putInt("resultCode",resultCode);
				msg.setData(data);
				msg.what = 1;
				mHandler.sendMessage(msg);

				Looper.loop();
			};

		}.start();

	}
	

	@Override
	public void onClick(View v){
		
		if (v == toolbar_back||v ==return_back){
			this.finish();
		}
		else if(v==remoteurlTV && remoteurl!=null && remoteurl.length()>0 ){
			//Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(remoteurl));
			//startActivity(browserIntent);			
			final String url = remoteurl;
			AlertDialog.Builder dialog = new Builder(JsJobViewActivity.this);
			dialog.setTitle("信息提示");
			dialog.setMessage("该链接为外部链接，是否启用本地浏览器访问？");
			dialog.setPositiveButton("确定", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which){					
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					startActivity(browserIntent);
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
		else if(v == companybtn){
			Intent intent = new Intent();
			intent.putExtra("companyid", jobDescBean.getCid());
			intent.setClass(this, JsComActivity.class);
			startActivity(intent);
		}
		else if(v == applybtn){
			SharedPreferences logindata = getSharedPreferences("logindata", 0);
			sessid = logindata.getString("sessid", "");
			accountid = logindata.getString("accountid", "");

			if(sessid==null||sessid.equals("")){//未登录
				Intent intent = new Intent();
				intent.setClass(this, UserLoginActivity.class);
				//startActivity(intent);
				startActivityForResult(intent,REQ_CODE+100);
			}
			//Map<String, String> applyMap = new HashMap<String, String>();
			subApplyData();

		}
		else if(v == collectjobbtn){ //收藏
			SharedPreferences logindata = getSharedPreferences("logindata", 0);
			sessid = logindata.getString("sessid", "");
			accountid = logindata.getString("accountid", "");

			if(sessid==null||sessid.equals("")){//未登录
				Intent intent = new Intent();
				intent.setClass(this, UserLoginActivity.class);
				//startActivity(intent);
				startActivityForResult(intent,REQ_CODE+101);
			}

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
		SharedPreferences logindata = getSharedPreferences("logindata", 0);
		sessid = logindata.getString("sessid", "");
		accountid = logindata.getString("accountid", "");

		switch(requestCode){
			case REQ_CODE+100://申请
				if(resultCode==RESULT_OK){
					subApplyData();
				}
				break;
			case REQ_CODE+101://收藏
				if(resultCode==RESULT_OK){
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
			public void onClick(DialogInterface dialog, int which)
			{

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
				intent.setClass(JsJobViewActivity.this, MainChooseActivity.class);
 				startActivity(intent);
				break;
			case R.id.jobcollect:
				intent = new Intent();
				intent.setClass(JsJobViewActivity.this, JobCollectActivity.class);
				startActivity(intent);
				break;
			case R.id.eixt:
				//mainActivityFinish();
				break;
		}
		return true;
	}

	private void recdataHandler(){
		final Handler mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg)
			{
				if (msg.what == 1) {
					setRecAdapter();
				}
			}
		};
		new Thread(){
			public void run(){
				// 子线程的循环标志位
				Looper.prepare();
				try{
					Thread.sleep(50);
				}
				catch (InterruptedException e){
					e.printStackTrace();
				}
				try{
					recdataConnect();
				}
				catch (Exception e){
					Log.e(TAG, e.toString());
				}
				// 给handle发送的消息
				Message msg = new Message();
				msg.what = 1;
				mHandler.sendMessage(msg);
				Looper.loop();
			};
		}.start();
	}
	private void setRecAdapter(){
		recjobList = new ArrayList<Map<String, String>>();
		if(recList==null || recList.size()>0) {
			for (JobItemInfoBean jinfo : recList) {
				String date = jinfo.getDate();
				Map<String, String> map = new HashMap<String, String>();
				map.put("city", jinfo.getCity());
				map.put("title", jinfo.getTitle());
				map.put("date", jinfo.getDate());
				map.put("linkid", String.valueOf(jinfo.getLinkid()));
				map.put("linktype", String.valueOf(jinfo.getLinktype()));
				recjobList.add(map);
			}
			recjobsAdapter = new SimpleAdapter(mContext, recjobList, R.layout.list_item_recjob, new String[]{"title", "city", "date"}, new int[]{R.id.title, R.id.city, R.id.date});
			recjobsView.setAdapter(recjobsAdapter);
			recjobtitTV.setVisibility(View.VISIBLE);
		}
		else{
			recjobtitTV.setVisibility(View.GONE);
		}
	}
	private void recdataConnect() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("jobid", String.valueOf(jsjid));
		map.put("type", "js");//全职招聘
		RecJobsHandler recJobsHandler = new RecJobsHandler();
		recJobsHandler.handlerData(map);
		int total = recJobsHandler.getTotal();
		if(total>=0){
			setRecData(recJobsHandler.getListData());
		}
	}

	public void setRecData(List<JobItemInfoBean> jlist){
		recList.addAll(jlist);
	}
}
