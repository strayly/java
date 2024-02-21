package com.xiaozhao.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.popupwindowlibrary.bean.FiltrateBean;
import com.example.popupwindowlibrary.view.ScreenPopWindow;
import com.google.android.material.tabs.TabLayout;
import com.xiaozhao.R;
import com.xiaozhao.bean.JobItemInfoBean;
import com.xiaozhao.datahandler.DictDataHandler;
import com.xiaozhao.util.MyApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.leefeng.promptlibrary.PromptDialog;

public class JobListActivity extends AppCompatActivity implements OnClickListener,OnItemClickListener{

	private static final String TAG = "JobList2Activity";
	// private TextView toolbar;
	private InputMethodManager imm;
	private TextView btnBottomRecommend,btnBottomJoblist,btnBottomXjh,btnBottomDeadline, btnBottomMore; //菜单栏按钮定义
	private TextView addr_text;
	// private ImageButton address_bt;
	private ImageButton searchbtn;
	private int currentPage = 1;
	private int currentJobterm = 0;
	private String currentCity = null;

	private final static String ZZ_FULL_TITLE = "全职招聘";
	private final static String ZZ_PART_TITLE = "兼职实习";

	private Context mContext = JobListActivity.this;

	private int refreshStatus = 0;


	private String tab_menu_names[] = new String[]{"全职招聘", "兼职实习"};
	private TabLayout mTabLayout;
	private LinearLayoutManager mManager;
	private List<FiltrateBean> cityFBList = new ArrayList<>();
	private ScreenPopWindow searchCityWindow;
	private PromptDialog promptDialog;

	private ViewPager viewPager;
	private List<Fragment> fragmentList;
	private FragmentAdapter fragmentAdapter;

