package com.xiaozhao.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaozhao.R;
import com.xiaozhao.bean.DalibaoBean;
import com.xiaozhao.datahandler.DalibaoHandler;
import com.xiaozhao.datahandler.XjhCollectHandler;
import com.xiaozhao.util.ErrorViewManager;
import com.xiaozhao.util.Html2Text;
import com.xiaozhao.util.MyApplication;
import com.xiaozhao.util.MyImageFix;
import com.xiaozhao.util.MyUrlClick;
import com.zzhoujay.richtext.RichText;

import java.util.HashMap;
import java.util.Map;

import me.leefeng.promptlibrary.PromptDialog;


public class DalibaoDetailActivity extends Activity implements OnClickListener{

	private static final String TAG = "DalibaoDetailActivity";

	private InputMethodManager imm;	
	

	private TextView titleTV,messageTV,attachmentTV,classnameTV,pubdateTV;
	protected String detail;

	private DalibaoBean dalibaoBean;
	protected String company;
	private Button toolbar_back,collectjobbtn;
	private int dlbid;
	private int collectid = 0;
	private String sessid;
	private String accountid;

	private Map<String, String> subCollectMap = new HashMap<String, String>();
	private int subCollectResultCode ;
	private final static int REQ_CODE = 1113;
	private MyImageFix myImageFix ;
	private MyUrlClick myUrlClick ;
	private Context mContext = DalibaoDetailActivity.this;
	private PromptDialog promptDialog;
	private WebView webView;
	private ErrorViewManager errorView;
	private int resultCode = 0;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.dalibaoshow);

		LinearLayout mainLayout = findViewById(R.id.mainLayout);
		errorView = ErrorViewManager.getInstance().init(mainLayout);
		errorView.setOnRetryChildClickListener(onRetryClickListener);

		dlbid = (Integer) getIntent().getSerializableExtra("dlbid");

		webView = (WebView) findViewById(R.id.pdf_webview);

		titleTV = (TextView) findViewById(R.id.titleTV);
		messageTV = (TextView) findViewById(R.id.messageTV);
		attachmentTV = (TextView) findViewById(R.id.attachmentTV);
		classnameTV = (TextView) findViewById(R.id.classnameTV);
		pubdateTV = (TextView) findViewById(R.id.pubdateTV);
		
		//collectjobbtn = (Button) findViewById(R.id.collectjobbtn);
		//collectjobbtn.setOnClickListener(this);
		
		toolbar_back = (Button) findViewById(R.id.toolbar_back);
		toolbar_back.setOnClickListener(this);

		myImageFix = new MyImageFix(getScreenWidth()) ;
		myUrlClick = new MyUrlClick(mContext) ;


		//if (android.os.Build.VERSION.SDK_INT > 9) {
		//	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		//	StrictMode.setThreadPolicy(policy);
		//}
		htmlHandler();
		//checkCollected();


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

						RichText.from(detail).autoFix(false).fix(myImageFix).urlClick(myUrlClick).error(R.drawable.nophoto).into(messageTV);
						String atturl = dalibaoBean.getAttachment();

						//基本信息
						//fieldCompany.setText(xjhInfoBean.getCompany());
						titleTV.setText(dalibaoBean.getTitle());
						attachmentTV.setText(atturl);
						classnameTV.setText(dalibaoBean.getClassname());
						pubdateTV.setText(dalibaoBean.getPubdate());
						if (atturl != null && atturl.length() > 7) {
							System.out.println(atturl);
							WebSettings webSettings = webView.getSettings();
							webSettings.setJavaScriptEnabled(true);
							webSettings.setAllowFileAccess(true);
							webSettings.setAllowFileAccessFromFileURLs(true);
							webSettings.setAllowUniversalAccessFromFileURLs(true);
							webView.loadUrl("file:///android_asset/pdf.html?" + atturl);

						}
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
                    DalibaoHandler dalibaoHandler = new DalibaoHandler();
					dalibaoBean =dalibaoHandler.handlerDetail(dlbid);
					resultCode = dalibaoHandler.getResultCode();
					if(resultCode==200) {
						detail = dalibaoBean.getMessage();
						Html2Text html2text = new Html2Text();
						detail = html2text.html2Text(detail);
						detail = detail.replaceAll("(<br />\\s*)+", "<br />");
					}
					//System.out.println("bbb:"+jobDetail);
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
						collectjobbtn.setText("已收藏");
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
					map.put("xjhid", String.valueOf(dlbid));
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

	//收藏


	@Override
	public void onClick(View v){
		
		if (v == toolbar_back){
			this.finish();
		}
	
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