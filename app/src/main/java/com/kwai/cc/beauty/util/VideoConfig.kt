package com.kwai.cc.beauty.util

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File

/**
 *@author 周智慧
 *2022/12/29 20:19
 *@description:
 **/
object VideoConfig {
    private val TAG = "VideoConfig:zzh"
    private const val VIDEO_DIRECTORY = "video_files"

    private fun providesRecordVideoRootPath(context: Context): String =
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()) { // sd卡能用
            context.getExternalFilesDir("")!!.path
        } else { // sd卡不能用
            context.filesDir.path
        }

    fun getRecordVideoDir(context: Context): File {
        val file = File(providesRecordVideoRootPath(context), VIDEO_DIRECTORY)
        var result = file.mkdirs()
        Log.i(
            TAG,
            "record video file.mkdirs() result: ${result} ${file.exists()} ${file.name} ${file.path}"
        )
        return file
    }

    fun getGoodsRecordVideoFilePath(context: Context, goodsId: Int): String =
        getRecordVideoDir(context).absolutePath + File.separator + "goodsId_" + goodsId + ".mp4"
}