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
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.xiaozhao.R;
import com.xiaozhao.util.MyApplication;

import java.util.HashMap;
import java.util.Map;

public class UserLoginActivity extends Activity implements OnClickListener{

	private static final String TAG = "UserLoginActivity";
	//private TextView addr_text;

	private ImageButton backbtn;

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
	private android.app.Dialog progressDialog;
	private Intent intent;
	private String loginResult = "NONE";
	//private UserLoginHandler loginHandler = new UserLoginHandler();
	
	/** Called when the activity is first created. */
	@SuppressLint("JavascriptInterface")
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.userlogin);

		backbtn = (ImageButton) findViewById(R.id.back_btn);
		backbtn.setOnClickListener(this);
		loginResult = "NONE";
		//readLoginData();
;



		SharedPreferences logindata = getSharedPreferences("logindata", 0);

		mobile = logindata.getString("mobile", "");
		email = logindata.getString("email", "");
		passwd = logindata.getString("pw", "");

		if(passwd==null) passwd = "";
		if(mobile==null) mobile = "";
		if(email==null) email = "";

        intent = new Intent();

        String loading = getResources().getString(R.string.dialog_loading);
        progressDialog = ProgressDialog.show(this, null, loading);

		//获得控件
		WebView webView = (WebView) findViewById(R.id.login_webview);

		//清空cookie
		CookieManager.getInstance().removeAllCookies(null);
		CookieManager.getInstance().flush();


		//访问网页

		webView.loadUrl("https://");

		webView.addJavascriptInterface(new JSInterface(this), "login_js");

		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);

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
			// 链接跳转都会走这个方法
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				view.loadUrl(url);// 强制在当前 WebView 中加载 url

				return true;
			}
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
                progressDialog.show();

			}


			@Override
			public void onPageFinished(WebView view, String url) {
				String loadjs0 = "if(document.getElementById('loginname'))document.getElementById('loginname').type='number';";
				loadjs0 += "if(document.getElementById('Code'))document.getElementById('Code').type='number';";
				loadjs0 += "window.login_js.showResult(document.body.innerHTML);";
				view.loadUrl("javascript:"+loadjs0+"");
				if ( url.equals("https://")) {
					//Toast.makeText(UserLoginActivity.this, "网页加载完成", 0).show();
					view.loadUrl("javascript:window.login_js.showResult(document.body.innerHTML);");
					loginResult = "SUCCESS";
					intent.putExtra("LoginResult", loginResult);
                    setResult(RESULT_OK,intent);

                    finish();
				}
				else if(url.contains("/get_login.php")) {
                    super.onPageFinished(view, url);
				    //验证滑块剧中
                    String loadjs = "(function(){if(document.getElementById('slide_wrapper')) document.getElementById('slide_wrapper').style.marginLeft=0;";
                    //记住登录信息
                    loadjs += "var phone = window.localStorage.getItem('phone'); var loginname = window.localStorage.getItem('loginname');var password = window.localStorage.getItem('password');";
                    loadjs += "if(!phone) phone = '';if(!loginname) loginname = '';if(!password) password = '';";
                    loadjs += "if(document.getElementById('login_btn')) document.getElementById('login_btn').onclick=function(){";
                    loadjs += "    if(document.getElementById('phone')) window.localStorage.setItem('phone',document.getElementById('phone').value);";
                    loadjs += "    if(document.getElementById('loginname')) window.localStorage.setItem('loginname',document.getElementById('loginname').value);";
                    loadjs += "    if(document.getElementById('password')) window.localStorage.setItem('password',document.getElementById('password').value);";
                    loadjs += "    submitByType('login');return false;";
                    loadjs += "};";

                    if (url.contains("loginway=1")) {//验证码登录
                        loadjs += "document.getElementById('phone').value = phone;";
					} else if (url.contains("by=phone")) {//手机 密码
                        loadjs += "document.getElementById('loginname').value = phone;document.getElementById('password').value = password;";
					} else {//帐号登录
                        loadjs += "document.getElementById('loginname').value = loginname;document.getElementById('password').value = password;";
					}
                    loadjs += "})();";
					//view.loadUrl("javascript:window.login_js.fetchPost(document.body.innerHTML);");
                    view.loadUrl("javascript:"+loadjs+"");
				}

                progressDialog.cancel();
			}
		});

	}
	
	


	@Override
	public void onClick(View v){


		if(v == backbtn){
			intent.putExtra("LoginResult", loginResult);
			setResult(RESULT_OK,intent);
			finish();
		}

	}


	public Map<String, String> getLoginData(){
		SharedPreferences logindata = getSharedPreferences("logindata", 0);
		Map<String, String> pmap = new HashMap<String, String>();
		if(logindata!=null) {
			pmap.put("loginstatus", logindata.getString("loginstatus", ""));
			pmap.put("sessid", logindata.getString("sessid", ""));
			pmap.put("accountid", logindata.getString("accountid", ""));
			pmap.put("usertoken", logindata.getString("usertoken", ""));
			pmap.put("username", logindata.getString("username", ""));
			pmap.put("uid", logindata.getString("uid", ""));
			pmap.put("mpstatus", logindata.getString("mpstatus", ""));
			pmap.put("checkmobile", logindata.getString("checkmobile", ""));
			pmap.put("mobile", logindata.getString("mobile", ""));
			pmap.put("email", logindata.getString("email", ""));
			pmap.put("applogintime", String.valueOf(logindata.getLong("applogintime", 0)));
		}
		return pmap;
	}

	private void writeLoginData(Map<String, String> paramsMap){
		SharedPreferences logindata = getSharedPreferences("logindata", 0);
		SharedPreferences.Editor edit = logindata.edit();
		long applogintime = System.currentTimeMillis()/1000;

		edit.putString("username", paramsMap.get("username"));
		edit.putString("sessid", paramsMap.get("sessid"));
		edit.putString("usertoken", paramsMap.get("usertoken"));
		edit.putString("uid", paramsMap.get("uid"));
		edit.putString("accountid", paramsMap.get("accountid"));
		edit.putString("loginstatus", paramsMap.get("loginstatus"));
		edit.putString("mpstatus", paramsMap.get("mpstatus"));
		edit.putString("checkmobile", paramsMap.get("checkmobile"));



		edit.putLong("applogintime", applogintime);

		if(passwd!="")edit.putString("pw", passwd);
		if(mobile!="")edit.putString("mobile", mobile);
		if(email!="")edit.putString("email", email);

		edit.commit();
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
				intent.setClass(UserLoginActivity.this, MainChooseActivity.class);
 				startActivity(intent);
				break;
			case R.id.jobcollect:
				intent = new Intent();
				intent.setClass(UserLoginActivity.this, JobCollectActivity.class);
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
		@JavascriptInterface
		public void showResult(String data) {

			Map<String, String> pmap = new HashMap<String, String>();
			String sarray1[] = data.split("\\<script\\>");
			if(sarray1.length>1){
				String sarray2[] = sarray1[1].split("\';");
				if(sarray2.length>=10) {

					String loginstatus="";String sessid="";String accountid="";String usertoken="";
					String username="";String uid="";String mpstatus="";String checkmobile="";
					String themobile="";String theemail="";String pw="";
					for(int i = 0; i < sarray2.length; i++){
						if(sarray2[i].contains("loginstatus=")) {
							String tmp1[] = sarray2[i].split("=\'");
							//System.out.println(tmp1[1]);
							pmap.put("loginstatus",tmp1[1]);
						}
						else if(sarray2[i].contains("sessid=")) {
							String tmp2[] = sarray2[i].split("=\'");
							pmap.put("sessid",tmp2[1]);
						}
						else if(sarray2[i].contains("accountid=")) {
							String tmp3[] = sarray2[i].split("=\'");
							pmap.put("accountid",(String)tmp3[1]);
						}
						if(sarray2[i].contains("usertoken=")) {
							String tmp4[] = sarray2[i].split("=\'");
							pmap.put("usertoken",(String)tmp4[1]);
						}
						else if(sarray2[i].contains("username=")) {
							String tmp5[] = sarray2[i].split("=\'");
							pmap.put("username",(String)tmp5[1]);
						}
						else if(sarray2[i].contains("uid=")) {
							String tmp6[] = sarray2[i].split("=\'");
							pmap.put("uid",(String)tmp6[1]);
						}
						else if(sarray2[i].contains("mpstatus")) {
							String tmp7[] = sarray2[i].split("=\'");
							pmap.put("mpstatus",(String)tmp7[1]);
						}
						else if(sarray2[i].contains("checkmobile=")) {
							String tmp8[] = sarray2[i].split("=\'");
							pmap.put("checkmobile",(String)tmp8[1]);
						}
						if(emailTest!=null && emailTest.length()>0) email = emailTest;
						if(mobileTest!=null && mobileTest.length()>0) mobile = mobileTest;
						if(passwdTest!=null && passwdTest.length()>0) passwd = passwdTest;

					}

					writeLoginData(pmap);

				}

			}


		}



		/** Show a toast from the web page */
		@JavascriptInterface
		public void showToast(String toast) {
			Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
		}
	}

}






