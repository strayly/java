package com.xiaozhao.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.xiaozhao.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XjhCollectAdapter extends ListBaseAdapter<Map<String, String>>{

    private List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
    private Context mContext;

    protected int mScreenWidth;
    public XjhCollectAdapter(Context context, List<Map<String, String>> dataList) {
        super(context);
        this.dataList = dataList;
        this.mContext = context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_item_xjhcollect;
    }
    public void setScreenWidth(int width) {
        mScreenWidth = width;
    }
    public void setDataList(List<Map<String, String>> dataList){
        this.dataList = dataList;
    }
    public List<Map<String, String>> getDataList(){
        return this.dataList;
    }

    public void addAll(List<Map<String, String>> list) {
        int lastIndex = this.dataList.size();
        this.dataList = list;
        notifyItemRangeInserted(lastIndex, list.size());
    }

    public void clear() {
        dataList.clear();
        notifyDataSetChanged();
    }
    public interface onSwipeListener {
        void onDel(int pos);

        void onTop(int pos);

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
        Button btnDelete = holder.getView(R.id.btnDelete);
        Map<String, String> map = dataList.get(position);

        TextView datetitle = holder.getView(R.id.datetitle);
        //datetitle.setText(map.get("datetitle"));
        LinearLayout dateLinear = holder.getView(R.id.xjh_date_line);

        TextView xjhcname = holder.getView(R.id.xjhcname);
        xjhcname.setText(map.get("xjhcname"));

        TextView xjhschool = holder.getView(R.id.xjhschool);
        xjhschool.setText(map.get("xjhschool"));

        TextView xjhcity = holder.getView(R.id.xjhcity);
        xjhcity.setText(map.get("xjhcity"));

        TextView xjhaddress = holder.getView(R.id.xjhaddress);
        xjhaddress.setText(map.get("xjhaddress"));

        TextView xjhtime = holder.getView(R.id.xjhtime);
        xjhtime.setText(map.get("xjhtime"));

        TextView xjhdate = holder.getView(R.id.xjhdate);
        xjhdate.setText(map.get("xjhdate"));

        String datetitleValue = map.get("datetitle");

        if(datetitleValue!=null && datetitleValue.length()>0){
            datetitle.setText(datetitleValue);
            datetitle.setVisibility(View.VISIBLE);
            dateLinear.setVisibility(View.VISIBLE);
        }
        else{
            datetitle.setVisibility(View.GONE);
            dateLinear.setVisibility(View.GONE);
        }

        //这句话关掉IOS阻塞式交互效果 并依次打开左滑右滑
       // ((SwipeMenuView)holder.itemView).setIos(false).setLeftSwipe(position % 2 == 0 ? true : false);
        //title.setText(getDataList().get(position).title + (position % 2 == 0 ? "我只能右滑动" : "我只能左滑动"));
        //隐藏控件
        //btnUnRead.setVisibility(position % 3 == 0 ? View.GONE : View.VISIBLE);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnSwipeListener) {
                    //如果删除时，不使用mAdapter.notifyItemRemoved(pos)，则删除没有动画效果，
                    //且如果想让侧滑菜单同时关闭，需要同时调用 ((CstSwipeDelMenu) holder.itemView).quickClose();
                    //((CstSwipeDelMenu) holder.itemView).quickClose();
                    mOnSwipeListener.onDel(position);
                }
            }
        });
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