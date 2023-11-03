# Android StreamLakeContentCreator接入文档

该demo工程提供了可直接运行的一个app项目，具体的接口调用时序请参照demo工程，包含了生产组件和播放器两个aar。

## 一 集成准备
<b>Step1</b>. AndroidStudio项目配置：  &nbsp;&nbsp;&nbsp;&nbsp;  
需要minSdkVersion>=24

<b>Step2</b>. 混淆配置配置：  
参考demo项目中的app/proguard-rules.pro。

<b>Step3</b>. 导入aar  
导入contentcreator.aar和播放器aar，并依赖这两个aar。具体参见附件里的demo：
:localaar:mediaplayer项目里的streamlake-mediaplayer-1.8.0.9.aar是播放器，项目中自带的有。  
:localaar:contentcreator项目里的contentcreator-release.aar是生产组件，这个aar需要我们手动传给客户，项目中没有这个aar，需要拿到这个aar放到该目录下。


<b>Step4</b>. execlude掉一些组件
如果您的仓库依赖以下组件，请先统一将其做exclude处理
```
configurations.all {
   exclude(group: 'com.facebook.fresco', module: 'imagepipeline-base')
   exclude(group: 'com.google.guava', module: 'listenablefuture')
   exclude group: "com.facebook.fresco", module: "animated-webp"
   exclude group: "com.facebook.soloader", module: "soloader"
   exclude group: "com.facebook.fresco", module: "animated-webp"
}
```


<b>Step5</b>. 添加所需权限权限	  
访问网络  
`<uses-permission android:name="android.permission.INTERNET"/>`

获取网络状态  
`<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>`

API>=33需要添加这三个权限：
```
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />
<uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />
```
   
<b>Step6</b>. 依赖公共三方库  
导入公共依赖，其中版本可能会有冲突，需要针对具体的case做一些class冲突错误的fix，具体参见demo中的 `app/common_dependency.gradle`


至此完成了接入的准备工作

## 二 初始化
在使用生产组件功能之前需要调用相关api完成初始化。对外的初始化接口主要涉及以下两个类：
CreativeEngine、KSMediaPlayerInit.

初始化主要分为以下几部分，大概顺序是
1. CreativeEngine.attachBaseContext(Application)：
   这主要是给生产组件的一些变量进行赋值操作，比如Context、appId、appKey、deviceId等。
   需要注意的是这个一行配置的初始化：
   SLCCGlobalConfig config = new SLCCGlobalConfig(KPN, SECURITY_PLUGIN_APP_KEY, SECURITY_PLUGIN_WB_KEY);
   这里有三个值：KPN、SECURITY_PLUGIN_APP_KEY、SECURITY_PLUGIN_WB_KEY由StreamLake提供，需要保证正确。

2. new KSMediaPlayerInit(this)：
   这里是初始化播放器组件，用到了第一步的参数KPN和SECURITY_PLUGIN_APP_KEY，和第一步保持一致，第三个参数deviceId可以由客户自己生成，sdk也提供了生成deviceId的方法：KSMediaPlayerConfig#generateDeviceId。

3. CreativeEngine.init(Application)：
   这一步主要是初始化生产组件的各个module；

4. CreativeEngine.launchActivityCreate(Activity, Bundle)：
   这一步初始化网络监听模块以及InitManagerImpl模块


## 三 生产组件使用
生产组件主要提供三个Activity页面：
1. 拍摄录制页
   调用代码示例,CreativeEngine#startCameraActivity：
   ```
   public static void startCameraActivity(Activity activity) {
      SLCCEntranceRouter.startCameraActivity(activity);
   }
   ```
2. 相册页
   调用代码示例CreativeEngine#startAlbumActivityInternal：
   ```
   public static void startAlbumActivityInternal(Activity activity) {
      SLCCEntranceRouter.startAlbumActivityInternal(activity);
   }
   ```
3. 编辑页
   调用代码示例CreativeEngine#startEditorActivityInternal：
   ```
   public static void startEditorActivityInternal(Activity activity, String path) {
      AlbumSelectedMediasHandler mMediasHandler = new AlbumSelectedMediasHandler((FragmentActivity) activity);
      List<QMedia> qMediaList = new ArrayList<>();
      QMedia item = new QMedia(1, path, 0, 0, 1);
      qMediaList.add(item);
      mMediasHandler.setExportCount(1);
      mMediasHandler.process(qMediaList, false, false, String.valueOf(Math.random()), "videoSelect", "videoSelectActivityId", false, false);
   }
   ```
其余API：
1. EditButler.INSTANCE.setEditCallback(EditCallback)：
   这一步是设置视频编辑的回调。该回调包含两个方法`onStartEdit`和`onOutput(String path)`。当sdk开始编辑的时候，会执行第一个回调。在视频编辑页，完成视频编辑后，生成一个mp4产物，存储在手机sd卡里，生产组件会把改视频文件的绝对路径用String类型的变量通过该接口的第二个方法通知给业务层，业务层拿到该String可以做一些业务的相关处理，比如上传、发布、发动态等。
