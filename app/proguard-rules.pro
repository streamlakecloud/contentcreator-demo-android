# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html
#
# Starting with version 2.2 of the Android plugin for Gradle, this file is distributed together with
# the plugin and unpacked at build-time. The files in $ANDROID_HOME are no longer maintained and
# will be ignored by new version of the Android plugin for Gradle.

# Optimizations: If you don't want to optimize, use the proguard-android.txt configuration file
# instead of this one, which turns off the optimization flags.
# Adding optimization introduces certain risks, since for example not all optimizations performed by
# ProGuard works on all versions of Dalvik.  The following flags turn off various optimizations
# known to have issues, but the list may not be complete or up to date. (The "arithmetic"
# optimization can be used if you are only targeting Android 2.0 or later.)  Make sure you test
# thoroughly if you go this route.
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-allowaccessmodification

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# Preserve some attributes that may be required for reflection.
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService
-keep public class com.google.android.vending.licensing.ILicensingService
-dontnote com.android.vending.licensing.ILicensingService
-dontnote com.google.vending.licensing.ILicensingService
-dontnote com.google.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep setters in Views so that animations can still work.
-keepclassmembers public class * extends android.view.View {
    void set*(***);
    *** get*();
}

-keep public class * extends android.app.Activity

# We want to keep methods in Activity that could be used in the XML attribute onClick.
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keep class com.kwai.hotfix.loader.** { *; }

# Preserve annotated Javascript interface methods.
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# The support libraries contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version. We know about them, and they are safe.
-dontnote android.support.**
-dontwarn android.support.**

# Understand the @Keep support annotation.
-keep class androidx.annotation.Keep
-keep class androidx.annotation.Keep

-keep @androidx.annotation.Keep class * {*;}
-keep @androidx.Keep class * {*;}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <init>(...);
}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <init>(...);
}

# hw 超级SDK
-keep class com.huawei.** { *; }

-keepattributes Signature,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod,*JavascriptInterface*
-dontwarn org.w3c.dom.*
-dontwarn org.slf4j.**
-dontwarn com.ning.**
-optimizationpasses 1
-optimizations !method/inlining/short
-ignorewarnings
-allowaccessmodification
-mergeinterfacesaggressively

-keepclassmembers class * extends android.webkit.WebChromeClient{
       public void openFileChooser(...);
}

-keepclassmembers class * extends com.kuaishou.webkit.WebChromeClient {
       public void openFileChooser(...);
}

-keep class com.squareup.wire.* {
        public <fields>;
        public <methods>;
}

# Tencent
#-dontwarn com.tencent.mm.**
-keep class com.tencent.mm.** {*;}
-keep class com.tencent.wxop.** {*;}

# Netty
-dontwarn io.netty.**

# OkHttp
-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-keepnames class com.squareup.okhttp.** {*;}
-keepnames class com.okio.** {*;}
-keep class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Serializable实体类
# 在这里去除掉common-log-proto包中的class，因为GeneratedMessageV3 implements Serializable
# 我们使用common-log-proto包只是用到了其中和client log content相关，全部keep会导致包大小增加过多
# 所有我们只keep有需要的类，而不是通过这种方式全keep
# TODO 未来可以梳理一下，是否还有implements Serializable但是不需要被keep的class，可以一定程度上减少包大小
# 未来发现有莫名其妙不想被keep的类被keep了可以看看是不是这条规则导致的

# 优化Serializable混淆配置, https://www.guardsquare.com/en/products/proguard/manual/examples
#-keepnames class * implements java.io.Serializable
#-keepclassmembers class !com.kuaishou.client.log.**,!com.kuaishou.log.**,!com.kuaishou.proto.**,!com.kuaishou.protobuf.** implements java.io.Serializable {
#    static final long serialVersionUID;
#    private static final java.io.ObjectStreamField[] serialPersistentFields;
#    !static !transient <fields>;
#    !private <fields>;
#    !private <methods>;
#    private void writeObject(java.io.ObjectOutputStream);
#    private void readObject(java.io.ObjectInputStream);
#    java.lang.Object writeReplace();
#    java.lang.Object readResolve();
#}
# 修复Serializable优化，导致Gson List<T>泛型解析失败
#-keep class * implements com.kwai.framework.model.response.ListResponse {
#    *;
#}

# 去掉运行时校验
-assumenosideeffects class com.smile.gifmaker.mvps.presenter.PresenterV2 {
    private void checkWholeViewAnnotation(...);
}

-assumenosideeffects class com.smile.gifshow.annotation.provider.v2.AccessorWrapper {
    private void checkForDuplication(...);
}

-assumenosideeffects class com.smile.gifmaker.mvps.utils.InjectDelegate {
    void injectCheck(...);
}

-assumenosideeffects class com.smile.gifmaker.mvps.utils.inject.InjectExtension {
    void checkInject(...);
}

# Support Library
#-keepnames class !android.support.v7.internal.view.menu.**,android.support.** {*;}
-keep class androidx.slidingpanelayout.widget.SlidingPaneLayout {*;}
-keep class androidx.viewpager.widget.ViewPager {*;}
-keepclassmembers class androidx.recyclerview.widget.RecyclerView {
    *;
}
-keep class androidx.recyclerview.widget.StaggeredGridLayoutManager {*;}
-keep class androidx.recyclerview.widget.RecyclerView$LayoutParams {*;}
-keep class androidx.core.widget.NestedScrollView {*;}
-keep public class * extends androidx.recyclerview.widget.RecyclerView$LayoutManager {
    public <init>(...);
}


## 风控 SDK
-keep class com.kuaishou.weapon.** {*;}


