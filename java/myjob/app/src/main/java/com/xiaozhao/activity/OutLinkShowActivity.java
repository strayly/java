package com.xiaozhao.activity;


//import com.baidu.mobstat.StatService;
import com.xiaozhao.util.MyApplication;
import com.xiaozhao.R;

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
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.webkit.JavascriptInterface;

import java.util.HashMap;
import java.util.Map;

public class OutLinkShowActivity extends Activity implements OnClickListener{

	private static final String TAG = "UserLoginActivity";
	//private TextView addr_text;

	private ImageButton nextbtn,beforebtn,backbtn;

	private ProgressDialog Dialog;

	private InputMethodManager imm;
	private String url ;
	private int jobid;
	private ProgressDialog  progressDialog;
	Intent intent;
	private WebView webView;
	//private UserLoginHandler loginHandler = new UserLoginHandler();
	

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.webview);

		backbtn = (ImageButton) findViewById(R.id.back_btn);
		backbtn.setOnClickListener(this);


		//jobid = (Integer) getIntent().getSerializableExtra("jobid");
		url = (String) getIntent().getSerializableExtra("url");

        intent = new Intent();

        String loading = getResources().getString(R.string.dialog_loading);
        progressDialog = ProgressDialog.show(this, null, loading);

		//获得控件
		webView = (WebView) findViewById(R.id.webview);
		//webView.addJavascriptInterface(new Handler(),"handler");
		//访问网页

		webView.loadUrl(url);
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

		webView.setWebViewClient(new WebViewClient() {
			// shouldOverrideUrlLoading处理页面控制和请求通知
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return false;// true用WebView打开，false用系统浏览器打开
			}
		});

		webView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int newProgress) {
				if (100 == newProgress) {
					// 加载完毕，关闭进度对话框
					if (null != progressDialog && progressDialog.isShowing()) {
						progressDialog.dismiss();// 关闭对话框
						progressDialog = null;
					}
				} else {
					// 加载网页中，显示加载进度
					if (null == progressDialog) {
						progressDialog = new ProgressDialog(OutLinkShowActivity.this);
						// 设置对话框样式
						progressDialog.setProgressStyle(ProgressDialog.THEME_HOLO_DARK);
						progressDialog.setTitle("loading...");
						progressDialog.setProgress(newProgress);
						progressDialog.show();
					} else {
						progressDialog.setProgress(newProgress);
					}
				}
			}

		});

	}
	
	


	@Override
	public void onClick(View v){


		if(v == backbtn){
			finish();
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


}






