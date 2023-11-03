package com.kwai.cc.beauty.tob;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.kwai.cc.beauty.tob.textureview.ToBBeautyTextureViewFrg;
import com.streamlake.demo.R;

/**
 * author: zhouzhihui
 * created on: 2023/6/30 15:57
 * description:
 */
public class ToBBeautyAct extends AppCompatActivity {
    private static final String TAG = "ToBBeautyAct:zzh";
    private int mViewType;
    public static final String KEY_BEAUTY_VIEW_TYPE = "key_beauty_view_type"; // view类型，1glsurfaceview， 2surfaceview， 3textureview
    public static final int VALUE_BEAUTY_VIEW_TYPE_GLSURFACEVIEW = 1;
    public static final int VALUE_BEAUTY_VIEW_TYPE_SURFACEVIEW = 2;
    public static final int VALUE_BEAUTY_VIEW_TYPE_TEXTUREVIEW = 3;
    public static void launch(Activity activity, int viewType) {
        Intent intent = new Intent(activity, ToBBeautyAct.class);
        intent.putExtra(KEY_BEAUTY_VIEW_TYPE, viewType);
        activity.startActivity(intent);
    }

    private BaseToBBeautyFrg mToBBeautyFrg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_act_frg);
        mViewType = getIntent().getIntExtra(KEY_BEAUTY_VIEW_TYPE, VALUE_BEAUTY_VIEW_TYPE_GLSURFACEVIEW);
        if (mViewType == VALUE_BEAUTY_VIEW_TYPE_GLSURFACEVIEW) {
            mToBBeautyFrg = ToBBeautyFrg.newInstance();
        } else if (mViewType == VALUE_BEAUTY_VIEW_TYPE_SURFACEVIEW) {

        } else if (mViewType == VALUE_BEAUTY_VIEW_TYPE_TEXTUREVIEW) {
            mToBBeautyFrg = ToBBeautyTextureViewFrg.newInstance();
        }
        if (mToBBeautyFrg != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.replaceFrg, mToBBeautyFrg, "mToBBeautyFrg").commitAllowingStateLoss();
        } else {
            Toast.makeText(this, "fragment is null", Toast.LENGTH_SHORT).show();
        }
    }
}