# Gson
-dontwarn com.google.gson.**
-keep class sun.misc.Unsafe { *; }

# 关闭proguard6.1.0后增加的gson优化，与主站冲突，会网络不可用
-optimizations !library/gson

-keepnames public class * extends java.lang.Exception

# tencent qvsdk
-keep class com.tencent.** { *; }

# daimajia.easing
# TODO 没必要
-keep class com.daimajia.easing.** { *; }
-keep interface com.daimajia.easing.** { *; }

# ButterKnife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**

-keep class androidx.multidex.** { *; }

# EventBus
-keepclassmembers class ** {
     public void onEvent*(**);
 }
-keepclassmembers class * extends de.greenrobot.event.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
# EventBus 3.0 annotation
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}

-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

# Netty相关
# Get rid of warnings about unreachable but unused classes referred to by Netty
-dontwarn org.jboss.netty.**
# Needed by commons logging
-keep class org.apache.commons.logging.* {*;}
#Some Factory that seemed to be pruned
-keep class java.util.concurrent.atomic.AtomicReferenceFieldUpdater {*;}
-keep class java.util.concurrent.atomic.AtomicReferenceFieldUpdaterImpl{*;}
#Some important internal fields that where removed
-keep class org.jboss.netty.channel.DefaultChannelPipeline{volatile <fields>;}
#A Factory which has a static factory implementation selector which is pruned
-keep class org.jboss.netty.util.internal.QueueFactory{static <fields>;}
#Some fields whose names need to be maintained because they are accessed using inflection
-keepclassmembernames class org.jboss.netty.util.internal.**{*;}

#kwaiplayer
# TODO 明显不合理
-keep class com.kwai.video.player.** { *; }
-dontwarn com.kwai.video.player.**
-keep class com.kwai.log.** { *; }
-keep class com.kwai.video.cache.** { *; }
-keep class com.kwai.video.hodor.** { *; }
-keep class com.kwai.player.debuginfo.** { *; }
-keep class com.kwai.debugtools.plugin.api.** { *; }

-keep class com.yxcorp.gifshow.media.builder.MP4Builder$ActionCallbackWrap
-keepclassmembers class com.yxcorp.gifshow.media.builder.MP4Builder$ActionCallbackWrap {
  *;
}
-keep class com.yxcorp.gifshow.media.MediaDecoder$ProgressChangedWrap
-keepclassmembers class com.yxcorp.gifshow.media.MediaDecoder$ProgressChangedWrap {
  *;
}
-dontwarn com.albinmathew.photocrop.photoview.**
-keep class android.webkit.JavascriptInterface {*;}
# 第三方平台SDK是通过反射加载的实例, 因此需要Keep
-keep class * extends com.yxcorp.plugin.login.base.ThirdPartyLoginPlatform {*;}

-keep public class com.yxcorp.bugly.**{*;}
-keep public class com.tencent.bugly.**{*;}

# push core
-keep class com.yxcorp.gifshow.push.model.* { *; }
# 个推
-dontwarn com.igexin.**
-keep class com.igexin.**{*;}
-keep class org.json.** { *; }
#魅族
-keep class com.meizu.** { *; }
-keep class com.yxcorp.gifshow.push.meizu.** { *; }
-dontwarn com.meizu.**
#华为
-keep class com.huawei.hms.** { *; }
-keep class com.yxcorp.gifshow.push.huawei.HuaweiPushManager {*;}
-dontwarn com.huawei.hms.**

# 小米
-keep class com.yxcorp.gifshow.push.xiaomi.XiaomiPushReceiver {*;}
-keep class com.xiaomi.mipush.sdk.MiPushClient {*;}
-keep class com.xiaomi.mipush.sdk.Logger {*;}

# 极光
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }

# AbTest
-keep class com.yxcorp.experiment.ABConfigJsonAdapter {*;}

# Baidu
-keep class com.baidu.** {*;}
-keep class mapsdkvi.com.** {*;}
-keep class vi.com.** {*;}
-keep class com.baidu.vi.** {*;}
-dontwarn com.baidu.**


# Fresco
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip
-keep class com.facebook.imagepipeline.core.ImagePipeline { *; }
-keep class com.facebook.imagepipeline.core.ProducerSequenceFactory { *; }
-keep class com.facebook.imagepipeline.producers.BitmapMemoryCacheGetProducer { *; }
-keep class com.facebook.imagepipeline.producers.ThreadHandoffProducer { *; }
-keep class com.facebook.imagepipeline.producers.BitmapMemoryCacheKeyMultiplexProducer { *; }
-keep class com.facebook.imagepipeline.producers.BitmapMemoryCacheProducer { *; }
-keep class com.facebook.imagepipeline.producers.LocalVideoThumbnailProducer { *; }
-keep class com.facebook.imageformat.DefaultImageFormatChecker { *; }
-keep class com.facebook.animated.gif.GifImage { *; }
-keep class com.facebook.animated.gif.GifFrame { *; }
# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}

-keep class bolts.Task { *; }

# Flex
-keep class com.kuaishou.flex.** {*;}
-keep class org.apache.el.ExpressionFactoryImpl { *; }
-keep,allowobfuscation @interface com.facebook.proguard.annotations.DoNotStrip
-keep,allowobfuscation @interface com.facebook.proguard.annotations.KeepGettersAndSetters

-keep @com.facebook.proguard.annotations.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.proguard.annotations.DoNotStrip *;
}

-keepclassmembers @com.facebook.proguard.annotations.KeepGettersAndSetters class * {
  void set*(***);
  *** get*();
}
# Flex end

