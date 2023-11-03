package com.kwai.mediaplayer;

import android.text.TextUtils;
import android.util.Log;

import com.kwai.video.ksmediaplayerkit.KSMediaPlayerConfig;
import com.kwai.video.ksmediaplayerkit.KSMediaPlayerException;
import com.kwai.video.ksmediaplayerkit.Logger.KSMediaPlayerLogListener;
import com.streamlake.demo.DemoApplication;

/**
 * 快手播放器初始化类
 *
 * @since 2023/3/8
 */

public class KSMediaPlayerInitializer {

    private static final String TAG = "KSMediaPlayerInit:zzh";

    private static String sAppId = "CHINABLUE";

    private static String sAppKey = "4a712089-8725-4c4f-9635-29b445b15438";;

    private static String sDeviceId = "zzh:test:deviceId";

    private static volatile boolean mInit = false;

    /**
     * 初始化快手播放器配置，可重复调用，不会重复初始化
     */
    public synchronized static void initKSPlayerConfig() {
        if (mInit) {
            Log.d(TAG, "KSMediaPlayerInitializer inited! do nothing!");
            return;
        }
        KSMediaPlayerConfig.init(DemoApplication.INSTANCE, sAppId, sDeviceId,
                new KSMediaPlayerConfig.OnInitListener() {
                    @Override
                    public void onInitSuccess() {
                        mInit = true;
                        Log.i("KSSuperPlayerImpl", "**** onInitSuccess");
                    }

                    @Override
                    public void onInitError(KSMediaPlayerException e) {
                        Log.e("KSSuperPlayerImpl", "****** onInitError = " + e.toString());
                    }
                });

        KSMediaPlayerConfig.setLogListener(new KSMediaPlayerLogListener() {
            @Override
            public void v(String tag, String message, Throwable e) {
                Log.v("KSSuperPlayerLog", "demoApp " + message, e);
            }

            @Override
            public void i(String tag, String message, Throwable e) {
                Log.i("KSSuperPlayerLog", "demoApp " + message, e);
            }

            @Override
            public void d(String tag, String message, Throwable e) {
                Log.d("KSSuperPlayerLog", "demoApp " + message, e);
            }

            @Override
            public void w(String tag, String message, Throwable e) {
                Log.w("KSSuperPlayerLog", "demoApp " + message, e);
            }

            @Override
            public void e(String tag, String message, Throwable e) {
                Log.e("KSSuperPlayerLog", "demoApp " + message, e);
            }
        });
    }
}
