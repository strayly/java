package com.xiaozhao.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.xiaozhao.R;
import com.xiaozhao.bean.ScheduleBean;

import java.util.ArrayList;
import java.util.List;

public class ScheduleAdapter extends ListBaseAdapter<ScheduleBean>{

    private List<ScheduleBean> dataList = new ArrayList<ScheduleBean>();
    private Context mContext;

    protected int mScreenWidth;
    public ScheduleAdapter(Context context, List<ScheduleBean> dataList) {
        super(context);
        this.dataList = dataList;
        this.mContext = context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_item_schedule;
    }
    public void setScreenWidth(int width) {
        mScreenWidth = width;
    }
    public void setDataList(List<ScheduleBean> dataList){
        this.dataList = dataList;
    }
    public List<ScheduleBean> getDataList(){
        return this.dataList;
    }

    public void addNewData(List<ScheduleBean> list) {
        int lastIndex = this.dataList.size();
        dataList = new ArrayList<ScheduleBean>();
        this.dataList = list;
        notifyItemRangeInserted(lastIndex, list.size());
    }

    public void clear() {
        //dataList.clear();
        dataList = new ArrayList<ScheduleBean>();
        notifyDataSetChanged();
    }
    public interface onSwipeListener {
        void onItemClick(View v,int pos);
    }
    private onSwipeListener mOnSwipeListener;

    public void setOnDelListener(onSwipeListener mOnDelListener) {
        this.mOnSwipeListener = mOnDelListener;
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, final int position) {
        View contentView = holder.getView(R.id.swipe_content);
        ScheduleBean itemInfo = dataList.get(position);
        TextView subjectTV = holder.getView(R.id.subject);
        String subject = itemInfo.getSubject();
        subjectTV.setText(subject);

        TextView noticetypeTV = holder.getView(R.id.noticetype);
        noticetypeTV.setText(itemInfo.getNoticetype());

        TextView infodateTV = holder.getView(R.id.infodate);
        infodateTV.setText(itemInfo.getInfodate());

        TextView companyTV = holder.getView(R.id.company);
        companyTV.setText(itemInfo.getCompany());



        //注意事项，设置item点击，不能对整个holder.itemView设置咯，只能对第一个子View，即原来的content设置，这算是局限性吧。
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AppToast.makeShortToast(mContext, getDataList().get(position).title);
                if (null != mOnSwipeListener) {
                    //如果删除时，不使用mAdapter.notifyItemRemoved(pos)，则删除没有动画效果，
                    //且如果想让侧滑菜单同时关闭，需要同时调用 ((CstSwipeDelMenu) holder.itemView).quickClose();
                    //((CstSwipeDelMenu) holder.itemView).quickClose();
                    mOnSwipeListener.onItemClick(v,position);
                }
            }
        });

    }
}