package com.xiaozhao.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.xiaozhao.R;
import com.xiaozhao.util.MyApplication;

public class MyResumeActivity extends Activity implements OnClickListener{

	private static final String TAG = "MyResumeActivity";
	//private TextView addr_text;
	private ImageButton backbtn, homebtn,outbtn,partjobbtn,fulljobbtn;
	private Button loginbtn;
	private EditText usernameET,passwordET;
	private ProgressDialog Dialog;
	private InputMethodManager imm;
	private String wvTeturn ;
	private String mobile ;
	private String email ;
	private String passwd ;
	private String mobileTest ;
	private String emailTest ;
	private String passwdTest ;
	private String sessid ;
	private String accountid ;
    android.app.Dialog pDialog;
	private Context mContext = MyResumeActivity.this;
	//private String loginResult = "NONE";
	//String loading;
    Intent intent;
	//private UserLoginHandler loginHandler = new UserLoginHandler();
	private final static int REQ_CODE = 1121;
	/** Called when the activity is first created. */
	@SuppressLint("JavascriptInterface")
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.resume);


		SharedPreferences logindata = getSharedPreferences("logindata", 0);

		mobile = logindata.getString("mobile", "");
		email = logindata.getString("email", "");
		passwd = logindata.getString("pw", "");
		sessid = logindata.getString("sessid", "");
		accountid = logindata.getString("accountid", "");

		if(passwd==null) passwd = "";
		if(mobile==null) mobile = "";
		if(email==null) email = "";

		backbtn = (ImageButton) findViewById(R.id.back_btn);
		backbtn.setOnClickListener(this);

        intent = new Intent();

        //loading = getResources().getString(R.string.dialog_loading);
		if(sessid==null||sessid.equals("")){//未登录
			Intent intent = new Intent();
			intent.setClass(this, UserLoginActivity.class);
			//startActivity(intent);
			startActivityForResult(intent,REQ_CODE);
		}
		else {
			loadRsmWebview();
		}

	}
	
	public void loadRsmWebview(){
		//获得控件
        //Toast.makeText(this, "网页加载完成", 1).show();
        ///*
		WebView webView = (WebView) findViewById(R.id.rsm_webview);
		//webView.addJavascriptInterface(new Handler(),"handler");
		//访问网页
		String isenglish = "c";
		String rsmurl = "https://baseinfo.php?sessid="+sessid+"&userid="+accountid+"&back=wx&isenglish="+isenglish;
		webView.loadUrl(rsmurl);
		//webView.addJavascriptInterface(new JSInterface(this), "login_js");

		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		//webSettings.addJavascriptInterface(new Handler(), "handler");
		//webView.addJavascriptInterface(new Handler(), "handler");
		//支持插件
		//webSettings.setPluginsEnabled(true);

		//设置自适应屏幕，两者合用
		webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
		webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

		//缩放操作
		webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
		webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
		webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

		//其他细节操作
		//webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		webSettings.setDomStorageEnabled(true);
		webSettings.setDatabaseEnabled(true);
		webSettings.setSavePassword(true);
		webSettings.setSaveFormData(true);

		webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
		webSettings.setAllowFileAccess(true); //设置可以访问文件
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
		webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
		webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
		pDialog = ProgressDialog.show(MyResumeActivity.this, null, getResources().getString(R.string.dialog_loading));
		webView.setWebViewClient(new WebViewClient() {
			// 链接跳转都会走这个方法
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				//Uri uri = Uri.parse(url);
				view.loadUrl(url);// 强制在当前 WebView 中加载 url
				//view.loadDataWithBaseURL(url, html, "text/html", "UTF-8", "");
				return true;
			}
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
                pDialog.show();

			}
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);

                pDialog.cancel();
			}
		});

	}


	@Override
	public void onClick(View v){
		if(v == backbtn){

			finish();
		}
	}

	//登录跳转回来后 刷新状态和数据
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQ_CODE:
				if (resultCode == RESULT_OK) {
					//dataHandler(curpage);
					String loginResult = (String) data.getSerializableExtra("LoginResult");
					if(loginResult.equals("SUCCESS")) {
						SharedPreferences logindata = getSharedPreferences("logindata", 0);
						sessid = logindata.getString("sessid", "");
						accountid = logindata.getString("accountid", "");
						loadRsmWebview();
					}
					else if(loginResult.equals("FAIL")){
						Toast.makeText(mContext, "登录失败！", Toast.LENGTH_SHORT).show();
					}
					else{
						finish();
					}
				}
				break;
			default:
		}
	}
	private void clearSearchFocus(){
		if(imm!=null){
			//imm.hideSoftInputFromWindow(search.getWindowToken(), 0);    
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
				intent.setClass(MyResumeActivity.this, MainChooseActivity.class);
 				startActivity(intent);
				break;
			case R.id.eixt:
				//mainActivityFinish();
				break;
		}
		return true;
	}
	class JSInterface {
		@SuppressLint("JavascriptInterface")
		Context mContext;

		/** Instantiate the interface and set the context */
		JSInterface(Context c) {
			mContext = c;
		}


		/** Show a toast from the web page */
		@JavascriptInterface
		public void showToast(String toast) {
			Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
		}
	}

}






