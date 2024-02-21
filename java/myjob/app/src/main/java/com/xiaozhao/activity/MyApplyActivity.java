package com.xiaozhao.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnNetWorkErrorListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.xiaozhao.R;
import com.xiaozhao.adapter.JobListAdapter;
import com.xiaozhao.bean.JobItemInfoBean;
import com.xiaozhao.datahandler.ApplyJobHandler;
import com.xiaozhao.util.ErrorViewManager;
import com.xiaozhao.util.MyApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




public class MyApplyActivity extends Activity implements OnClickListener,OnItemClickListener{

	private static final String TAG = "MyApplyActivity";
	private TextView toolbar;
	private ImageButton backbtn, homebtn,outbtn,collectxjhbtn,collectjobbtn;
	private int curpage = 1;
	private int total = 0,pernum = 20,maxpages = 0;
	private ProgressDialog Dialog;

	private String sessid = "";
	private String accountid = "";

	private final static int REQ_CODE = 1111;


	private LRecyclerView mRecyclerView = null;
	private List<JobItemInfoBean> allDataList = new ArrayList<JobItemInfoBean>();
	private JobListAdapter myAdapter;
	private Context mContext = MyApplyActivity.this;
	private ItemTouchHelper mItemTouchHelper;
	private LRecyclerViewAdapter mLRecyclerViewAdapter;
	private int refreshStatus = 0;
	private ErrorViewManager errorView;
	private int resultCode = 0;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.myapply);

		LinearLayout mainLayout = findViewById(R.id.mainLayout);
		errorView = ErrorViewManager.getInstance().init(mainLayout);
		errorView.setOnRetryChildClickListener(onRetryClickListener);


		SharedPreferences logindata = getSharedPreferences("logindata", 0);
		sessid = logindata.getString("sessid", "");
		accountid = logindata.getString("accountid", "");
		//sessid = null;

		//sessid ="2v3a3puo7ju4dcfnd90l46elpn0oe5oi";

		backbtn = (ImageButton) findViewById(R.id.back_btn);
		backbtn.setOnClickListener(this);

		TextView toolbarTitle = (TextView) findViewById(R.id.collect_job_toolbar);
		toolbarTitle.setText("我申请的职位");

		mRecyclerView = (LRecyclerView) findViewById(R.id.listview_collect);
		mRecyclerView.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.VERTICAL, R.drawable.view_decoration));
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		dataHandler(curpage);
		if(sessid==null||sessid.equals("")){//未登录
			Intent intent = new Intent();
			intent.setClass(this, UserLoginActivity.class);
			//startActivity(intent);
			startActivityForResult(intent,REQ_CODE);
		}
		else{
			dataHandler(curpage);
		}

	}
	private ErrorViewManager.OnLoadingAndRetryListener onRetryClickListener = new ErrorViewManager.OnLoadingAndRetryListener(){
		public void onClick() {
			dataHandler(curpage);
		}
	};
	private void loadAdapter() {
		mLRecyclerViewAdapter.setOnItemClickListener(new com.github.jdsjlzx.interfaces.OnItemClickListener() {
			@Override
			public void onItemClick(View v,int pos) {//
				//TLog.error("onTop pos = " + pos);
				Intent intent = new Intent();
				JobItemInfoBean jsinfo = getItemInfo(pos);
				int linkid = jsinfo.getLinkid();
				String linktype = jsinfo.getLinktype();
				//JobInfo jobinfo = new JobInfo();
				//System.out.println("ddddd: "+linkid+" ");
				if(linktype.equals("jsjobid")){
					intent.putExtra("jobid", linkid);
					intent.setClass(mContext, JsJobViewActivity.class);
					mContext.startActivity(intent);
				}
				else if(linktype.equals("zzjobid")){//转载
					intent.putExtra("jobid", linkid);
					intent.setClass(mContext, ZzJobViewActivity.class);
					mContext.startActivity(intent);
				}
				else if(linktype.equals("companyid")){//转载
					intent.putExtra("companyid", linkid);
					intent.setClass(mContext, JsComActivity.class);
					mContext.startActivity(intent);
				}
				else{
					//直接打开网页
					//System.out.println(jsinfo.getJoburl());
					final String url = jsinfo.getLinkurl();
					intent.putExtra("jobid", linkid);
					intent.putExtra("url", url);
					intent.setClass(mContext, OutLinkShowActivity.class);
					mContext.startActivity(intent);
					return;
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

	private JobItemInfoBean getItemInfo(int pos){
		JobItemInfoBean sinfo = new JobItemInfoBean();
		if(pos>=0){
			sinfo = allDataList.get(pos);
		}
		return sinfo;
	}


	@Override
	public void onClick(View v){
		//keyword = search.getText()==null ? "" : search.getText().toString();
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		if(v == homebtn){
			Intent intent = new Intent();
			intent.setClass(this, MainChooseActivity.class);
			startActivity(intent);
		}
		else if(v == collectjobbtn){
			resetdata();
			dataHandler(1);
		}
		else if(v == collectxjhbtn){
			Intent intent = new Intent();
			intent.setClass(this, XjhCollectActivity.class);
			startActivity(intent);
		}
		else if(v == backbtn){

			finish();
			 //confirmExit().show();
		}
		
	}

	// 自定义RecyclerView的分割线
	public class RecyclerViewDivider extends RecyclerView.ItemDecoration {
		private Paint mPaint;
		private Drawable mDivider;
		private int mDividerHeight = 2; //分割线高度
		private int mOrientation; // 列表的方向LinearLayoutManager.VERTICAL / LinearLayoutManager.HORIZONTAL
		private final int[] ATTRS = new int[]{android.R.attr.listDivider};

		/**
		 * 默认分割线
		 * @param mContext
		 * @param orientation
		 */

		public RecyclerViewDivider(Context mContext, int orientation) {
			if (orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL) {
				throw new IllegalArgumentException("请输入正确的参数！");
			}
			mOrientation = orientation;

			final TypedArray a = mContext.obtainStyledAttributes(ATTRS);
			mDivider = a.getDrawable(0);
			a.recycle();
		}

		/**
		 * 自定义分割线
		 * @param mContext
		 * @param orientation
		 * @param drawableId
		 */

		public RecyclerViewDivider(Context mContext, int orientation, int drawableId) {
			this(mContext,orientation);
			mDivider = ContextCompat.getDrawable(mContext, drawableId);
			mDividerHeight = mDivider.getIntrinsicHeight();
		}


		/**
		 * 自定义分割线
		 * @param mContext
		 * @param orientation
		 * @param drawableHeight
		 * @param drawableColor
		 */
		public RecyclerViewDivider(Context mContext, int orientation, int drawableHeight, int drawableColor) {
			this(mContext,orientation);
			mDividerHeight = drawableHeight;
			mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPaint.setColor(drawableColor);
			mPaint.setStyle(Paint.Style.FILL);
		}

		// 获取分割线尺寸
		@Override
		public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
			super.getItemOffsets(outRect, view, parent, state);
			int childCount = parent.getAdapter().getItemCount();

			switch (mOrientation) {
				case LinearLayoutManager.VERTICAL:
					childCount -= 1;
					outRect.set(0, 0, 0, mDividerHeight);
					break;

				case LinearLayoutManager.HORIZONTAL:
					childCount -= 1;
					outRect.set(0, 0, mDividerHeight, 0);
					break;
			}
		}

		@Override
		public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
			super.onDraw(c, parent, state);
			if (mOrientation == LinearLayoutManager.VERTICAL) {
				drawVertical(c, parent);
			} else {
				drawHorizontal(c, parent);
			}
		}

		/**
		 *  绘制纵向列表时的分割线
		 *  left相同时，top根据child变化，right相同，bottom也变化
		 * @param canvas
		 * @param parent
		 */
		private void drawVertical(Canvas canvas,  RecyclerView parent) {
			final int left = parent.getPaddingLeft();
			final int right = parent.getMeasuredWidth() - parent.getPaddingRight();
			final int childSize = parent.getChildCount();
			for (int i = 0; i < childSize; i++) {
				final View child = parent.getChildAt(i);
				RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
				final int top = child.getBottom() + layoutParams.bottomMargin;
				final int bottom = top + mDividerHeight;

				if (mDivider != null) {
					mDivider.setBounds(left, top, right, bottom);
					mDivider.draw(canvas);
				}

				if (mPaint != null) {
					canvas.drawRect(left, top, right, bottom, mPaint);
				}
			}
		}

		/**
		 *  绘制横向列表时的分割线
		 *  left/right 变化，top/bottom不变
		 * @param canvas
		 * @param parent
		 */

		private void drawHorizontal(Canvas canvas, RecyclerView parent) {
			final int top = parent.getPaddingTop();
			final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom();
			final int childSize = parent.getChildCount();
			for (int i = 0; i < childSize; i++) {
				final View child = parent.getChildAt(i);
				RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
				final int left = child.getRight() + layoutParams.rightMargin;
				final int right = left + mDividerHeight;

				if (mDivider != null) {
					mDivider.setBounds(left, top, right, bottom);
					mDivider.draw(canvas);
				}
				if (mPaint != null) {
					canvas.drawRect(left, top, right, bottom, mPaint);
				}
			}
		}
	}
	// 自定义RecyclerView的分割线 End


	private void dataHandler(final int page){
		if(page<=1 && refreshStatus==0)errorView.showLoading();//Toast.makeText(this, "没有您要查找的宣讲会信息", 1).show();
		final Handler mHandler = new Handler(){
			// 处理来自线程的消息,并将线程中的数据设置入listview
			@Override
			public void handleMessage(Message msg){
				if (msg.what == 1){
					if(resultCode==401){//需要登录
						errorView.showContent();
						Toast.makeText(MyApplyActivity.this,"您的登录信息已失效，请重新登录！",Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						intent.setClass(mContext, UserLoginActivity.class);
						startActivityForResult(intent,REQ_CODE);
						//startActivity(intent);
						return;
					}
					else if(resultCode==200) {
						if (page <= 1 && refreshStatus == 0) errorView.showContent();

						if (page <= 1) {
							myAdapter = new JobListAdapter(mContext, allDataList);
							mLRecyclerViewAdapter = new LRecyclerViewAdapter(myAdapter);
							mRecyclerView.setAdapter(mLRecyclerViewAdapter);
						} else {
							//myAdapter.setDataList(dataList);
							myAdapter.addNewData(allDataList);
						}
						//if(page>=maxpages)mRecyclerView.setNoMore(true);
						mLRecyclerViewAdapter.notifyDataSetChanged();
						mRecyclerView.refreshComplete(pernum, total);
						//mLRecyclerViewAdapter.addFooterView(new SampleFooter(this));
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
		map.put("sessid", sessid);
		map.put("userid", accountid);
		//map.put("keyword", keyword);//全职招聘
		ApplyJobHandler applyJobHandler = new ApplyJobHandler();
		applyJobHandler.myApplyList(map);
		resultCode = applyJobHandler.getResultCode();
		int total = 0;
		//System.out.println(resultCode);
		if(resultCode==200){
			//if(total==0)Toast.makeText(XjhActivity.this, "该条件下没有任何宣讲会信息！", 5).show();
			total = applyJobHandler.getTotal();
			setTotal(total);
			//System.out.println(jobHandler.getListData());
			setDataList(applyJobHandler.getApplyList());
			setMaxpages((int) Math.ceil((double)total/(double)pernum));
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
						dataHandler(curpage);
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


	
	public void setMaxpages(int maxpages){
		this.maxpages= maxpages;
	}
	public void setTotal(int total){
		this.total= total;
	}

	public void setDataList(List<JobItemInfoBean> jlist){
		allDataList.addAll(jlist);
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

	public void resetdata(){
		this.allDataList = new ArrayList<JobItemInfoBean>();
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
				intent.setClass(MyApplyActivity.this, MainChooseActivity.class);
 				startActivity(intent);
				break;
			case R.id.jobcollect:
				intent = new Intent();
				intent.setClass(MyApplyActivity.this, JobCollectActivity.class);
				startActivity(intent);
				break;
			case R.id.eixt:
				//mainActivityFinish();
				break;
		}
		return true;
	}
}






