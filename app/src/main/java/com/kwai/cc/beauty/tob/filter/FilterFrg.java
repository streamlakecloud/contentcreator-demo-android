package com.kwai.cc.beauty.tob.filter;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kwai.async.KwaiSchedulers;
import com.kwai.cc.beauty.tob.sdk.ICcBeautyEngine;
import com.kwai.feature.post.api.componet.prettify.filter.model.FilterConfig;
import com.kwai.gifshow.post.api.core.util.PostSchedulers;
import com.streamlake.demo.databinding.FrgFilterBinding;
import com.yxcorp.gifshow.prettify.filter.RecordFilterDataManager;
import com.yxcorp.gifshow.prettify.v4.magic.filter.FilterDownloadHelper;

import io.reactivex.Observable;

/**
 * author: zhouzhihui
 * created on: 2023/7/20 20:08
 * description:
 */
public class FilterFrg extends Fragment {
    private static final String TAG = "FilterFrg:zzh";
    private RecordFilterDataManager mFilterDataManager = new RecordFilterDataManager();
    private FrgFilterBinding bind;
    private FilterTabAdapter mTabAdapter;
    private FilterItemAdapter mItemsAdapter;
    private ICcBeautyEngine mSdk;

    public static FilterFrg newInstance() {
        FilterFrg frg = new FilterFrg();
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
        bind = FrgFilterBinding.inflate(inflater, container, false);
        initTabList();
        initItemList();
        refreshData();
        bind.btnFilterNull.setOnClickListener(v -> {
            bind.probarAdjustFilter.setProgress(0);
            mSdk.adjustFilter(null);
        });
        bind.probarAdjustFilter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                FilterConfig curFilter = getCurFilter();
                if (curFilter != null) {
                    curFilter.mIntensity = progress / 100f;
                    mSdk.adjustFilter(curFilter);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        return bind.getRoot();
    }

    private void initTabList() {
        mTabAdapter = new FilterTabAdapter();
        bind.filterTabList.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        bind.filterTabList.setAdapter(mTabAdapter);
    }

    private void initItemList() {
        mItemsAdapter = new FilterItemAdapter(bind.filterItemList, new FilterItemAdapter.FilterItemClick() {
            @Override
            public void onClickFilterItem(int pos) {
                Log.i(TAG, "click filter item pos=" + pos);
            }

            @Override
            public void selectFilterItem(int pos) {
                FilterConfig curFilter = getCurFilter();
                Log.i(TAG, "select filter item pos=" + pos + " curFilter=" + curFilter);
                if (curFilter != null) {
                    mSdk.adjustFilter(curFilter);
                    bind.probarAdjustFilter.setProgress((int) (curFilter.mIntensity * 100));
                }
            }
        });
        bind.filterItemList.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        bind.filterItemList.setAdapter(mItemsAdapter);
    }

    private void refreshData() {
        Observable.fromCallable(() -> {
                    Log.i(TAG, FilterConfig.LogKeyPath.KEYPATH_DATA + "mFilterDataManager init");
                    mFilterDataManager.init();
                    return true;
                }).subscribeOn(PostSchedulers.USER_RELATED_ASYNC)
                .observeOn(KwaiSchedulers.MAIN)
                .subscribe(result -> {
                    if (mFilterDataManager.getGroupsInfo().size() > 0) {
                        mTabAdapter.setDatas(mFilterDataManager.getGroupsInfo());
                    }
                    if (mFilterDataManager.getFilters().size() > 0) {
                        mItemsAdapter.setDatas(mFilterDataManager.getFilters());
                    } else {
                        Toast.makeText(getActivity(), "get filter list empty", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "get filter empty");
                    }
                }, e -> {
                    Toast.makeText(getActivity(), "get filter list error=" + e, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "get filter error", e);
                });
    }

    private FilterConfig getCurFilter() {
        FilterConfig config = null;
        if (mFilterDataManager.getFilters() != null && mFilterDataManager.getFilters().size() > mItemsAdapter.getmSelect()) {
            config = mFilterDataManager.getFilters().get(mItemsAdapter.getmSelect());
        }
        if (config != null && FilterDownloadHelper.isFilterResExist(config)) {
            return config;
        }
        return null;
    }
}
