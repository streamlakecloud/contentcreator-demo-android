package com.kwai.cc.beauty.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.streamlake.demo.DemoApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhaoshuang on 2018/7/20.
 */

public class CameraHelp2 {
    // https://www.freecodecamp.org/news/android-camera2-api-take-photos-and-videos/
    private static final String TAG = "CameraHelp2:zzh";
    private Context mContext;
    private int defaultSize = DemoApplication.screenHeight * DemoApplication.screenWidth;
    private CameraManager cameraManager;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder previewRequestBuilder;
    protected int mCameraTexId;
    private Handler mHandler;
    private int[] mPreviewSize = new int[]{DemoApplication.screenHeight, DemoApplication.screenWidth};
    private CameraCaptureSession mCameraCaptureSession;
    private ImageReader mImageReader;
    private boolean mUseBack;
    private int mBackOrientation;
    private int mFrontOrientation;
    private String mFrontCameraId;
    private String mBackCameraId;
    private HandlerThread handlerThread;
    private ICreateCaptureSession mICreateCaptureSession;
    private CaptureImageCallback mCaptureImageCallback;

    public CameraHelp2(Context context, ICreateCaptureSession createCaptureSession) {
        this.mContext = context;
        cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        mICreateCaptureSession = createCaptureSession;
        // mCameraTexId = OpenGLUtil.createTextureObject(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
    }

    public void setCaptureImageCallback(CaptureImageCallback callback) {
        mCaptureImageCallback = callback;
    }

