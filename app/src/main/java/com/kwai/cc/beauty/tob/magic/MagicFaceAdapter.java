package com.kwai.cc.beauty.tob.magic;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kwai.framework.app.AppEnv;
import com.streamlake.processor.contentcreator.R;
import com.yxcorp.gifshow.image.KwaiImageView;
import com.yxcorp.gifshow.inflater.AsyncMultiViewsInflater;
import com.yxcorp.gifshow.magic.data.datahub.MagicFaceController;
import com.yxcorp.gifshow.magic.data.download.IMagicFaceDownloader;
import com.yxcorp.gifshow.magic.data.download.MagicDownloadListener;
import com.yxcorp.gifshow.magic.data.download.MagicFaceDownloadManager;
import com.yxcorp.gifshow.magic.data.download.MagicFacePreDownloadHelper;
import com.yxcorp.gifshow.magic.util.MagicFaceResourceHelper;
import com.yxcorp.gifshow.model.MagicBaseConfig;
import com.yxcorp.gifshow.model.MagicEmoji;
import com.yxcorp.utility.ArrayUtil;
import com.yxcorp.utility.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * author: zhouzhihui
 * created on: 2023/7/24 11:36
 * description:
 */
public class MagicFaceAdapter extends RecyclerView.Adapter<MagicFaceAdapter.ThisVh> {
    private static final String TAG = "MagicFaceAdapter:zzh";
    private static final String DEBUG_TAG = "MagicFaceAdapter:zzh";
    private RecyclerView myRecyclerView;
    public List<MagicEmoji.MagicFace> faces = new ArrayList<>();
    private MagicFaceClick mMagicFaceClick;
    private int mSelect;
    public MagicFaceAdapter(RecyclerView view, MagicFaceClick click) {
        mMagicFaceClick = click;
        myRecyclerView = view;
    }

    public int getmSelect() {
        return mSelect;
    }

