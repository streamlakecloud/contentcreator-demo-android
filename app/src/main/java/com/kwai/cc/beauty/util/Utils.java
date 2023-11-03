package com.kwai.cc.beauty.util;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * author: zhouzhihui
 * created on: 2023/7/20 15:32
 * description:
 */
public class Utils {
    private static final String TAG = "Utils:zzh";
    public static Bitmap getBitmapFromAsset(AssetManager mgr, String path) {
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = mgr.open(path);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (final IOException e) {
            Log.e(TAG, "getBitmapFromAsset: ", e);
            bitmap = null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                    Log.e(TAG, "getBitmapFromAsset: ", ignored);
                }
            }
        }
        return bitmap;
    }
}
