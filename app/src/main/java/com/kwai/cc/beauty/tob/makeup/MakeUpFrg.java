package com.kwai.cc.beauty.tob.makeup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kwai.cc.beauty.tob.sdk.ICcBeautyEngine;
import com.streamlake.demo.databinding.FrgMakeupBinding;

/**
 * author: zhouzhihui
 * created on: 2023/7/20 20:08
 * description:
 */
public class MakeUpFrg extends Fragment {
    private static final String TAG = "MakeUpFrg:zzh";
    private FrgMakeupBinding bind;
    private ICcBeautyEngine mSdk;

    public static MakeUpFrg newInstance() {
        MakeUpFrg frg = new MakeUpFrg();
        Bundle bundle = new Bundle();
        bundle.putString("aV", "aK");
        frg.setArguments(bundle);
        return frg;
    }

    public void setmSdk(ICcBeautyEngine sdk) {
        mSdk = sdk;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bind = FrgMakeupBinding.inflate(inflater, container, false);
        return bind.getRoot();
    }

}