    public void initCamera() {
        try {
            String[] ids = cameraManager.getCameraIdList();
            String cameraId = "";
            for (String id : ids) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(id);
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                Integer cameraOrientation = characteristics.get(CameraCharacteristics.LENS_FACING);
                Integer sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                Log.i(TAG, "get camera id=" + id + " facing=" + cameraOrientation);
                Size[] outputSizes = null;
                if (map != null) {
                    outputSizes = map.getOutputSizes(SurfaceTexture.class);
                    if (outputSizes == null) {
                        outputSizes = map.getOutputSizes(ImageFormat.JPEG);
                    }
                }
                if (cameraOrientation != null && outputSizes != null) {
                    if (cameraOrientation == CameraCharacteristics.LENS_FACING_BACK && mBackCameraId == null) {
                        mBackCameraId = id;
                        mBackOrientation = sensorOrientation;
                    } else if (cameraOrientation == CameraCharacteristics.LENS_FACING_FRONT && mFrontCameraId == null) {
                        mFrontCameraId = id;
                        cameraId = id;
                        mFrontOrientation = sensorOrientation;
                    }
                } else {
                    Log.e(TAG, "camera id" + id + " error as facing=" + cameraOrientation + " outputSizes=" + outputSizes);
                }
                // https://blog.csdn.net/Jason_Lee155/article/details/126319221
                // 是否有闪光灯 FLASH_INFO_AVAILABLE
                // 是否有 AE 模式 CONTROL_AE_AVAILABLE_MODE
                //是否支持 Camera2 的高级特性
                Integer level = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                /**
                 * 不支持 Camera2 的特性
                 */
                if (level == null || level == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY){
                    //  Toast.makeText(this, "您的手机不支持Camera2的高级特效", Toast.LENGTH_SHORT).show();
                    //   break;
                }
            }
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            int[] size = handlePreviewSize(cameraId);
            if (size != null) {
                mPreviewSize = size;
            }
            Log.i(TAG, "init camera get perfect preview size=" + Arrays.toString(mPreviewSize));
            Log.i(TAG, "init camera back orien=" + mBackOrientation + " front orien=" + mFrontOrientation);
        } catch (Exception e) {
            Log.e(TAG, "init camera exception", e);
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    public void openCamera(boolean useBack, final ImageView imageView, Surface... surfaceList) {
        // if (mSurfaceList == null || mSurfaceList.size() <= 0) {
        //     Log.w(TAG, "open camera fail as must have at least one Surface target");
        //     return;
        // }
        if (mImageReader != null) {
            Log.w(TAG, "open camera fail as already opened");
            return;
        }
        startBackground();
        // mSurfaceList.clear();
        // if (surfaceList != null) {
        //     mSurfaceList.addAll(surfaceList);
        // }
        mUseBack = useBack;
        String cameraId = useBack ? mBackCameraId : mFrontCameraId;
        try {
            mImageReader = ImageReader.newInstance(mPreviewSize[1], mPreviewSize[0], ImageFormat.JPEG, 1);
            mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Log.i(TAG, "on image available thread=" + Thread.currentThread());
                    if (mCaptureImageCallback != null) {
                        mCaptureImageCallback.onCaptureImage(reader);
                    }
                    Image image = reader.acquireLatestImage();
                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                    byte[] imgs = new byte[buffer.remaining()];
                    buffer.get(imgs);
                    try {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imgs, 0, imgs.length);
                        Matrix matrix = new Matrix();
                        matrix.setRotate(useBack ? mBackOrientation : mFrontOrientation);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);

                        String photoPath = VideoConfig.INSTANCE.getRecordVideoDir(mContext).getAbsolutePath() + File.separator + System.currentTimeMillis() + ".jpeg";
                        FileOutputStream fos = new FileOutputStream(photoPath);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.close();
                    } catch (IOException e) {
                        Log.e(TAG, "on image available handle bitmap error", e);
                        e.printStackTrace();
                    }
                    image.close();
                }
            }, mHandler);

            cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    Log.i(TAG, "on camera opened");
                    mCameraDevice = camera;
                    createCameraSession(surfaceList);
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    Log.i(TAG, "on camera disconnected");
                    camera.close();
                    mCameraDevice = null;
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    Log.i(TAG, "on camera error");
                    camera.close();
                    mCameraDevice = null;
                }

                @Override
                public void onClosed(@NonNull CameraDevice camera) {
                    Log.i(TAG, "on camera closed");
                    super.onClosed(camera);
                }
            }, mHandler);
        } catch (Exception e) {
            Log.e(TAG, "open camera exception", e);
            e.printStackTrace();
        }
    }

    public void closeCamera() {
        Log.d(TAG, "closeCamera. thread:" + Thread.currentThread().getName());
        try {
            if (mCameraCaptureSession != null) {
                mCameraCaptureSession.close();
                mCameraCaptureSession = null;
            }
            if (mCameraDevice != null) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (mImageReader != null) {
                mImageReader.close();
                mImageReader = null;
            }
            stopBackGround();
        } catch (Throwable e) {
            Log.e(TAG, "closeCamera error thread:" + Thread.currentThread().getName(), e);
        }
    }

    private void startBackground() {
        handlerThread = new HandlerThread("CameraHelp2");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
    }
    private void stopBackGround() {
        if (handlerThread != null) {
            handlerThread.quitSafely();
            try {
                handlerThread.join();
                handlerThread = null;
                mHandler = null;
            } catch (Exception e) {
                Log.e(TAG, "stop back ground error=", e);
            }
        }
    }

    public void shotPhoto() {
        try {
            CaptureRequest.Builder captureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureRequestBuilder.addTarget(mImageReader.getSurface());
            // 自动对焦
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            // 自动曝光
            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, mUseBack ? mBackOrientation : mFrontOrientation);
            CaptureRequest captureRequest = captureRequestBuilder.build();
            mCameraCaptureSession.capture(captureRequest, null, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void openFlash() {
        try {
            previewRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
            CaptureRequest previewRequest = previewRequestBuilder.build();
            mCameraCaptureSession.setRepeatingRequest(previewRequest, null, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void closeFlash() {
        try {
            previewRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
            CaptureRequest previewRequest = previewRequestBuilder.build();
            mCameraCaptureSession.setRepeatingRequest(previewRequest, null, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createCameraSession(Surface[] surfaceList) {
        try {
            previewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            List<Surface> list = new ArrayList<>();
            Log.i(TAG, "create camera session surface list size=" + surfaceList.length);
            for (int i = 0; i < surfaceList.length; i++) {
                list.add(surfaceList[i]);
                previewRequestBuilder.addTarget(surfaceList[i]);
            }
            // surfaceList.forEach(s -> {
            //     list.add(s);
            //     previewRequestBuilder.addTarget(s);
            // });
            list.add(mImageReader.getSurface());
            mCameraDevice.createCaptureSession(list, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Log.i(TAG, "create camera session on configured thread=" + Thread.currentThread() + " ismain=" + isMainThread());
                    try {
                        mCameraCaptureSession = cameraCaptureSession;
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        CaptureRequest previewRequest = previewRequestBuilder.build();
                        cameraCaptureSession.setRepeatingRequest(previewRequest, null, mHandler);
                        if (mICreateCaptureSession != null) {
                            mICreateCaptureSession.createCaptureSessionSucc();
                        }
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onConfigureFailed exception: ", e);
                        if (mICreateCaptureSession != null) {
                            mICreateCaptureSession.createCaptureSessionFail();
                        }
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Log.e(TAG, "onConfigureFailed: " + session);
                    if (mICreateCaptureSession != null) {
                        mICreateCaptureSession.createCaptureSessionFail();
                    }
                }
            }, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //摄像大小
    private int[] handlePreviewSize(String cameraId) {
        CameraCharacteristics characteristics = null;
        try {
            characteristics = cameraManager.getCameraCharacteristics(cameraId);
        } catch (CameraAccessException e) {
            Log.e(TAG, "get camera characteristics error", e);
        }
        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        Size[] sizes = map.getOutputSizes(ImageFormat.JPEG);
        if (sizes == null) {
            sizes = map.getOutputSizes(SurfaceTexture.class);
        }
        if (sizes == null) {
            return null;
        }
        int[] previewSize = new int[2];
        boolean flag = false;
        for (Size size : sizes) {
            int w = size.getWidth();
            int h = size.getHeight();
            Log.i(TAG, "cameraId=" + cameraId + " w=" + w + " h=" + h);
            if (w * h == defaultSize) {
                previewSize[0] = w;
                previewSize[1] = h;
                flag = true;
            }
        }
        if (!flag) {
            int difference = 0;
            int position = 0;
            for (int x = 0; x < sizes.length; x++) {
                Size size = sizes[x];
                int n = Math.abs(defaultSize - size.getWidth() * size.getHeight());
                if (x == 0 || difference > n) {
                    difference = n;
                    position = x;
                }
            }
            previewSize[0] = sizes[position].getWidth();
            previewSize[1] = sizes[position].getHeight();
        }
        Log.i(TAG, "get preview size=" + Arrays.toString(previewSize) + " flag=" + flag);
        return previewSize;
    }

    public int[] getPreviewSize() {
        return mPreviewSize;
    }

    private byte[] getYUVI420(Image image) {

        int width = image.getWidth();
        int height = image.getHeight();

        byte[] yuvI420 = new byte[image.getWidth() * image.getHeight() * 3 / 2];

        byte[] yData = new byte[image.getPlanes()[0].getBuffer().remaining()];
        byte[] uData = new byte[image.getPlanes()[1].getBuffer().remaining()];
        byte[] vData = new byte[image.getPlanes()[2].getBuffer().remaining()];
        image.getPlanes()[0].getBuffer().get(yData);
        image.getPlanes()[1].getBuffer().get(uData);
        image.getPlanes()[2].getBuffer().get(vData);

        System.arraycopy(yData, 0, yuvI420, 0, yData.length);
        int index = yData.length;

        for (int r = 0; r < height / 2; ++r) {
            for (int c = 0; c < width; c += 2) { //各一个byte存一个U值和V值
                yuvI420[index++] = uData[r * width + c];
            }
        }
        for (int r = 0; r < height / 2; ++r) {
            for (int c = 0; c < width; c += 2) { //各一个byte存一个U值和V值
                yuvI420[index++] = vData[r * width + c];
            }
        }
        return yuvI420;
    }

    public static interface ICreateCaptureSession {
        void createCaptureSessionSucc();
        void createCaptureSessionFail();
    }

    public interface CaptureImageCallback {
        void onCaptureImage(ImageReader reader);
    }

    public boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

}
