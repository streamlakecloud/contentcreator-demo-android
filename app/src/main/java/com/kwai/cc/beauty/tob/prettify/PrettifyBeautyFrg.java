package com.kwai.cc.beauty.tob.prettify;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kwai.cc.beauty.tob.sdk.ICcBeautyEngine;
import com.kwai.feature.post.api.componet.prettify.beauty.BeautifyConfig;
import com.kwai.feature.post.api.componet.prettify.beauty.BeautifyIds;
import com.streamlake.demo.databinding.FrgPrettifyBinding;
import com.streamlake.processor.contentcreator.R;
import com.yxcorp.gifshow.prettify.beauty.BeautyFilterItem;
import com.yxcorp.gifshow.prettify.utils.LightBeautyUtil;
import com.yxcorp.gifshow.prettify.utils.NatureSuiteUtil;
import com.yxcorp.gifshow.prettify.utils.PrettifyUtils;
import com.yxcorp.gifshow.prettify.v4.prettify.widget.PrettifyDoubleSeekBar;

import java.util.List;

/**
 * author: zhouzhihui
 * created on: 2023/7/20 20:08
 * description:
 */
public class PrettifyBeautyFrg extends Fragment {
    private static final String TAG = "PrettifyBeautyFrg:zzh";
    private FrgPrettifyBinding bind;
    public PrettifyDoubleSeekBar mPrettifyDoubleSeekBar;
    private ICcBeautyEngine mSdk;
    private BeautyItemAdapter mBeautyItemAdapter;
    protected BeautifyConfig mSelectedBeautifyConfig = new BeautifyConfig();
    protected BeautifyConfig mClearConfig = new BeautifyConfig();

    public static PrettifyBeautyFrg newInstance() {
        PrettifyBeautyFrg frg = new PrettifyBeautyFrg();
        Bundle bundle = new Bundle();
        bundle.putString("aV", "aK");
        frg.setArguments(bundle);
        return frg;
    }

    public void setmSdk(ICcBeautyEngine sdk) {
        mSdk = sdk;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bind = FrgPrettifyBinding.inflate(inflater, container, false);
        bind.btnClearBeauty.setOnClickListener(v -> clearBeautyEffect());
        mPrettifyDoubleSeekBar = bind.includeBeautyBar.findViewById(R.id.filter_double_seek_bar);
        mPrettifyDoubleSeekBar.setVisibility(View.VISIBLE);
        mPrettifyDoubleSeekBar.setOnSeekBarChangeListener(new PrettifyDoubleSeekBar.OnPrettifyDoubleSeekBarChangeListener() {
            @Override
            public void onProgressChanged(PrettifyDoubleSeekBar seekBar, int progress) {
                BeautyFilterItem selectItem = getCurItem();
                float value = getFilterValueInternal(selectItem, progress, seekBar.getMaxProgress());
                Log.i(TAG, "progress changed beauty proP=" + progress + " value=" + value);
                selectItem.setFilterValue(mSelectedBeautifyConfig, value);
                applyBeautyItem();
            }

            @Override
            public void onStartTrackingTouch(PrettifyDoubleSeekBar seekBar, int progress) {
            }

            @Override
            public void onStopTrackingTouch(PrettifyDoubleSeekBar seekBar, int progress) {
            }
        });
        initList();
        bind.filterSeekBar.setOnSeekBarChangeListener(new PrettifyDoubleSeekBar.OnPrettifyDoubleSeekBarChangeListener() {
            @Override
            public void onProgressChanged(PrettifyDoubleSeekBar seekBar, int progress) {
                BeautyFilterItem selectItem = getCurItem();
                refreshBeautyItemValueText();
                float value = getFilterValueInternal(selectItem, progress, seekBar.getMaxProgress());
                Log.i(TAG, "progress changed proP=" + progress + " value=" + value);
                selectItem.setFilterValue(mSelectedBeautifyConfig, value);
                applyBeautyItem();
            }

            @Override
            public void onStartTrackingTouch(PrettifyDoubleSeekBar seekBar, int progress) {
            }

            @Override
            public void onStopTrackingTouch(PrettifyDoubleSeekBar seekBar, int progress) {
            }
        });
        return bind.getRoot();
    }

    // 更新滑杆提示文字
    private void refreshBeautyItemValueText() {
        int progress = bind.filterSeekBar.getProgress();
        bind.filterSeekBar.setProgress(progress, String.valueOf(progress));
    }

    private float getFilterValueInternal(BeautyFilterItem beautyFilterItem, int progress, int max) {
        boolean mIsLightBeautyConfig = false;
        boolean mIsNatureBeautyConfig = false;
        if (mIsLightBeautyConfig) {
            return LightBeautyUtil.getFilterValue(beautyFilterItem, progress, max);
        } else if (mIsNatureBeautyConfig) {
            return NatureSuiteUtil.getFilterValue(beautyFilterItem, progress, max);
        } else {
            return beautyFilterItem.getFilterValue(progress, max);
        }
    }

    private void initList() {
        mBeautyItemAdapter = new BeautyItemAdapter(new BeautyItemAdapter.BeautyItemClick() {
            @Override
            public void onClickBeautyItem(BeautyFilterItem item) {
                clickBeautyItem(item);
            }
        });
        List<BeautyFilterItem> items = PrettifyUtils.generateRecordBeautyItems();
        items.remove(BeautyFilterItem.ITEM_RESET_DEFAULT);
        mBeautyItemAdapter.setDatas(items);
        bind.listPrettifyBeauty.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        bind.listPrettifyBeauty.setAdapter(mBeautyItemAdapter);
        checkShowDifferentBars(); // init
    }

