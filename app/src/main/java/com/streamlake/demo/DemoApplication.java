package com.streamlake.demo;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

import com.kwai.cc.beauty.util.ScreenUtil;

/**
 * @Author: monkeydbobo
 * @Date: 2022/7/12
 * @Email: wanghaobo@kuaishou.com
 * @Description:
 */
public class DemoApplication extends Application {

    private static final String TAG = "DemoApplication";
    public static Application INSTANCE;

    public static int screenWidth;
    public static int screenHeight; // scr

    public DemoApplication() {
        super();
        Log.i("PluginDataProvider", "init application.");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        CreativeEngine.attachBaseContext(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        CreativeEngine.reallyInitTts(this);
        /**
         * resource-manager依赖load hodor
         * 或者播放器的初始化会主动load hodor
         */
//        System.loadLibrary("hodor");
        /**
         * 走播放器的初始化去加载hodor, 否则上下文会有很多问题
         */
        new KSMediaPlayerInit(this);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        ScreenUtil.init(this);
    }
}
