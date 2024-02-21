package com.xiaozhao.activity;

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
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.popupwindowlibrary.bean.FiltrateBean;
import com.example.popupwindowlibrary.view.ScreenPopWindow;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnNetWorkErrorListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.xiaozhao.DB.SQLiteDBHelper;
import com.xiaozhao.R;
import com.xiaozhao.adapter.XjhListAdapter;
import com.xiaozhao.bean.XjhInfoBean;
import com.xiaozhao.datahandler.DictDataHandler;
import com.xiaozhao.datahandler.XjhHandler;
import com.xiaozhao.util.ErrorViewManager;
import com.xiaozhao.util.MyApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.leefeng.promptlibrary.PromptDialog;

//import com.baidu.mobstat.StatService;


public class XjhActivity extends AppCompatActivity implements OnClickListener,OnItemClickListener{

	private static final String TAG = "XjhActivity";
	

	private ProgressDialog Dialog;
	private SimpleAdapter adapter;

	private EditText searchET;
	
	private TextView addr_text;
	private String[] citys_china;
	
	
	
	private String city = "";
	private int cityid = 0;
	private String keyword;

	private InputMethodManager imm;	

	private int curpage = 1;
	private int total = 0,pernum = 20,maxpages = 0;

	private LRecyclerView mRecyclerView = null;
	private List<Map<String, String>> xjhdata = new ArrayList<Map<String, String>>();
	private XjhListAdapter myAdapter;
	private Context mContext = XjhActivity.this;
	private LRecyclerViewAdapter mLRecyclerViewAdapter;
	private int refreshStatus = 0;

	private PromptDialog promptDialog;

	private TextView btnBottomRecommend,btnBottomJoblist,btnBottomXjh,btnBottomDeadline, btnBottomMore;
	//private ImageButton address_bt;
	private ImageButton search_bt;

	private List<FiltrateBean> cityFBList = new ArrayList<>();
	private ScreenPopWindow searchCityWindow;

