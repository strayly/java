package com.xiaozhao.activity;

import android.app.AlertDialog;
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
import com.xiaozhao.adapter.JobListAdapter;
import com.xiaozhao.bean.JobItemInfoBean;
import com.xiaozhao.datahandler.DictDataHandler;
import com.xiaozhao.datahandler.SearchJobHandler;
import com.xiaozhao.util.ErrorViewManager;
import com.xiaozhao.util.MyApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.leefeng.promptlibrary.PromptDialog;

//import com.baidu.mobstat.StatService;


public class JobSearchActivity extends AppCompatActivity implements OnClickListener,OnItemClickListener{

	private static final String TAG = "JobSearchActivity";
	private ImageButton homebtn,outbtn,partjobbtn,fulljobbtn,zzfulljobbtn,jsfulljobbtn;
	private TextView addr_text;
	private ImageButton searchbtn;
	private ImageButton backbtn;
	private String city = "全国";
	private String[] jobcitys;
	private String keyword;
	private EditText searchET;
	private int curpage = 1;
	private int total = 0,pernum = 20,maxpages = 0;

	private SimpleAdapter recordsAdapter;
	private List<Map<String, String>> wordList = new ArrayList<Map<String, String>>();
    private TextView clearRecordsTv,closeRecordsTv;
    private LinearLayout searchRecordsLl;
    private View recordsHistoryView;
    //private EditText searchContentEt;
    private ListView recordsListLv;
    private LinearLayout searchHistoryView;

    private InputMethodManager imm;

