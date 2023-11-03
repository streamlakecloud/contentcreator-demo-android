package com.kwai.cc.beauty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.streamlake.demo.R;

/**
 * author: zhouzhihui
 * created on: 2023/6/28 17:53
 * description:
 */
public class CcBeautyAct extends AppCompatActivity {
    private static final String TAG = "CcBeautyAct:zzh:whb";
    // public final CameraActivityCameraManager mCameraManager = CameraActivityCameraManager.newInstance(this);

    private CameraFrg mCameraFrg;
    public static void launch(Activity activity) {
        activity.startActivity(new Intent(activity, CcBeautyAct.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_act_frg);
        // mCameraFrg = CameraFrg.newInstance();
        // FragmentManager fragmentManager = getSupportFragmentManager();
        // fragmentManager.beginTransaction().replace(R.id.replaceFrg, mCameraFrg, "CameraFrg").commitAllowingStateLoss();
    }

    // @Override
    // public CameraSDK getCameraSDK() {
    //     // return mCameraManager.getCameraSDK();
    //     return mCameraFrg.mCameraHelper;
    // }
    //
    // @Override
    // public CameraBaseFragment getCurrentCameraBaseFragment() {
    //     return mCameraFrg;
    // }
}
