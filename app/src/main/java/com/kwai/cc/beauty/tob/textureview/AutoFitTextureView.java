package com.kwai.cc.beauty.tob.textureview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Size;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by 周智慧 on 2023/4/2.
 */
public class AutoFitTextureView extends TextureView {
    private Size mPreviewSize;
    public AutoFitTextureView(@NonNull Context context) {
        this(context, null);
    }

    public AutoFitTextureView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoFitTextureView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AutoFitTextureView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
    }

    public void setPreviewSize(Size size) {
        mPreviewSize = size;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        if (mPreviewSize == null) {
            setMeasuredDimension(w, h);
        } else {
            setTransform(null);
            if (w < ((h * mPreviewSize.getWidth()) / mPreviewSize.getHeight())) {
                // 控件本身的宽小于 根据比例计算来得宽，则使用控件本身的宽
                setMeasuredDimension(w, (w * mPreviewSize.getHeight()) / mPreviewSize.getWidth());
            } else {
                setMeasuredDimension((h * mPreviewSize.getWidth()) / mPreviewSize.getHeight(), h);
            }
        }
    }
}
