package com.xiaozhao.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.xiaozhao.R;
import com.xiaozhao.bean.DalibaoBean;

import java.util.ArrayList;
import java.util.List;

public class DalibaoAdapter extends ListBaseAdapter<DalibaoBean>{

    private List<DalibaoBean> dataList = new ArrayList<DalibaoBean>();
    private Context mContext;

    protected int mScreenWidth;
    public DalibaoAdapter(Context context, List<DalibaoBean> dataList) {
        super(context);
        this.dataList = dataList;
        this.mContext = context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_item_dalibao;
    }
    public void setScreenWidth(int width) {
        mScreenWidth = width;
    }
    public void setDataList(List<DalibaoBean> dataList){
        this.dataList = dataList;
    }
    public List<DalibaoBean> getDataList(){
        return this.dataList;
    }

    public void addNewData(List<DalibaoBean> list) {
        int lastIndex = this.dataList.size();
        dataList = new ArrayList<DalibaoBean>();
        this.dataList = list;
        notifyItemRangeInserted(lastIndex, list.size());
    }

    public void clear() {
        //dataList.clear();
        dataList = new ArrayList<DalibaoBean>();
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
        DalibaoBean dalibaoItemInfo = dataList.get(position);
        TextView titleTV = holder.getView(R.id.title);
        String title = dalibaoItemInfo.getTitle();
        titleTV.setText(title);

        TextView showdateTV = holder.getView(R.id.showdate);
        showdateTV.setText(dalibaoItemInfo.getShowdate());

        TextView classnameTV = holder.getView(R.id.classname);
        showdateTV.setText(dalibaoItemInfo.getClassname());

        TextView sourceTV = holder.getView(R.id.source);
        showdateTV.setText(dalibaoItemInfo.getSource());



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