# Keep native methods
# 注释掉, 会导致无用native方法被keep
#-keepclassmembers class * {
#    native <methods>;
#}

# 解决 retrofit 的 keep
-keep class com.yxcorp.gifshow.retrofit.service.** { *; }
-keep class okhttp3.Request { *; }
-keep class retrofit2.adapter.rxjava2.** { *; }
-keep class * implements retrofit2.Converter {*;}
-keepclassmembernames class com.yxcorp.retrofit.model.ResponseCall {
    retrofit2.Call *;
}
-keepclassmembernames class retrofit2.OkHttpCall {
    okhttp3.Call *;
}
-keepclassmembernames class okhttp3.RealCall {
    okhttp3.EventListener *;
}

# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain service method parameters.
-keepclassmembernames,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement


-keep class com.baidu.traffic.** { *; }
-keep class com.baidu.android.lbspay.** { *; }
-keep class com.baidu.personal.** { *; }
-keep class com.baidu.seclab.sps.** { *; }
-keep class com.baidu.transfer.** { *; }
-keep class vi.com.gdi.bgl.android.java.** { *; }
#-dontwarn com.baidu.searchbox.plugin.api.**
#如果接入nfc需要增加nfc的混淆配置 nfc start
-keep class com.baidu.nfc.** { *; }
#nfc end
# passsdk start
-keep class com.baidu.sapi2.** {*;}
-keepattributes JavascriptInterface
-keepattributes *Annotation*
#passsdk end
#baiduwallet end

#alipay start
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
#alipay end

# unionpay start
-dontwarn com.unionpay.**
-keep class com.unionpay.** {*;}
-keep class org.simalliance.openmobileapi.** {*;}
# unionpay end

#plugin
# TODO 明显不对
#-keep class * implements com.yxcorp.utility.plugin.Plugin {*;}

# TODO 明显不对
-keep class com.yxcorp.gifshow.log.db.** {*;}

# TODO 明显不对
-keep class com.yxcorp.gifshow.log.greendao.** {*;}
-keep class com.yxcorp.gifshow.log.realtime.** {*;}

-keep class com.yxcorp.gifshow.log.model.CommonParams {*;}

# proto 旧的keep逻辑
#-keep class com.kuaishou.client.log.content.packages.ClientContent** {*;}
# proto 新的keep逻辑（包体精简）
-keep enum com.kuaishou.client.log.content.packages.ClientContent$* {*;}
-keep enum com.kuaishou.client.log.content.packages.ClientContent$*$* {*;}
-keepclassmembers class com.kuaishou.client.log.content.packages.ClientContent$* {
    com.kuaishou.client.log.content.packages.ClientContent$* get* ();
    com.kuaishou.client.log.content.packages.ClientContent$*$* get* ();
}
-keep enum com.kuaishou.client.log.content.packages.ClientContentWrapper$*$* {*;}
-keepclassmembers class com.kuaishou.client.log.content.packages.ClientContentWrapper$* {
    com.kuaishou.client.log.content.packages.ClientContentWrapper$* get* ();
    com.kuaishou.client.log.content.packages.ClientContentWrapper$*$* get* ();
    com.kuaishou.client.log.content.packages.ClientContent$* get* ();
    com.kuaishou.client.log.content.packages.ClientContent$*$* get* ();
}

-keep class com.kuaishou.client.log.packages.ClientBase {*;}
-keep class com.kuaishou.client.log.packages.ClientBase$ThirdPartyPlatform {*;}

-keep class com.kuaishou.client.log.**.nano.** {*;}

#魔法表情
-keep class com.yxcorp.plugin.magicemoji.virtualface.** { *; }
-keep class org.wysaid.nativePort.** { *;}
-keep class com.kwai.FaceMagic.AE2.** { *;}
-keep class com.kwai.FaceMagic.nativePort.** { *;}
-keep class com.kwai.kscnnrenderlib.** { *;}
-keep class com.kwai.isomedia.** { *;}
-keep class com.viktorpih.** { *;}

-keepnames class com.yxcorp.gifshow.init.module.*

#watermark
-keep class okhttp3.Address { *; }
-keep class okhttp3.FormBody { *; }
-keep class okhttp3.internal.http.RealInterceptorChain { *; }

-keep class com.kwai.ksvideorendersdk.**  { *; }

-dontwarn com.sijla.**
-keep class com.sijla.**{*; }
-dontwarn com.q.**
-keep class com.q.Qt{*; }

#Tencent locate sdk
-keep class com.tencent.map.** {*;}
-keep class c.t.m.** {*;}

#MessageSDK
-keep class com.kwai.chat.kwailink.connect.** {*;}
# TODO 明显不对
-keep class com.kwai.chat.kwailink.utils.** {*;}
-keep class net.jpountz.** { *; }

#JieJun让加的
-keep class com.kwai.video.editorsdk2.** {*;}
-keep class com.kwai.ksvideorendersdk.**  { *; }
-keepclassmembers class com.kwai.video.editorsdk2.JpegBuilder {
    private void onProgress(double);
    private void onFinished(int);
}
-keepclassmembers class com.kwai.video.editorsdk2.PreviewPlayer {
    private void acceptPerfEntry(byte[]);
    private void onNativeEvent(byte[]);
    private byte[] onDumpedNextFrame(ByteBuffer, int, int);
    private void onTextureToRenderBeauty(int, int, byte[]);
    private void onReleaseBeauty();
}

-keepclassmembers class com.kwai.video.editorsdk2.ThumbnailGenerator {
    private void onNativeCallback(long, int, int, byte[]);
    private void onTextureToRenderBeauty(int, int, byte[]);
    private void onReleaseBeauty();
}

