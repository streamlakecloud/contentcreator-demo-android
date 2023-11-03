package com.kwai.cc.beauty.tob;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.kwai.cc.beauty.tob.filter.FilterFrg;
import com.kwai.cc.beauty.tob.magic.MagicFrg;
import com.kwai.cc.beauty.tob.prettify.PrettifyBeautyFrg;
import com.streamlake.demo.R;
import com.streamlake.demo.databinding.TobFrgPreviewBinding;

/**
 * author: zhouzhihui
 * created on: 2023/6/30 15:58
 * description:
 */
public class ToBBeautyFrg extends BaseToBBeautyFrg {
    public static ToBBeautyFrg newInstance() {
        ToBBeautyFrg frg = new ToBBeautyFrg();
        Bundle bundle = new Bundle();
        bundle.putString("aK", "aV");
        return frg;
    }
    MagicFrg mMagicFrg;
    FilterFrg mFilterFrg;
    PrettifyBeautyFrg mPrettifyBeautyFrg;
    private static final String TAG = "ToBBeautyFrg:zzh";
    private TobFrgPreviewBinding bind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bind = TobFrgPreviewBinding.inflate(inflater);
        bind.ivTakePhoto.setOnClickListener(v -> bind.ivTakePhoto.setImageBitmap(null));
        bind.cameraPreview.setmTakePhotoCallback(bitmap -> getActivity().runOnUiThread(() -> {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) bind.ivTakePhoto.getLayoutParams();
            lp.height = bitmap.getHeight() * bind.ivTakePhoto.getWidth() / bitmap.getWidth();
            bind.ivTakePhoto.setImageBitmap(bitmap);
        }));
        bind.switchFace.setOnClickListener(v -> bind.cameraPreview.switchCamera());

        bind.btnOpenMagicFrg.setOnClickListener(v -> showMagicFrg());
        bind.getRoot().setOnClickListener(v -> {
            hideMagicFrg();
            hideFilterFrg();
            hideMakeUpFrg();
            hidePrettifyBeautyFrg();
        });
        bind.btnFilter.setOnClickListener(v -> showFilterFrg());
        bind.btnPrettifyMakeUp.setOnClickListener(v -> showMakeUpFrg());
        bind.btnPrettifyBeauty.setOnClickListener(v -> showPrettifyBeautyFrg());
        bind.btnTakePhoto.setOnClickListener(v -> bind.cameraPreview.takePhoto());
        return bind.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bind.cameraPreview.release();
    }

    private void showMagicFrg() {
        if (bind.cameraPreview.getSdk() == null) {
            Toast.makeText(getActivity(), "sdk 初始化ing...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mMagicFrg == null) {
            mMagicFrg = MagicFrg.newInstance();
            mMagicFrg.setmSdk(bind.cameraPreview.getSdk());
        }
        FragmentManager fm = getChildFragmentManager();
        if (mMagicFrg.isVisible()) {
            fm.beginTransaction().hide(mMagicFrg).commitAllowingStateLoss();
            return;
        }
        if (mMagicFrg.isAdded()) {
            fm.beginTransaction().show(mMagicFrg).commitAllowingStateLoss();
        } else {
            fm.beginTransaction().replace(R.id.frgContainer, mMagicFrg, "mMagicFrg").commitAllowingStateLoss();
        }
    }

    private void hideMagicFrg() {
        Log.i(TAG, "hide magic frg");
        if (mMagicFrg != null) {
            FragmentManager fm = getChildFragmentManager();
            if (mMagicFrg.isAdded()) {
                fm.beginTransaction().hide(mMagicFrg).commitAllowingStateLoss();
            }
        }
    }

    private void showFilterFrg() {
        if (mFilterFrg == null) {
            mFilterFrg = FilterFrg.newInstance();
            mFilterFrg.setmSdk(bind.cameraPreview.getSdk());
        }
        FragmentManager fm = getChildFragmentManager();
        if (mFilterFrg.isVisible()) {
            fm.beginTransaction().hide(mFilterFrg).commitAllowingStateLoss();
            return;
        }
        if (mFilterFrg.isAdded()) {
            fm.beginTransaction().show(mFilterFrg).commitAllowingStateLoss();
        } else {
            fm.beginTransaction().replace(R.id.frgContainer, mFilterFrg, "mFilterFrg").commitAllowingStateLoss();
        }
    }

    private void hideFilterFrg() {
        Log.i(TAG, "hide filter frg");
        if (mFilterFrg != null) {
            FragmentManager fm = getChildFragmentManager();
            if (mFilterFrg.isAdded()) {
                fm.beginTransaction().hide(mFilterFrg).commitAllowingStateLoss();
            }
        }
    }

    private void showMakeUpFrg() {
    }

    private void hideMakeUpFrg() {
    }

    private void showPrettifyBeautyFrg() {
        if (mPrettifyBeautyFrg == null) {
            mPrettifyBeautyFrg = PrettifyBeautyFrg.newInstance();
            mPrettifyBeautyFrg.setmSdk(bind.cameraPreview.getSdk());
        }
        FragmentManager fm = getChildFragmentManager();
        if (mPrettifyBeautyFrg.isVisible()) {
            fm.beginTransaction().hide(mPrettifyBeautyFrg).commitAllowingStateLoss();
            return;
        }
        if (mPrettifyBeautyFrg.isAdded()) {
            fm.beginTransaction().show(mPrettifyBeautyFrg).commitAllowingStateLoss();
        } else {
            fm.beginTransaction().replace(R.id.frgContainer, mPrettifyBeautyFrg, "mPrettifyBeautyFrg").commitAllowingStateLoss();
        }
    }

    private void hidePrettifyBeautyFrg() {
        Log.i(TAG, "hide prettify beauty frg");
        if (mPrettifyBeautyFrg != null) {
            FragmentManager fm = getChildFragmentManager();
            if (mPrettifyBeautyFrg.isAdded()) {
                fm.beginTransaction().hide(mPrettifyBeautyFrg).commitAllowingStateLoss();
            }
        }
    }

    public interface TakePhotoCallback {
        void onTakePhotoResult(Bitmap bitmap);
    }
}
