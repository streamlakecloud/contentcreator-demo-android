package com.kwai.cc.beauty.tob.glview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import com.kwai.camerasdk.MediaCallback;
import com.kwai.camerasdk.models.DataExtractTriggerMode;
import com.kwai.camerasdk.models.DataExtractType;
import com.kwai.camerasdk.models.Transform;
import com.kwai.camerasdk.preprocess.CropAndFlipProcessor;
import com.kwai.camerasdk.preprocess.DataExtractProcessor;
import com.kwai.camerasdk.preprocess.WaterMarkProcessor;
import com.kwai.camerasdk.video.VideoFrame;
import com.kwai.camerasdk.videoCapture.FrameBuffer;
import com.kwai.cc.beauty.tob.ToBBeautyFrg;
import com.kwai.cc.beauty.tob.render.ContextFactory;
import com.kwai.cc.beauty.tob.sdk.EngineMgr;
import com.kwai.cc.beauty.tob.sdk.ICcBeautyEngine;
import com.kwai.cc.beauty.util.CameraHelp2;
import com.kwai.cc.beauty.util.TextureTransfer;
import com.kwai.cc.beauty.util.VideoConfig;

import org.wysaid.common.Common;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: zhouzhihui
 * created on: 2023/7/3 16:29
 * description:
 */
public class MyGlSurfaceView extends GLSurfaceView {
    private static final String TAG = "MyGlSurfaceView:zzh";
    private Context mContext;
    private ICcBeautyEngine mCcBeautySdk;
    public int mTextureId = -1;
    public boolean mBackCamera;
    private GLRender mGLRenderer;
    private CameraHelp2 mCameraHelp2;
    private Size sizeO;
    private File filePath;
    private ToBBeautyFrg.TakePhotoCallback mTakePhotoCallback;
    private ContextFactory mContextFactory;
    private EGLContext mEGLContext;
    private TextureTransfer mTextureTransfer = new TextureTransfer();
    public MyGlSurfaceView(Context context) {
        this(context, null);
    }

    public MyGlSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public void setTextureId(int textureId) {
        if (mTextureId == -1) {
            mTextureId = textureId;
            mCcBeautySdk.initInGlThread();
            // add processor
            DataExtractProcessor dataExtractProcessor = new DataExtractProcessor(DataExtractType.kDataExtractTypeTexture) {
                @Override
                public void onReceiveRawData(VideoFrame videoFrame) {
                    // Log.i(TAG, "on ReceiveRawData videoFrame=" + videoFrame + " txtId=" + videoFrame.textureId + " bitmap=" + videoFrame.bitmap + " zzhthread=" + Thread.currentThread());
                    receiveVideoFrame(videoFrame);
                }
            };
            // 总是请求，会影响性能，目前只用于拍状态中高斯蒙版
            dataExtractProcessor.setTriggerMode(DataExtractTriggerMode.kTriggerModeAlways);
            // mDaenerys.addGLPreProcessorAtGroup(mDataExtractProcessor, GlProcessorGroup.kMainGroup, true);

            CropAndFlipProcessor mCropAndFlipProcessor = new CropAndFlipProcessor();

            WaterMarkProcessor watermarkProcessor  = new WaterMarkProcessor();
            Bitmap image = com.kwai.cc.beauty.util.Utils.getBitmapFromAsset(mContext.getAssets(), "test.png");
            watermarkProcessor.setImage(image);
            watermarkProcessor.setScale(0.6f);
            watermarkProcessor.setStartPoint(new PointF(0.2f, 0.3f));
            // watermarkProcessor.setExternalFilterEnabled(true);

            // GlPreProcessorGroup mProcessorGroup = mDaenerys.createGlProcessorGroup();
            // mProcessorGroup.addProcessor(mCropAndFlipProcessor);
            // mProcessorGroup.addProcessor(mDataExtractProcessor);
            // mProcessorGroup.addProcessor(watermarkProcessor);
            // mProcessorGroup.addProcessor(mDataExtractProcessor);
            // mDaenerys.addGLPreProcessorAtGroup(mProcessorGroup, GlProcessorGroup.kMainGroup);
            mCcBeautySdk.addGLPreProcessor(watermarkProcessor, dataExtractProcessor);
            // set media callback
            MediaCallback mediaCallback = new MediaCallback() {
                @Override
                public void onVideoFrame(VideoFrame videoFrame) {
                    //上层获取videoframe
                    // Log.i(TAG, "on mediaCallback videoFrame=" + videoFrame + " txtId=" + videoFrame.textureId + " bitmap=" + videoFrame.bitmap);
                }
            };
            mCcBeautySdk.setMediaCallback(sizeO.getHeight(), sizeO.getWidth(), 30, mediaCallback);
        } else {
            Log.e(TAG, "cannot init twice sdk");
        }
    }