-keepclassmembers class com.kwai.video.editorsdk2.ExportTask {
    private void onNativeEvent(byte[]);
    private void onTextureToRenderBeauty(int, int, byte[]);
    private void onReleaseBeauty();
}

-keepclasseswithmembernames class com.kwai.video.editorsdk2.* {
    native <methods>;
}
# TODO 明显不对
-keep class com.kwai.ksvideorendersdk.** {
  *;
}

-keep class com.yxcorp.gifshow.widget.adv.VideoSDKPlayerView {*;}

-dontwarn java.lang.invoke.*
-dontwarn **$$Lambda$*

-keep class com.kwai.sdk.libkpg.KpgUtil {*;}

-keep class com.kwai.video.editorsdk2.AndroidPlatformImageLoader {*;}

# 跨平台项目Godzilla
# idc
-keep public class com.kuaishou.godzilla.Godzilla {
    *;
}
-keep public class com.kuaishou.godzilla.idc.KwaiIDCSelector {
    native <methods>;
}
-keep public class com.kuaishou.godzilla.idc.KwaiIDCHost {
    public <init>(java.lang.String, int, boolean);
    public <fields>;
}
-keep public class com.kuaishou.godzilla.idc.KwaiSpeedTestResult {
    public int mReponseCode;
    public java.lang.String mTspCode;
    public java.lang.String mException;
    public <init>(com.kuaishou.godzilla.idc.KwaiIDCHost, long, long, long, int, boolean, java.lang.String, java.lang.String);
}
-keep interface com.kuaishou.godzilla.idc.KwaiIDCStorage {
    *;
}
-keep interface com.kuaishou.godzilla.idc.KwaIDCSpeedTestCallback {
    *;
}
-keep interface com.kuaishou.godzilla.idc.KwaiSpeedTestRequest {
    *;
}
-keep interface com.kuaishou.godzilla.idc.KwaiSpeedTestRequestGenerator {
    *;
}
-keep public class com.kuaishou.godzilla.idc.KwaiIDCSpeedTester {
    native <methods>;
}

# parcel 的自动生成
-keep interface org.parceler.Parcel
-keep @org.parceler.Parcel class * { *; }
-keep class **$$Parcelable { *; }

# com.google.protobuf.nano
-keep class com.google.protobuf.nano.** {*;}
-keep class com.kuaishou.livestream.message.nano.** {*;}
-keep class com.kuaishou.protobuf.livestream.nano.** {*;}
-keep class com.kuaishou.merchant.message.nano.** {*;}
-keep class com.kuaishou.protobuf.merchant.message.nano.** {*;}
-keep class com.kuaishou.protobuf.gamezone.** {*;}
-keep class com.kuaishou.socket.nano.** {*;}
-keep class com.kuaishou.kwaishop.live.assistant.protobuf.nano.** {*;}

#oppo push
-keep public class * extends android.app.Service
-keep public class com.yxcorp.gifshow.push.oppo.OppoPushUtils {*;}
-keep class com.heytap.mcssdk.** {*;}

# xinge push
-keep public class * extends android.content.BroadcastReceiver
-keep class com.tencent.android.tpush.** {* ;}
-keep class com.tencent.mid.** {* ;}
-keep class com.qq.taf.jce.** {*;}

# vivo push sdk
-dontwarn com.vivo.push.**
-keep class com.vivo.push.**{*; }
-keep class com.vivo.vms.**{*; }
-keep class com.yxcorp.gifshow.push.vivo.**{*;}

#beta-sdk和jacoco覆盖率
-keep class com.yxcorp.beta.sdk.performance.DynamicData {*;}
-keep class org.jacoco.** {*;}

#emoji compat
-keep class android.support.text.emoji.** {*;}
-keep class androidx.emoji.text.** {*;}

# greendao
-keep class org.greenrobot.greendao.**{*;}
-keep public interface org.greenrobot.greendao.**
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties
-keep class net.sqlcipher.database.**{*;}
-keep public interface net.sqlcipher.database.**
-dontwarn net.sqlcipher.database.**
-dontwarn org.greenrobot.greendao.**
-dontwarn com.tencent.smtt.**

# Tencent Location SDK
-keepclassmembers class ** {
    public void on*Event(...);
}
-keep class c.t.**{*;}
-keep class com.tencent.map.geolocation.**{*;}
-keep class com.tencent.tencentmap.lbssdk.service.**{*;}

-dontwarn  org.eclipse.jdt.annotation.**
-dontwarn  c.t.**

# Arya
# TODO 明显不对
-keep class com.kwai.video.** { *; }

# CameraSdk
-keep class com.kuaishou.live.core.basic.pushclient.LiveCameraInitializer { *; }

# oppo 超级防抖 sdk
-keep class com.oplus.**{*;}
-keep class com.coloros.**{*;}

# live 通用配置
-keep class com.kuaishou.live.core.basic.config.LiveCommonConfigFetcher { *; }
-keep class com.kuaishou.protobuf.mmusound.soundrecognize.** {*;}
-keep interface com.kuaishou.protobuf.mmusound.soundrecognize.** {*;}
-keepclasseswithmembers class * {
    @com.kwai.camerasdk.annotations.CalledFromNative *;
}
-keepclassmembers class * {
    @com.kwai.camerasdk.annotations.CalledFromNative *;
}
-keepclasseswithmembers class * {
    @com.kwai.camerasdk.annotations.ReadFromNative *;
}
-keepclassmembers class * {
    @com.kwai.camerasdk.annotations.ReadFromNative *;
}

