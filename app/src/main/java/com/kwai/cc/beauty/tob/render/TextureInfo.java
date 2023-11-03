package com.kwai.cc.beauty.tob.render;

import android.opengl.GLES30;
import android.util.Log;

import org.wysaid.common.FrameBufferObject;

/**
 * author: zhouzhihui
 * created on: 2023/3/22 21:50
 * description:
 */
public class TextureInfo {
    public int name;
    public int width;
    public int height;
    public long timeStamp;
    private long fenceGPUSync;
    private long fenceCPUSync;
    public FrameBufferObject framebuffer;

    public void addGPUSync() {
        if (fenceGPUSync != 0) {
            GLES30.glDeleteSync(fenceGPUSync);
        }
        fenceGPUSync = GLES30.glFenceSync(GLES30.GL_SYNC_GPU_COMMANDS_COMPLETE, 0);
        GLES30.glFlush();
    }

    public void addCPUSync() {
        if (fenceCPUSync != 0) {
            GLES30.glDeleteSync(fenceCPUSync);
        }
        fenceCPUSync = GLES30.glFenceSync(GLES30.GL_SYNC_GPU_COMMANDS_COMPLETE, 0);
    }

    public void waitSyncGPU() {
        if (fenceGPUSync != 0) {
            // int ret = GLES30.glClientWaitSync(fenceGPUSync, 0, GLES30.GL_TIMEOUT_IGNORED);
            // if (ret == GLES30.GL_WAIT_FAILED) {
            //     Log.e("zzh", "waitSync failed!");
            // }
            GLES30.glWaitSync(fenceGPUSync, 0, GLES30.GL_TIMEOUT_IGNORED);
            GLES30.glDeleteSync(fenceGPUSync);
            fenceGPUSync = 0;
        }
    }

    public void waitSyncCPU() {
        if (fenceCPUSync != 0) {
            int ret = GLES30.glClientWaitSync(fenceCPUSync, 0, GLES30.GL_TIMEOUT_IGNORED);
            if (ret == GLES30.GL_WAIT_FAILED) {
                Log.e("zzh", "waitSync CPU failed!");
            }
            GLES30.glDeleteSync(fenceCPUSync);
            fenceCPUSync = 0;
        }
    }

}
