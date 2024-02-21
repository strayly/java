package com.xiaozhao.util;

import android.content.Context;
import android.content.Intent;

import com.xiaozhao.activity.OutLinkShowActivity;
import com.zzhoujay.richtext.callback.OnUrlClickListener;


public class MyUrlClick implements OnUrlClickListener {
    private Context mContext;
    public MyUrlClick(Context mContext){
        this.mContext = mContext;
    }

    @Override
    public boolean urlClicked(String url) {

        Intent intent = new Intent();
        intent.putExtra("jobid", 0);
        intent.putExtra("url", url);
        intent.setClass(mContext, OutLinkShowActivity.class);
        mContext.startActivity(intent);
        return true;
    }

}