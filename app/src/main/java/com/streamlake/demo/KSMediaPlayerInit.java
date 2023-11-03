package com.streamlake.demo;

import android.app.Application;
import android.util.Log;

import com.kwai.video.ksmediaplayerkit.KSMediaPlayerConfig;
import com.kwai.video.ksmediaplayerkit.KSMediaPlayerException;
import com.kwai.video.ksmediaplayerkit.Logger.KSMediaPlayerLogListener;

/**
 * @Author: monkeydbobo
 * @Date: 2023/4/23
 * @Email: wanghaobo@kuaishou.com
 * @Description:
 */
public class KSMediaPlayerInit {
    private static final String TAG = "KSMediaPlayerInit";
    public KSMediaPlayerInit(Application application) {
        KSMediaPlayerConfig.init(application, CreativeEngine.KPN,
                "android_demo_xyz", new KSMediaPlayerConfig.OnInitListener() {
                    @Override
                    public void onInitSuccess() {
                        Log.i(TAG, "onInitSuccess");
                    }

                    @Override
                    public void onInitError(KSMediaPlayerException e) {
                        Log.i(TAG, e.getMessage());
                    }
                });
        KSMediaPlayerConfig.setLogListener(new KSMediaPlayerLogListener() {
            @Override
            public void v(String tag, String message, Throwable e) {
                Log.v(tag, "demoApp " + message, e);
            }

            @Override
            public void i(String tag, String message, Throwable e) {
                Log.i(tag, "demoApp " + message, e);
            }

            @Override
            public void d(String tag, String message, Throwable e) {
                Log.d(tag, "demoApp " + message, e);
            }

            @Override
            public void w(String tag, String message, Throwable e) {
                Log.w(tag, "demoApp " + message, e);
            }

            @Override
            public void e(String tag, String message, Throwable e) {
                Log.e(tag, "demoApp " + message, e);
            }
        });

    }
}
