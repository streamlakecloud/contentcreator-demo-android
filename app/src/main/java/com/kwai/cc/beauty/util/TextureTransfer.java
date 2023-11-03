package com.kwai.cc.beauty.util;

import android.opengl.GLES20;
import android.opengl.GLES30;

import com.kwai.cc.beauty.tob.render.TextureInfo;

import org.wysaid.common.FrameBufferObject;
import org.wysaid.common.TextureDrawer;
import org.wysaid.common.TextureExternalOESDrawer;

/**
 * author: zhouzhihui
 * created on: 2023/7/20 17:09
 * description:
 * texture2d和OES纹理相互转换
 */
public class TextureTransfer {
    private static final String TAG = "TextureTransfer:zzh";
    private boolean mBack;
    private int mW;
    private int mH;

    private TextureExternalOESDrawer mOESDrawer;
    private TextureDrawer mDrawer;
    private FrameBufferObject mFbo;

    private TextureInfo mTexture;
    private int debugLogIndex;
    private int originalId;
    private Integer finalBeautyId;
    public void setSize(int w, int h) {
        mW = w;
        mH = h;
    }

    public int oesToTexture2D(int textureId) {
        originalId = textureId;
        // if (mOESDrawer == null) {
        //     mOESDrawer = TextureExternalOESDrawer.create();
        // }
        // mOESDrawer.setFlipScale(1, mBack ? 1 : -1);
        // mOESDrawer.setRotation(mBack ? -(float) Math.PI/2.0f : (float) Math.PI/2.0f);
        // if (mDrawer == null) {
        //     mDrawer = TextureDrawer.create();
        //     mDrawer.setRotation(mBack ? -(float) Math.PI/2.0f : (float) Math.PI);
        // }
        // if (mTexture == null) {
        //     mTexture = new TextureInfo();
        // }
        // if (mFbo  == null) {
        //     mFbo = new FrameBufferObject();
        // }
        // if (mTexture.width !=  mW || mTexture.height != mH) {
        //     if (mTexture.name > 0) {
        //         Common.deleteTextureID(mTexture.name);
        //     }
        //     mTexture.width = mW;
        //     mTexture.height = mH;
        //     mTexture.name = Common.genBlankTextureID(mW, mH);
        //     mFbo.bindTexture(mTexture.name);
        // }
        // mFbo.bind();
        // GLES20.glViewport(0, 0, mW, mH);
        // mOESDrawer.drawTexture(textureId);
        // if (debugLogIndex <= 10) {
        //     Log.i(TAG, "old textureid=" + textureId + " new textureid=" + mTexture.name);
        //     debugLogIndex++;
        // }
        //
        // // draw
        // GLES20.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        // GLES20.glViewport(0, 0, mW, mH);
        // // mDrawer.setFlipScale(-1, 1);
        // mDrawer.drawTexture(mTexture.name);
        // return mTexture.name;
        if (finalBeautyId != null) {
            // mFbo.bind();
            // GLES20.glViewport(0, 0, mW, mH);
            // // // mDrawer.setFlipScale(-1, 1);
            // mDrawer.drawTexture(finalBeautyId);
        }
        return originalId;
    }

    public void setBeautyId(int textureId) {
        if (textureId > 0) {
            finalBeautyId = textureId;
        }
    }

    public void draw(boolean back) {
        mBack = back;
        // if (mOESDrawer == null) {
        //     mOESDrawer = TextureExternalOESDrawer.create();
        // }
        // mOESDrawer.setFlipScale(-1, mBack ? 1 : -1);
        // mOESDrawer.setRotation(mBack ? -(float) Math.PI/2.0f : (float) Math.PI/2.0f);
        // if (mTexture == null) {
        //     mTexture = new TextureInfo();
        // }
        if (mFbo  == null) {
            mFbo = new FrameBufferObject();
        }
        // mFbo.bind();
        // GLES20.glViewport(0, 0, mW, mH);
        // mOESDrawer.drawTexture(originalId);
        if (finalBeautyId != null) {
            if (mDrawer == null) {
                mDrawer = TextureDrawer.create();
            }
            if (back) {
                // mDrawer.setRotation((float) -Math.PI);
                mDrawer.setFlipScale(1, 1);
            } else {
                // mDrawer.setRotation((float) Math.PI);
                mDrawer.setFlipScale(1, -1);
            }
            // mFbo.bind();
            GLES20.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
            GLES20.glViewport(0, 0, mW, mH);
            mDrawer.drawTexture(finalBeautyId);

            // GLES20.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
            // GLES20.glViewport(0, 0, mW, mH);
            // // mDrawer.setFlipScale(1, 1);
            // mDrawer.drawTexture(mTexture.name);
        } else {
            // GLES20.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
            // GLES20.glViewport(0, 0, mW, mH);
            // // mDrawer.setFlipScale(-1, 1);
            // mDrawer.drawTexture(mTexture.name);
        }
    }
}
