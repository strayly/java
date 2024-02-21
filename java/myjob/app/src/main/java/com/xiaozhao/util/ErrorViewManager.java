package com.xiaozhao.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.IdRes;

import com.xiaozhao.R;

import java.util.ArrayList;
import java.util.List;



/**
 *
 */
public class ErrorViewManager {
    private static final int NO_LAYOUT_ID = 0;

    private static int BASE_RETRY_LAYOUT_ID = NO_LAYOUT_ID;
    private static int BASE_EMPTY_LAYOUT_ID = NO_LAYOUT_ID;
    private static int BASE_LOADING_LAYOUT_ID = NO_LAYOUT_ID;


    private List<View> mContentList;
    private View mLoadingView;
    private View mRetryView;
    private View mEmptyView;

    private ErrorViewManager() {

    }

    public static ErrorViewManager getInstance() {
        return new ErrorViewManager();
    }

    /**
     * 设置默认布局(建议在Application中设置)
     *
     * @param loadingId 加载布局
     * @param retryId   重试布局
     * @param emptyId   空白布局(没有数据)
     */
    public static void defaultLayout(int loadingId, int retryId, int emptyId) {
        BASE_LOADING_LAYOUT_ID = loadingId;
        BASE_RETRY_LAYOUT_ID = retryId;
        BASE_EMPTY_LAYOUT_ID = emptyId;
    }


    public ErrorViewManager init(ViewGroup viewGroup) {
        //if (BASE_LOADING_LAYOUT_ID == NO_LAYOUT_ID || BASE_RETRY_LAYOUT_ID == NO_LAYOUT_ID || BASE_EMPTY_LAYOUT_ID == NO_LAYOUT_ID) {
        //    throw new IllegalArgumentException(" No default layout or call is set init(Object activityOrFragmentOrView, int loadingId, int retryId, int emptyId)");
       // }
        BASE_LOADING_LAYOUT_ID = R.layout.view_loading;
        BASE_RETRY_LAYOUT_ID = R.layout.view_retry;
        BASE_EMPTY_LAYOUT_ID = R.layout.view_empty;
        return init(viewGroup, BASE_LOADING_LAYOUT_ID, BASE_RETRY_LAYOUT_ID, BASE_EMPTY_LAYOUT_ID);
    }
    /**
     * @param loadingId 加载布局
     * @param retryId   重试布局
     * @param emptyId   空白布局(没有数据)
     */
    public ErrorViewManager init(ViewGroup viewGroup, int loadingId, int retryId, int emptyId) {
        Context context = viewGroup.getContext();
        mContentList = new ArrayList<>();
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            mContentList.add(view);
        }
        mLoadingView = LayoutInflater.from(context).inflate(loadingId, viewGroup, false);
        mRetryView = LayoutInflater.from(context).inflate(retryId, viewGroup, false);
        mEmptyView = LayoutInflater.from(context).inflate(emptyId, viewGroup, false);

        mLoadingView.setVisibility(View.GONE);
        mRetryView.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.GONE);

        viewGroup.addView(mLoadingView);
        viewGroup.addView(mRetryView);
        viewGroup.addView(mEmptyView);

        return this;
    }

    public void setOnRetryClickListener(final OnLoadingAndRetryListener onRetryClickListener) {
        if (mLoadingView == null || mRetryView == null || mEmptyView == null) {
            throw new IllegalArgumentException("Do not create init()");
        }
        mRetryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRetryClickListener.onClick();
            }
        });
    }

    public void setOnEmptyClickListener(final OnLoadingAndRetryListener onEmptyClickListener) {
        if (mLoadingView == null || mRetryView == null || mEmptyView == null) {
            throw new IllegalArgumentException("Do not create init()");
        }
        mEmptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEmptyClickListener.onClick();
            }
        });
    }

    public void setOnEmptyChildClickListener(@IdRes int id, final OnLoadingAndRetryListener onEmptyChildClickListener) {
        if (mLoadingView == null || mRetryView == null || mEmptyView == null) {
            throw new IllegalArgumentException("Do not create init()");
        }
        mEmptyView.findViewById(id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEmptyChildClickListener.onClick();
            }
        });
    }

    public void setOnRetryChildClickListener(final OnLoadingAndRetryListener onRetryChildClickListener) {
        if (mLoadingView == null || mRetryView == null || mEmptyView == null) {
            throw new IllegalArgumentException("Do not create init()");
        }
        mRetryView.findViewById(R.id.id_btn_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRetryChildClickListener.onClick();
            }
        });
    }


    public void showLoading() {
        if (mLoadingView == null || mRetryView == null || mEmptyView == null) {
            throw new IllegalArgumentException("Do not create init()");
        }
        show(View.GONE, View.VISIBLE, View.GONE, View.GONE);
    }

    public void showRetry() {
        if (mLoadingView == null || mRetryView == null || mEmptyView == null) {
            throw new IllegalArgumentException("Do not create init()");
        }

        show(View.GONE, View.GONE, View.VISIBLE, View.GONE);
    }

    public void showContent() {
        if (mLoadingView == null || mRetryView == null || mEmptyView == null) {
            throw new IllegalArgumentException("Do not create init()");
        }

        show(View.VISIBLE, View.GONE, View.GONE, View.GONE);
    }

    public void showEmpty() {
        if (mLoadingView == null || mRetryView == null || mEmptyView == null) {
            throw new IllegalArgumentException("Do not create init()");
        }

        show(View.GONE, View.GONE, View.GONE, View.VISIBLE);
    }


    private void show(int contentView, int loadingView, int retryView, int emptyView) {
        for (View view : mContentList) {
            view.setVisibility(contentView);
        }
        mLoadingView.setVisibility(loadingView);
        mRetryView.setVisibility(retryView);
        mEmptyView.setVisibility(emptyView);
    }
    public void hide() {

        for (View view : mContentList) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
            params.width = 0;
            params.height = 0;
            view.setLayoutParams(params);
            //view.setVisibility(View.GONE);
        }


        show(View.GONE, View.GONE, View.GONE, View.GONE);
    }
    public interface OnLoadingAndRetryListener {
        void onClick();
    }
}