# condom
-dontwarn com.oasisfeng.condom.CondomContext$CondomContentResolver
-dontwarn com.oasisfeng.condom.ContentResolverWrapper
-dontwarn com.oasisfeng.condom.PackageManagerWrapper
-dontwarn com.oasisfeng.condom.PseudoContextWrapper
-dontwarn com.oasisfeng.condom.kit.NullDeviceIdKit$CondomTelephonyManager
-keep class com.oasisfeng.condom.**

# Performance sdk
-keep class kuaishou.perf.battery.allprocess.upload.** { *; }
-keep class kuaishou.perf.util.reflect.** { *; }
-keep class * extends kuaishou.perf.env.common.AbstractMonitor { *; }
-keep class kuaishou.perf.battery.allprocess.hooks.** { *; }
-keep class kuaishou.perf.activity.cpp.ClassHack { *; }
-keep class kuaishou.perf.block.systrace.hook.** { *; }
-keep class kuaishou.perf.fps.FrameRateMonitor { *; }
-keep class kuaishou.perf.mem.JvmHeapReporter { *; }
-keep class kuaishou.perf.fd.FileDescriptorReporter { *; }
-keep class kuaishou.perf.bitmap.**{*;}


# Pipeline sdk
# TODO 明显不对
-keep class com.ks.ksuploader.** { *; }
# marsdaemon native
-keep public class * extends com.marswin89.marsdaemon.NativeDaemonBase {*;}

# 广告模板
-keepnames class com.yxcorp.gifshow.ad.adview.*
-keep public class com.yxcorp.gifshow.ad.adview.* {
 public <init>(...);
}
-keep class * extends com.yxcorp.gifshow.photoad.model.AdDataWrapper {*;}
-keep class com.yxcorp.gifshow.commercial.adsdk.model.AdUrlInfo {*;}

-keep class cn.com.chinatelecom.account.api.**{*;}
-keep class com.kuaishou.android.security.**{*;}

-dontwarn com.cmic.sso.sdk.**
-keep class com.cmic.sso.sdk.** {*;}

# aop 需要用到
-keep interface com.yxcorp.image.ImageCallback {*;}

# 支付sdk  start
-keep class * extends com.yxcorp.gateway.pay.activity.BaseActivity {
    *;
}
-keep class com.yxcorp.gateway.pay.params.* {*;}
-keep class com.yxcorp.gateway.pay.api.* {*;}
-keep class com.yxcorp.gateway.pay.service.** { *; }
-keep class com.yxcorp.gateway.pay.withdraw.WithDrawHelper {*;}

-keep class com.tencent.mm.opensdk.** {*;}
-keep class com.tencent.wxop.** {*;}
-keep class com.tencent.mm.sdk.** {*;}

-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
-keep class com.alipay.sdk.app.H5PayCallback {
    <fields>;
    <methods>;
}
-keep class com.alipay.android.phone.mrpc.core.** { *; }
-keep class com.alipay.apmobilesecuritysdk.** { *; }
-keep class com.alipay.mobile.framework.service.annotation.** { *; }
-keep class com.alipay.mobilesecuritysdk.face.** { *; }
-keep class com.alipay.tscenter.biz.rpc.** { *; }
-keep class org.json.alipay.** { *; }
-keep class com.alipay.tscenter.** { *; }
-keep class com.ta.utdid2.** { *;}
-keep class com.ut.device.** { *;}
# 支付sdk end

# 小程序
-keep class com.eclipsesource.v8.** {*;}
-keep class com.kwai.kwapp.KWAppApi {
    public <methods>;
}
-keep class com.kwai.kwapp.KWAppConfig {*;}
-keep class com.kwai.kwapp.KWAppConfig$* {*;}
-keep class com.kwai.kwapp.component.Bean {*;}
-keep class com.kwai.kwapp.component.Bean$* {*;}
-keep class com.kwai.kwapp.component.KS*$* {*;}

## 安全组件+设备指纹
-keep class com.kuaishou.android.security.**{*;}

-keep class com.kuaishou.dfp.KWEGIDDFP {*;}
-keep class com.kuaishou.dfp.envdetect.jni.Watermelon {*;}
-keep class com.kuaishou.dfp.envdetect.Proxy.EngineProxy {*;}
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**
-keep class com.google.protobuf.** {*;}
-keep class com.bun.miitmdid.core.** {*;}

# 打包注入时候需要，不能混淆
-keep class com.yxcorp.utility.AssetUtils {*;}

# MMA SDK反射用到了v4包里的API
# todo mma support androidx
-keep class android.support.v4.content.ContextCompat { *; }
-keep class androidx.core.content.ContextCompat { *; }
# 执行了Aspect
-keep class com.airbnb.lottie.LottieTask {*;}
-keep class com.airbnb.lottie.LottieAnimationView {*;}

# proto生成的代码
-keep class * extends com.google.protobuf.GeneratedMessageLite { *; }


# 执行了Aspect
-keep class com.airbnb.lottie.LottieTask {*;}

# TODO 明显不对
-keep class com.kwai.kve.** {*;}


# flutter
-keep class io.flutter.** {*;}
-keep class com.kuaishou.flutter.** {*;}
-dontwarn io.flutter.**

-keep class com.yxcorp.gifshow.growth.keepalive.MIUIAlarmJobService$* {*;}

-keep class com.kwai.fastoat.** { *; }

-keep public class * extends android.app.Application {
    <init>(...);
    void attachBaseContext(android.content.Context);
}

-keepclasseswithmembernames public class * implements com.kwai.hotfix.entry.ApplicationLifeCycle {
    <init>(...);
    void onBaseContextAttached(android.content.Context);
}