	private LRecyclerView mRecyclerView = null;
	private List<JobItemInfoBean> allDataList = new ArrayList<JobItemInfoBean>();
	private JobListAdapter myAdapter;
	private Context mContext = JobSearchActivity.this;
	private LRecyclerViewAdapter mLRecyclerViewAdapter;
	private int refreshStatus = 0;
	private PromptDialog promptDialog;
	private List<FiltrateBean> cityFBList = new ArrayList<>();
	private ScreenPopWindow searchCityWindow;
	private ErrorViewManager errorView;
	private int resultCode = 0;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.jobsearch);
		
		ReadSharedPreferences();
		jobcitys = getResources().getStringArray(R.array.jobcity);


		LinearLayout mainLayout = findViewById(R.id.mainLayout);
		errorView = ErrorViewManager.getInstance().init(mainLayout);
		errorView.setOnRetryChildClickListener(onRetryClickListener);
		//if(city==null||city.equals(""))city="全国";
		addr_text = (TextView) findViewById(R.id.addr_text);
		addr_text.setText(city);
		//addr_text.setOnClickListener(this);

		imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

		backbtn = (ImageButton) findViewById(R.id.back_btn);
		backbtn.setOnClickListener(this);
		
		searchbtn = (ImageButton) findViewById(R.id.search_button);
		searchbtn.setOnClickListener(this);

		FiltrateBean selectCityHot = new FiltrateBean();
		selectCityHot.setTypeName("热门地区");
		FiltrateBean selectCityMore = new FiltrateBean();
		selectCityMore.setTypeName("更多地区");
		List<FiltrateBean.Children> cityChildHotList = new ArrayList<>();
		List<FiltrateBean.Children> cityChildMoreList = new ArrayList<>();
		DictDataHandler dictDataHandler = new DictDataHandler(mContext);
		List<HashMap<String, String>> cityAllList = new ArrayList<HashMap<String, String>>();
		cityAllList = dictDataHandler.getJobLocation(true);

		for (HashMap<String, String> cityMap : cityAllList) {
			FiltrateBean.Children cityChildren = new FiltrateBean.Children();
			cityChildren.setValue(cityMap.get("name"));
			//if(Integer.parseInt(cityMap.get("ishot"))==1)cityChildHotList.add(cityChildren);
			//else cityChildMoreList.add(cityChildren);
			cityChildMoreList.add(cityChildren);
		}
		//selectCityHot.setChildren(cityChildHotList);
		selectCityMore.setChildren(cityChildMoreList);
		//cityFBList.add(selectCityHot);
		cityFBList.add(selectCityMore);

		searchCityWindow = new ScreenPopWindow(mContext, cityFBList);
		searchCityWindow.setBoxWidth(150);
		searchCityWindow.setStrokeWidth(1);
		//searchCityWindow.setChecked();

		addr_text.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				hideInput();
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
						}
						addr_text.setText(city);
						//Toast.makeText(mContext, str.toString(), Toast.LENGTH_SHORT).show();
					}
				});

			}
		});

		//显示历史记录lv
        searchHistoryView = (LinearLayout) findViewById(R.id.search_history_view);
        searchRecordsLl = (LinearLayout) findViewById(R.id.search_content_show_ll);
        recordsHistoryView = LayoutInflater.from(this).inflate(R.layout.search_history, null);
        //添加搜索view
        searchRecordsLl.addView(recordsHistoryView);

		recordsListLv = (ListView)recordsHistoryView.findViewById(R.id.search_records_lv);
        recordsListLv.setAdapter(listViewAdapter());
        recordsListLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				keyword = wordList.get(position).get("word");
				searchET.setText(keyword);
                //searchHistoryHide();
                resetdata();
				refreshStatus = 0;
                dataHandler(keyword,city,1);

                //loadRecyclerView();
                //Toast.makeText(MainActivity.this, fruit.getName(), Toast.LENGTH_SHORT).show();
            }
        });

		//清除搜索历史记录
		clearRecordsTv = (TextView) recordsHistoryView.findViewById(R.id.clear_search_word);
		clearRecordsTv.setOnClickListener(this);
		//关闭
		 closeRecordsTv = (TextView) recordsHistoryView.findViewById(R.id.close_search_word);
		 closeRecordsTv.setOnClickListener(this);

        //searchHistoryHide();
		mRecyclerView = (LRecyclerView) findViewById(R.id.listview_search);
        searchET = (EditText) findViewById(R.id.search_edit);
		searchET.requestFocus();
		searchET.setOnTouchListener(new TextView.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				searchHistoryShow();
				mRecyclerView.setVisibility(View.GONE);
				return false;
			}
		});
        searchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					if (searchET.getText().toString().length() > 0) {
                        resetdata();
                        keyword = searchET.getText().toString();
                        SQLiteDBHelper dbHelper = new SQLiteDBHelper(mContext);
                        dbHelper.saveSearchWord(keyword,"job");
                        dataHandler(keyword,city,1);
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
		mRecyclerView.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayoutManager.VERTICAL, R.drawable.view_decoration));
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mRecyclerView.setPullRefreshEnabled(false);
	}


	private ErrorViewManager.OnLoadingAndRetryListener onRetryClickListener = new ErrorViewManager.OnLoadingAndRetryListener(){
		public void onClick() {
			dataHandler(keyword,city,curpage);
		}
	};
	private void loadAdapter() {
		mRecyclerView.setVisibility(View.VISIBLE);
		mLRecyclerViewAdapter.setOnItemClickListener(new com.github.jdsjlzx.interfaces.OnItemClickListener()
		{
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
				dataHandler(keyword,city,1);
				refreshStatus = 0;
			}
		});
		mRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				curpage = curpage + 1;
				if (curpage <= maxpages) {
					// loading data
					dataHandler(keyword,city,curpage);
				} else {
					mRecyclerView.setNoMore(true);
				}
			}
		});
		mRecyclerView.setOnNetWorkErrorListener(new OnNetWorkErrorListener() {
			@Override
			public void reload() {
				// loading data
				if (curpage <= maxpages) dataHandler(keyword,city,curpage);
			}
		});
	}

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
		hideInput();
		//InputMethodManager inputManager =(InputMethodManager)searchET.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		//inputManager.hideSoftInput(searchET, 0);
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
		List wordinfos = dbHelper.getListSearchWord("job",0, 10);
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
		clearSearchFocus();
	}
	private JobItemInfoBean getItemInfo(int pos){
		JobItemInfoBean sinfo = new JobItemInfoBean();
		//System.out.println("ddddd: "+pos+" cccccccccccc "+xjhdata.size());
		if(pos>=0){
			sinfo = allDataList.get(pos);
		}
		return sinfo;
	}
	
	@Override
	public void onClick(View v){
		keyword = searchET.getText()==null ? "" : searchET.getText().toString();
		clearSearchFocus();
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		if (v==addr_text){
			resetdata();
			AlertDialog.Builder city_dialog = new AlertDialog.Builder(this);
			city_dialog.setTitle(getResources().getString(R.string.dialog_city_title));
			city_dialog.setSingleChoiceItems(jobcitys, -1,new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which){
							city = jobcitys[which];

							addr_text.setText(city);
							dialog.dismiss();
						}
					});
			city_dialog.show();
		} else if (v == searchbtn){
            //searchHistoryHide();
			if(keyword==null||keyword.length()<=0){
				Toast.makeText(JobSearchActivity.this, "请输入搜索关键词！", Toast.LENGTH_LONG).show();
			}
			else{
				resetdata();
				dataHandler(keyword,city,1);
                String record = searchET.getText().toString();
                SQLiteDBHelper dbHelper = new SQLiteDBHelper(mContext);
                dbHelper.saveSearchWord(keyword,"job");
				//loadRecyclerView();
			}
		} else if(v == backbtn){
			finish();
		} else if(v == searchET){

        } else if(v == clearRecordsTv){
			SQLiteDBHelper dbHelper = new SQLiteDBHelper(this);
			dbHelper.deleteAllSearchWord("job");
			wordList.clear();
			recordsAdapter.notifyDataSetChanged();
			searchHistoryHide();
		} else if(v == closeRecordsTv){
			searchHistoryHide();
		}

	}
	private void ReadSharedPreferences()
	{
		SharedPreferences jobinfo = getSharedPreferences("searchjob", 0);
		city = jobinfo.getString("city", city);
		keyword = jobinfo.getString("keyword", keyword);
	}
	private void WriteSharedPreferences(String city){
		SharedPreferences jobinfo = getSharedPreferences("searchjob", 0);
		SharedPreferences.Editor edit = jobinfo.edit();
		edit.putString("city", city);
		edit.putString("keyword", keyword);
		edit.commit();
	}

	private void dataHandler(final String keyword,final String city,final int page){
		curpage = page;
		searchHistoryHide();
		if(page<=1 && refreshStatus==0){
			errorView.showLoading();

		}
		final Handler mHandler = new Handler(){
			// 处理来自线程的消息,并将线程中的数据设置入listview
			@Override
			public void handleMessage(Message msg){
				if (msg.what == 1){

					if(resultCode==200) {
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
						//mRecyclerView.refreshComplete(total>pernum ? pernum : total);
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
                    //searchHistoryHide();
					dataConnect(keyword,city,page);
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
	private void dataConnect(String keyword,String city,int page) {

		Map<String, String> map = new HashMap<String, String>();
		map.put("locationname", city);
		map.put("page", String.valueOf(page));
		map.put("keyword", keyword);//全职招聘
		SearchJobHandler jobHandler = new SearchJobHandler();
		jobHandler.handlerData(map);
		resultCode = jobHandler.getResultCode();
		if(resultCode==200){

			int total = jobHandler.getTotal();
			setTotal(total);
			setDataList(jobHandler.getListData());
			setMaxpages((int) Math.ceil((double)total/(double)pernum));
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

	private void clearSearchFocus(){
		if(imm!=null){
			imm.hideSoftInputFromWindow(searchET.getWindowToken(), 0);
		}
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
	

}






