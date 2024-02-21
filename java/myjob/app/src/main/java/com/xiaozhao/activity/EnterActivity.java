package com.xiaozhao.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Address;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.xiaozhao.R;
import com.xiaozhao.datahandler.DictDataHandler;
import com.xiaozhao.datahandler.VersionHandler;
import com.xiaozhao.util.LocationUtils;
import com.xiaozhao.util.MyApplication;
import com.xiaozhao.util.PermissionListener;
import com.xiaozhao.util.PermissionsUtil;
import com.xiaozhao.util.UpdateManager;

public class EnterActivity extends AppCompatActivity implements OnClickListener {


	//private String url;

	private Context mContext = EnterActivity.this;


	//申请的权限
	private static final String[] mPermissions = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.INTERNET};


	private static final int PERMISSION_REQUEST_CODE = 64;
	private boolean isRequireCheck;

	private String[] permission;
	private String key;
	private boolean showTip;
	private PermissionsUtil.TipInfo tipInfo;

	private final String defaultTitle = "帮助";
	private final String defaultContent = "当前应用缺少必要权限。\n \n 请点击 \"设置\"-\"权限\"-打开所需权限。";
	private final String defaultCancel = "取消";
	private final String defaultEnsure = "设置";


	private final int SPLASH_DISPLAY_LENGHT = 3000;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enter);
		MyApplication.getInstance().addActivity(this);


		ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);        
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();    
        if (networkinfo == null || !networkinfo.isAvailable()) {
        	
        	Toast.makeText(this, "网络连接失败，请检查您的手机网络", Toast.LENGTH_LONG).show();

        	//AlertDialog.Builder netBuilder = new AlertDialog.Builder(YjsActivity.this);
        	//netBuilder.setTitle("网络错误");

        	//netBuilder.setMessage("不能连接网络，请检查网络连接再重新启动该应用！");
        	//netBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
	        	//public void onClick(DialogInterface dialoginterface, int which){
	        		//Toast.makeText(YjsActivity.this, "你选择了确定", Toast.LENGTH_LONG).show();
	        		//startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));//进入无线网络配置界面
	        		//YjsActivity.this.finish();//关闭activity
	        	//}
	        //});
        	/*
        	netBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener(){
	        	public void onClick(DialogInterface dialoginterface, int which){
	        		//Toast.makeText(YjsActivity.this, "你选择了取消", Toast.LENGTH_LONG).show();
	        	}
	        });
	        */
        	//netBuilder.create().show();

			Toast.makeText(this, "网络连接失败，请检查您的手机网络", Toast.LENGTH_LONG).show();
			enterMain();

			return;
		} else {
			//Intent intent = new Intent();
			//intent.setClass(this, MainChooseActivity.class);
			//intent.putExtra("LastAct", "Enter");//
			//startActivity(intent);
        	//fi
        	// 处理来自线程的消息,并将线程中的数据设置入listview
			final Handler mHandler = new Handler(){
				// 处理来自线程的消息,并将线程中的数据设置入listview
				@Override
				public void handleMessage(Message msg) {
					if (msg.what == 1) {
						//加载数据字典

					}
				}
			};

			new Thread(){
				public void run(){

					// 子线程的循环标志位
					Looper.prepare();
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					try {
						initapp();
						DictDataHandler dictDataHandler = new DictDataHandler(mContext);
						dictDataHandler.getXjhLocation(false);
						dictDataHandler.getJobLocation(false);
					} catch (Exception e) {
						//Log.e(TAG, e.toString());
					}

					// 给handle发送的消息
					Message m = new Message();
					m.what = 1;
					mHandler.sendMessage(m);
					Looper.loop();
				}

				;
			}.start();
		}
	}
	public boolean isAccessUpdate(){
		return true;

	}
	private void initapp() {

		//检测版本
		//final int isenter = 1;
		if (!isAccessUpdate()) {
			enterMain();
		} else {
			VersionHandler versionHandler = new VersionHandler();
			versionHandler.handlerData();
			int resultCode = versionHandler.getResultCode();
			String newVersion = "";
			String downloadUrl = "";
			if (resultCode == 200) {
				newVersion = versionHandler.getNewVersion();
				downloadUrl = versionHandler.getDownloadUrl();
			}
			String appVersion;
			PackageManager manager = EnterActivity.this.getPackageManager();
			PackageInfo info;
			try {
				info = manager.getPackageInfo(EnterActivity.this.getPackageName(), 0);
				appVersion = info.versionName;   //版本名
				if (appVersion != null && newVersion != null && newVersion.length() > 0 && newVersion.equals(appVersion) == false) {
					final UpdateManager mUpdateManager = new UpdateManager(EnterActivity.this);
					mUpdateManager.setDownloadUrl(downloadUrl);
					AlertDialog.Builder dialog = new Builder(EnterActivity.this);
					dialog.setTitle("软件版本更新");
					dialog.setMessage("有新版本" + newVersion + "更新是否下载，目前版本" + appVersion + "");
					dialog.setPositiveButton("现在下载", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mUpdateManager.showDownloadDialog();
						}
					});
					dialog.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							enterMain();
						}
					});
					dialog.show();
				} else {
					SharedPreferences jobinfo = getSharedPreferences("job", 0);
					String selectCity = jobinfo.getString("city", "");
					//如果没有选择过城市，去定位

					if(selectCity!=null && selectCity.length()>0) {
						enterMain();
					}
					else {
						requestPermissions();
					}
					//
				}

			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void enterMain() {


		Intent intent = new Intent();
		intent.setClass(EnterActivity.this, TjJobActivity.class);
		startActivity(intent);

	}


	@Override
	public void onClick(View v){		

	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	     //按下键盘上返回按钮
	    if(keyCode == KeyEvent.KEYCODE_BACK){
			MyApplication.getInstance().exit();
	    	return true;
	     }else{
	    	 return super.onKeyDown(keyCode, event);
	     }
	}
	/**
	 * 首页退出Activity
	 */
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

	/**
	 * 请求权限
	 */
	private void requestPermissions() {

		if (PermissionsUtil.hasPermission(this,mPermissions)) {

			//有访问权限
			initLocation();
			enterMain();
		} else {
			PermissionsUtil.requestPermission(this, new PermissionListener() {
				@Override
				public void permissionGranted(@NonNull String[] permissions) {
					//用户授予了访问权限

					initLocation();
				}
				@Override
				public void permissionDenied(@NonNull String[] permissions) {
					//用户拒绝了访问的申请

				}
			}, mPermissions);
		}
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

		//部分厂商手机系统返回授权成功时，厂商可以拒绝权限，所以要用PermissionChecker二次判断
		if (requestCode == PERMISSION_REQUEST_CODE && PermissionsUtil.isGranted(grantResults)
				&& PermissionsUtil.hasPermission(this, permissions)) {
			permissionsGranted();

		}  else { //不需要提示用户
			permissionsDenied();
		}
	}
	private void permissionsDenied() {

		enterMain();
	}

	// 全部权限均已获取
	private void permissionsGranted() {

		initLocation();
		enterMain();
	}
	/**
	 * 加载位置
	 */
	private void initLocation() {
		LocationUtils.getInstance(this).setAddressCallback(new LocationUtils.AddressCallback() {
			@Override
			public void onGetAddress(Address address) {
				String countryName = address.getCountryName();//国家
				String adminArea = address.getAdminArea();//省
				String locality = address.getLocality();//市
				String subLocality = address.getSubLocality();//区
				String featureName = address.getFeatureName();//街道

			}

			@Override
			public void onGetLocation(double lat, double lng) {

			}
		});
	}




}