	private List<JobItemInfoBean> allDataList = new ArrayList<JobItemInfoBean>();
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);

		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.joblist);
		ReadSharedPreferences();

		String jobterm = (String) getIntent().getSerializableExtra("jobterm");

		if(jobterm!=null) currentJobterm=Integer.parseInt(jobterm);
		if(currentJobterm!=1)currentJobterm=0;

		//promptDialog = new PromptDialog(this);
		//promptDialog.getDefaultBuilder().touchAble(false).round(3).loadingDuration(300);

		//软键盘关闭
		imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);  
		//clearSearchFocus();
		
		if(currentCity==null||currentCity.equals(""))currentCity="全国";

		addr_text = (TextView) findViewById(R.id.addr_text);
		addr_text.setText(currentCity);
		addr_text.setOnClickListener(this);
		// address_bt = (ImageButton) findViewById(R.id.addr_button);

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

		//searchCityWindow.setChecked();

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
							currentCity = list.get(i);
						}
						currentPage = 1;
						//System.out.println(currentCity);
                        //viewPager.setCurrentItem(currentJobterm);
						fragmentAdapter.notifyDataSetChanged();;
						addr_text.setText(currentCity);
					}
				});
			}
		});

		searchbtn = (ImageButton) findViewById(R.id.search_button);
		searchbtn.setOnClickListener(this);

		btnBottomRecommend = (TextView) findViewById(R.id.btn_bottom_recommend);
		btnBottomRecommend.setOnClickListener(this);

		btnBottomJoblist = (TextView) findViewById(R.id.btn_bottom_joblist);
		btnBottomJoblist.setOnClickListener(this);
		btnBottomJoblist.setSelected(true);

		btnBottomXjh = (TextView) findViewById(R.id.btn_bottom_xjh);
		btnBottomXjh.setOnClickListener(this);

		btnBottomDeadline = (TextView) findViewById(R.id.btn_bottom_deadline);
		btnBottomDeadline.setOnClickListener(this);

		btnBottomMore = (TextView) findViewById(R.id.btn_bottom_more);
		btnBottomMore.setOnClickListener(this);



		mTabLayout = (TabLayout) findViewById(R.id.tab_menu_top);

		mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				//点击tab的时候，RecyclerView自动滑到该tab对应的item位置
				//mManager.scrollToPositionWithOffset(tab.getPosition(), 0);
				//resetdata();
				currentJobterm = tab.getPosition();
				if(currentJobterm != 1) currentJobterm=0;
				getIntent().putExtra("jobterm", String.valueOf(currentJobterm));
				viewPager.setCurrentItem(currentJobterm);;
			}
			@Override
			public void onTabUnselected(TabLayout.Tab tab) {}
			@Override
			public void onTabReselected(TabLayout.Tab tab) {}
		});


		//加载地区字典
		new Thread(){
			public void run(){
				try{
					DictDataHandler dictDataHandler = new DictDataHandler(mContext);
					dictDataHandler.getJobLocation(false);

				} catch (Exception e){
					//Log.e(TAG, e.toString());
				}
			};
		}.start();





		//oneFragment = new OneFragment();
        LayoutInflater inflater = getLayoutInflater();
		viewPager = (ViewPager) findViewById(R.id.mainViewPager);
		viewPager.setOffscreenPageLimit(2);
		fragmentList = new ArrayList<Fragment>();
		JobListFragment fullFragment = new JobListFragment();
		Bundle fullBundle = new Bundle();
		fullBundle.putString("city",currentCity);
		fullBundle.putInt("jobterm",currentJobterm);
		fullBundle.putInt("page",currentPage);
		fullFragment.setArguments(fullBundle);

		JobListFragment partFragment = new JobListFragment();
		Bundle partBundle = new Bundle();
		partBundle.putString("city",currentCity);
		partBundle.putInt("jobterm",currentJobterm);
		partBundle.putInt("page",currentPage);
		partFragment.setArguments(partBundle);

		fragmentList.add(fullFragment);
		fragmentList.add(partFragment);
		fragmentAdapter = new FragmentAdapter(this.getSupportFragmentManager(),fragmentList);

		viewPager.setAdapter(fragmentAdapter);

		viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}
			@Override
			public void onPageSelected(int position) {
				mTabLayout.getTabAt(position).select();
			}
			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});


	}

	private void ReadSharedPreferences(){
		SharedPreferences sp = getSharedPreferences("job", 0);
		currentCity = sp.getString("city", "");
	}


	private void WriteSharedPreferences(String city){
		SharedPreferences xjhinfo = getSharedPreferences("job", 0);
		SharedPreferences.Editor edit = xjhinfo.edit();
		edit.putString("city", currentCity);
		edit.commit();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,long arg3){

	}
	@Override
	public void onClick(View v){
		//keyword = search.getText()==null ? "" : search.getText().toString();

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		if(v == btnBottomMore){
			Intent intent = new Intent();
			intent.setClass(this, MyActivity.class);
			startActivity(intent);
		} else if(v == btnBottomRecommend){
			Intent intent = new Intent();
			intent.setClass(this, TjJobActivity.class);
			startActivity(intent);
		} else if(v == btnBottomJoblist){
			//dataHandler(1,city);
			currentJobterm = 0;
			currentPage = 1;
            viewPager.setCurrentItem(currentJobterm);
			fragmentAdapter.notifyDataSetChanged();
		} else if(v == btnBottomXjh){
			Intent intent = new Intent();
			intent.setClass(this, XjhActivity.class);
			startActivity(intent);
		} else if (v == btnBottomDeadline) {
			Intent intent = new Intent();
			intent.setClass(this, DeadlineActivity.class);
			startActivity(intent);
		} else if(v ==searchbtn){
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
 




	@Override
	protected void onStop(){
		WriteSharedPreferences(currentCity);
		super.onStop();
	}


	@Override
	protected void onDestroy(){
		WriteSharedPreferences(currentCity);
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
				WriteSharedPreferences(currentCity);
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
	public class FragmentAdapter extends FragmentStatePagerAdapter {

		List<Fragment> fragmentList = new ArrayList<Fragment>();

		public FragmentAdapter(FragmentManager fm, List<Fragment> fragmentList) {
			super(fm);
			this.fragmentList = fragmentList;
		}

		@Override
		public Fragment getItem(int position) {
			//return fragmentList.get(position);
			currentJobterm = position;
			if(currentJobterm!=1)currentJobterm=0;
			return JobListFragment.newInstance(currentCity,currentPage,currentJobterm);
		}
		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			currentJobterm = position;
			if(currentJobterm!=1)currentJobterm=0;
			JobListFragment fragment  = (JobListFragment) super.instantiateItem(container, position);
			fragment.updateArguments(currentCity,currentPage,currentJobterm);
			//LoggerUtil.i("MartialMoreNoticeFragme","instantiateItem-->position-->"+position);
			return fragment;
		}
		@Override
		public int getItemPosition(Object object) {
			return PagerAdapter.POSITION_NONE;
		}
		@Override
		public int getCount() {
			return fragmentList.size();
		}
	}



}
