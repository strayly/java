package com.xiaozhao.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.xiaozhao.R;
import com.xiaozhao.bean.JobItemInfoBean;

import java.util.ArrayList;
import java.util.List;

public class JobListAdapter extends ListBaseAdapter<JobItemInfoBean>{

    private List<JobItemInfoBean> dataList = new ArrayList<JobItemInfoBean>();


    protected int mScreenWidth;
    public JobListAdapter(Context context, List<JobItemInfoBean> dataList) {
        super(context);
        this.dataList = dataList;
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_item_job;
    }
    public void setScreenWidth(int width) {
        mScreenWidth = width;
    }
    public void setDataList(List<JobItemInfoBean> dataList){
        this.dataList = dataList;
    }
    public List<JobItemInfoBean> getDataList(){
        return this.dataList;
    }

    public void addNewData(List<JobItemInfoBean> list) {
        int lastIndex = this.dataList.size();
        dataList = new ArrayList<JobItemInfoBean>();
        this.dataList = list;
        notifyItemRangeInserted(lastIndex, list.size());
    }

    public void clear() {
        //dataList.clear();
        dataList = new ArrayList<JobItemInfoBean>();
        notifyDataSetChanged();
    }



    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }


    @Override
    public void onBindItemHolder(SuperViewHolder holder, final int position) {
        View contentView = holder.getView(R.id.swipe_content);
        JobItemInfoBean jobItemInfo = dataList.get(position);
        TextView titleTV = holder.getView(R.id.title);
        String title = jobItemInfo.getTitle();
        titleTV.setText(title);
        TextView topTV = holder.getView(R.id.top);
        int top = jobItemInfo.getTop();
        if (top == 1) {
            topTV.setText("顶");
            topTV.setBackgroundResource(R.drawable.label_corners_red);
            topTV.setVisibility(View.VISIBLE);
        }  else{
            topTV.setText("");
            topTV.setVisibility(View.GONE);
        }
        TextView cityTV = holder.getView(R.id.city);
        cityTV.setText(jobItemInfo.getCity());
        TextView jobtermTV = holder.getView(R.id.jobterm);
        jobtermTV.setText(jobItemInfo.getJobterm());
        TextView dateTV = holder.getView(R.id.date);
        dateTV.setText(jobItemInfo.getDate());

    }

}