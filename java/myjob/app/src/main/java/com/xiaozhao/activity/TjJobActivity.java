package com.xiaozhao.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
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
import android.view.WindowManager;
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
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.google.android.material.tabs.TabLayout;
import com.xiaozhao.R;
import com.xiaozhao.adapter.JobListAdapter;
import com.xiaozhao.bean.JobItemInfoBean;
import com.xiaozhao.util.ErrorViewManager;
import com.xiaozhao.util.MyApplication;

import java.util.ArrayList;
import java.util.List;

import me.leefeng.promptlibrary.PromptDialog;




public class TjJobActivity extends AppCompatActivity implements OnClickListener,OnItemClickListener {

    private static final String TAG = "TjJobActivity";
    private TextView addr_text, toolbar;
    private ImageButton homebtn, mybtn, partjobbtn, fulljobbtn;

    private ImageButton searchbtn;
    private TextView btnBottomRecommend, btnBottomJoblist, btnBottomXjh, btnBottomDeadline, btnBottomMore;
    // private LinearLayout address_bt;

    private String[] jobcitys;
    //private String keyword;
    //private EditText search;
    private int curpage = 1;
    private int total = 0, pernum = 20, maxpages = 0;
    private ProgressDialog Dialog;
    private String myjobtype = "";


    private LRecyclerView mRecyclerView = null;
    private List<JobItemInfoBean> allDataList = new ArrayList<JobItemInfoBean>();
    private JobListAdapter myAdapter;
    private Context mContext = TjJobActivity.this;
    private LRecyclerViewAdapter mLRecyclerViewAdapter;
    private int refreshStatus = 0;
    private PromptDialog promptDialog;
    private TabLayout mTabLayout;
    private LinearLayoutManager mManager;
    private ScreenPopWindow searchCityWindow;
    private List<FiltrateBean> cityFBList = new ArrayList<>();
    private int resultCode = 0;
    private ErrorViewManager errorView;
    private int currentPage = 1;
    private int currentJobterm = 0;
    private String currentCity = null;
    private ViewPager viewPager;
    private List<Fragment> fragmentList;
    private TjJobActivity.FragmentAdapter fragmentAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        MyApplication.getInstance().addActivity(this);

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		//  this.requestWindowFeature(Window.FEATURE_NO_TITLE);去掉标题栏
        setContentView(R.layout.tjjob);
        readSharedPreferences();
        jobcitys = getResources().getStringArray(R.array.jobcity);

        String jobterm = (String) getIntent().getSerializableExtra("jobterm");
        if (jobterm != null) currentJobterm = Integer.parseInt(jobterm);
        if (currentJobterm != 1) currentJobterm = 0;

        //if(city==null||city.equals(""))city="全国";
        addr_text = (TextView) findViewById(R.id.addr_text);
        addr_text.setText(currentCity);
        addr_text.setOnClickListener(this);

        FiltrateBean selectCityFB = new FiltrateBean();
        selectCityFB.setTypeName("地区");
        List<FiltrateBean.Children> cityChildFBList = new ArrayList<>();
        for (String cityName : jobcitys) {
            FiltrateBean.Children cityChildren = new FiltrateBean.Children();
            cityChildren.setValue(cityName);
            cityChildFBList.add(cityChildren);
        }
        selectCityFB.setChildren(cityChildFBList);
        cityFBList.add(selectCityFB);
        // address_bt = (LinearLayout) findViewById(R.id.addr_button);
        addr_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                searchCityWindow = new ScreenPopWindow(mContext, cityFBList);
                //searchCityWindow.setBoxWidth(150);
                searchCityWindow.setStrokeWidth(1);
                searchCityWindow.build();