-keepclasseswithmembernames public class * extends com.kwai.hotfix.loader.TinkerLoader {
    <init>(...);
}

-keep class com.kwai.hotfix.** {
  *;
}

# 生产编辑页贴纸的 info.json 文件解析出来的数据类
-keep class com.yxcorp.gifshow.v3.editor.sticker.jsonmodel.** {*;}

# Ensure that methods from LollipopSysdeps don't get inlined. LollipopSysdeps.fallocate references
# an exception that isn't present prior to Lollipop, which trips up the verifier if the class is
# loaded on a pre-Lollipop OS.
-keep class com.facebook.ld.SysUtil$LollipopSysdeps {
    public <methods>;
}

# 下面几个类预加载会用到，需要保留名字
-keepnames class com.yxcorp.gifshow.retrofit.service.KwaiApiService
-keepnames class com.kwai.framework.player.config.PhotoPlayerConfig
-keepnames class com.yxcorp.utility.RomUtils
-keepnames class com.yxcorp.gifshow.util.DateUtils

-keep class com.kwai.sdk.kbar.qrdetection.kbarImage { *; }
-keepclassmembers class com.kwai.sdk.kbar.qrdetection.JniQrCodeDetection$QRCodeEncodeSetting { *; }
-keep class com.sina.weibo.sdk.** { *; }

# Somehow android architecture lifecycle files getting omitted with Android bundle on Pie(Android9
# https://keep.corp.kuaishou.com/#/exception-analysis/android/crash/detail?pid=1&md5=9c34006ca1f14693b11afed5df09487b&queryParams=%7B%22start_time%22%3A1571106682977,%22end_time%22%3A1571365882977,%22appVersionName%22%3A%5B%226.9.1.11064%22%5D%7D
-keep class androidx.lifecycle.ProcessLifecycleOwnerInitializer { *; }
-keep class androidx.lifecycle.** {*;}

# TODO  明显不对
-keep class com.kuaishou.ax2c.** {*;}

-keepclassmembers class androidx.recyclerview.widget.GapWorker {
     void postFromTraversal(...);
}

-keep class org.wysaid.nativePort.** { *; }
-keep class com.kwai.FaceMagic.AE2.** { *;}
-keep class com.kwai.FaceMagic.nativePort.** { *; }
-keepclassmembers class org.wysaid.nativePort.** { *; }
-keepclassmembers class com.kwai.FaceMagic.AE2.** { *;}
-keepclassmembers class com.kwai.FaceMagic.nativePort.** { *; }
-keepclasseswithmembers class org.wysaid.nativePort.** { *; }
-keepclasseswithmembers class com.kwai.FaceMagic.AE2.** { *;}
-keepclasseswithmembers class com.kwai.FaceMagic.nativePort.** { *; }

# TODO  明显不对
-keep class com.kuaishou.ztgame.**{*;}

# TODO  明显不对
-keep interface com.kwai.stentor.Audio.** {*;}

###乐播投屏
###jmdns
-keep class javax.jmdns.** { *; }
-dontwarn javax.jmdns.**

###CyberGarage-upnp
-keep class org.cybergarage.** { *; }
-dontwarn org.cybergarage.**

###plist
-keep class com.dd.plist.** { *; }
-dontwarn com.dd.plist.**

###Lebo
-keep class com.hpplay.**{*;}
-keep class com.hpplay.**$*{*;}
-dontwarn com.hpplay.**
###乐播投屏

###hprof
-keep class com.kuaishou.oomkiller.dump.HprofDumpHacker { *; }
-keep class com.kuaishou.oomkiller.analysis.LeakModel { *; }
-keep class com.kuaishou.oomkiller.analysis.LeakModel$* { *; }
-keep class com.kuaishou.oomkiller.analysis.LeakModel$*$* { *; }
-keep class com.yxcorp.zcompress.** {*; }

-keep class com.kwai.performance.stability.hprof.dump.ForkJvmHeapDumper { *; }
-keep class com.kwai.performance.stability.hprof.dump.NativeHandler { *; }
-keep class com.kwai.performance.stability.oom.monitor.analysis.LeakModel { *; }
-keep class com.kwai.performance.stability.oom.monitor.analysis.LeakModel$* { *; }
-keep class com.kwai.performance.stability.oom.monitor.analysis.LeakModel$*$* { *; }

-keep class * extends com.kwai.performance.monitor.base.Monitor { *; }
###hprof



###############################################
# R8用到的规则，请勿随便改动引发bug
# 为了完备，尽可能保留了完整的规则，可能与其他规则重复
###############################################


# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>


# Retain generated class which implement Unbinder.
-keep public class * implements butterknife.Unbinder { public <init>(**, android.view.View); }

# Prevent obfuscation of types which use ButterKnife annotations since the simple name
# is used to reflectively look up the generated ViewBinding.
-keep class butterknife.*
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Gson 动态注册 TypeAdapter
-keepclassmembers class com.google.gson.Gson {
  java.util.List factories;
}

##---------------End: proguard configuration for Gson  ----------

# 解决webp动图565编码和首帧在主线程解码问题
-keep class com.facebook.fresco.animation.bitmap.BitmapAnimationBackend { *; }
-keep class com.facebook.fresco.animation.bitmap.preparation.DefaultBitmapFramePreparer { *; }
-keep class com.facebook.fresco.animation.bitmap.wrapper.AnimatedDrawableBackendFrameRenderer { *; }

########################################
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties {*;}

-keepclassmembers class **$Properties { public static <fields>;  }

