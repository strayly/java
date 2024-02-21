package com.xiaozhao.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaozhao.R;
import com.xiaozhao.bean.ScheduleBean;
import com.xiaozhao.datahandler.ScheduleHandler;
import com.xiaozhao.util.ErrorViewManager;
import com.xiaozhao.util.Html2Text;
import com.xiaozhao.util.MyApplication;
import com.xiaozhao.util.MyImageFix;
import com.xiaozhao.util.MyUrlClick;
import com.zzhoujay.richtext.RichText;


public class ScheduleDetailActivity extends Activity implements OnClickListener{

	private static final String TAG = "ScheduleDetailActivity";

	private InputMethodManager imm;	
	
	

	private TextView subjectTV,companyTV,noticetypeTV,infodateTV,infofromTV,fromurlTV,messageTV;


	private ScheduleBean scheduleBean;
	protected String detail;
	private Button toolbar_back;
	private int tid;

	private String sessid;
	private String accountid;

	private final static int REQ_CODE = 1113;
	private MyImageFix myImageFix ;
	private MyUrlClick myUrlClick ;
	private Context mContext = ScheduleDetailActivity.this;

	private ErrorViewManager errorView;
	private int resultCode = 0;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.scheduleshow);

		tid = (Integer) getIntent().getSerializableExtra("tid");

		LinearLayout mainLayout = findViewById(R.id.mainLayout);
		errorView = ErrorViewManager.getInstance().init(mainLayout);
		errorView.setOnRetryChildClickListener(onRetryClickListener);
		subjectTV = (TextView) findViewById(R.id.subjectTV);
		companyTV = (TextView) findViewById(R.id.companyTV);
		noticetypeTV = (TextView) findViewById(R.id.noticetypeTV);
		infodateTV = (TextView) findViewById(R.id.infodateTV);
		infofromTV = (TextView) findViewById(R.id.infofromTV);
		fromurlTV = (TextView) findViewById(R.id.fromurlTV);
		messageTV = (TextView) findViewById(R.id.messageTV);

		
		toolbar_back = (Button) findViewById(R.id.toolbar_back);
		toolbar_back.setOnClickListener(this);

		myImageFix = new MyImageFix(getScreenWidth()) ;
		myUrlClick = new MyUrlClick(mContext) ;
		htmlHandler();

	}
	private ErrorViewManager.OnLoadingAndRetryListener onRetryClickListener = new ErrorViewManager.OnLoadingAndRetryListener(){
		public void onClick() {
			htmlHandler();
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
						//detail =scheduleBean.getMessage();
						RichText.from(detail).autoFix(false).fix(myImageFix).urlClick(myUrlClick).error(R.drawable.nophoto).into(messageTV);

						//基本信息
						subjectTV.setText(scheduleBean.getCompany());
						companyTV.setText(scheduleBean.getCompany());
						noticetypeTV.setText(scheduleBean.getNoticetype());
						infodateTV.setText(scheduleBean.getInfodate());
						infofromTV.setText(scheduleBean.getInfofrom());
						fromurlTV.setText(scheduleBean.getFromurl());

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
					ScheduleHandler scheduleHandler = new ScheduleHandler();
					scheduleBean = scheduleHandler.handlerDetail(tid);
					resultCode = scheduleHandler.getResultCode();

					if(resultCode==200) {
						detail = scheduleBean.getMessage();
						Html2Text html2text = new Html2Text();
						detail = html2text.html2Text(detail);
						detail = detail.replaceAll("(<br />\\s*)+", "<br />");

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

	@Override
	public void onClick(View v){
		
		if (v == toolbar_back){
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
				intent.setClass(mContext, MainChooseActivity.class);
 				startActivity(intent);
				break;
			case R.id.jobcollect:
				intent = new Intent();
				intent.setClass(mContext, JobCollectActivity.class);
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