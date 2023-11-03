package com.streamlake.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.kuaishou.ksposter.SLCCGlobalConfig;
import com.kuaishou.ksposter.StreamLakeContentCreator;
import com.kuaishou.ksposter.base.SLCCEntranceRouter;
import com.kuaishou.ksposter.base.SLCCInitializer;
import com.kuaishou.ksposter.init.module.AzerothInitModule;
import com.kuaishou.ksposter.init.module.KwaiLinkInitModule;
import com.kuaishou.ksposter.init.module.PassportInitModule;
import com.kwai.framework.abtest.ABTestInitModule;
import com.kwai.framework.model.user.QCurrentUser;
import com.yxcorp.gifshow.camera.record.album.AlbumSelectedMediasHandler;
import com.yxcorp.gifshow.models.QMedia;
import com.yxcorp.gifshow.music.network.MusicApiService;
import com.yxcorp.gifshow.music.network.MusicService;
import com.yxcorp.utility.NetworkUtilsCached;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * author: zhouzhihui
 * created on: 2023/4/23 20:12
 * description:
 */
public class CreativeEngine {
    private static final String TAG = "CreativeEngine:zzheditor";
    public static final String SECURITY_PLUGIN_APP_KEY = "cab60836-a75f-4f9a-8af0-df09eefa1636";
    public static final String SECURITY_PLUGIN_WB_KEY = "aT7hhS4Sf";
    public static final String KPN = "STREAMLAKE";
    private static boolean initOk;

    public static void attachBaseContext(Application application) {
        Log.i(TAG, "content creator init1 proid=" + android.os.Process.myPid() + " proname=" + getProcessName());
        // AppEnv.mAppCustomer = AppEnv.APP_CUSTOMER_ZMG_CHINABLUETV;
        SLCCGlobalConfig config = new SLCCGlobalConfig(KPN, SECURITY_PLUGIN_APP_KEY, SECURITY_PLUGIN_WB_KEY);
        StreamLakeContentCreator.get().init(application, config);
    }

    public static void init(Application application) {
        Log.i(TAG, "content creator init2 proid=" + android.os.Process.myPid() + " proname=" + getProcessName() + " initOk=" + initOk);
        if (initOk) {
            return;
        }
        StreamLakeContentCreator.get().attach();
        com.yxcorp.utility.internal.BuildConfig.DEBUG = false;

        SLCCInitializer.get().executeBaseModuleInitialize(application);

        StreamLakeContentCreator.get().attachLicensing(application);
    }

    public static void launchActivityCreate(Activity activity, Bundle savedInstanceState) {
        if (initOk) {
            return;
        }
        initOk = true;
        NetworkUtilsCached.init(new HandlerThread("network_monitor"), 5000L);
        SLCCInitializer.get().onHomeActivityCreate(activity, savedInstanceState);
    }

    // 进入拍摄录制页
    public static void startCameraActivity(Activity activity) {
        SLCCEntranceRouter.startCameraActivity(activity);
    }

    // 进入相册页
    public static void startAlbumActivityInternal(Activity activity) {
        SLCCEntranceRouter.startAlbumActivityInternal(activity);
    }

    // 进入草稿页
    public static void startLocalDraft(Activity activity) {
        SLCCEntranceRouter.startLocalDraft(activity);
    }

    // 进入编辑页
    public static void startEditorActivityInternal(Activity activity, String path) {
        AlbumSelectedMediasHandler mMediasHandler = new AlbumSelectedMediasHandler((FragmentActivity) activity);
        List<QMedia> qMediaList = new ArrayList<>();
        QMedia item = new QMedia(1, path, 0, 0, 1);
        qMediaList.add(item);
        mMediasHandler.setExportCount(1);
        mMediasHandler.process(qMediaList, false, false, String.valueOf(Math.random()), "videoSelect", "videoSelectActivityId", false, false);
    }

    public static void setCustomMusicService(MusicApiService customMusicService) {
        MusicService.setCustomMusicService(customMusicService);
    }

    // 初始化语音识别模块子进程
    public static void initTtsProcess(Context context) {
        new PassportInitModule(context);
        new KwaiLinkInitModule(context);
    }