2. 这个API可以监听视频的编辑进度，`data?.first`表示最终编辑生成的视频文件，`data?.second`范围0-1表示编辑进度，如果`data?.first != null`或者`data?.second == 1`表示进度完成：
```kotlin
EditButler.observeEdit(this, Observer<EditEvent<Pair<List<File?>, Float>>> { event ->
   LogUtils.i("observeEditFile", "publish zmg edit progress=${event}")
   if (event.seq != EditButler.curEventSeq()) {
       LogUtils.w("observeEditFile", "publish zmg edit event seq not equal")
       return@Observer
   }
   if (event.isVideo) {
       // editVideoFile(event)
   } else {
       // editMultiPictureFile(event)
   }
})
```
3. 由于音乐涉及到版权问题，客户自己自己设计服务端维护自己的音乐数据，端侧API：
```java
CreativeEngine.setCustomMusicService(MusicApiService customMusicService)
```
服务端接口设计参考文档[快手生产组件音乐接口数据格式](https://docs.qingque.cn/d/home/eZQDSChpaHF0uXkDlp1A_xltf?identityId=1wSl4usGyQN)。

## 四 生产组件美颜api
生产组件具有美颜能力，以及相关暴露给业务的美颜api接口。暴露给业务使用的美颜接口类是`ICcBeautyEngine.java`，使用接口之前需要构造出一个接口实例，可以参考demo项目的代码EngineMgr.java：`CcBeautyFactory.createDefaultSdk(context)`。有了实例，就可以调用美颜相关api。

总共有以下几个api：

1. ICcBeautyEngine#initInGlThread()
   初始化接口，需要在gl线程调用

1. ICcBeautyEngine#deliverTextureFrame(VideoFrame videoFrame)
   推帧接口

1. ICcBeautyEngine#addGLPreProcessor(AbstractPreProcessor... processors)添加processor插件

1. ICcBeautyEngine#adjustBeauty(BeautifyConfig beautifyConfig)调节美颜参数

1. ICcBeautyEngine#adjustFilter(FilterConfig config)调节滤镜参数

1. ICcBeautyEngine#adjustMagic(MagicEmoji.MagicFace face, String path)调节魔表参数

1. ICcBeautyEngine#release()释放美颜实例

api的具体调用细节可以参考demo项目里的`ToBBeautyFrg.java`和`MyGlSurfaceView.java`这两个类。

## 五 扩展功能
### 一 拍摄页底部Tab扩展功能
- #### Tab基本扩展能力
Tab的基本扩展能力包括设置Tab的字体、颜色、字号三个基本功能，通过`com.yxcorp.gifshow.camera.record.tab.DefaultConfig`三个属性实现
```java
public class DefaultConfig {
    public static int tvColorResId;
    public static int tvSize; // sp
    public static Typeface typeface;
}
```
- #### Tab高级扩展能力
在拍摄页底部，通过添加自定义的`com.yxcorp.gifshow.camera.record.tab.CameraTabItem`和`CameraTabHelper.addCustomTabs(List)`，可以拓展Tab的能力：
1. Tab的字体、字号、颜色可以单独设置，如果不设置，使用`DefaultConfig`里的全局值
2. Tab顺序：前两个（`多段拍`、`随手拍`）位置不可改动，后面的新增的tab可按需排位；
3. Tab支持自定义xml布局
4. 可以设置拍摄页的初始Tab：`intent.putExtra(CameraConstants.KEY_OPTION_DEFAULT_SELECT_TAB, CameraTabId.CAMERA_TAB_CUSTOM_ONE);`，默认是`intent.putExtra(CameraConstants.KEY_OPTION_DEFAULT_SELECT_TAB, CameraTabId.CAMERA_TAB_SNAPSHOT);`即随手拍。

需要实现`com.yxcorp.gifshow.camera.record.tab.CameraTabItem`的接口和接口功能说明：
```java
/**
 * 要添加的Tab的id，可以从预留的4个扩展Tab Id里选：
 * CameraTabId.CAMERA_TAB_CUSTOM_ONE
 * CameraTabId.CAMERA_TAB_CUSTOM_TWO
 * CameraTabId.CAMERA_TAB_CUSTOM_THREE
 * CameraTabId.CAMERA_TAB_CUSTOM_FOUR
*/ 
int getTabId();

/**
 * Tab的颜色资源id 
 */
default int textColorResId() {
   return View.NO_ID;
}

/**
 * Tab的字号大小，单位sp
 */
default int textSize() {
   return -1; // default text_size2=15sp
}

/**
 * Tab的字体
 */
default Typeface getTypeface() {
   return null;
}

/**
 * Tab点击事件
 */
default void click() {
}

/**
 * Tab文案
 */
default String getTabText() {
  return "TAB_ITEM_" + this.getTabId();
}

/**
 * 自定义布局xml资源id，如果提供自定义布局xml资源id，字体的颜色和字号将不再起作用，效果将完全由自定义布局决定
 */
default int getItemViewId() {
   return R.layout.camera_tab_item;
}

/**
 * Tab对应的Fragment
 */
default CameraItemFragment newFragment() {
   return null;
}
```

需要注意的有以下两点：
1. 必须提供tabId并且唯一，如果多个tab的id相同，SDK会作为一个tab处理；如果提供自定义布局xml，可以将xml的根View的id作为tabId。
2. 如果提供自定义布局xml资源id，字体的颜色和字号将不再起作用，效果将完全由自定义xml布局决定。

最后调用`CameraTabHelper.addCustomTabs(List<CameraTabItem>)`将自定义Tab添加给SDK。

## 六 常见问题&注意事项
1. 生产组件和开源sdk：com.noober.background:core 存在冲突，解决方案是把该sdk的代码和资源复制到客户的项目中，并把冲突的资源重命名，declare-styleable命名加上noober_前缀，
2. 生产组件可能会跟客户的一些so静态库冲突，可以在app/build.gradle中使用pickFirst 'lib/*/xxx.so'解决
3. 可能缺少依赖某些sdk导致找不到一些资源属性，比如这个报错：
AAPT error: attribute actualImageScaleType not found，解决办法是请参考demo中的 `app/common_dependency.gradle`加上fresco的的几个依赖

