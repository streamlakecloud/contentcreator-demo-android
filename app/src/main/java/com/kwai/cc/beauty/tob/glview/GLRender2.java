package com.kwai.cc.beauty.tob.glview;

/**
 * author: zhouzhihui
 * created on: 2023/7/3 16:57
 * description:
 */

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.util.Size;

import com.kwai.cc.beauty.util.OpenGLUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRender2 implements GLSurfaceView.Renderer {
    private static final String TAG = "GLRender:zzh";
    private GenderDraw mDrawer;
    private MyGlSurfaceView mMyGlSurfaceView;

    private Context mContext;

    private int BYTES_PER_FLOAT = 4;

    private String VERTEX_ATTRIB_POSITION = "aPosVertex";
    private int VERTEX_ATTRIB_POSITION_SIZE = 2;

    private String VERTEX_ATTRIB_TEXTURE_POSITION = "aTexVertex";
    private int VERTEX_ATTRIB_TEXTURE_POSITION_SIZE = 2;

    private String UNIFORM_TEXTURE = "s_texture";
    private String UNIFORM_VMATRIX = "vMatrix";

    private int mVertexLocation;
    private int mTextureLocation;
    private int mUTextureLocation;
    private int mVMatrixLocation;

    private Size mPreviewSize;

    private float[] mVertexCoord = {
            -1f, -1f,  // 左下
            1f, -1f,   // 右下
            -1f, 1f,   // 左上
            1f, 1f,    // 右上
    };

    // 纹理坐标（s,t）
    /*纹理坐标需要经过变换
     * (1).顺时针旋转90°
     * (2).镜像
     */
    public float[] mTextureCoord = {
            0.0f, 0.0f,  // 左下
            1.0f, 0.0f,  // 右下
            0.0f, 1.0f,  // 左上
            1.0f, 1.0f,  // 右上
    };

    public float[] vMatrix = new float[16];

    private FloatBuffer mVertexCoordBuffer;
    private FloatBuffer mTextureCoordBuffer;

    private int mShaderProgram;

    // 接收相机数据的纹理
    // 接收相机数据的 SurfaceTexture
    public SurfaceTexture mSurfaceTexture;

    public GLRender2(Context context, MyGlSurfaceView view) {
        mContext = context;
        mMyGlSurfaceView = view;
    }

    public void initData(Size size) {
        mPreviewSize = size;
    }

    // 向外提供 surfaceTexture 实例
    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mDrawer = new GenderDraw();
        int textureId = OpenGLUtils.getExternalOESTextureID();
        Log.i(TAG, "onSurfaceCreated() textureId=" + textureId);

        mMyGlSurfaceView.setTextureId(textureId);
        mSurfaceTexture = new SurfaceTexture(textureId);
        // mCameraProxy.setPreviewSurface(mSurfaceTexture);
        // mDrawer = new CameraDrawer();
        // 这么写不规范，先这么来吧，后续Camera流程要独立出来
        mMyGlSurfaceView.openCamera();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.v(TAG, "onSurfaceChanged(): " + width + " x " + height);
        /* glViewport(GLint x,GLint y,GLsizei width,GLsizei height)
         * x、y是指距离左下角的位置，单位是像素
         * 如果不设置width，height，他们的值是布局默认大小，渲染占满整个布局
         */
        // glViewport(0, 0, width, height);
        // glViewport(0, 450, mPreviewSize.getHeight(), mPreviewSize.getWidth());
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0, 0, 0, 0);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mSurfaceTexture.updateTexImage();
        mDrawer.draw(mMyGlSurfaceView.getTextureId(), !mMyGlSurfaceView.mBackCamera);
    }

}

