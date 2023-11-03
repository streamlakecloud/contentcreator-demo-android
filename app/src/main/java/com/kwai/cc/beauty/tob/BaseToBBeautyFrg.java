package com.kwai.cc.beauty.tob;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kwai.feature.post.api.componet.prettify.beauty.BeautifyConfig;
import com.kwai.feature.post.api.componet.prettify.filter.model.FilterConfig;

import java.util.Arrays;

/**
 * author: zhouzhihui
 * created on: 2023/6/30 15:58
 * description:
 */
public class BaseToBBeautyFrg extends Fragment {
    public static BaseToBBeautyFrg newInstance() {
        BaseToBBeautyFrg frg = new BaseToBBeautyFrg();
        Bundle bundle = new Bundle();
        bundle.putString("aK", "aV");
        return frg;
    }

    private static final String TAG = "BaseToBBeautyFrg:zzh";
    protected BeautifyConfig mSelectedBeautifyConfig;

    protected FilterConfig mFilterConfig;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBeautyConfig();
        initFilterConfig();
    }

    private void initBeautyConfig() {
        mSelectedBeautifyConfig = new BeautifyConfig();
        mSelectedBeautifyConfig.mBrightItem = "blond";
        mSelectedBeautifyConfig.mEnableClarityAndStereo = false;
        mSelectedBeautifyConfig.mId = 2;
        mSelectedBeautifyConfig.mIsV4Down = false;
        mSelectedBeautifyConfig.mUseQuickBeauty = false;
        mSelectedBeautifyConfig.mVersion = 2;
        BeautifyConfig.SmoothSkinConfig smoothSkinConfig = new BeautifyConfig.SmoothSkinConfig();
        mSelectedBeautifyConfig.mSmoothSkinConfig = smoothSkinConfig;

        BeautifyConfig.DeformConfig deformConfig = new BeautifyConfig.DeformConfig();
        mSelectedBeautifyConfig.mDeformConfig = deformConfig;
    }

    private void initFilterConfig() {
        /*
        * {
          "minVersion": 0,
          "filterName": "copy_98108_26330276b82c450c987a33140095e571",
          "dimension": 8,
          "filterId": 38,
          "imageType": 0,
          "filterType": 1,
          "sourceType": 0,
          "resourceUrls": [
            [
              {
                "cdn": "bd.a.yximgs.com",
                "url": "https://bd.a.yximgs.com/bs2/ztMaterial/KUAISHOU_FILTER_94CA94BBF36249B9817DB64CB24A242D.png"
              },
              {
                "cdn": "ali2.a.yximgs.com",
                "url": "https://ali2.a.yximgs.com/bs2/ztMaterial/KUAISHOU_FILTER_94CA94BBF36249B9817DB64CB24A242D.png"
              }
            ]
          ],
          "intensity": 0.6,
          "autoDownload": true,
          "iconUrls": [
            {
              "cdn": "ali2.a.yximgs.com",
              "url": "https://ali2.a.yximgs.com/bs2/ztMaterial/STREAMLAKE_FILTER_EF819B34B39E444C805D9432A3B91966.png"
            },
            {
              "cdn": "bd.a.yximgs.com",
              "url": "https://bd.a.yximgs.com/bs2/ztMaterial/STREAMLAKE_FILTER_EF819B34B39E444C805D9432A3B91966.png"
            }
          ],
          "filterNameList": [
            "lookup_38_KUAISHOU_FILTER_94CA94BBF36249B9817DB64CB24A242D.png"
          ],
          "ratioIntensity": 1.0,
          "colorType": 2,
          "zipSourceFile": null,
          "displayName": "冷白皮"
        }
        * */
        mFilterConfig = new FilterConfig();
        mFilterConfig.mAutoDownload = true;
        mFilterConfig.mCanSaveAsLast = false;
        mFilterConfig.mChangeByExternal = false;
        mFilterConfig.mChangeIntensityByMagic = false;
        mFilterConfig.mColorFilterType = 2;
        mFilterConfig.mDimension = 8;
        mFilterConfig.mDisplayName = "冷白皮";
        mFilterConfig.mDisplayType = "人像";
        mFilterConfig.mEnhanceFilterParam = null;
        mFilterConfig.mFeatureId = 38;
        mFilterConfig.mFilterName = "copy_98108_26330276b82c450c987a33140095e571";
        mFilterConfig.mFilterResources = Arrays.asList("lookup_38_KUAISHOU_FILTER_94CA94BBF36249B9817DB64CB24A242D.png");
        mFilterConfig.mFilterResourcesUrl = Arrays.asList("https://bd.a.yximgs.com/bs2/ztMaterial/KUAISHOU_FILTER_94CA94BBF36249B9817DB64CB24A242D.png");
        mFilterConfig.setGroupId(1236);
        mFilterConfig.setGroupName("人像");
        mFilterConfig.mImageType = 0;
        mFilterConfig.mIntensity = 0; // 0-1
        mFilterConfig.mIntensityBeforeMagic = 0;
        mFilterConfig.mIntensityFromMagic = 0;
        // mFilterConfig.isDivider() = false;
        mFilterConfig.mMinVersion = 0;
        mFilterConfig.mOperationMaskTag = null;
        mFilterConfig.mPageType = 1;
        mFilterConfig.setPosition(7);
        mFilterConfig.mRatioIntensity = 1;
        mFilterConfig.mSourceType = 0;
        mFilterConfig.mThumbImageName = null;
        mFilterConfig.mThumbImageUrl = "https://tx2.a.yximgs.com/bs2/ztMaterial/STREAMLAKE_FILTER_EF819B34B39E444C805D9432A3B91966.png";
        mFilterConfig.mZipSourceFile = null;
    }

}
