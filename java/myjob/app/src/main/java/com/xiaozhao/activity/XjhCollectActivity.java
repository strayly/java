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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnNetWorkErrorListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.xiaozhao.R;
import com.xiaozhao.adapter.XjhCollectAdapter;
import com.xiaozhao.bean.XjhInfoBean;
import com.xiaozhao.datahandler.XjhCollectHandler;
import com.xiaozhao.util.ErrorViewManager;
import com.xiaozhao.util.MyApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class XjhCollectActivity extends Activity implements OnClickListener,OnItemClickListener{

	private static final String TAG = "XjhCollectActivity";


	private ProgressDialog Dialog;

	private InputMethodManager imm;

	private ImageButton backbtn, homebtn,collectxjhbtn,collectjobbtn,mybtn;
	private Button address_bt,search_bt;

	private int curpage = 1;
	private int total = 0,pernum = 20,maxpages = 0;



    private String sessid = "";
    private String accountid = "";

	private final static int REQ_CODE = 1112;

	private LRecyclerView mRecyclerView = null;
	private List<Map<String, String>> xjhdata = new ArrayList<Map<String, String>>();
	private XjhCollectAdapter myAdapter;
	private Context mContext = XjhCollectActivity.this;
	private LRecyclerViewAdapter mLRecyclerViewAdapter;
	private int refreshStatus = 0;
	private int resultCode = 0;
	private ErrorViewManager errorView;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);

		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.xjhcollect);

		LinearLayout mainLayout = findViewById(R.id.mainLayout);
		errorView = ErrorViewManager.getInstance().init(mainLayout);
		errorView.setOnRetryChildClickListener(onRetryClickListener);


		//软键盘关闭
		imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		//clearSearchFocus();


		backbtn = (ImageButton) findViewById(R.id.back_btn);
		backbtn.setOnClickListener(this);


		mRecyclerView = (LRecyclerView) findViewById(R.id.listview);
		mRecyclerView.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.VERTICAL, R.drawable.view_decoration));
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences logindata = getSharedPreferences("logindata", 0);
        sessid = logindata.getString("sessid", "");
        accountid = logindata.getString("accountid", "");

		if(sessid==null||sessid.equals("")){//未登录
            Intent intent = new Intent();
            intent.setClass(this, UserLoginActivity.class);
            //startActivity(intent);
            startActivityForResult(intent,REQ_CODE);
        }
        else{
            xjhDataHandler(curpage);

        }
	}
	private ErrorViewManager.OnLoadingAndRetryListener onRetryClickListener = new ErrorViewManager.OnLoadingAndRetryListener(){
		public void onClick() {
			xjhDataHandler(curpage);
		}
	};
	private void loadAdapter() {
		myAdapter.setOnDelListener(new XjhCollectAdapter.onSwipeListener() {
			@Override
			public void onDel(int pos) {
				//Toast.makeText(mContext, "删除:" + pos, Toast.LENGTH_SHORT).show();
				String collid = xjhdata.get(pos).get("indexId");
				//取消收藏
				unsubCollectData(Integer.parseInt(collid));
				xjhdata.remove(pos);
				myAdapter.setDataList(xjhdata);//.remove(pos);
				myAdapter.notifyItemRemoved(pos);//推荐用这个

				if (pos != (myAdapter.getDataList().size())) { // 如果移除的是最后一个，忽略 注意：这里的mDataAdapter.getDataList()不需要-1，因为上面已经-1了
					myAdapter.notifyItemRangeChanged(pos, myAdapter.getDataList().size() - pos);
				}
				//且如果想让侧滑菜单同时关闭，需要同时调用 ((CstSwipeDelMenu) holder.itemView).quickClose();
			}

			@Override
			public void onTop(int pos) {//置顶
				//TLog.error("onTop pos = " + pos);
			}
			@Override
			public void onItemClick(View v,int pos) {//
				//TLog.error("onTop pos = " + pos);
				Intent intent = new Intent();
				XjhInfoBean xinfo = getItemInfo(pos);
				int xjhid = xinfo.getId();
				if(xjhid>0){//宣讲会详细页
					intent.putExtra("xjhid", xjhid);
					intent.setClass(mContext, XjhViewActivity.class);
					startActivity(intent);
				}
			}
		});
		mRecyclerView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				resetXjhdata();
				refreshStatus = 1;
				xjhDataHandler(1);
				refreshStatus = 0;
			}
		});
		mRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				curpage = curpage + 1;
				if (curpage <= maxpages) {
					// loading data
					xjhDataHandler(curpage);
				} else {
					mRecyclerView.setNoMore(true);
				}
			}
		});
		mRecyclerView.setOnNetWorkErrorListener(new OnNetWorkErrorListener() {
			@Override
			public void reload() {
				// loading data
				if (curpage <= maxpages) xjhDataHandler(curpage);
			}
		});
	}

	/**
	 * View内的按钮事件
	 */
	@Override
	public void onClick(View v){
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		if(v == homebtn){
			Intent intent = new Intent();
			intent.setClass(this, MainChooseActivity.class);
			startActivity(intent);
		}
		else if(v ==backbtn){
			finish();
		}
		else if(v == collectxjhbtn){
			resetXjhdata();
			xjhDataHandler(1);
		}
		else if(v == collectjobbtn){
			Intent intent = new Intent();
			intent.setClass(this, JobCollectActivity.class);
			startActivity(intent);
		}
		else if(v == mybtn){
			Intent intent = new Intent();
			intent.setClass(this, MyActivity.class);
			startActivity(intent);
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,long arg3){

	}

	/**
	 * 开启一条线程来执行从网络获取的数据
	 */
	private void xjhDataHandler(final int page){
		curpage = page;
		if(page <= 1 && refreshStatus==0)errorView.showLoading();//Toast.makeText(this, "没有您要查找的宣讲会信息", 1).show();
		final Handler mHandler = new Handler(){
			// 处理来自线程的消息,并将线程中的数据设置入listview
			@Override
			public void handleMessage(Message msg){
				if (msg.what == 1){
					if(resultCode==401){//需要登录
						errorView.showContent();
						Toast.makeText(XjhCollectActivity.this,"您的登录信息已失效，请重新登录！",Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						intent.setClass(mContext, UserLoginActivity.class);
						startActivityForResult(intent,REQ_CODE);
						//startActivity(intent);
						return;
					}
					else if(resultCode==200) {
						if (page <= 1 && refreshStatus == 0) errorView.showContent();

						if (page <= 1) {
							myAdapter = new XjhCollectAdapter(mContext, xjhdata);
							mLRecyclerViewAdapter = new LRecyclerViewAdapter(myAdapter);
							mRecyclerView.setAdapter(mLRecyclerViewAdapter);
						} else {
							//myAdapter.setDataList(dataList);
							myAdapter.addAll(xjhdata);
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
					xjhDataConnect(page);
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

	//取消收藏
	public void unsubCollectData(final int collectid) {
		System.out.println(collectid);
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
					String resultCode = data.getString("resultCode");
					int subCollectResultCode = Integer.parseInt(resultCode);
					//Log.i(TAG,"请求结果:" + resultCode);
					switch(subCollectResultCode){
						case -1 :
							Toast.makeText(mContext, "读取远程服务器数据失败！", Toast.LENGTH_LONG).show();
							break; //可选
						case -2 :
							Toast.makeText(mContext, "远程数据错误，请稍后尝试！\r\n", Toast.LENGTH_LONG).show();
							break; //可选
						case 401 :
							Toast.makeText(mContext,"您的登录信息已失效，请重新登录！",Toast.LENGTH_SHORT).show();
							Intent intent = new Intent();
							intent.setClass(mContext, UserLoginActivity.class);
							startActivityForResult(intent,REQ_CODE);
							//startActivity(intent);
							//return;
							break; //可选
						case 200 :
							Toast.makeText(mContext, "收藏已取消", Toast.LENGTH_LONG).show();
							break; //可选
						//你可以有任意数量的case语句
						default : //可选
							Toast.makeText(mContext, subCollectResultCode+"服务请求失败！", Toast.LENGTH_LONG).show();
					}
				}
			}
		};
		new Thread(new Runnable(){
			@Override
			public void run() {
				XjhCollectHandler xjhCollectHandler = new XjhCollectHandler();
				Map<String, String> map = new HashMap<String, String>();
				map.put("uid", accountid);
				map.put("id", String.valueOf(collectid));
				map.put("sessid", sessid);
				map.put("userid", accountid);
				xjhCollectHandler.unsubCollect(map);
				int subResult = xjhCollectHandler.getResultCode();

				Message msg = new Message();
				Bundle data = new Bundle();
				data.putString("resultCode",String.valueOf(subResult));
				msg.setData(data);
				msg.what = 1;
				mHandler.sendMessage(msg);
			}
		}).start();
		//return handlerResult;
	}

	private void xjhDataConnect(int page) {

		Map<String, String> map = new HashMap<String, String>();
		map.put("page", String.valueOf(page));
        map.put("sessid", sessid);
        map.put("userid", accountid);
		XjhCollectHandler xjhHandler = new XjhCollectHandler();
		xjhHandler.handlerData(map);
		resultCode = xjhHandler.getResultCode();


		if(resultCode==200){
			//if(total==0)Toast.makeText(XjhActivity.this, "该条件下没有任何宣讲会信息！", 5).show();
			total = xjhHandler.getTotal();
			setTotal(total);
			//System.out.println(jobHandler.getListData());
			setDataList(xjhHandler.getListXjh());
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
						xjhDataHandler(curpage);
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

	public void setDataList(List<XjhInfoBean> xlist){
		String lastXjhdate = ""; int i = 0;
		for (XjhInfoBean xinfo : xlist){
			String xjhdate = xinfo.getXjhdate();
			Map<String, String> map = new HashMap<String, String>();
			map.put("xjhcity", "" + xinfo.getCityname() + "");
			//System.out.println("cc:"+xinfo.getCityname());
			map.put("xjhschool", xinfo.getSchool());
			map.put("xjhdate", xjhdate);
			map.put("xjhcname", xinfo.getCompany());
			map.put("xjhaddress", xinfo.getAddress());
			map.put("xjhtime", xinfo.getXjhtime());
			map.put("infoid", String.valueOf(xinfo.getId()));
			map.put("indexId", String.valueOf(xinfo.getIndexId()));
			if(i==0||!lastXjhdate.equals(xjhdate)){
				String datearr[] = xjhdate.split("-");
				map.put("datetitle", datearr[0]+"年"+datearr[1]+"月"+datearr[2]+"日");
				lastXjhdate = xjhdate;
			}
			else{
				map.put("datetitle", "");
			}

			//map.put("mark", xinfo.getMark());
			//System.out.println(xinfo.getCityname());
			xjhdata.add(map);
			i++;
		}

	}
	private XjhInfoBean getItemInfo(int pos){
		XjhInfoBean xjhinfo = new XjhInfoBean();
		//System.out.println("ddddd: "+pos+" cccccccccccc "+xjhdata.size());
		if(pos>=0){
			Map<String, String> map = xjhdata.get(pos);
			xjhinfo.setId(Integer.parseInt(map.get("infoid")));
			xjhinfo.setCompany(map.get("xjhcname"));
			xjhinfo.setAddress(map.get("xjhaddress"));
			xjhinfo.setCityname(map.get("xjhcity"));
			xjhinfo.setXjhdate(map.get("xjhdate"));
			xjhinfo.setXjhtime(map.get("xjhtime"));
			xjhinfo.setSchool(map.get("xjhschool"));
			//xjhinfo.setSchool(map.get("xjhschool"));
			xjhinfo.setSchool(map.get("indexId"));
		}
		return xjhinfo;
	}

	public void resetXjhdata(){
		this.xjhdata= new ArrayList<Map<String, String>>();
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
}