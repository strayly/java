package com.xiaozhao.activity;

import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.xiaozhao.R;
import com.xiaozhao.adapter.JobListAdapter;
import com.xiaozhao.bean.JobItemInfoBean;
import com.xiaozhao.datahandler.JobListHandler;

import com.xiaozhao.util.ErrorViewManager;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.leefeng.promptlibrary.PromptDialog;


public class JobListFragment extends Fragment{

	private static final String TAG = "JobListFragment";

	private int currentPage = 1;
	private int currentJobterm = 0;
	private String currentCity = null;

	private int total = 0,pernum = 20,maxpages = 0;


	private LRecyclerView mRecyclerView = null;
	private LRecyclerViewAdapter mLRecyclerViewAdapter;
	private List<JobItemInfoBean> allDataList = new ArrayList<JobItemInfoBean>();
	private JobListAdapter myAdapter;
	//private Context mContext = JobListActivity.this;
	private Context mContext; //= JobListActivity.this;
	private int refreshStatus = 0;
	private PromptDialog promptDialog;
	private Context viewContext;
	private View fragmentView;
	protected boolean isVisible;
	private boolean mHasLoadedOnce;
	private boolean isPrepared;
	private int resultCode;
	private ErrorViewManager errorView;
	private boolean fromCache = true;
	public static JobListFragment newInstance(String city,int page,int jobterm) {
		JobListFragment fragment = new JobListFragment();
		Bundle bundle = new Bundle();
		bundle.putString("city", city);
		bundle.putInt("page", page);
		bundle.putInt("jobterm", jobterm);
		fragment.setArguments(bundle);
		return fragment;
	}
	public void updateArguments(String city,int page,int jobterm) {
		this.currentCity = city;
		this.currentPage = page;
		this.currentJobterm = jobterm;
		Bundle bundleArgs = getArguments();
		if (bundleArgs != null) {
			bundleArgs.putString("city", city);
			bundleArgs.putInt("page", page);
			bundleArgs.putInt("jobterm", jobterm);
		}

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = this.getActivity();
		if (fragmentView == null) {
			fragmentView = inflater.inflate(R.layout.joblist_fragment, null);
			//fragmentView = inflater.inflate(R.layout.joblist_fragment, container, false);
			mRecyclerView = (LRecyclerView) fragmentView.findViewById(R.id.listview);
			//emptyLayout = (EmptyLayout) fragmentView.findViewById(R.id.emptyLayout);
			viewContext = fragmentView.getContext();
			//promptDialog = new PromptDialog(this.getActivity());
		}
		return fragmentView;
		
	}

	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Bundle args = getArguments();
		//if (args != null) 	this.category = args.getString("category");
	}
	private ErrorViewManager.OnLoadingAndRetryListener onRetryClickListener = new ErrorViewManager.OnLoadingAndRetryListener(){
		public void onClick() {
			dataHandler();
		}
	};
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mRecyclerView.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayoutManager.VERTICAL, R.drawable.view_decoration));
		mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

		//promptDialog = new PromptDialog(this.getActivity());
		errorView = ErrorViewManager.getInstance().init((ViewGroup)fragmentView);
		errorView.setOnRetryChildClickListener(onRetryClickListener);
		Bundle bundle = getArguments();
		if(null != bundle){
			currentCity = bundle.getString("city");
			currentPage = bundle.getInt("page");
			currentJobterm = bundle.getInt("jobterm");
		}
		if(currentCity==null||currentCity.equals(""))currentCity="全国";
		if(currentJobterm!=1)currentJobterm=0;

		myAdapter = new JobListAdapter(this.getActivity(), allDataList);
		mLRecyclerViewAdapter = new LRecyclerViewAdapter(myAdapter);
		mRecyclerView.setAdapter(mLRecyclerViewAdapter);
		//请求数据
		dataHandler();

		//数据请求失败的处理
		/*
		emptyLayout.setErrorButtonClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dataHandler();
			}
		});

		*/

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

	private  OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(View v,int pos) {//
			//TLog.error("onTop pos = " + pos);
			Intent intent = new Intent();
			JobItemInfoBean jsinfo = null;
			jsinfo = allDataList.get(pos);
			int linkid = jsinfo.getLinkid();
			String linktype = jsinfo.getLinktype();
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

	};
	private void loadAdapter() {
		mContext = this.getActivity();
		mLRecyclerViewAdapter.setOnItemClickListener(onItemClickListener);

		mRecyclerView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				resetdata();
				refreshStatus = 1;
				currentPage = 1;
				fromCache = false;
				dataHandler();
				refreshStatus = 0;
			}
		});
		mRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				currentPage = currentPage + 1;
				if (currentPage <= maxpages) {
					// loading data
					dataHandler();
				} else {
					mRecyclerView.setNoMore(true);
				}
			}
		});


	}
	/**
	 * 开启一条线程来执行从网络获取的数据
	 */
	private void dataHandler(){

		JobItemInfoBean jobitemInfoBean = null;
		JSONArray jsonArray;

		///*
		//mContext = this.getActivity();
		if(currentPage<=1 && refreshStatus==0){
			//promptDialog.showLoading("加载中...");//Toast.makeText(this, "没有您要查找的宣讲会信息", 1).show();
			//emptyLayout.showLoading();
			errorView.showLoading();
		}
		final Handler mHandler = new Handler(){
			// 处理来自线程的消息,并将线程中的数据设置入listview
			@Override
			public void handleMessage(Message msg){
				if (msg.what == 1){
					if(resultCode==200) {
						if (currentPage <= 1 && refreshStatus == 0){
							//emptyLayout.hide();
							errorView.showContent();
						}

						myAdapter.addNewData(allDataList);
						if(currentPage==1)mRecyclerView.scrollToPosition(0);
						mLRecyclerViewAdapter.notifyDataSetChanged();
						mRecyclerView.refreshComplete(pernum);
						loadAdapter();
						mHasLoadedOnce = true;
					}
					else{
						//
						//emptyLayout.showError();
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
					Thread.sleep(20);
				} catch (InterruptedException e){
					e.printStackTrace();
				}
				try{
					dataConnect();
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
		//*/
	}



	private void dataConnect() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("locationname", currentCity);

		map.put("page", String.valueOf(currentPage));
		map.put("jobterm", currentJobterm==1 ? "parttime" : "fulltime");//全职招聘
		map.put("pernum", String.valueOf(pernum));
		JobListHandler jobListHandler = new JobListHandler(mContext);
		jobListHandler.handlerData(map,fromCache);

		resultCode = jobListHandler.getResultCode();
		if(resultCode==200){
			int total = jobListHandler.getTotal();
			this.total = jobListHandler.getTotal();
			allDataList.addAll(jobListHandler.getListData());
			this.maxpages = (int) Math.ceil((double)total/(double)pernum);
		}
	}

	public void resetdata(){
		this.allDataList = new ArrayList<JobItemInfoBean>();
	}
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (fragmentView != null) {
			((ViewGroup) fragmentView.getParent()).removeView(fragmentView);
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);

		if(getUserVisibleHint()) {
			isVisible = true;
			onVisible();
		} else {
			isVisible = false;
			onInvisible();
		}
	}
	//  可见
	public void onVisible() {
		//lazyLoad();
	}
	// 不可见
	public void onInvisible() {

	}
	//@Override
	public void lazyLoad() {
		if (!isPrepared || !isVisible || mHasLoadedOnce){
			//return;
		}
		dataHandler();
		//mHasLoadedOnce = true;
	}

}