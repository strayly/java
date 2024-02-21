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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.xiaozhao.DB.SQLiteDBHelper;
import com.xiaozhao.R;
import com.xiaozhao.adapter.MyHistoryAdapter;
import com.xiaozhao.bean.JobItemInfoBean;
import com.xiaozhao.util.MyApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyHistoryActivity extends Activity implements OnClickListener,OnItemClickListener {

	private static final String TAG = "MyHistoryActivity";
	private TextView toolbar;
	private ImageButton backbtn, homebtn,outbtn,collectxjhbtn,collectjobbtn;
	private ProgressDialog Dialog;
	private List<JobItemInfoBean> jobinfos = new ArrayList<JobItemInfoBean>();

	private SQLiteDBHelper dbHelper;
	private String sessid = "";
	private String accountid = "";

	private LRecyclerView mRecyclerView = null;
	private List<Map<String, String>> jobdata = new ArrayList<Map<String, String>>();
	private MyHistoryAdapter myAdapter;
	private Context mContext = MyHistoryActivity.this;
	private ItemTouchHelper mItemTouchHelper;
	private LRecyclerViewAdapter mLRecyclerViewAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.history);

		setListData();
		initView();




		SharedPreferences logindata = getSharedPreferences("logindata", 0);
		sessid = logindata.getString("sessid", "");
		accountid = logindata.getString("accountid", "");
		//sessid = null;

		backbtn = (ImageButton) findViewById(R.id.back_btn);
		backbtn.setOnClickListener(this);


	}
	private void initView() {
		mRecyclerView = (LRecyclerView) findViewById(R.id.list_history);
		mRecyclerView.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.VERTICAL, R.drawable.view_decoration));
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


		myAdapter = new MyHistoryAdapter( mContext,jobdata);
		mLRecyclerViewAdapter = new LRecyclerViewAdapter(myAdapter);
		mRecyclerView.setAdapter(mLRecyclerViewAdapter);
		mRecyclerView.setLoadMoreEnabled(false);
		mRecyclerView.setPullRefreshEnabled(false);
		/*
		mRecyclerView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				setListData();
				myAdapter.addAll(jobdata);
				mLRecyclerViewAdapter.notifyDataSetChanged();
				mRecyclerView.refreshComplete(jobdata.size());
			}
		});
		 */
		//mRecyclerView.refresh();
		myAdapter.setOnDelListener(new MyHistoryAdapter.onSwipeListener() {
			@Override
			public void onDel(int pos) {
				//Toast.makeText(mContext, "删除:" + pos, Toast.LENGTH_SHORT).show();
				deleteData(Integer.parseInt(jobdata.get(pos).get("linkid")),jobdata.get(pos).get("linktype"));
				jobdata.remove(pos);
				myAdapter.setDataList(jobdata);//.remove(pos);
				myAdapter.notifyItemRemoved(pos);//推荐用这个

				if(pos != (myAdapter.getDataList().size())){ // 如果移除的是最后一个，忽略 注意：这里的mDataAdapter.getDataList()不需要-1，因为上面已经-1了
					myAdapter.notifyItemRangeChanged(pos, myAdapter.getDataList().size() - pos);
				}
				//且如果想让侧滑菜单同时关闭，需要同时调用 ((CstSwipeDelMenu) holder.itemView).quickClose();
			}

			@Override
			public void onTop(int pos) {//置顶功能
				//TLog.error("onTop pos = " + pos);
			}
			@Override
			public void onItemClick(View v,int pos) {//
				//TLog.error("onTop pos = " + pos);
				Intent intent = new Intent();

				JobItemInfoBean jsinfo = getInfo(pos);
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
					final String url = jsinfo.getLinkurl();
					intent.putExtra("jobid", linkid);
					intent.putExtra("url", url);
					intent.setClass(mContext, OutLinkShowActivity.class);
					mContext.startActivity(intent);
					return;
				}
			}
		});
	}
	public void setListData(){
		jobdata = new ArrayList<Map<String, String>>();
		jobinfos = new ArrayList<JobItemInfoBean>();
		SQLiteDBHelper dbHelper = new SQLiteDBHelper(this);
		jobinfos = dbHelper.getListJob(0, 200);

		if (!jobinfos.isEmpty()){
			for(JobItemInfoBean jinfo : jobinfos) {
				String date = jinfo.getDate();
				Map<String, String> map = new HashMap<String, String>();

				map.put("city", "" + jinfo.getCity() + "");
				map.put("title", jinfo.getTitle());
				map.put("date", date);
				map.put("jobterm", jinfo.getJobterm());
				//map.put("source", jinfo.getSource());
				map.put("jsid", String.valueOf(jinfo.getJsid()));
				map.put("zzid", String.valueOf(jinfo.getZzid()));
				map.put("linkid", String.valueOf(jinfo.getLinkid()));
				map.put("linktype", String.valueOf(jinfo.getLinktype()));
				map.put("linkurl", String.valueOf(jinfo.getLinkurl()));
				jobdata.add(map);
			}
		}
	}
	public void deleteData(int linkid,String linktype){
		SQLiteDBHelper dbHelper = new SQLiteDBHelper(this);
		dbHelper.deleteJob(linkid,linktype);
	}

	private JobItemInfoBean getInfo(int pos){
		JobItemInfoBean sinfo = new JobItemInfoBean();
		if(pos>=0){
			Map<String, String> map = jobdata.get(pos);
			sinfo.setLinkid(Integer.parseInt(map.get("linkid")));
			sinfo.setLinktype(map.get("linktype"));
			sinfo.setTitle(map.get("title"));
			//sinfo.setIntro(map.get("intro"));
			sinfo.setCity(map.get("city"));
			sinfo.setDate(map.get("date"));
			sinfo.setJobterm(map.get("jobterm"));
			sinfo.setLinkurl(map.get("linkurl"));
			//sinfo.setSource(map.get("source"));
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
		if(v == backbtn){
			finish();
		}
		else if(v == outbtn){
			mainActivityFinish();
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


	@Override
	public void onItemClick(View v,int pos){

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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//按下键盘上返回按钮
		if(keyCode == KeyEvent.KEYCODE_BACK){

			this.finish();

			return true;
		}else{
			return super.onKeyDown(keyCode, event);
		}
	}


}






