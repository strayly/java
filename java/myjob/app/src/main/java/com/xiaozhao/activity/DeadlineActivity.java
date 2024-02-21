package com.xiaozhao.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnNetWorkErrorListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.xiaozhao.R;
import com.xiaozhao.adapter.DeadlineAdapter;
import com.xiaozhao.bean.DeadlineBean;
import com.xiaozhao.datahandler.DeadlineHandler;
import com.xiaozhao.util.ErrorViewManager;
import com.xiaozhao.util.MyApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeadlineActivity extends FragmentActivity implements OnClickListener,OnItemClickListener{

	private static final String TAG = "DeadlineActivity";
	private InputMethodManager imm;

	private ImageButton searchbtn; // 顶部导航搜索按钮
	private TextView btnBottomRecommend, btnBottomJoblist, btnBottomXjh, btnBottomDeadline, btnBottomMore; // 底部导航
	
	private int curpage = 1;
	private int total = 0,pernum = 20,maxpages = 0;

	private LRecyclerView mRecyclerView = null;
	private LRecyclerViewAdapter mLRecyclerViewAdapter;
	private List<DeadlineBean> allDataList = new ArrayList<DeadlineBean>();
	private DeadlineAdapter myAdapter;
	private Context mContext = DeadlineActivity.this;

	private int refreshStatus = 0;


	private ErrorViewManager errorView;
	private int resultCode = 0;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);

		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏

		setContentView(R.layout.deadlinelist);


		LinearLayout mainLayout = findViewById(R.id.mainLayout);
		errorView = ErrorViewManager.getInstance().init(mainLayout);
		errorView.setOnRetryChildClickListener(onRetryClickListener);
		//promptDialog.getDefaultBuilder().touchAble(false).round(3).loadingDuration(300);

		//软键盘关闭
		imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);  
		//clearSearchFocus();

		searchbtn = (ImageButton) findViewById(R.id.search_button);
		searchbtn.setOnClickListener(this);

		btnBottomRecommend = (TextView) findViewById(R.id.btn_bottom_recommend);
		btnBottomRecommend.setOnClickListener(this);

		btnBottomJoblist = (TextView) findViewById(R.id.btn_bottom_joblist);
		btnBottomJoblist.setOnClickListener(this);

		btnBottomXjh = (TextView) findViewById(R.id.btn_bottom_xjh);
		btnBottomXjh.setOnClickListener(this);

		btnBottomDeadline = (TextView) findViewById(R.id.btn_bottom_deadline);
		btnBottomDeadline.setOnClickListener(this);
		btnBottomDeadline.setSelected(true);

		btnBottomMore = (TextView) findViewById(R.id.btn_bottom_more);
		btnBottomMore.setOnClickListener(this);

		mRecyclerView = (LRecyclerView) findViewById(R.id.listview);
		mRecyclerView.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.VERTICAL, R.drawable.view_decoration));
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

		//请求数据
		dataHandler(curpage);

	}

	private void loadAdapter() {
		myAdapter.setOnDelListener(new DeadlineAdapter.onSwipeListener() {
			@Override
			public void onItemClick(View v,int pos) {//
				//TLog.error("onTop pos = " + pos);
				Intent intent = new Intent();
				DeadlineBean jsinfo = getItemInfo(pos);
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
	private ErrorViewManager.OnLoadingAndRetryListener onRetryClickListener = new ErrorViewManager.OnLoadingAndRetryListener(){
		public void onClick() {
			dataHandler(curpage);
		}
	};


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
		if (v == btnBottomMore) {
			Intent intent = new Intent();
			intent.setClass(this, MyActivity.class);
			// intent.setClass(this, MainChooseActivity.class);
			startActivity(intent);
		} else if (v == btnBottomRecommend) {
			Intent intent = new Intent();
			intent.setClass(this, TjJobActivity.class);
			startActivity(intent);
		} else if (v == btnBottomJoblist) {
			Intent intent = new Intent();
			intent.setClass(this, JobListActivity.class);
			startActivity(intent);
		} else if (v == btnBottomXjh) {
			Intent intent = new Intent();
			intent.setClass(this, XjhActivity.class);
			startActivity(intent);
		} else if (v == btnBottomDeadline) {
			resetdata();
			dataHandler(1);
		} else if (v == searchbtn) {
			Intent intent = new Intent();
			intent.setClass(this, JobSearchActivity.class);
			startActivity(intent);
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
							myAdapter = new DeadlineAdapter(mContext, allDataList);
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
		DeadlineHandler deadlineHandler = new DeadlineHandler();
		deadlineHandler.handlerListData(map);
		resultCode = deadlineHandler.getResultCode();
		if(resultCode==200){
			total = deadlineHandler.getTotal();
			//if(total==0)Toast.makeText(XjhActivity.this, "该条件下没有任何宣讲会信息！", 5).show();
			setTotal(total);
			setDataList(deadlineHandler.getDeadlineList());
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

	public void setDataList(List<DeadlineBean> jlist){
		allDataList.addAll(jlist);
	}

	private DeadlineBean getItemInfo(int pos){
		DeadlineBean sinfo = new DeadlineBean();
		//System.out.println("ddddd: "+pos+" cccccccccccc "+xjhdata.size());
		if(pos>=0){
			sinfo = allDataList.get(pos);
		}
		return sinfo;
	}
	public void resetdata(){
		this.allDataList = new ArrayList<DeadlineBean>();
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
				intent.setClass(this, MyActivity.class);
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