package com.kwai.cc.beauty.tob.render;

import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

/**
 * author: zhouzhihui
 * created on: 2023/7/21 00:12
 * description:
 */
public class ContextFactory implements GLSurfaceView.EGLContextFactory {
    private static final String TAG = "ContextFactory:zzh";
    private static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
    private static final float glVersion = 3.3f;

    @Override
    public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig) {
        Log.w(TAG, "creating OpenGL ES $glVersion context");
        int[] arr = new int[]{EGL_CONTEXT_CLIENT_VERSION, (int) glVersion, EGL10.EGL_NONE};
        return egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, arr);
    }

    @Override
    public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
        egl.eglDestroyContext(display, context);
    }
}
