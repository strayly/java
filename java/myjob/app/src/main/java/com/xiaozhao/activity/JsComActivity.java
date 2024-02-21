package com.xiaozhao.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.xiaozhao.R;
import com.xiaozhao.bean.CompanyInfoBean;
import com.xiaozhao.bean.JobItemInfoBean;
import com.xiaozhao.datahandler.JsJobViewHandler;
import com.xiaozhao.util.ErrorViewManager;
import com.xiaozhao.util.MyApplication;
import com.xiaozhao.util.ScrollViewWithListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.leefeng.promptlibrary.PromptDialog;

//import com.baidu.mobstat.StatService;


public class JsComActivity extends Activity implements OnClickListener{

	private static final String TAG = "JsComActivity";
	
	//private List<XjhInfo> xjhdata = new ArrayList<XjhInfo>(); 
	private List<Map<String, String>> jobdata = new ArrayList<Map<String, String>>();
	
	private InputMethodManager imm;	
	
	
	private ProgressDialog Dialog;
	private TextView jobdescTV,comdescTV,bumenTV,hangyeTV,guimoTV,xingzhiTV,renshuTV,emailTV,remoteurlTV,recjobtitTV;
	private TextView jobnameTV,jobtypeTV,jobcityTV,companyTV;
	protected String response_str;
	protected String jobDetail,comDetail;

	private Button showcompany;
	private JobItemInfoBean jobinfo;
	protected String company;
	private Button mycollect;
	private Button return_back,collectjobbtn,applybtn;
	private ImageButton toolbar_back;
	private CompanyInfoBean companyInfoBean;
	private int companyid;
	private String remoteurl;
	private PromptDialog promptDialog;
	private Context mContext = JsComActivity.this;

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
		setContentView(R.layout.jscomshow);

		LinearLayout mainLayout = findViewById(R.id.mainLayout);
		errorView = ErrorViewManager.getInstance().init(mainLayout);
		errorView.setOnRetryChildClickListener(onRetryClickListener);

		companyid = (Integer) getIntent().getSerializableExtra("companyid");
		//jid = jobinfo.getId();
		

		
		companyTV = (TextView) findViewById(R.id.companyTV);
		//companyTV.setText(jobinfo.getJobcom());

		recjobtitTV = (TextView) findViewById(R.id.rec_job_tit);
		
		toolbar_back = (ImageButton) findViewById(R.id.toolbar_back);
		toolbar_back.setOnClickListener(this);
		
		//return_back = (Button) findViewById(R.id.return_back);
		//return_back.setOnClickListener(this);

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
		recdataHandler();

	}
	private ErrorViewManager.OnLoadingAndRetryListener onRetryClickListener = new ErrorViewManager.OnLoadingAndRetryListener(){
		public void onClick() {
			htmlHandler();
		}
	};
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
					if(resultCode==200) {
						errorView.showContent();
						comDetail = companyInfoBean.getComintro();
						comDetail = comDetail.replaceAll("\n", "<br />");
						comDetail = comDetail.replaceAll("(<br />\\s*)+", "<br />");

						companyTV.setText(companyInfoBean.getCname());
						comdescTV.setText(Html.fromHtml(comDetail));
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
					jsJobHandler.handlerCompanyDetail(companyid);
					resultCode = jsJobHandler.getResultCode();
					if(resultCode==200) {
						companyInfoBean = jsJobHandler.getCompanyInfoBean();
					}

				} catch (Exception e)
				{
					//Toast.makeText(JsJobViewActivity.this, "该招聘信息不存在或已过期", 1).show();
					Log.e(TAG, ""+e.toString());
				}

				// 给handle发送的消息
				Message m = new Message();
				m.what = 1;
				mHandler.sendMessage(m);

				Looper.loop();
			};

		}.start();

	}

	

	@Override
	public void onClick(View v){
		
		if (v == toolbar_back||v ==return_back){
			this.finish();
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
				intent.setClass(JsComActivity.this, MainChooseActivity.class);
 				startActivity(intent);
				break;
			case R.id.jobcollect:
				intent = new Intent();
				intent.setClass(JsComActivity.this, JobCollectActivity.class);
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
		map.put("companyid", String.valueOf(companyid));
		//map.put("type", "zz");//全职招聘
		JsJobViewHandler jsJobViewHandler = new JsJobViewHandler();
		jsJobViewHandler.handlerCompanyJobs(map);
		int total = jsJobViewHandler.getTotal();
		if(total>=0){
			setRecData(jsJobViewHandler.getListData());
		}
	}

	public void setRecData(List<JobItemInfoBean> jlist){
		recList.addAll(jlist);
	}
}
