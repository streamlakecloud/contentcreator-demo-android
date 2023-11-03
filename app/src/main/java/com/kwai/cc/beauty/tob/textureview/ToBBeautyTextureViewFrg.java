package com.kwai.cc.beauty.tob.textureview;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kwai.cc.beauty.tob.BaseToBBeautyFrg;
import com.kwai.cc.beauty.tob.sdk.EngineMgr;
import com.kwai.cc.beauty.tob.sdk.ICcBeautyEngine;
import com.kwai.cc.beauty.util.CameraHelp2;
import com.streamlake.demo.databinding.TobFrgPreviewTextureviewBinding;

/**
 * author: zhouzhihui
 * created on: 2023/6/30 15:58
 * description:
 */
public class ToBBeautyTextureViewFrg extends BaseToBBeautyFrg {
    public static ToBBeautyTextureViewFrg newInstance() {
        ToBBeautyTextureViewFrg frg = new ToBBeautyTextureViewFrg();
        Bundle bundle = new Bundle();
        bundle.putString("aK", "aV");
        return frg;
    }

    private ICcBeautyEngine mCcBeautySdk;
    private CameraHelp2 mCameraHelp2;
    public boolean mBackCamera;

    private static final String TAG = "ToBBeautyTextureViewFrg:zzh";
    private TobFrgPreviewTextureviewBinding bind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bind = TobFrgPreviewTextureviewBinding.inflate(inflater);
        mCcBeautySdk = EngineMgr.createEngine(getContext());
        mCcBeautySdk.initInGlThread();
        mCameraHelp2 = new CameraHelp2(getContext(), null);
        mCameraHelp2.initCamera();
        int[] previewSize = mCameraHelp2.getPreviewSize();
        Size size = new Size(previewSize[1], previewSize[0]);
        bind.btnTakePhoto.setOnClickListener(v -> mCameraHelp2.shotPhoto());
        bind.cameraPreview.setPreviewSize(size);
        bind.cameraPreview.setKeepScreenOn(true);
        int tid = 1;
        bind.cameraPreview.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int width, int height) {
                Log.i(TAG, "texture view surface available surface=" + surfaceTexture + " thread=" + Thread.currentThread());
                int[] previewSize = mCameraHelp2.getPreviewSize();
                surfaceTexture.setDefaultBufferSize(previewSize[0], previewSize[1]);
                tryOpenCamera(); // textureview surface available
                // surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                //     @Override
                //     public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                //         Log.i(TAG, "texture view surface frame available=" + " thread=" + Thread.currentThread());
                //     }
                // });

                // new Thread(() -> {
                //     glGenTextures(1, new int[]{tid}, 0);
                //     surfaceTexture.attachToGLContext(tid);
                // }).start();
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
                Log.i(TAG, "texture view surface size changed w=" + width + " h=" + height);
            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                Log.i(TAG, "texture view surface destroy");
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
                // Log.i(TAG, "texture view surface update surface=" + surface + " thread=" + Thread.currentThread());
                // mCcBeautySdk.tryDeliverTextureFrame(surface, tid);
            }
        });
        bind.switchFace.setOnClickListener(v -> switchCamera());
        bind.seekBarBeauty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSelectedBeautifyConfig.mSmoothSkinConfig.mBright = progress;
                mSelectedBeautifyConfig.mSmoothSkinConfig.mRuddy = progress;
                mSelectedBeautifyConfig.mSmoothSkinConfig.mSoften = progress;
                mCcBeautySdk.adjustBeauty(mSelectedBeautifyConfig);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        return bind.getRoot();
    }

    private void tryOpenCamera() {
        if (bind.cameraPreview.getSurfaceTexture() != null) {
            // if (bind.preview.getSurfaceTexture() != null) {
            int[] previewSize = mCameraHelp2.getPreviewSize();
            bind.cameraPreview.getSurfaceTexture().setDefaultBufferSize(previewSize[0], previewSize[1]);
            mCameraHelp2.openCamera(mBackCamera, null, new Surface(bind.cameraPreview.getSurfaceTexture()));
            // mCameraHelp2.openCamera(useBack, null, new Surface(bind.preview.getSurfaceTexture()));
        }
    }

    private void switchCamera() {
        mCameraHelp2.closeCamera();
        mBackCamera = !mBackCamera;
        mCameraHelp2.openCamera(mBackCamera, null, new Surface(bind.cameraPreview.getSurfaceTexture()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
