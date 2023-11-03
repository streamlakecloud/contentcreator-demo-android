package com.kwai.cc.beauty.tob.magic;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kwai.async.KwaiSchedulers;
import com.kwai.cc.beauty.tob.sdk.ICcBeautyEngine;
import com.streamlake.demo.databinding.FrgMagicBinding;
import com.yxcorp.gifshow.magic.data.datahub.MagicFaceController;
import com.yxcorp.gifshow.magic.data.datahub.MagicSelectDataHelper;
import com.yxcorp.gifshow.magic.data.download.MagicFacePreDownloadHelper;
import com.yxcorp.gifshow.magic.data.pageRepository.MagicBaseRepository;
import com.yxcorp.gifshow.magic.data.pageRepository.MagicRepositoryFactory;
import com.yxcorp.gifshow.magic.data.repo.briefpage.MagicEmojiPageRefreshUtil;
import com.yxcorp.gifshow.magic.ui.magicemoji.MagicEmojiFragment;
import com.yxcorp.gifshow.magic.util.MagicFaceResourceHelper;
import com.yxcorp.gifshow.model.MagicEmoji;
import com.yxcorp.gifshow.model.response.MagicEmojiResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;

/**
 * author: zhouzhihui
 * created on: 2023/7/20 20:08
 * description:
 */
public class MagicFrg extends Fragment {
    private static final String TAG = "MagicFrg:zzh";
    private static final String DEBUG_TAG = "MagicFrg:zzh";
    private Map<Integer, MagicBaseRepository> mRepo = new HashMap<>();
    private FrgMagicBinding bind;
    private MagicTabAdapter mTabAdapter;
    private MagicFaceAdapter mFaceAdapter;
    private MagicEmojiResponse mResponse; // 分组brief信息
    private MagicEmojiFragment.Source mSource = MagicEmojiFragment.Source.CAMERA_FULLSCREEN;
    private ICcBeautyEngine mSdk;

    public static MagicFrg newInstance() {
        MagicFrg frg = new MagicFrg();
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
        bind = FrgMagicBinding.inflate(inflater, container, false);
        bind.flMagicClearContainer.setOnClickListener(v -> applySelectFace(null));
        initTabList();
        return bind.getRoot();
    }

    private void initTabList() {
        mTabAdapter = new MagicTabAdapter(magicEmoji -> {
            MagicBaseRepository repo = setRepo();
            MagicEmoji emoji = mResponse.mMagicEmojis.get(mTabAdapter.getmSelect());
            Log.i(TAG, "click magic tab magicTab=" + magicEmoji + " repo=" + repo);
            if (emoji.mMagicFaces.size() > 0) {
                mFaceAdapter.setList(emoji.mMagicFaces);
                return;
            }
            if (repo != null) {
                repo.loadMore();
            } else {
                Toast.makeText(getActivity(), "repo is null", Toast.LENGTH_SHORT).show();
            }
        });
        bind.tabsList.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        bind.tabsList.setAdapter(mTabAdapter);

        mFaceAdapter = new MagicFaceAdapter(bind.faceList, new MagicFaceAdapter.MagicFaceClick() {
            @Override
            public void onClickMagicFace(int pos, MagicEmoji.MagicFace face) {
                clickFace(pos, face);
            }

            @Override
            public void selectMagicFace(MagicEmoji.MagicFace face) {
                applySelectFace(face);
            }
        });
        bind.faceList.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        bind.faceList.setAdapter(mFaceAdapter);
        if (mResponse == null) {
            refreshCategories();
        } else {
            mTabAdapter.setmBriefEmojis(mResponse);
            initFirstPageFace();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (mResponse == null) {
                refreshCategories();
            }
        }
    }

    private void initFirstPageFace() {
        MagicEmoji emoji = mResponse.mMagicEmojis.get(mTabAdapter.getmSelect());
        List<MagicEmoji.MagicFace> faces = emoji.mMagicFaces;
        mFaceAdapter.setList(faces);
    }

