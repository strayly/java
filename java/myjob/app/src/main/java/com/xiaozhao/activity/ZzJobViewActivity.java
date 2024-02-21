package com.xiaozhao.activity;

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
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
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
import com.xiaozhao.datahandler.JobCollectHandler;
import com.xiaozhao.datahandler.RecJobsHandler;
import com.xiaozhao.datahandler.ZzJobViewHandler;
import com.xiaozhao.util.ErrorViewManager;
import com.xiaozhao.util.Html2Text;
import com.xiaozhao.util.MyApplication;
import com.xiaozhao.util.MyImageFix;
import com.xiaozhao.util.MyUrlClick;
import com.xiaozhao.util.ScrollViewWithListView;
import com.xiaozhao.util.SwipeActivity;
import com.zzhoujay.richtext.RichText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.leefeng.promptlibrary.PromptDialog;

//import com.baidu.mobstat.StatService;


public class ZzJobViewActivity extends SwipeActivity implements OnClickListener{

	private static final String TAG = "ZzJobViewActivity";
	
	//private List<XjhInfo> xjhdata = new ArrayList<XjhInfo>(); 
	private List<Map<String, String>> jobdata = new ArrayList<Map<String, String>>();
	
	private InputMethodManager imm;	
	
	
	private ProgressDialog Dialog;
	private TextView jobdescTV,comdescTV,bumenTV,hangyeTV,guimoTV,xingzhiTV,renshuTV,emailTV,recjobtitTV;
	private TextView jobnameTV,jobtypeTV,jobcityTV,companyTV;
	protected String jobDetail,comDetail;

	private MyImageFix myImageFix ;
    private MyUrlClick myUrlClick ;
	private JobItemInfoBean jobItemInfoBean;
	protected String company;
	//private Button mycollect;
	private Button return_back;
	private ImageButton toolbar_back;
	private ImageButton collectjobbtn;
	private JobDescBean jobDescBean;
	private int zzjid;
	private int collectid = 0;
	private String sessid = "";
	private String accountid = "";
	private int subCollectResultCode ;
	private Map<String, String> subCollectMap = new HashMap<String, String>();
	private final static int REQ_CODE = 1115;
	private Context mContext = ZzJobViewActivity.this;
	private PromptDialog promptDialog;

