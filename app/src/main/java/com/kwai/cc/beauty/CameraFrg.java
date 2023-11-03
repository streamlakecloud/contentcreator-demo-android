package com.kwai.cc.beauty;

/**
 * author: zhouzhihui
 * created on: 2023/6/30 09:57
 * description:
 */
public class CameraFrg {
    // private static final String TAG = "CameraFrg:zzh:whb";
    // private CcBeautyAct mActivity;
    // private ActCcBeautyBinding bind;
    // // protected CallerContext mCallerContext;
    // protected final EncodeConfig mEncodeConfig = getEncodeConfig();
    // protected final CameraConfig mCameraConfig = getCameraConfig();
    // public @NonNull CameraSDK mCameraHelper;
    // private CameraStateCallBack mCameraStateCallBack = new CameraStateCallBack() {
    //     @Override
    //     public void onCameraOpened() {
    //         Log.i(TAG, "on camera opened");
    //         mRecordBeautifyController.setCameraHelper(mCameraHelper);
    //     }
    //
    //     @Override
    //     public void onOpenCameraFailed(ErrorCode errorCode, Exception e) {
    //         Log.i(TAG, "on camera open failed");
    //     }
    //
    //     @Override
    //     public void onCameraClosed() {
    //         Log.i(TAG, "on camera closed");
    //     }
    //
    //     @Override
    //     public void onCameraPositionChanged() {
    //         Log.i(TAG, "on camera pos changed");
    //     }
    // };
    // public static CameraFrg newInstance() {
    //     CameraFrg frg = new CameraFrg();
    //     Bundle bundle = new Bundle();
    //     bundle.putString("aV", "aK");;
    //     frg.setArguments(bundle);
    //     return frg;
    // }
    //
    // @Override
    // public void onCreate(@Nullable Bundle savedInstanceState) {
    //     mActivity = (CcBeautyAct) getActivity();
    //     mCameraHelper = new CameraSDK(AppEnv.get().getAppContext(), mCameraStateCallBack, ResourceRestoreHelper.getInstance(), YtechLogReporter.YtechBussiness.CameraSdk);
    //     // mCameraHelper = mActivity.getCameraSDK();
    //     initBeautyController();
    //     super.onCreate(savedInstanceState);
    //     mRecordBeautifyController.onCreate(getActivity().getIntent());
    // }
    //
    // @Nullable
    // @Override
    // public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    //     bind = ActCcBeautyBinding.inflate(inflater);
    //     openCamera();
    //     bind.btnSwichCamera.setOnClickListener(v -> mCameraHelper.switchCamera());
    //     bind.btnShowBeautyFrg.setOnClickListener(v -> showBeautyFrg());
    //     return bind.getRoot();
    // }
    //
    // public void openCamera() {
    //     KLogRecordBase.get().i(TAG, "openCamera");
    //     bind.cameraView.getCameraView().getSurfaceView().resume();
    //     mCameraHelper.addCameraStatusListenerOnUIThread(() -> {
    //         boolean isCameraClosed = mCameraHelper.isClosed();
    //         CameraParameters newParameters = getOpenCameraParameter();
    //         if (isCameraClosed) {
    //             if (PermissionUtils.hasPermission(mActivity, Manifest.permission.CAMERA)) {
    //                 KLogRecordBase.get().w(TAG, " openCamera because it's closed");
    //                 CameraParameters parameters = null;
    //                 if (mCameraHelper.getRestoreEffects() != null) {
    //                     KLogRecordBase.get().i(TAG, "restore last camera status");
    //                     parameters = mCameraHelper.getCameraParameters();
    //                 }
    //                 if (parameters == null) {
    //                     parameters = newParameters;
    //                 }
    //                 BeautifyVersion beautifyVersion = null;
    //                 if (PluginManager.get(PrettifyPlugin.class).isAvailable()) {
    //                     beautifyVersion = PluginManager.get(PrettifyPlugin.class).getPostBeautyVersion().get();
    //                 }
    //                 MagicParams magicParams = new MagicParams.Builder()
    //                         .setBeautifyVersion(beautifyVersion)
    //                         .build();
    //                 mCameraHelper.open(bind.cameraView.getCameraView().getSurfaceView(), new VideoContext(), parameters, magicParams);
    //                 mCameraHelper.setEnableChangeFrameMode(true);
    //             } else {
    //                 KLogRecordBase.get().w(TAG, " does't has camera permission");
    //             }
    //         } else {
    //             KLogRecordBase.get().i(TAG, "camera already opened, resetCameraController");
    //             mCameraHelper.setSurfaceView(bind.cameraView.getCameraView().getSurfaceView());
    //             mCameraHelper.resetCameraController(newParameters);
    //         }
    //     });
    //     // addCameraStatusListenerOnUIThread不可嵌套，重载后，嵌套不可以保证Runnable执行顺序，
    //     mCameraHelper.addCameraStatusListenerOnUIThread(() -> {
    //         KLogRecordBase.get().i(TAG, "resumePreview in openCamera");
    //         mCameraHelper.resumePreview();
    //     });
    // }
    //
    // protected CameraPageConfig getCameraPageConfig() {
    //     return mCameraConfig.getRecordPageConfig();
    // }
    //
    // @Override
    // public AnimCameraView getAnimCameraView() {
    //     return bind.cameraView;
    // }
    //
    // protected CameraResolutionParameters getCameraResolutionParameters() {
    //     CameraResolutionParameters parameters = new CameraResolutionParameters();
    //     parameters.mPreviewWidth = getCameraPageConfig().mPreviewWidth;
    //     parameters.mPreviewHeight = getCameraPageConfig().mPreviewHeight;
    //     parameters.mPreviewMaxSize = getCameraPageConfig().mPreviewMaxEdgeSize;
    //     return parameters;
    // }
    //
    // public CameraParameters getOpenCameraParameter() {
    //     return CameraParamUtil.getOpenCameraParameter(
    //             mCameraConfig,
    //             mEncodeConfig,
    //             getCameraPageConfig(),
    //             getCameraResolutionParameters());
    // }
    //
    // RecordPrettifyFragment mRecordPrettifyFragment;
    // private void showBeautyFrg() {
    //     String tag = "mRecordPrettifyFragment";
    //     FragmentManager fragmentManager = getChildFragmentManager();
    //     if (mRecordPrettifyFragment == null) {
    //         mRecordPrettifyFragment = new RecordPrettifyFragment();
    //         PrettifyOption option = new PrettifyOption.Builder()
    //                 .forbid(ForbidOption.Companion.builder().build()).build();
    //         option.getFragmentOption().getSubFeatures().add(OptionConstant.SubFeature.BEAUTY);
    //         // option.getBeautyOption().setLastBeautyConfig(getCurrentConfig());
    //         // option.getBeautyOption().setIPrettifyLifecycleListener(this);
    //         option.getBeautyOption().setBeautyVersionType(BeautyVersionManager.instance().getPostBeautyVersion());
    //         // option.getBeautyOption().setBeautyFilterItems(generateRecordBeautyItems());
    //         option.getBeautyOption().setEnableBeautyV2(true);
    //         option.getBeautyOption().setIBeautyConfigRepo(new RecordBeautyRepoWrapper());
    //         option.getBeautyOption().setReportBeautyVersionInfo(true);
    //         option.getBeautyOption().setRecordSuiteMode(RecordBeautyVersionHelper.isNewVersionOn()
    //                 ? RecordBeautySuiteMode.ENABLE : RecordBeautySuiteMode.DISABLE);
    //         option.getBeautyOption()
    //                 .setEnableReorderBeautifySuit(PostExperimentUtils.enableReorderBeautifySuit());
    //         option.getBeautyOption().setIBeautyEventListener(mRecordBeautifyController);
    //         Bundle bundle = new Bundle();
    //         bundle.putSerializable(INTENT_KEY_PAGE_TYPE, CameraPageType.VIDEO);
    //         mRecordPrettifyFragment.setPrettifyOption(option);
    //         mRecordPrettifyFragment.setArguments(bundle);
    //     }
    //     if (mRecordPrettifyFragment.isAdded()) {
    //         mRecordPrettifyFragment.show(fragmentManager, tag);
    //         return;
    //     } else {
    //         fragmentManager.beginTransaction().replace(R.id.frgContainerBeauty, mRecordPrettifyFragment, tag).commitAllowingStateLoss();
    //     }
    // }
    //
    // @Override
    // protected List<ICameraController> buildControllers() {
    //     return new ArrayList<>();
    // }
    //
    // @Override
    // public CameraPageType getPageType() {
    //     return CameraPageType.VIDEO;
    // }
    //
    // @Override
    // public void onDestroy() {
    //     super.onDestroy();
    //     mCameraHelper.destroy();
    // }
    //
    // private void initBeautyController() {
    //     RecordPModuleRegister.getInstance().checkAndRun(RecordPModuleRegister.PRETTIFY,
    //             factory -> factory.beforeInitController(this, null));
    //     // mCallerContext = new CallerContext(this, mControllerManager);
    //     mRecordPrettifyController = new RecordPrettifyController(CameraPageType.VIDEO, mCallerContext);
    //     RecordControllersBridge.getInstance().setRecordController(RecordPrettifyController.class, mRecordPrettifyController);
    //     mRecordBeautifyController = new RecordBeautifyController(CameraPageType.VIDEO, mCallerContext, mRecordPrettifyController);
    // }
    //
    // private IControllerManager mControllerManager = new IControllerManager() {};
    // RecordPrettifyController mRecordPrettifyController;
    // RecordBeautifyController mRecordBeautifyController;
}