    private MagicBaseRepository setRepo() {
        int index = mTabAdapter.getmSelect();
        MagicBaseRepository existItem = mRepo.get(index);
        if (existItem != null) {
            return existItem;
        }
        MagicEmoji emoji = mResponse.mMagicEmojis.get(index);
        MagicBaseRepository.MagicDataChangeListener listener = new MagicBaseRepository.MagicDataChangeListener() {
            @Override
            public void onLoading() {
                Log.i(TAG, "on loading");
            }

            @Override
            public void onLoadMore(@NonNull List<MagicEmoji.MagicFace> list) {
                Log.i(TAG, "on load more list=" + list);
                MagicEmoji emoji = mResponse.mMagicEmojis.get(mTabAdapter.getmSelect());
                emoji.mMagicFaces.clear();
                emoji.mMagicFaces.addAll(list);
                mFaceAdapter.setList(list);
            }

            @Override
            public void onLoadError(@NonNull Throwable e) {
                Log.e(TAG, "on load error=", e);
            }
        };
        MagicBaseRepository repo = MagicRepositoryFactory.INSTANCE.createRepository(MagicFaceController.getEmojiKeyBySource(mSource), emoji, null, listener);
        mRepo.put(index, repo);
        return repo;
    }


    private void refreshCategories() {
        Consumer<MagicEmojiResponse> successConsumer = response -> onSuccess(response);
        Consumer<Throwable> errorConsumer = e -> {
            Toast.makeText(getActivity(), "refresh magic tab error=" + e, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "refresh magic tab error=", e);
        };
        MagicEmojiPageRefreshUtil
                .refreshFirstPageObservable(MagicFaceController.getEmojiKeyBySource(mSource), null)
                .observeOn(KwaiSchedulers.MAIN)
                .subscribe(successConsumer, errorConsumer);
    }

    private void onSuccess(MagicEmojiResponse response) {
        if (!isAdded()) {
            return;
        }
        Log.i(TAG, "onSuccess");
        // PostPageLogger.getInstance(MagicMonitorTag.SCENE).recordStageEnd(MagicMonitorTag.CREATE_TO_LOAD_DATA);
        // PostPageLogger.getInstance(MagicMonitorTag.SCENE).recordStageBegin(MagicMonitorTag.LOAD_DATA_TO_PREVIEW);
        mResponse = response;
        mTabAdapter.setmBriefEmojis(mResponse);
        initFirstPageFace();
    }

    private void applySelectFace(MagicEmoji.MagicFace face) {
        Log.i(TAG, "apply select magic face =" + face);
        MagicFaceController.onMagicFaceUsed(face);
        String magicFacePath = face != null ? MagicFaceResourceHelper.getMagicFaceFile(face).getAbsolutePath() : null;
        mSdk.adjustMagic(face, magicFacePath);
    }

    private void clickFace(int pos, MagicEmoji.MagicFace face) {
        Log.i(TAG, "click magic face =" + face);
        if (MagicFaceResourceHelper.isNeedDownloadMagicFace(face)) {
            Log.i(TAG, "点击，需要下载 " + face.mName);
            mFaceAdapter.beginDownload(pos, face);
            return;
        }
        MagicSelectDataHelper.sSelectedMagicFace = face;
        Log.i(TAG, "点击，不需要下载 " + face.mName);
        if (MagicFacePreDownloadHelper.getInstance().isPreDownloaded(face)) {
            Log.i(TAG, "点击，已经预下载，转一圈再应用 " + face.mName);
            MagicFacePreDownloadHelper.getInstance().markDownloadManually(MagicSelectDataHelper.sSelectedMagicFace);
            // mDownloadPresenter.handlePreDownloaded(MagicSelectDataHelper.sSelectedMagicFace);
            applySelectFace(face);
        } else {
            Log.i(TAG, "点击，调整下载队列 " + face.mName);
            MagicFacePreDownloadHelper.getInstance().markDownloadManually(MagicSelectDataHelper.sSelectedMagicFace);
            applySelectFace(face);
        }
    }

}