#######################################################################################
# 注意这里与上面被注释掉的略有不同，最后增加了**，R8的proguard文件解析和proguard解析不完全一致
-keepclassmembers class !com.kuaishou.client.log.**,!com.kuaishou.log.**,!com.kuaishou.proto.**,!com.kuaishou.protobuf.**, ** implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep class * implements com.kwai.framework.model.response.ListResponse {
    *;
}

# 一些序列化方法用反射解析json或类似规则，如果enum不keep，反射会出错
-keepclassmembers enum * {*;}

# 这几个类被反射调用，且R8没有识别出来，单独Keep一下
-keep class androidx.slidingpanelayout.widget.SlidingPaneLayout { *; }
-keep class androidx.customview.widget.ViewDragHelper { *; }
-keep class androidx.fragment.app.DialogFragment { *; }

# R8规则结束

# 相册自定义UI proguard
-keep class com.yxcorp.gifshow.album.viewbinder.** {
    <init>(...);
}
-keep class * implements com.yxcorp.gifshow.base.fragment.IViewBinder {
    <init>(...);
}

-keep class com.kwai.component.photo.detail.core.helper.VideoPlayProgressHelper { *; }
# Flex解析本地模板Proguard
-dontwarn org.dom4j.**
-keep class org.dom4j.** { *; }

#InitModule的名字被作为id查询,只keep类名
-keep class * extends com.kwai.framework.init.InitModule

#下面的keep都是为了暂时解决r8在android 6.0上出的问题
-keep class com.kwai.framework.fileuploader.UploadUtils { *; }
-keep class com.yxcorp.gifshow.pymk.log.PymkLogSender { *; }
-keep class com.yxcorp.gifshow.util.config.ConfigHelper { *; }
-keep class com.kwai.component.realtime.startup.network.RealtimeStartupApi { *; }

## aiedit
-keep class com.kwai.aiedit.** { *; }
-keep class com.kwai.aieditlib.** { *; }
-keep class com.kwai.aieditrenderlib.** { *; }

#华为鸿蒙手表 wearengine sdk
-keep class com.huawei.wearengine.** { *; }

# handler post 监控
-keepclassmembers class io.reactivex.** {
    ** onNext;
    ** onError;
    ** onComplete;
    ** onSubscribe;
    ** parent;
    ** actual;
    ** downstream;
    ** delegate;
}

# 极速版proguard start

-keep class com.yxcorp.gifshow.HookIntent$ActivityManagerQ { *; }

# 极速版proguard end

# react-native-svg
-keep public class com.horcrux.svg.** {*;}

-keep class com.smile.gifshow.annotation.plugin.Factory { *; }

# sk2c,反射要用
-keep public class com.kuaishou.sk2c.** { *; }


# kswebview反射要用
# KsWebView
-keep class com.kuaishou.gifshow.kswebview.KsWebViewInitModule {
  static *** getCoreInitCallback(...);
}

# RxDogTag
-keep class com.uber.rxdogtag.RxDogTag { *; }

-keepclassmembers,allowobfuscation class * {
    <init>(***);
}

-keepclassmembers,allowobfuscation interface * {
    <methods>;
}

-keep,allowobfuscation,allowoptimization interface * {
    <methods>;
}

# OAID configs
-keep class XI.CA.XI.**{*;}
-keep class XI.K0.XI.**{*;}
-keep class XI.XI.K0.**{*;}
-keep class XI.xo.XI.XI.**{*;}
-keep class com.asus.msa.SupplementaryDID.**{*;}
-keep class com.asus.msa.sdid.**{*;}
-keep class com.bun.lib.**{*;}
-keep class com.bun.miitmdid.**{*;}
-keep class com.huawei.hms.ads.identifier.**{*;}
-keep class com.samsung.android.deviceidservice.**{*;}
-keep class com.zui.opendeviceidlibrary.**{*;}
-keep class org.json.**{*;}
-keep public class com.netease.nis.sdkwrapper.Utils {public <methods>;}

# WorkManager
-keep class androidx.work.impl.background.systemalarm.SystemAlarmScheduler {
        void scheduleWorkSpec(**);
}

# 效率工具sdk
-keep class com.kwai.sdk.etools_library.model.**{ *;}
-keep class com.kwai.sdk.etools_library.net.data.** { *;}

# 增长端上智能框架native方法
-keep class com.kwai.sdk.edge.**{*;}

# kwai 路由
-keep public class * extends com.kwai.platform.krouter.handler.UriHandler

# 直播弹窗管理通过反射调用了这两个类的变量
-keep class androidx.fragment.app.FragmentManagerImpl { *; }
-keep class androidx.fragment.app.FragmentHostCallback { *; }

# 列表页面框架通过反射访问了相关变量、方法
-keepclassmembers class androidx.recyclerview.widget.RecyclerView {<fields>;}
-keep class androidx.recyclerview.widget.RecyclerView$LayoutParams {*;}
-keep class androidx.recyclerview.widget.RecyclerView$ViewHolder {*;}
-keep class androidx.recyclerview.widget.ChildHelper {*;}
-keep class androidx.recyclerview.widget.ChildHelper$Bucket {*;}
-keep class androidx.recyclerview.widget.ViewInfoStore {*;}
-keep class androidx.recyclerview.widget.ViewInfoStore$InfoRecord {*;}
-keep class androidx.recyclerview.widget.RecyclerView$LayoutManager {*;}
-keep class androidx.recyclerview.widget.LinearLayoutManager {
    void ensureLayoutState();
    void resolveShouldLayoutReverse();
}

