package com.kwai.cc.beauty.tob.sdk;

import android.content.Context;

/**
 * author: zhouzhihui
 * created on: 2023/7/26 21:26
 * description:
 */
public class EngineMgr {
    public static ICcBeautyEngine createEngine(Context context) {
        return CcBeautyFactory.createDefaultSdk(context);
    }
}
