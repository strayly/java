package com.xiaozhao.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.opengl.GLES10;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;


import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.callback.ImageFixCallback;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;


public class MyImageFix implements ImageFixCallback{

    private int screenWidth ;
    public MyImageFix(int screenWidth)
    {
        this.screenWidth = screenWidth;
    }

    //@Override
    public void onFix(ImageHolder holder,boolean imageReady){
        //if (holder.getWidth() > 500 || holder.getHeight() > 500) {holder.setAutoFix(true); }
    }

    @Override
    public void onInit(ImageHolder holder){
    }

    @Override
    public void onLoading(ImageHolder holder){
    }

    @Override
    public void onSizeReady(ImageHolder holder,int imgWidth,int imgHeight,ImageHolder.SizeHolder sizeHolder){
        //图片下载完成（未加载到内存）
        try{
            //int screenWidth = ((TinkerBaseApplicationLike) TinkerManager.getTinkerApplicationLike()).getScreenWidth();
            //int screenWidth=getScreenWidth(mContext);
            if(imgWidth>0&&imgHeight>0){
                //判断图片宽度是否大于屏幕宽度
                if(imgWidth>screenWidth){
                    int height=screenWidth*imgHeight/imgWidth;
                    sizeHolder.setSize(screenWidth,height);
                }

                //判断缩放后的高度是否大于限制高度
                int limitHeight=getOpenglRenderLimitValue();
                    if(imgHeight>limitHeight){
                        sizeHolder.setSize((int)(limitHeight*1.0f/(imgHeight*1.0f)*imgWidth),limitHeight);
                    }
                }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onImageReady(ImageHolder holder,int width,int height){

    }

    @Override
    public void onFailure(ImageHolder holder,Exception e){

    }


    //获取分享封面图（整个页面控件减去状态栏和标题栏的高度）
    public static Bitmap getBitmapFromView(View v) {
        Bitmap b = null;
        try {
            b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredWidth(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            int left = v.getLeft();
            int top = v.getTop();
            int right = v.getRight();
            int bottom = v.getBottom();
            v.layout(left, top, right, bottom);
            // Draw background
            Drawable bgDrawable = v.getBackground();
            //绘制图片本身的背景

            //手动设置背景
            c.drawColor(Color.WHITE);
            // Draw view to canvas
            v.draw(c);
        } catch (Exception e) {
            return null;
        }

        return b;
    }

    public static int getOpenglRenderLimitValue() {
        int maxsize;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            maxsize = getOpenglRenderLimitEqualAboveLollipop();
        } else {
            maxsize = getOpenglRenderLimitBelowLollipop();
        }
        return maxsize == 0 ? 3574 : maxsize;
    }

    private static int getOpenglRenderLimitBelowLollipop() {
        int[] maxSize = new int[1];
        GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0);
        return maxSize[0];
    }

    private static int getOpenglRenderLimitEqualAboveLollipop() {
        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay dpy = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        int[] vers = new int[2];
        egl.eglInitialize(dpy, vers);
        int[] configAttr = {
                EGL10.EGL_COLOR_BUFFER_TYPE, EGL10.EGL_RGB_BUFFER,
                EGL10.EGL_LEVEL, 0,
                EGL10.EGL_SURFACE_TYPE, EGL10.EGL_PBUFFER_BIT,
                EGL10.EGL_NONE
        };
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfig = new int[1];
        egl.eglChooseConfig(dpy, configAttr, configs, 1, numConfig);
        if (numConfig[0] == 0) {// TROUBLE! No config found.
        }
        EGLConfig config = configs[0];
        int[] surfAttr = {
                EGL10.EGL_WIDTH, 64,
                EGL10.EGL_HEIGHT, 64,
                EGL10.EGL_NONE
        };
        EGLSurface surf = egl.eglCreatePbufferSurface(dpy, config, surfAttr);
        final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;// missing in EGL10
        int[] ctxAttrib = {
                EGL_CONTEXT_CLIENT_VERSION, 1,
                EGL10.EGL_NONE
        };
        EGLContext ctx = egl.eglCreateContext(dpy, config, EGL10.EGL_NO_CONTEXT, ctxAttrib);
        egl.eglMakeCurrent(dpy, surf, surf, ctx);
        int[] maxSize = new int[1];
        GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0);
        egl.eglMakeCurrent(dpy, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_CONTEXT);
        egl.eglDestroySurface(dpy, surf);
        egl.eglDestroyContext(dpy, ctx);
        egl.eglTerminate(dpy);
        return maxSize[0];
    }

}