    // 初始化语音识别，上个方法仅仅是启动子进程并没有完成真正的初始化
    public static void reallyInitTts(Context context) {
        String proName = getProcessName();
        String sub = ":messagesdk";
        if ((context.getPackageName() + sub).equals(proName)) {
            Log.i(TAG, "zzh init tts in sub process=" + proName);
            StreamLakeContentCreator.get().attach();
            QCurrentUser.init();
            new AzerothInitModule().execute();
            new ABTestInitModule().execute();

            new PassportInitModule(context);
            new KwaiLinkInitModule(context);
        }
    }

//     public static void initEditOutputCallback(Activity mActivity) {
//         AppManager.get().setmEditOutputCallback(new EditOutputCallback() {
//             @Override
//             public void onOutput(String s) {
//                 Log.i(TAG, "zzh on edit output s=" + s);
//                 if (StringUtils.isEmpty(s)) {
//                     return;
//                 }
//                 VideoInfo mVideoInfo = new VideoInfo();
//                 mVideoInfo.setPath(s);
//                 mVideoInfo.setCreateTime(System.currentTimeMillis() + "");
//                 // 获取截取视频的第一帧图片
//                 MediaMetadataRetriever media = new MediaMetadataRetriever();
//                 // videoPath 本地视频的路径
//                 Bitmap bitmap;
//                 media.setDataSource(s);
//                 bitmap = media.getFrameAtTime();
//                 if (bitmap == null) {
//                     bitmap = media.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
//                 }
//                 String cover = FileUtils.saveBitmapToFile(mActivity, bitmap,
//                         System.currentTimeMillis() + ParamsManager.VideoCover);
//
// //            mVideoInfo.setWidth(Integer.valueOf(media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)));
// //            mVideoInfo.setHeight(Integer.valueOf(media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)));
// //            mVideoInfo.setDuration(60 * 1000);
//                 mVideoInfo.setCover(cover);
//
//                 SendDynamicDataBean sendDynamicDataBean = new SendDynamicDataBean();
//                 sendDynamicDataBean.setDynamicBelong(SendDynamicDataBean.NORMAL_DYNAMIC);
//                 List<ImageBean> pic = new ArrayList<>();
//                 ImageBean imageBean = new ImageBean();
//                 imageBean.setImgUrl(mVideoInfo.getCover());
//                 pic.add(imageBean);
//                 sendDynamicDataBean.setDynamicPrePhotos(pic);
// //            sendDynamicDataBean.setDynamicType(SendDynamicDataBean
// //                    .VIDEO_TEXT_DYNAMIC);
//                 sendDynamicDataBean.setVideoInfo(mVideoInfo);
//                 // 移除activity
//                 clearActivity();
//                 TheRouter.build(UgcConstant.PUBLISH_DYNAMIC.VIDEO_PATH)
//                         .withString(UgcConstant.PUBLISH_DYNAMIC.DYNAMIC_SOURCE, "5")
//                         .withString(UgcConstant.PUBLISH_DYNAMIC.DYNAMIC_SECTION, "")
//                         .withParcelable("video_data", sendDynamicDataBean)
//                         .navigation(mActivity);
//             }
//         });
//     }
//
//     public static void uninitEditOutputCallback() {
//         AppManager.get().setmEditOutputCallback(null);
//     }
//
//     private static void clearActivity() {
//         while (true) {
//             Activity activity = org.me.kevin.base.AppManager.getAppManager().currentActivity();
//             if (activity instanceof RecordAlbumActivity || activity instanceof CameraActivity || activity instanceof EditorActivity) {
//                 activity.finish();
//                 org.me.kevin.base.AppManager.getAppManager().removeActivity(activity);
//                 continue;
//             }
//             break;
//         }
//     }

    public static String getProcessName() {
        if (Build.VERSION.SDK_INT >= 28)
            return Application.getProcessName();

        // Using the same technique as Application.getProcessName() for older devices
        // Using reflection since ActivityThread is an internal API

        try {
            @SuppressLint("PrivateApi")
            Class<?> activityThread = Class.forName("android.app.ActivityThread");

            // Before API 18, the method was incorrectly named "currentPackageName", but it still returned the process name
            // See https://github.com/aosp-mirror/platform_frameworks_base/commit/b57a50bd16ce25db441da5c1b63d48721bb90687
            String methodName = Build.VERSION.SDK_INT >= 18 ? "currentProcessName" : "currentPackageName";

            Method getProcessName = activityThread.getDeclaredMethod(methodName);
            return (String) getProcessName.invoke(null);
        } catch (Exception e) {
            Log.e(TAG, "test code error=", e);
        }
        return "";
    }
}