    private BeautyFilterItem getCurItem() {
        int i = mBeautyItemAdapter.getmSelect();
        BeautyFilterItem item = null;
        if (i >= 0 && i < mBeautyItemAdapter.getItemCount()) {
            item = mBeautyItemAdapter.datas.get(i);;
        }
        return item;
    }

    private void clickBeautyItem(BeautyFilterItem item) {
        Log.i(TAG, "click beauty item=" + item);
        checkShowDifferentBars(); // click
        if (item == BeautyFilterItem.ITEM_RESET_DEFAULT || item == BeautyFilterItem.ITEM_ONE_SHOT || item == BeautyFilterItem.ITEM_NULL) {
            // hideSeekBarContainer();
            // hideDoubleSeekBarContainer(false, null);
        } else if ((item == BeautyFilterItem.ITEM_BRIGHT_V2)) {
            // mPrettifyDoubleSeekBar.setDefaultIndicatorProgress(mDefaultBeautifyConfig == null ? -1 : getProgressValueInternal(item, mDefaultBeautifyConfig, mPrettifyDoubleSeekBar.getMaxProgress()));
            // int progress = item.getProgressValue(mSelectedBeautifyConfig, mPrettifyDoubleSeekBar.getMaxProgress());
            // progress = Math.min(progress, mPrettifyDoubleSeekBar.getMaxProgress());
            // progress = Math.max(progress, -mPrettifyDoubleSeekBar.getMaxProgress());
            // mPrettifyDoubleSeekBar.setProgress(progress, String.valueOf(Math.abs(progress)));
            // showDoubleSeekBarContainer(false);
        } else if (item == BeautyFilterItem.ITEM_NOSE_BRIDGE
                || item == BeautyFilterItem.ITEM_JAW
                || item == BeautyFilterItem.ITEM_LONG_NOSE
                || item == BeautyFilterItem.ITEM_PHILTRUM
                || item == BeautyFilterItem.ITEM_MOUTH
                || item == BeautyFilterItem.ITEM_EYE_POSITION
                || item == BeautyFilterItem.ITEM_EYEBROW_WIDTH
                || item == BeautyFilterItem.ITEM_HAIR_LINE) {
            bind.filterSeekBar.setupSeekBarMode(PrettifyDoubleSeekBar.SEEKBAR_MODE.DOUBLE);
            setupSeekBar();
        } else {
            // 磨皮、清晰、立体
            bind.filterSeekBar.setupSeekBarMode(PrettifyDoubleSeekBar.SEEKBAR_MODE.SINGLE);
            setupSeekBar();
        }
        // if (mBeautyConfigViewListener != null) {
        //     mBeautyConfigViewListener.onBeautyItemSelect(item, mSelectedBeautifyConfig);
        // }
    }

    private void setupSeekBar() {
        BeautyFilterItem item = getCurItem();
        bind.filterSeekBar.setDefaultIndicatorProgress(getDefaultProgress());
        bind.filterSeekBar.setProgress(getProgressValueInternal(item, mSelectedBeautifyConfig, bind.filterSeekBar.getMaxProgress()));
        refreshBeautyItemValueText();
    }

    private int getProgressValueInternal(BeautyFilterItem beautyFilterItem, BeautifyConfig beautifyConfig, int max) {
        if (beautifyConfig == null) {
            return 0;
        }
        boolean mIsLightBeautyConfig = false;
        boolean mIsNatureBeautyConfig = false;
        int progress;
        if (beautifyConfig.mId == BeautifyIds.ID_LIGHT_BEAUTY) {
            mIsLightBeautyConfig = true;
            mIsNatureBeautyConfig = false;
            progress = LightBeautyUtil.getProgressValue(beautyFilterItem, beautifyConfig, max);
        } else if (beautifyConfig.mId == BeautifyIds.ID_NATURE) {
            mIsNatureBeautyConfig = true;
            mIsLightBeautyConfig = false;
            progress = NatureSuiteUtil.getProgressValue(beautyFilterItem, beautifyConfig, max);
        } else {
            mIsLightBeautyConfig = false;
            mIsNatureBeautyConfig = false;
            progress = beautyFilterItem.getProgressValue(beautifyConfig, max);
        }
        progress = Math.min(progress, max);
        progress = Math.max(progress, -max);
        return progress;
    }

    private int getDefaultProgress() {
        BeautyFilterItem item = getCurItem();
        return getProgressValueInternal(item, mSelectedBeautifyConfig, bind.filterSeekBar.getMaxProgress());
    }

    private void checkShowDifferentBars() {
        BeautyFilterItem curItem = getCurItem();
        boolean beauty = curItem == BeautyFilterItem.ITEM_BRIGHT_V2;
        bind.filterSeekBar.setVisibility(beauty ? View.GONE : View.VISIBLE);
        // mPrettifyDoubleSeekBar.setVisibility(beauty ? View.VISIBLE : View.INVISIBLE);
        bind.includeBeautyBar.setVisibility(beauty ? View.VISIBLE : View.GONE);
    }

    private void applyBeautyItem() {
        mSdk.adjustBeauty(mSelectedBeautifyConfig); // apply beauty effect
    }

    private void clearBeautyEffect() {
        mSdk.adjustBeauty(mClearConfig); // clear beauty effect
    }

}