    public int getTextureId() {
        return mTextureId;
    }

    public void setmTakePhotoCallback(ToBBeautyFrg.TakePhotoCallback callback) {
        mTakePhotoCallback = callback;
    }

    private final InvocationHandler INVOCATION_HANDLER = new InvocationHandler() {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Class<?> retType = method.getReturnType();
            if (retType == EGLContext.class) {
                Log.i(TAG, "zzh invoke method=" + method.getName());
                mEGLContext = (EGLContext) method.invoke(mContextFactory, args);
                return mEGLContext;
            }
            return method.invoke(proxy, args);
        }
    };

    private void init() {
        filePath = new File(VideoConfig.INSTANCE.getRecordVideoDir(mContext).getAbsolutePath() + File.separator + "a-1689667105250_.jpeg"); // 人像测试
        // mContextFactory = new ContextFactory();
        // try {
        //     EGLContextFactory object = (EGLContextFactory) Proxy.newProxyInstance(mContextFactory.getClass().getClassLoader(), mContextFactory.getClass().getInterfaces(), INVOCATION_HANDLER);
        //     Log.i(TAG, "create eglcontext object=" + object.getClass());
        //     setEGLContextFactory(object); //need to use this function
        // } catch (Exception e) {
        //     Log.e(TAG, "create eglcontext error=", e);
        // }
        mCcBeautySdk = EngineMgr.createEngine(mContext);
        // mCcBeautySdk.setFetchEGLContext(() -> mEGLContext);
        mCameraHelp2 = new CameraHelp2(getContext(), new CameraHelp2.ICreateCaptureSession() {
            @Override
            public void createCaptureSessionSucc() {
                // restoreLastBeautifyConfig(true);
            }

            @Override
            public void createCaptureSessionFail() {
            }
        });
        mCameraHelp2.initCamera();
        int[] size = mCameraHelp2.getPreviewSize();
        sizeO = new Size(size[0], size[1]);
        setEGLContextClientVersion(3);
        mTextureTransfer.setSize(size[1], size[0]);
        mGLRenderer = new GLRender(getContext(), this);
        mGLRenderer.initData(sizeO);
        setRenderer(mGLRenderer);
        // setRenderMode(RENDERMODE_WHEN_DIRTY);
        mGLRenderer.setDrawFrameCallback(new GLRender.DrawFrameCallback() {
            @Override
            public void onDrawFrame(GL10 gl) {
                // Log.i(TAG, "on draw frame zzhthread=" + Thread.currentThread() + " looper=" + Looper.myLooper());
                int newTextureId = mTextureTransfer.oesToTexture2D(mTextureId);
                VideoFrame videoFrame = prePushFrame(newTextureId);
                mCcBeautySdk.deliverTextureFrame(videoFrame);
                mTextureTransfer.draw(mBackCamera);
            }
        });
    }

    private void receiveVideoFrame(VideoFrame videoFrame) {
        if (takePhoto) {
            if (videoFrame != null && mTakePhotoCallback != null) {
                Bitmap bb = Common.grabBitmapWithTexture(videoFrame.textureId, videoFrame.width, videoFrame.height);
                if (videoFrame.bitmap == null) {
                    videoFrame.bitmap = bb;
                }
                mTakePhotoCallback.onTakePhotoResult(videoFrame.bitmap);
            }
            takePhoto = false;
        }
        if (videoFrame != null) {
            mTextureTransfer.setBeautyId(videoFrame.textureId);
        }
        // requestRender();
    }

    public void openCamera() {
        SurfaceTexture.OnFrameAvailableListener listener = new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                // Log.i(TAG, "on frame available zzhthread=" + Thread.currentThread() + " looper=" + Looper.myLooper());
                // // 请求绘制每一帧数据, 这是主线程
                // int newTextureId = mTextureTransfer.oesToTexture2D(mTextureId);
                // VideoFrame videoFrame = prePushFrame(newTextureId);
                // mCcBeautySdk.tryDeliverTextureFrame(videoFrame);
                // requestRender();
                // mTextureTransfer.draw();
            }
        };
        mGLRenderer.getSurfaceTexture().setOnFrameAvailableListener(listener);
        mCameraHelp2.openCamera(mBackCamera, null, new Surface(getCurSurfaceTexture()));
    }

    public void switchCamera() {
        mCameraHelp2.closeCamera();
        mBackCamera = !mBackCamera;
        mCameraHelp2.openCamera(mBackCamera, null, new Surface(getCurSurfaceTexture()));
    }

    private SurfaceTexture getCurSurfaceTexture() {
        return mGLRenderer.getSurfaceTexture();
    }

    private boolean takePhoto;
    public void takePhoto() {
        takePhoto = true;
    }

    public void release() {
        mCameraHelp2.closeCamera();
        mCcBeautySdk.release();
        // mWesteros.dispose();
    }

    private VideoFrame prePushFrame(int textureId) {
        int w = sizeO != null ? sizeO.getHeight() : 1080;
        int h = sizeO != null ? sizeO.getWidth() : 2336;
        // Bitmap bb = Common.grabBitmapWithTexture(textureId, w, h);
        // bb.recycle();
        // getSurfaceTextureHelper().eglBase.makeCurrent();
        // getSurfaceTextureHelper().surfaceTexture.updateTexImage();
        VideoFrame videoFrame;
        // VideoFrame videoFrame = VideoFrame.fromTexture(textureId, false, 1080, 2214, TimeUnit.NANOSECONDS.toMillis(surfaceTexture.getTimestamp()));
        if (filePath.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(filePath.getAbsolutePath());
            // videoFrame = VideoFrame.fromBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), TimeUnit.NANOSECONDS.toMillis(surfaceTexture.getTimestamp()));
            ByteBuffer buffer = ByteBuffer.allocateDirect(bitmap.getByteCount());
            bitmap.copyPixelsToBuffer(buffer);

            FrameBuffer frameBuffer = new FrameBuffer(buffer);
            videoFrame = VideoFrame.fromCpuFrame(frameBuffer, bitmap.getWidth(), bitmap.getHeight(), VideoFrame.RGBA, 0);
            videoFrame.attributes.setFov(60);
            videoFrame.attributes.setIsCaptured(false);//区分camera数据流自动推帧
            videoFrame.attributes.setFromFrontCamera(false);
            videoFrame.attributes.setTransform(Transform.newBuilder().setMirror(false).setRotation(0));
        } else {
            videoFrame = VideoFrame.fromTexture(textureId, true, h, w, 0);
            videoFrame.attributes.setFov(60);
            videoFrame.attributes.setIsCaptured(false);//区分camera数据流自动推帧
            videoFrame.attributes.setFromFrontCamera(!mBackCamera);
            videoFrame.attributes.setTransform(Transform.newBuilder().setMirror(true).setRotation(270));
        }
        return videoFrame;
    }

    public ICcBeautyEngine getSdk() {
        return mCcBeautySdk;
    }

}