# 扫一扫sdk
-keep class com.kwai.sdk.kbar.qrdetection.DecodeRet { *; }
-keep class com.kwai.sdk.kbar.qrdetection.DecodeRet$Builder { *; }
-keep class com.kwai.sdk.kbar.qrdetection.KBarConfig { *; }
-keep class com.kwai.sdk.kbar.qrdetection.KBarConfig$Builder { *; }
-keep class com.kwai.sdk.kbar.qrdetection.KBarConfig$AspectRatio { *; }
-keep class com.kwai.sdk.kbar.qrdetection.KBarConfig$InputImgType { *; }
-keep class com.kwai.sdk.kbar.qrdetection.KBarConfig$DetectType { *; }
-keep class com.kwai.sdk.kbar.qrdetection.KBarConfig$CameraPosition { *; }
-keep class com.kwai.sdk.kbar.qrdetection.KBarConfig$DeviceOrientation { *; }

# 电商卖家端首页
-keep class * extends com.kuaishou.biz_home.homepage.viewbinder.BaseHomeVewBinder {
     <init>(...);
}

# 直播通用链路数据，通过反射构造了对象
-keep class * extends com.kuaishou.live.common.core.component.trace.common.LiveCommonTraceInfo {
     <init>(...);
}

# Eve-Executor
-keep class cn.vimfung.luascriptcore.** { *; }
-keep class com.kwai.eve.so.SoVm { *; }
-keep class com.kwai.sdk.eve.internal.common.utils.EveLog { *; }

# eve的pb
-keep class com.kwai.sdk.eve.proto.** { *; }

# Link
-keep class com.kuaishou.cover.Link
-keep class com.kuaishou.cover.protocol.** { *;}
-keep class com.kuaishou.cover.network.** { *;}



#Keep kotlin library
-keep,allowobfuscation class kotlin.** {
    <methods>;
    <fields>;
    <init>(***);
}

-keep,allowobfuscation class kotlinx.** {
    <methods>;
    <fields>;
    <init>(***);
}

-keepclassmembers,allowobfuscation class kotlin.** {
     <methods>;
     <fields>;
     <init>(***);
}

-keepclassmembers,allowobfuscation class kotlinx.** {
     <methods>;
     <fields>;
     <init>(***);
}

-keepclassmembers,allowobfuscation class * {
    <fields>;
    abstract <methods>;
    <init>(***);
}

-keepclassmembers,allowobfuscation interface * {
    <fields>;
    <methods>;
}

-keep class  master.flame.danmaku.ui.widget.DanmakuView {
       public void updateDanmaku(...);
 }
#-keep,allowobfuscation class * {
#    <fields>;
#    abstract <methods>;
#    <init>(***);
#}
#
#-keep,allowobfuscation interface * {
#    <fields>;
#    <methods>;
#}

# 智能走查自动化测试，只用于 test 环境
-keep class com.kwai.kid.** { *;}
-keep class com.kwai.sdk.etools_api.** { *;}
-keep class com.kwai.sdk.etools_library.** { *;}


#for streamlakeapp
-keep class com.kwai.creativeengine.** { *; }
-keep class * implements com.kwai.infra.ITraceHost {*;}
-keep class * implements com.kwai.infra.IKtraceHost {*;}
-keep class * implements com.kwai.infra.Segment {*;}
-keep class com.kwai.framework.app.AppEnv {*;}
-keep class androidx.recyclerview.widget.* {*;}
-keep class com.kuaishou.ksposter.callback.* {*;}
-keep class com.kuaishou.ksposter.base.* {*;}
-keep class com.kuaishou.ksposter.AppManager {*;}
-keep class com.kwai.framework.network.cronet.CronetManager {*;}
-keep class * implements com.kwai.framework.network.cronet.CronetManager {*;}
#-keep class com.kwai.framework.network.cronet.CronetManagerImpl$* {*;}
#-keep class com.kwai.framework.network.cronet.RequestDataUploader$* {*;}
-keep class androidx.appcompat.** { *; }
-keep class * extends com.kuaishou.aegon.Aegon$LibraryLoader {*;}
-keep class com.kwai.framework.network.cronet.CronetManager$* {*;}
-keep class com.kuaishou.ksposter.PostApplication {*;}
-keep class com.kuaishou.ksposter.StreamLakeContentCreator {*;}
-keep class com.facebook.**{*;}
-keep class aegon.chrome.*.**{*;}
-keep class com.yxcorp.gifshow.camera.**{*;}

#for gettag
-keep class com.smile.gifshow.annotation.provider.**{*;}
-keep class com.smile.gifshow.annotation.inject.**{*;}

-keep class com.kwai.camerasdk.**{*;}
-keep class com.kwai.video.westeros.**{*;}

-keep class com.yxcorp.gifshow.v3.EditorActivityAccessor{*;}
-keep class com.kwai.performance.stability.**{*;}
-keep class com.streamlake.slo.**{*;}
-keep class com.streamlake.SLCCLink{*;}
-keep class com.yxcorp.gifshow.music.network.**{*;}
-keep class com.streamlake.licensing.**{*;}
#for network request
-keep class com.kwai.middleware.resourcemanager.**{*;}
-keep class com.kuaishou.ksposter.SLCCGlobalConfig{*;}
-keep class com.yxcorp.passport.PassportManager{*;}

-keep class com.yxcorp.utility.singleton.Singleton{*;}
-keep class com.yxcorp.utility.plugin.PluginManager{*;}
-keep class com.yxcorp.utility.impl.ImplManager{*;}
-keep class com.getkeepsafe.relinker.**{*;}
-keep class com.yxcorp.gifshow.edit.text.network.**{*;}