	private SimpleAdapter recordsAdapter;
	private List<Map<String, String>> wordList = new ArrayList<Map<String, String>>();
	private TextView clearRecordsTv,closeRecordsTv;
	private LinearLayout searchRecordsLl;
	private View recordsHistoryView;
	//private EditText searchContentEt;
	private ListView recordsListLv;
	private RelativeLayout searchHistoryView;
	private ErrorViewManager errorView;
	private int resultCode = 0;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.xjh);
		ReadSharedPreferences();
		//citys_china = getResources().getStringArray(R.array.citys_china);

		//loading 数据请求失败的处理
		LinearLayout mainLayout = findViewById(R.id.mainLayout);
		errorView = ErrorViewManager.getInstance().init(mainLayout);
		errorView.setOnRetryChildClickListener(onRetryClickListener);


		//promptDialog = new PromptDialog(this);

		keyword = "";
		searchET = (EditText) findViewById(R.id.search_edit);
		//searchET.setText(keyword);
		//软键盘关闭
		imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);  
		//clearSearchFocus();
		addr_text = (TextView) findViewById(R.id.addr_text);
		addr_text.setText(city);
		addr_text.setOnClickListener(this);

		FiltrateBean selectCityFB = new FiltrateBean();
		selectCityFB.setTypeName("地区");
		List<FiltrateBean.Children> cityChildFBList = new ArrayList<>();

		DictDataHandler dictDataHandler = new DictDataHandler(mContext);
		List<HashMap<String, String>> cityList = new ArrayList<HashMap<String, String>>();
		cityList = dictDataHandler.getXjhLocation(true);

		for (HashMap<String, String> cityMap : cityList) {
			FiltrateBean.Children cityChildren = new FiltrateBean.Children();
			cityChildren.setValue(cityMap.get("name"));
			cityChildFBList.add(cityChildren);
		}
		selectCityFB.setChildren(cityChildFBList);
		cityFBList.add(selectCityFB);

		// address_bt = (ImageButton) findViewById(R.id.addr_button);
		addr_text.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				searchCityWindow = new ScreenPopWindow(mContext, cityFBList);
				searchCityWindow.setBoxWidth(150);
				searchCityWindow.setStrokeWidth(1);
				searchCityWindow.build();

				//默认单选，因为共用的一个bean，这里调用reset重置下数据
				//searchCityWindow.reset().build();
				searchCityWindow.showAsDropDown(addr_text);
				searchCityWindow.setOnConfirmClickListener(new ScreenPopWindow.OnConfirmClickListener() {
					@Override
					public void onConfirmClick(List<String> list) {
						StringBuilder str = new StringBuilder();
						for (int i=0;i<list.size();i++) {
							//str.append(list.get(i)).append(" ");
							city = list.get(i);
							//System.out.println(city);
						}
						resetXjhdata();
						xjhDataHandler(keyword,cityid,city,1);
						addr_text.setText(city);
						//Toast.makeText(mContext, str.toString(), Toast.LENGTH_SHORT).show();
					}
				});

			}
		});
		
		search_bt = (ImageButton) findViewById(R.id.search_button);
		search_bt.setOnClickListener(this);

		btnBottomRecommend = (TextView) findViewById(R.id.btn_bottom_recommend);
		btnBottomRecommend.setOnClickListener(this);

		btnBottomJoblist = (TextView) findViewById(R.id.btn_bottom_joblist);
		btnBottomJoblist.setOnClickListener(this);

		btnBottomXjh = (TextView) findViewById(R.id.btn_bottom_xjh);
		btnBottomXjh.setOnClickListener(this);
		btnBottomXjh.setSelected(true);

		btnBottomDeadline = (TextView) findViewById(R.id.btn_bottom_deadline);
		btnBottomDeadline.setOnClickListener(this);

		btnBottomMore = (TextView) findViewById(R.id.btn_bottom_more);
		btnBottomMore.setOnClickListener(this);

		//url = "http://sou.zhaopin.com/jobs/jobsearch_jobtype.aspx";

		//显示历史记录lv
		searchHistoryView = (RelativeLayout) findViewById(R.id.search_history_view);
		searchRecordsLl = (LinearLayout) findViewById(R.id.search_content_show_ll);
		recordsHistoryView = LayoutInflater.from(this).inflate(R.layout.search_history, null);
		//添加搜索view
		searchRecordsLl.addView(recordsHistoryView);

		recordsListLv = (ListView)recordsHistoryView.findViewById(R.id.search_records_lv);
		//recordsListLv.setAdapter(listViewAdapter());
		recordsListLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				keyword = wordList.get(position).get("word");
				searchET.setText(keyword);
				resetXjhdata();
				xjhDataHandler(keyword,cityid,city,1);
				//loadRecyclerView();
				//Toast.makeText(MainActivity.this, fruit.getName(), Toast.LENGTH_SHORT).show();
			}
		});

		//清除搜索历史记录
		clearRecordsTv = (TextView) recordsHistoryView.findViewById(R.id.clear_search_word);
		clearRecordsTv.setOnClickListener(this);

		closeRecordsTv = (TextView) recordsHistoryView.findViewById(R.id.close_search_word);
		closeRecordsTv.setOnClickListener(this);
		//searchHistoryHide();

		searchET.setOnTouchListener(new TextView.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//System.out.println("aaaaaaaaaaaaaaaa");
				searchHistoryShow();
				return false;
			}
		});
		searchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					if (searchET.getText().toString().length() > 0) {
						resetXjhdata();
						keyword = searchET.getText().toString();
						SQLiteDBHelper dbHelper = new SQLiteDBHelper(mContext);
						dbHelper.saveSearchWord(keyword,"xjh");
						xjhDataHandler(keyword,cityid,city,1);
						//loadRecyclerView();
						//searchHistoryHide();
					} else {
						Toast.makeText(mContext, "搜索内容不能为空！", Toast.LENGTH_LONG).show();
					}
					return false;
				}
				else{
					return true;
				}
			}
		});

		mRecyclerView = (LRecyclerView) findViewById(R.id.listview);
		mRecyclerView.addItemDecoration(new XjhActivity.RecyclerViewDivider(mContext, LinearLayoutManager.VERTICAL, R.drawable.view_decoration));
		mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        xjhDataHandler(keyword,cityid,city,curpage);
		new Thread(){
			public void run(){
				try{
					DictDataHandler dictDataHandler = new DictDataHandler(mContext);
					dictDataHandler.getXjhLocation(false);

				} catch (Exception e){
					//Log.e(TAG, e.toString());
				}
			};
		}.start();
	}
	private ErrorViewManager.OnLoadingAndRetryListener onRetryClickListener = new ErrorViewManager.OnLoadingAndRetryListener(){
		public void onClick() {
			xjhDataHandler(keyword,cityid,city,curpage);
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

	//设置历史记录是否显示
	private void searchHistoryShow(){
		searchHistoryView.setVisibility(View.VISIBLE);
		recordsListLv.setAdapter(listViewAdapter());
		//setWordList();
		//recordsAdapter.notifyDataSetChanged();
		//mRecyclerView.setVisibility(View.GONE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		//InputMethodManager inputManager =(InputMethodManager)searchET.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		//inputManager.showSoftInput(searchET, 0);
	}
	private void searchHistoryHide(){
		searchHistoryView.setVisibility(View.GONE);
		//InputMethodManager inputManager =(InputMethodManager)searchET.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		//inputManager.hideSoftInput(searchET, 0);
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		hideInput();
		//mRecyclerView.setVisibility(View.VISIBLE);
	}

	public void showInput(final EditText et) {
		et.requestFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
	}

	/**
	 * 隐藏键盘
	 */
	protected void hideInput() {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		View v = getWindow().peekDecorView();
		if (null != v) {
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
	}

	private void setWordList(){
		wordList = new ArrayList<Map<String, String>>();
		SQLiteDBHelper dbHelper = new SQLiteDBHelper(this);
		List wordinfos = dbHelper.getListSearchWord("xjh",0, 10);
		if (wordinfos.isEmpty()){
			//Toast.makeText(this, "您的收藏夹里面是空的", 1).show();
			searchHistoryView.setVisibility(View.GONE);
		}
		else {
			searchHistoryView.setVisibility(View.VISIBLE);
			for (int i = 0; i < wordinfos.size(); i++) {
				Map<String, String> map = new HashMap<String, String>();
				String sword = String.valueOf(wordinfos.get(i));
				map.put("word", sword);
				map.put("key", "word");
				wordList.add(map);
			}
		}
	}
	private SimpleAdapter listViewAdapter(){
		setWordList();
		recordsAdapter = new SimpleAdapter(this, wordList, R.layout.search_history_item,
				new String[] { "word" }
				, new int[] {R.id.search_content_tv});
		return recordsAdapter;
	}

	private void loadAdapter() {
		myAdapter.setOnDelListener(new XjhListAdapter.onSwipeListener() {
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
				xjhDataHandler(keyword,cityid,city,1);
				refreshStatus = 0;
			}
		});
		mRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				curpage = curpage + 1;
				if (curpage <= maxpages) {
					// loading data
					xjhDataHandler(keyword,cityid,city,curpage);
				} else {
					mRecyclerView.setNoMore(true);
				}
			}
		});
		mRecyclerView.setOnNetWorkErrorListener(new OnNetWorkErrorListener() {
			@Override
			public void reload() {
				// loading data
				if (curpage <= maxpages) xjhDataHandler(keyword,cityid,city,curpage);
			}
		});
	}

	/**
	 * 将地址信息跟关键字信息从preference中读取出来
	 */
	private void ReadSharedPreferences(){
		SharedPreferences xjhinfo = getSharedPreferences("xjh", 0);
		cityid = xjhinfo.getInt("cityid", 0);
		city = xjhinfo.getString("city", "");
		keyword = xjhinfo.getString("keyword", "");
	}

	/**
	 * 将相关信息写入preference
	 *
	 *  地址信息
	 *  关键字信息
	 */
	private void WriteSharedPreferences(int cityid,String city,String keyword){
		SharedPreferences xjhinfo = getSharedPreferences("xjh", 0);
		SharedPreferences.Editor edit = xjhinfo.edit();
		edit.putInt("cityid", cityid);
		edit.putString("city", city);
		edit.putString("keyword", keyword);
		edit.commit();
	}

	/**
	 * View内的按钮事件
	 */
	@Override
	public void onClick(View v){
		keyword = searchET.getText()==null ? "" : searchET.getText().toString();
		clearSearchFocus();
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		if (v == search_bt){
			//htmlHandler(Setting.XJH_SERVICE_URL, city, search.getText().toString(), page);
			resetXjhdata();
			xjhDataHandler(keyword,cityid,city,1);
			String record = searchET.getText().toString();
			SQLiteDBHelper dbHelper = new SQLiteDBHelper(mContext);
			dbHelper.saveSearchWord(keyword,"xjh");
			//loadRecyclerView();
		} else if (v==addr_text){

		} else if(v == btnBottomMore){
			Intent intent = new Intent();
			intent.setClass(this, MyActivity.class);
			startActivity(intent);
		} else if(v == btnBottomRecommend){
			Intent intent = new Intent();
			intent.setClass(this, TjJobActivity.class);
			startActivity(intent);
		} else if(v == btnBottomJoblist){
			Intent intent = new Intent();
			intent.setClass(this, JobListActivity.class);
			startActivity(intent);
		} else if(v == btnBottomXjh){
			resetXjhdata();
			keyword = "";
			xjhDataHandler(keyword,cityid,city,1);
		} else if (v == btnBottomDeadline) {
			Intent intent = new Intent();
			intent.setClass(this, DeadlineActivity.class);
			startActivity(intent);
		} else if(v == searchET){

		} else if(v == clearRecordsTv){
			SQLiteDBHelper dbHelper = new SQLiteDBHelper(this);
			dbHelper.deleteAllSearchWord("xjh");
			wordList.clear();
			recordsAdapter.notifyDataSetChanged();
			searchHistoryHide();
		} else if(v == closeRecordsTv){
			searchHistoryHide();
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
 

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,long arg3){

	}
	/**
	 * 开启一条线程来执行从网络获取的数据
	 */
	private void xjhDataHandler(final String word,final int cityid,final String city,final int page){
		searchHistoryHide();
		curpage = page;
		if(page<=1)errorView.showLoading();//Toast.makeText(this, "没有您要查找的宣讲会信息", 1).show();
		final Handler mHandler = new Handler(){
			// 处理来自线程的消息,并将线程中的数据设置入listview
			@Override
			public void handleMessage(Message msg){
				if (msg.what == 1){

					if(resultCode==200) {
						if (page <= 1 && refreshStatus == 0)errorView.showContent();
						addr_text.setText(city);
						if (page <= 1) {
							myAdapter = new XjhListAdapter(mContext, xjhdata);
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
					xjhDataConnect(word,cityid,city,page);
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
	
	
	

	private void xjhDataConnect(String word,int cityid,String city,int page) {

		Map<String, String> map = new HashMap<String, String>();
		map.put("keyword", word);
		map.put("cityid", String.valueOf(cityid));
		map.put("locationname", city);
		map.put("page", String.valueOf(page));
		XjhHandler xjhHandler = new XjhHandler();
		xjhHandler.handlerXjhData(map);
		resultCode = xjhHandler.getResultCode();
		if(resultCode==200){
			int total = xjhHandler.getTotal();
			setTotal(total);
            setDataList(xjhHandler.getListXjh());
			setMaxpages((int) Math.ceil((double)total/(double)pernum));
		}
		
	}
	



	/**
	 * 当停止的时候保存数据
	 */
	@Override
	protected void onStop(){
		WriteSharedPreferences(cityid,city, keyword);
		super.onStop();
	}

	/**
	 * 当销毁的时候保存数据入Preferences
	 */
	@Override
	protected void onDestroy(){
		WriteSharedPreferences(cityid,city, keyword);
		super.onDestroy();
	}

	private void mainActivityFinish(){
		AlertDialog.Builder dialog = new Builder(this);
		dialog.setTitle(getResources().getString(R.string.dialog_title));
		dialog.setMessage(getResources().getString(R.string.dialog_exit_message));
		dialog.setPositiveButton("确定", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which){
				//finish();
				WriteSharedPreferences(cityid,city, "");
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
	private void clearSearchFocus(){
		if(imm!=null){
			imm.hideSoftInputFromWindow(searchET.getWindowToken(), 0);
		}
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