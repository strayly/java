package com.xiaozhao.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnNetWorkErrorListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.xiaozhao.R;
import com.xiaozhao.adapter.DalibaoAdapter;
import com.xiaozhao.bean.DalibaoBean;
import com.xiaozhao.datahandler.DalibaoHandler;
import com.xiaozhao.util.ErrorViewManager;
import com.xiaozhao.util.MyApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DalibaoActivity extends FragmentActivity implements OnClickListener,OnItemClickListener{

	private static final String TAG = "DalibaoActivity";

	private InputMethodManager imm;	

	private Button backbtn;
	
	
	
	private int curpage = 1;
	private int total = 0,pernum = 20,maxpages = 0;



	private LRecyclerView mRecyclerView = null;
	private LRecyclerViewAdapter mLRecyclerViewAdapter;
	private List<DalibaoBean> allDataList = new ArrayList<DalibaoBean>();
	private DalibaoAdapter myAdapter;
	private Context mContext = DalibaoActivity.this;

	private int refreshStatus = 0;


	private ErrorViewManager errorView;
	private int resultCode = 0;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);



		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.dalibaolist);

		LinearLayout mainLayout = findViewById(R.id.mainLayout);
		errorView = ErrorViewManager.getInstance().init(mainLayout);
		errorView.setOnRetryChildClickListener(onRetryClickListener);

		//promptDialog.getDefaultBuilder().touchAble(false).round(3).loadingDuration(300);

		//软键盘关闭
		imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);  
		//clearSearchFocus();

		backbtn = (Button) findViewById(R.id.back_btn);
		backbtn.setOnClickListener(this);


		mRecyclerView = (LRecyclerView) findViewById(R.id.listview);
		mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

		//请求数据
		dataHandler(curpage);

	}
	private ErrorViewManager.OnLoadingAndRetryListener onRetryClickListener = new ErrorViewManager.OnLoadingAndRetryListener(){
		public void onClick() {
			dataHandler(curpage);
		}
	};
	private void loadAdapter() {
		myAdapter.setOnDelListener(new DalibaoAdapter.onSwipeListener() {
			@Override
			public void onItemClick(View v,int pos) {//
				//TLog.error("onTop pos = " + pos);
				Intent intent = new Intent();
				DalibaoBean jsinfo = getItemInfo(pos);
				int dlbid = jsinfo.getDlbid();

				//JobInfo jobinfo = new JobInfo();
				//System.out.println("ddddd: "+linkid+" ");
				if(dlbid>0){
					intent.putExtra("dlbid", dlbid);
					intent.setClass(mContext, DalibaoDetailActivity.class);
					mContext.startActivity(intent);
				}

			}
		});
		mRecyclerView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				resetdata();
				refreshStatus = 1;
				dataHandler(1);
				refreshStatus = 0;
			}
		});
		mRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				curpage = curpage + 1;
				if (curpage <= maxpages) {
					// loading data
					dataHandler(curpage);
				} else {
					mRecyclerView.setNoMore(true);
				}
			}
		});
		mRecyclerView.setOnNetWorkErrorListener(new OnNetWorkErrorListener() {
			@Override
			public void reload() {
				// loading data
				if (curpage <= maxpages) dataHandler(curpage);
			}
		});

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,long arg3){

	}
	/**
	 * View内的按钮事件
	 */
	@Override
	public void onClick(View v){
		//keyword = search.getText()==null ? "" : search.getText().toString();

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		if(v ==backbtn){
			finish();
		}
	}
 

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	     //按下键盘上返回按钮
	    if(keyCode == KeyEvent.KEYCODE_BACK){
	    	//mainActivityFinish();
	    	this.finish();
	    	 return true;
	     }else{
	    	 return super.onKeyDown(keyCode, event);
	     }
	}
 


	/**
	 * 开启一条线程来执行从网络获取的数据
	 */
	private void dataHandler(final int page){
		curpage = page;
		if(page<=1 && refreshStatus==0)errorView.showLoading();//Toast.makeText(this, "没有您要查找的宣讲会信息", 1).show();
		final Handler mHandler = new Handler(){
			// 处理来自线程的消息,并将线程中的数据设置入listview
			@Override
			public void handleMessage(Message msg){
				if (msg.what == 1){
					if(resultCode==200) {
						if (page <= 1 && refreshStatus == 0) errorView.showContent();

						if (page <= 1) {
							myAdapter = new DalibaoAdapter(mContext, allDataList);
							mLRecyclerViewAdapter = new LRecyclerViewAdapter(myAdapter);
							mRecyclerView.setAdapter(mLRecyclerViewAdapter);
						} else {
							//myAdapter.setDataList(dataList);
							myAdapter.addNewData(allDataList);
						}
						mLRecyclerViewAdapter.notifyDataSetChanged();
						mRecyclerView.refreshComplete(pernum);
						loadAdapter();
					}
					else{
						errorView.showRetry();
					}
				}
			}
		};

		new Thread(){
			public void run(){
				// 子线程的循环标志位
				Looper.prepare();
				try{
					Thread.sleep(10);
				} catch (InterruptedException e){
					e.printStackTrace();
				}
				try{
					dataConnect(page);
				} catch (Exception e){
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
	
	

	private void dataConnect(int page) {

		Map<String, String> map = new HashMap<String, String>();
		map.put("page", String.valueOf(page));
		DalibaoHandler dalibaoHandler = new DalibaoHandler();
		dalibaoHandler.handlerListData(map);
		resultCode = dalibaoHandler.getResultCode();
		if(resultCode==200){
			//if(total==0)Toast.makeText(XjhActivity.this, "该条件下没有任何宣讲会信息！", 5).show();
			total = dalibaoHandler.getTotal();
			setTotal(total);
			setDataList(dalibaoHandler.getDalibaoList());
			setMaxpages((int) Math.ceil((double)total/(double)pernum));
		}
	}

	/**
	 * 当停止的时候保存数据
	 */
	@Override
	protected void onStop(){
		super.onStop();
	}

	/**
	 * 当销毁的时候保存数据入Preferences
	 */
	@Override
	protected void onDestroy(){
		super.onDestroy();
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
	
	
	public void setMaxpages(int maxpages){
		this.maxpages= maxpages;
	}
	public void setTotal(int total){
		this.total= total;
	}

	public void setDataList(List<DalibaoBean> jlist){
		allDataList.addAll(jlist);
	}
	private DalibaoBean getItemInfo(int pos){
		DalibaoBean sinfo = new DalibaoBean();
		//System.out.println("ddddd: "+pos+" cccccccccccc "+xjhdata.size());
		if(pos>=0){
			sinfo = allDataList.get(pos);
		}
		return sinfo;
	}
	public void resetdata(){
		this.allDataList = new ArrayList<DalibaoBean>();
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
				intent.setClass(this, MainChooseActivity.class);
 				startActivity(intent);
				break;
			case R.id.jobcollect:
				intent = new Intent();
				intent.setClass(this, JobCollectActivity.class);
				startActivity(intent);
				break;
			case R.id.eixt:
				//mainActivityFinish();
				break;
		}
		return true;
	}
}