package com.streamlake.demo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.kuaishou.ksposter.EditButler;
import com.kuaishou.ksposter.callback.EditCallback;
import com.kwai.cc.beauty.CcBeautyAct;
import com.kwai.cc.beauty.tob.ToBBeautyAct;
import com.kwai.gifshow.post.api.core.interfaces.CameraItemFragment;
import com.streamlake.slo.InitParams;
import com.streamlake.slo.SloManager;
import com.yxcorp.gifshow.camera.record.tab.CameraTabHelper;
import com.yxcorp.gifshow.camera.record.tab.CameraTabItem;
import com.yxcorp.utility.TextUtils;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity:zzh";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                CreativeEngine.init(getApplication());
                CreativeEngine.launchActivityCreate(MainActivity.this, savedInstanceState);
                initEditOutputCallback(MainActivity.this);

                // 启动语音识别子进程
                CreativeEngine.initTtsProcess(getApplicationContext());
                return false;
            }
        });
        setContentView(R.layout.activity_main);
        findViewById(R.id.demo_camera_btn).setOnClickListener(
                v -> CreativeEngine.startCameraActivity(this));
        findViewById(R.id.demo_creator_draft).setOnClickListener(
                v -> CreativeEngine.startLocalDraft(this));
        findViewById(R.id.btnCcBeautyDemo).setOnClickListener(v -> CcBeautyAct.launch(this));
        findViewById(R.id.btnCcTobBeautyDemo).setOnClickListener(v -> ToBBeautyAct.launch(this, ToBBeautyAct.VALUE_BEAUTY_VIEW_TYPE_GLSURFACEVIEW));
        findViewById(R.id.btnCcTobBeautyTextureView).setOnClickListener(v -> ToBBeautyAct.launch(this, ToBBeautyAct.VALUE_BEAUTY_VIEW_TYPE_TEXTUREVIEW));
    }

    private void initOpenAPI() {
        SloManager.get().init(new InitParams() {
            @Override
            public String getHost() {
                return "https://vod.streamlakeapi.com";
            }

            @Override
            public String getAccessKey() {
                return "17c5a3611c874687a9487f0641117db7";
            }

            @Override
            public String getSecretKey() {
                return null;
            }

            @Override
            public String getVersion() {
                return "1.0.0";
            }

            @Override
            public String getS3Ak() {
                return "5b0f2d684b4140a9a640e9560d17fa5a";
            }

            @Override
            public String getS3Sk() {
                return "861ac3373e7a4534b1bf789937454eb7";
            }
        });
    }

    private void initEditOutputCallback(Activity mActivity) {
        EditButler.INSTANCE.setEditCallback(new EditCallback() {
            @Override
            public void onStartEdit() {
                Log.i(TAG, "edit on start");
            }

            @Override
            public void onOutput(String s) {
                Log.i(TAG, "edit on end s=" + s);
            }
        });
    }
}