    public void setList(List<MagicEmoji.MagicFace> list) {
        if (list != null) {
            faces.clear();
            faces.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void beginDownload(int pos, MagicEmoji.MagicFace item) {
        ThisVh holder = (ThisVh) myRecyclerView.findViewHolderForAdapterPosition(pos);
        holder.beginDownloadManually(pos, item);
    }
    @NonNull
    @Override
    public ThisVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = AsyncMultiViewsInflater.getInstance().getView(parent.getContext(), R.layout.list_item_magic_emoji_mul_row);
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(
                    parent.getContext().getResources()
                            .getDimensionPixelSize(R.dimen.magic_emoji_item_width_size),
                    parent.getContext().getResources()
                            .getDimensionPixelSize(R.dimen.magic_emoji_item_size));
        } else {
            layoutParams.height = parent.getContext().getResources()
                    .getDimensionPixelSize(R.dimen.magic_emoji_item_size);
            layoutParams.width = parent.getContext().getResources()
                    .getDimensionPixelSize(R.dimen.magic_emoji_item_width_size);
        }
        itemView.setLayoutParams(layoutParams);
        return new ThisVh(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ThisVh holder, int pos) {
        if (pos >= 0 && pos < faces.size()) {
            holder.bind(pos, faces.get(pos));
        }
    }

    @Override
    public int getItemCount() {
        return faces.size();
    }

    class ThisVh extends RecyclerView.ViewHolder {
        TextView nameTv;
        ImageView undownload_flag;
        KwaiImageView mCoverView;
        ProgressBar download_progress;
        MagicEmoji.MagicFace mMagicFace;
        private DownloadListenerInfo mDownloadListenerInfo;
        public ThisVh(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.magic_emoji_name_tv);
            undownload_flag = itemView.findViewById(R.id.undownload_flag);
            mCoverView = itemView.findViewById(R.id.magic_emoji_cover);
            download_progress = itemView.findViewById(R.id.download_progress);
        }

        public void bind(int pos, MagicEmoji.MagicFace magicFace) {
            mMagicFace = magicFace;
            if (!ArrayUtil.isEmpty(magicFace.mImages)) {
                mCoverView.bindUrls(magicFace.mImages, null, mCoverView.getWidth(), mCoverView.getWidth());
            } else if (!TextUtils.isEmpty(magicFace.mImage)) {
                mCoverView.bindUri(Uri.parse(magicFace.mImage), mCoverView.getWidth(), mCoverView.getWidth(), null);
            } else if (AppEnv.isTestChannel()){
                Toast.makeText(itemView.getContext(), "魔表:" + magicFace + " icon url 下发为null!!!", Toast.LENGTH_SHORT).show();
            } else {
                Log.w(TAG, "magicFace:" + magicFace + " icon url is null");
            }

            nameTv.setText(magicFace.mName);
            nameTv.setTextColor(itemView.getContext().getResources().getColor(pos == mSelect ? R.color.red :R.color.black));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int preIndex = mSelect;
                    mSelect = pos;
                    mMagicFaceClick.onClickMagicFace(pos, faces.get(pos));
                    notifyItemChanged(preIndex);
                    notifyItemChanged(mSelect);
                }
            });

            boolean needDownload = MagicFaceResourceHelper.isNeedDownloadMagicFace(magicFace);
            undownload_flag.setVisibility(needDownload ? View.VISIBLE : View.GONE);

            boolean downloading = MagicFaceDownloadManager.getInstance().isDownloading(magicFace);
            download_progress.setVisibility(downloading ? View.VISIBLE : View.GONE);
            if (downloading) {
                IMagicFaceDownloader downloader = MagicFaceDownloadManager.getInstance();
                // downloader.addListener(magicFace, createMagicDownloadListener(pos, magicFace));
                notifyProgress(downloader.getDownloadTaskProgress(magicFace.mId));
            }
        }

        private MagicDownloadListener createMagicDownloadListener(int pos, MagicEmoji.MagicFace magicFace) {
            // 删除上一次ui创建的下载监听
            removeListener();
            MagicDownloadListener baseListener = new MagicDownloadListener() {
                @Override
                public void onProgress(MagicBaseConfig magicFace, int cur, int max) {
                    if (getModel() == null || !getModel().mId.equals(magicFace.mId) || !isCreated()) {
                        return;
                    }
                    notifyProgress(cur);
                }

                @Override
                public void onFailed(MagicBaseConfig magicFace, Throwable e) {
                    Log.e(DEBUG_TAG, "MagicFaceDownloadPresenter onFailed " + magicFace.mName, e);
                    if (getModel() == null || !getModel().mId.equals(magicFace.mId) || !isCreated()) {
                        Log.i(DEBUG_TAG, "onFailed 下载的魔表跟当前魔表不匹配.");
                        return;
                    }
                    // sSimilarDownloadMagics.remove(magicFace.getUniqueIdentifier());
                    // notifyFailed();
                    notifyItemChanged(pos);
                }

                @Override
                public void onCompleted(MagicBaseConfig magicFace) {
                    Log.d(DEBUG_TAG, "MagicFaceDownloadPresenter onCompleted" + magicFace.mName);
                    if (getModel() == null || !getModel().mId.equals(magicFace.mId) || !isCreated()) {
                        Log.i(DEBUG_TAG, "onFailed 下载的魔表跟当前魔表不匹配.");
                        return;
                    }
                    notifyCompleted(pos, true);
                }
            };
            mDownloadListenerInfo = new DownloadListenerInfo(magicFace, baseListener);
            return baseListener;
        }

        private void removeListener() {
            if (mDownloadListenerInfo != null) {
                MagicFaceDownloadManager.getInstance().removeListener(mDownloadListenerInfo.mMagicFace,
                        mDownloadListenerInfo.mDownloadListener);
                mDownloadListenerInfo = null;
            }
        }

        private boolean isCreated() {
            return itemView != null;
        }

        private void notifyProgress(int pro) {
            download_progress.setProgress(pro);
        }

        private void notifyCompleted(int pos, boolean needApplyOnCompleted) {
            MagicFaceResourceHelper.sMagicExistMap.put(getModel().mId, true);
            notifyItemChanged(pos);
            if (needApplyOnCompleted) {
                applyOnCompleted();
            }
        }

        private MagicEmoji.MagicFace getModel() {
            return mMagicFace;
        }

        private void applyOnCompleted() {
            mMagicFaceClick.selectMagicFace(getModel());
        }

        public void beginDownloadManually(int pos, MagicEmoji.MagicFace item) {
            MagicFaceController.onMagicFaceDownloadStarted(item);
            boolean isPreDownloadBefore = MagicFacePreDownloadHelper.getInstance().markDownloadManually(item);

            // if (mAdapter.isIsSimilarItem()) {
            //     sSimilarDownloadMagics.add(item.getUniqueIdentifier());
            // } else {
            //     sSimilarDownloadMagics.remove(item.getUniqueIdentifier());
            // }

            if (!MagicFaceResourceHelper.isNeedDownloadMagicFace(item)) {
                Log.i(DEBUG_TAG, "beginDownloadManually，已下载完成" + item.mName);
                notifyCompleted(pos, true);
                return;
            }

            if (MagicFaceDownloadManager.getInstance().isDownloading(item) && !isPreDownloadBefore
                    && mDownloadListenerInfo != null) {
                Log.i(DEBUG_TAG, "beginDownloadManually，其它地方正在下载" + item.mName);
                return;
            }

            // Please do care about mAdapter！！！！
            // When click parent, first child will be download auto.
            // Here, we may in parent'presenter, but try download child item.
            MagicDownloadListener baseListener = createMagicDownloadListener(pos, item);
            Log.i(DEBUG_TAG, "beginDownloadManually，真正开始下载" + item.mName);
            MagicFaceDownloadManager.getInstance().download(item, true, baseListener);
            // if (getModel() == item) {
            //     notifyFailed();
            //     notifyProgress(0);
            // }
            notifyItemChanged(pos);
        }
    }

    public interface MagicFaceClick {
        void onClickMagicFace(int pos, MagicEmoji.MagicFace face);
        void selectMagicFace(MagicEmoji.MagicFace face);
    }

    static class DownloadListenerInfo {
        private MagicEmoji.MagicFace mMagicFace;
        private MagicDownloadListener mDownloadListener;

        public DownloadListenerInfo(MagicEmoji.MagicFace magicFace, MagicDownloadListener listener) {
            mMagicFace = magicFace;
            mDownloadListener = listener;
        }
    }
}