                //默认单选，因为共用的一个bean，这里调用reset重置下数据
                //searchCityWindow.reset().build();
                searchCityWindow.showAsDropDown(addr_text);
                searchCityWindow.setOnConfirmClickListener(new ScreenPopWindow.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(List<String> list) {
                        StringBuilder str = new StringBuilder();
                        for (int i = 0; i < list.size(); i++) {
                            //str.append(list.get(i)).append(" ");
                            currentCity = list.get(i);
                            //System.out.println(city);
                        }
                        //viewPager.setCurrentItem(currentJobterm);;

                        currentPage = 1;
                        //viewPager.setCurrentItem(currentJobterm);
                        fragmentAdapter.notifyDataSetChanged();

                        addr_text.setText(currentCity);
                        writeSharedPreferences(currentCity);
                        //Toast.makeText(mContext, str.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        //search = (EditText) findViewById(R.id.search_edit);
        //search.setText(keyword);

        searchbtn = (ImageButton) findViewById(R.id.search_button);
        searchbtn.setOnClickListener(this);

        btnBottomRecommend = (TextView) findViewById(R.id.btn_bottom_recommend);
        btnBottomRecommend.setOnClickListener(this);
        btnBottomRecommend.setSelected(true);

        btnBottomJoblist = (TextView) findViewById(R.id.btn_bottom_joblist);
        btnBottomJoblist.setOnClickListener(this);

        btnBottomXjh = (TextView) findViewById(R.id.btn_bottom_xjh);
        btnBottomXjh.setOnClickListener(this);

        btnBottomDeadline = (TextView) findViewById(R.id.btn_bottom_deadline);
        btnBottomDeadline.setOnClickListener(this);

        btnBottomMore = (TextView) findViewById(R.id.btn_bottom_more);
        btnBottomMore.setOnClickListener(this);




        if (currentCity == null || currentCity.equals("")) currentCity = "全国";


        mTabLayout = (TabLayout) findViewById(R.id.tab_menu_top);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //点击tab的时候，RecyclerView自动滑到该tab对应的item位置
                currentJobterm = tab.getPosition();
                if (currentJobterm != 1) currentJobterm = 0;
                getIntent().putExtra("jobterm", String.valueOf(currentJobterm));
                viewPager.setCurrentItem(currentJobterm);
                ;
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        LayoutInflater inflater = getLayoutInflater();
        viewPager = (ViewPager) findViewById(R.id.mainViewPager);
        viewPager.setOffscreenPageLimit(2);
        fragmentList = new ArrayList<Fragment>();
        TjJobFragment fullFragment = new TjJobFragment();
        Bundle fullBundle = new Bundle();
        fullBundle.putString("city", currentCity);
        fullBundle.putInt("jobterm", currentJobterm);
        fullBundle.putInt("page", currentPage);
        fullFragment.setArguments(fullBundle);

        TjJobFragment partFragment = new TjJobFragment();
        Bundle partBundle = new Bundle();
        partBundle.putString("city", currentCity);
        partBundle.putInt("jobterm", currentJobterm);
        partBundle.putInt("page", currentPage);
        partFragment.setArguments(partBundle);

        fragmentList.add(fullFragment);
        fragmentList.add(partFragment);
        fragmentAdapter = new FragmentAdapter(this.getSupportFragmentManager(), fragmentList);

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

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {

    }

    @Override
    public void onClick(View v) {
        //keyword = search.getText()==null ? "" : search.getText().toString();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (v == btnBottomMore) {
            Intent intent = new Intent();
            intent.setClass(this, MyActivity.class);
            // intent.setClass(this, MainChooseActivity.class);
            startActivity(intent);
        } else if (v == btnBottomRecommend) {
            currentJobterm = 0;
            currentPage = 1;
            viewPager.setCurrentItem(currentJobterm);
            fragmentAdapter.notifyDataSetChanged();
        } else if (v == btnBottomJoblist) {
            Intent intent = new Intent();
            intent.setClass(this, JobListActivity.class);
            startActivity(intent);
        } else if (v == btnBottomXjh) {
            Intent intent = new Intent();
            intent.setClass(this, XjhActivity.class);
            startActivity(intent);
        } else if (v == btnBottomDeadline) {
            Intent intent = new Intent();
            intent.setClass(this, DeadlineActivity.class);
            startActivity(intent);
        } else if (v == searchbtn) {
            Intent intent = new Intent();
            intent.setClass(this, JobSearchActivity.class);
            startActivity(intent);
        }
    }

    private void readSharedPreferences() {
        SharedPreferences jobinfo = getSharedPreferences("job", 0);
		currentCity = jobinfo.getString("city", "");
		if(currentCity==null || currentCity.length()<1) {
			String systemcity = jobinfo.getString("systemcity", "");
			if(systemcity!=null && systemcity.length()>1){
				for (String jobcity : jobcitys) {
					if(jobcity.equals(systemcity)){
						currentCity = jobcity;
						writeSharedPreferences(currentCity);
						break;
					}
				}
			}
		}
	}
	private void writeSharedPreferences(String city){
		SharedPreferences jobinfo = getSharedPreferences("job", 0);
		SharedPreferences.Editor edit = jobinfo.edit();
		edit.putString("city", city);
		//edit.putString("keyword", keyword);
		edit.commit();
	}
	private void mainActivityFinish(){
		AlertDialog.Builder dialog = new Builder(this);
		dialog.setTitle(getResources().getString(R.string.dialog_title));
		dialog.setMessage(getResources().getString(R.string.dialog_exit_message));
		dialog.setPositiveButton("确定", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which){
				//finish();
				writeSharedPreferences(currentCity);
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

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//按下键盘上返回按钮
		if(keyCode == KeyEvent.KEYCODE_BACK){
			mainActivityFinish();
			//MyApplication.getInstance().exit();
			return true;
		}else{
			return super.onKeyDown(keyCode, event);
		}
	}


    public void onPause() {
        //Log.w(Conf.TAG, "Activity2.onPause()");
        super.onPause();
        //StatService.onPause(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.jobmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        // return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.homepage:
                // TODO Auto-generated method stub
                // 关于
                intent = new Intent();
                intent.setClass(TjJobActivity.this, MyActivity.class);
                startActivity(intent);
                break;
            case R.id.jobcollect:
                intent = new Intent();
                intent.setClass(TjJobActivity.this, JobCollectActivity.class);
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
            if (currentJobterm != 1) currentJobterm = 0;
            return TjJobFragment.newInstance(currentCity, currentPage, currentJobterm);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            currentJobterm = position;
            if (currentJobterm != 1) currentJobterm = 0;
            TjJobFragment fragment = (TjJobFragment) super.instantiateItem(container, position);
            fragment.updateArguments(currentCity, currentPage, currentJobterm);
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