	private List<JobItemInfoBean> recList = new ArrayList<JobItemInfoBean>();
	private ScrollViewWithListView recjobsView = null;
	private SimpleAdapter recjobsAdapter;
	private int recCount = 0;
	private List<Map<String, String>> recjobList = new ArrayList<Map<String, String>>();
	private int resultCode = 0;
	private ErrorViewManager errorView;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.zzjobshow);
		setSwipeAnyWhere(false);
		zzjid = (Integer) getIntent().getSerializableExtra("jobid");

		LinearLayout mainLayout = findViewById(R.id.mainLayout);
		errorView = ErrorViewManager.getInstance().init(mainLayout);
		errorView.setOnRetryChildClickListener(onRetryClickListener);
		
		jobnameTV = (TextView) findViewById(R.id.jobnameTV);		
		jobtypeTV = (TextView) findViewById(R.id.jobtypeTV);
		jobcityTV = (TextView) findViewById(R.id.jobcityTV);		
		companyTV = (TextView) findViewById(R.id.companyTV);
		recjobtitTV = (TextView) findViewById(R.id.rec_job_tit);


		collectjobbtn = (ImageButton) findViewById(R.id.collectjobbtn);
		collectjobbtn.setOnClickListener(this);
		
		toolbar_back = (ImageButton) findViewById(R.id.toolbar_back);
		toolbar_back.setOnClickListener(this);
		
		//return_back = (Button) findViewById(R.id.return_back);
		//return_back.setOnClickListener(this);

		jobdescTV = (TextView) findViewById(R.id.jobdescTV);
		comdescTV = (TextView) findViewById(R.id.comdescTV);

		//recjobtitTV.setVisibility(View.VISIBLE);

		SQLiteDBHelper dbHelper = new SQLiteDBHelper(ZzJobViewActivity.this);
		myImageFix = new MyImageFix(getScreenWidth()) ;
        myUrlClick = new MyUrlClick(mContext) ;
		//mycollect = (Button) findViewById(R.id.mycollect);
		//mycollect.setOnClickListener(this);
		RichText.initCacheDir(this);

		//if (android.os.Build.VERSION.SDK_INT > 9) {
		//	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		//	StrictMode.setThreadPolicy(policy);
		//}
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
					map.put("linkid", String.valueOf(zzjid));
					map.put("linktype", "zzjobid");
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
							Toast.makeText(ZzJobViewActivity.this, "读取远程服务器数据失败！", Toast.LENGTH_LONG).show();
							break; //可选
						case -2 :
							Toast.makeText(ZzJobViewActivity.this, "远程数据错误，请稍后尝试！\r\n", Toast.LENGTH_LONG).show();
							break; //可选
						case 401 :
							Toast.makeText(ZzJobViewActivity.this,"您的登录信息已失效，请重新登录！",Toast.LENGTH_SHORT).show();
							Intent intent = new Intent();
							intent.setClass(ZzJobViewActivity.this, UserLoginActivity.class);
							startActivityForResult(intent,REQ_CODE);
							//startActivity(intent);
							//return;
							break; //可选
						case 200 :
							Toast.makeText(ZzJobViewActivity.this, "收藏已取消", Toast.LENGTH_LONG).show();
							collectjobbtn.setImageResource(R.mipmap.icon_genaral_fav_normal);
							collectid = 0;
							break; //可选
						//你可以有任意数量的case语句
						default : //可选
							Toast.makeText(ZzJobViewActivity.this, subCollectResultCode+"服务请求失败！", Toast.LENGTH_LONG).show();
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
							Toast.makeText(ZzJobViewActivity.this, "读取远程服务器数据失败！", Toast.LENGTH_LONG).show();
							break; //可选
						case -2 :
							Toast.makeText(ZzJobViewActivity.this, "远程数据错误，请稍后尝试\r\n或者通过应届生网站访问！", Toast.LENGTH_LONG).show();
							break; //可选
						case 401 :
							Toast.makeText(ZzJobViewActivity.this,"您的登录信息已失效，请重新登录！",Toast.LENGTH_SHORT).show();
							Intent intent = new Intent();
							intent.setClass(ZzJobViewActivity.this, UserLoginActivity.class);
							startActivityForResult(intent,REQ_CODE);
							//startActivity(intent);
							//return;
							break; //可选
						case 200 :
							Toast.makeText(ZzJobViewActivity.this, "信息已收藏", Toast.LENGTH_LONG).show();
							collectjobbtn.setImageResource(R.mipmap.icon_genaral_fav_selected);
							//iscollect = 0;
							break; //可选
						//你可以有任意数量的case语句
						default : //可选
							Toast.makeText(ZzJobViewActivity.this, subCollectResultCode+"服务请求失败！", Toast.LENGTH_LONG).show();
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
					infoJson.put("zzid",String.valueOf(jobItemInfoBean.getZzid()));
					infoJson.put("linktype","zzjobid");
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
					errorView.showContent();
					Bundle data = msg.getData();
					int resultCode = data.getInt("resultCode");
					if(resultCode==200) {
						//jobdescTV.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

						RichText.from(jobDetail).autoFix(false).fix(myImageFix).urlClick(myUrlClick).error(R.drawable.nophoto).into(jobdescTV);
						//                .from(text) // 数据源
//                .type(RichText.TYPE_MARKDOWN) // 数据格式,不设置默认是Html,使用fromMarkdown的默认是Markdown格式
//                .autoFix(true) // 是否自动修复，默认true
//                .autoPlay(true) // gif图片是否自动播放
//                .showBorder(true) // 是否显示图片边框
//                .borderColor(Color.RED) // 图片边框颜色
//                .borderSize(10) // 边框尺寸
//                .borderRadius(50) // 图片边框圆角弧度
//                .scaleType(ImageHolder.ScaleType.FIT_CENTER) // 图片缩放方式
//                .size(ImageHolder.MATCH_PARENT, ImageHolder.WRAP_CONTENT) // 图片占位区域的宽高
//                .fix(imageFixCallback) // 设置自定义修复图片宽高
//                .fixLink(linkFixCallback) // 设置链接自定义回调
//                .noImage(true) // 不显示并且不加载图片
//                .resetSize(false) // 默认false，是否忽略img标签中的宽高尺寸（只在img标签中存在宽高时才有效），true：忽略标签中的尺寸并触发SIZE_READY回调，false：使用img标签中的宽高尺寸，不触发SIZE_READY回调
//                .clickable(true) // 是否可点击，默认只有设置了点击监听才可点击
//                .imageClick(onImageClickListener) // 设置图片点击回调
//                .imageLongClick(onImageLongClickListener) // 设置图片长按回调
//                .urlClick(onURLClickListener) // 设置链接点击回调
//                .urlLongClick(onUrlLongClickListener) // 设置链接长按回调
//                .placeHolder(placeHolder) // 设置加载中显示的占位图
//                .error(errorImage) // 设置加载失败的错误图
//                .cache(Cache.ALL) // 缓存类型，默认为Cache.ALL（缓存图片和图片大小信息和文本样式信息）
//                .imageGetter(yourImageGetter) // 设置图片加载器，默认为DefaultImageGetter，使用okhttp实现
//                .bind(tag) // 绑定richText对象到某个object上，方便后面的清理
//                .done(callback) // 解析完成回调
//                .into(textView); // 设置目标TextView
						//.error(R.drawable.pic_default_topic)
						//jobdescTV.setText(Html.fromHtml(jobDetail));
						//jobdescTV.setText(spanned);
						comdescTV.setText(Html.fromHtml(comDetail));

						//基本信息
						jobnameTV.setText(jobDescBean.getJobItemInfoBean().getJname());
						jobtypeTV.setText(jobDescBean.getJobItemInfoBean().getJobterm());
						jobcityTV.setText(jobDescBean.getJobItemInfoBean().getCity());
						companyTV.setText(jobDescBean.getJobItemInfoBean().getCname());
						addHistory();
						//setRecAdapter();
					}
					else{// if(resultCode==400)
						Toast.makeText(ZzJobViewActivity.this, "职位不存在或已删除", Toast.LENGTH_LONG).show();
						jobdescTV.setText("职位不存在或已删除");
						comdescTV.setText("");
						//基本信息
						jobnameTV.setText("");
						jobtypeTV.setText("");
						jobcityTV.setText("");
						companyTV.setText("");
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
					Thread.sleep(50);

				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				try
				{

					ZzJobViewHandler zzJobViewHandler = new ZzJobViewHandler();
					zzJobViewHandler.handlerDataDetail(zzjid);
					resultCode = zzJobViewHandler.getResultCode();
					if(resultCode==200) {
						jobDescBean = zzJobViewHandler.getJobDescBean();
						jobDetail = jobDescBean.getJobintro();
						comDetail = jobDescBean.getComintro();
						Html2Text html2text = new Html2Text();
						jobDetail = html2text.html2Text(jobDetail);
						comDetail = html2text.html2Text(comDetail);

						comDetail = comDetail.replaceAll("(<br />\\s*)+", "<br />");
						jobItemInfoBean = jobDescBean.getJobItemInfoBean();
						//recdataConnect();
					}
					//System.out.println("bbb:"+jobDetail);


				} catch (Exception e)
				{
					Log.e(TAG, e.toString());
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

	/**
	 * 开启一条线程来执行从网络获取的数据
	 */
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
					Thread.sleep(1);
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
		map.put("jobid", String.valueOf(zzjid));
		map.put("type", "zz");//全职招聘
		RecJobsHandler recJobsHandler = new RecJobsHandler();
		recJobsHandler.handlerData(map);
		int total = recJobsHandler.getTotal();
		if(total>=0){
			setRecCount(total);
			setRecData(recJobsHandler.getListData());
		}
	}

	public void setRecCount(int count){
		this.recCount= count;
	}
	public void setRecData(List<JobItemInfoBean> jlist){
		recList.addAll(jlist);
	}

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
	
	@Override
	public void onClick(View v){
		
		if (v == toolbar_back||v == return_back){
			ZzJobViewActivity.this.finish();
		}
		else if(v == collectjobbtn){ //收藏
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
				intent.setClass(ZzJobViewActivity.this, MainChooseActivity.class);
 				startActivity(intent);
				break;
			case R.id.jobcollect:
				intent = new Intent();
				intent.setClass(ZzJobViewActivity.this, JobCollectActivity.class);
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