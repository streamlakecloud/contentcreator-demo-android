package com.kwai.cc.beauty.tob.filter;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.streamlake.processor.contentcreator.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.kwai.feature.post.api.componet.prettify.filter.interfaces.FilterDownloadListener;
import com.kwai.feature.post.api.componet.prettify.filter.model.FilterConfig;
import com.kwai.feature.post.api.componet.prettify.filter.plugin.FilterPlugin;
import com.yxcorp.gifshow.image.KwaiImageView;
import com.yxcorp.gifshow.inflater.AsyncMultiViewsInflater;
import com.yxcorp.gifshow.prettify.v4.magic.filter.FilterDownloadHelper;
import com.yxcorp.gifshow.prettify.v4.magic.filter.PrettifyTagView;
import com.yxcorp.gifshow.util.resource.StaticCdnImageUtils;
import com.yxcorp.utility.NetworkUtils;
import com.yxcorp.utility.SafetyUriCalls;
import com.yxcorp.utility.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: zhouzhihui
 * created on: 2023/7/24 11:36
 * description:
 */
public class FilterItemAdapter extends RecyclerView.Adapter<FilterItemAdapter.ThisVh> {
    private List<FilterConfig> datas = new ArrayList<>();
    private static final String TAG = "MagicFaceAdapter:zzh";
    private RecyclerView myRecyclerView;
    private FilterItemClick mFilterItemClick;
    protected final Map<Integer, Float> mFilterProgressMap = new HashMap<>();
    private FilterPlugin.FilterEntranceType mEntryType = FilterPlugin.FilterEntranceType.VIDEO;
    private int mSelect;

    public FilterItemAdapter(RecyclerView view, FilterItemClick click) {
        mFilterItemClick = click;
        myRecyclerView = view;
    }

    public void setDatas(List<FilterConfig> list) {
        if (list != null && list.size() > 0) {
            datas.clear();
            datas.addAll(list);
            notifyDataSetChanged();
        }
    }

    public int getmSelect() {
        return mSelect;
    }

    @NonNull
    @Override
    public ThisVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = AsyncMultiViewsInflater.getInstance().getView(parent.getContext(), R.layout.prettify_common_pic_item_new_ui);
        return new ThisVh(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ThisVh holder, int pos) {
        if (pos >= 0 && pos < datas.size()) {
            holder.bind(pos);
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class ThisVh extends RecyclerView.ViewHolder {
        KwaiImageView mFilterIcon;
        @Nullable
        ImageView mIndicator;
        PrettifyTagView mTagView;
        TextView mFilterName;
        ImageView mUndownloadFlag;
        ProgressBar mDownloadProgressBar;

        public ThisVh(@NonNull View itemView) {
            super(itemView);
            mFilterIcon = itemView.findViewById(R.id.icon);
            mFilterName = itemView.findViewById(R.id.name);
            mIndicator = itemView.findViewById(R.id.select_indicator_new_ui);
            mUndownloadFlag = itemView.findViewById(R.id.undownload_flag);
            mDownloadProgressBar = itemView.findViewById(R.id.download_progressbar);
            mTagView = itemView.findViewById(R.id.prettify_tag);
        }

        public void bind(int pos) {
            boolean selected = pos ==mSelect;
            FilterConfig item = datas.get(pos);
            mFilterName.setText(item.mDisplayName);
            mFilterName.setTextColor(itemView.getResources().getColor(selected ? R.color.red : R.color.black));
            loadImage(mFilterIcon, item);

            // 下载状态
            if (FilterDownloadHelper.isFilterResExist(item)) {
                mUndownloadFlag.setVisibility(View.GONE);
                mDownloadProgressBar.setVisibility(View.GONE);
            } else if (mFilterProgressMap.containsKey(item.mFilterId)) {
                mUndownloadFlag.setVisibility(View.GONE);
                mDownloadProgressBar.setVisibility(View.VISIBLE);
                mDownloadProgressBar.setProgress((int) (mFilterProgressMap.get(item.mFilterId) * mDownloadProgressBar.getMax()));
            } else {
                mUndownloadFlag.setVisibility(View.VISIBLE);
                mDownloadProgressBar.setVisibility(View.GONE);
                FilterDownloadHelper.downloadAllFilters(mEntryType);
            }

            itemView.setOnClickListener(v -> {
                int oldPos = mSelect;
                mSelect = pos;
                notifyItemChanged(oldPos);
                notifyItemChanged(pos);
                if (FilterDownloadHelper.isFilterResExist(item)) {
                    // 增加文件md5的校验
                    if (FilterDownloadHelper.isResourceValid(item)) {
                        Log.d(TAG, "config is Valid, " + item.toString());
                        if (mUndownloadFlag.getVisibility() == View.VISIBLE) {
                            mUndownloadFlag.setVisibility(View.GONE);
                        }
                        mFilterItemClick.selectFilterItem(pos);
                    } else { // 如果资源md5不对，则直接重新下载
                        Log.e(TAG, "config is not Valid, " + item.toString());
                        downloadFilterRes(this, item);
                    }
                } else {
                    downloadFilterRes(this, item);
                }
            });
        }

        protected void downloadFilterRes(final ThisVh holder, FilterConfig filter) {
            if (holder.mDownloadProgressBar.getVisibility() == View.VISIBLE) {
                return;
            }
            mFilterProgressMap.put(filter.mFilterId, 0.0f);
            holder.mUndownloadFlag.setVisibility(View.GONE);
            holder.mDownloadProgressBar.setVisibility(View.VISIBLE);
            holder.mDownloadProgressBar.setProgress(0);
            FilterDownloadListener downloadListener =
                    new FilterDownloadListener() {
                        @Override
                        public void onProgress(int id, float progress) {
                            if (id != filter.mFilterId) {
                                Log.i(TAG, "onProgress " + id + " holder.id" + filter.mFilterId);
                                return;
                            }
                            mFilterProgressMap.put(id, progress);
                            holder.mDownloadProgressBar.setProgress((int) (progress * holder.mDownloadProgressBar.getMax()));
                        }

                        @Override
                        public void onComplete(int id) {
                            Utils.runOnUiThread(() -> onFilterDownLoadComplete(id, holder));
                        }

                        @Override
                        public void onError(int id) {
                            Utils.runOnUiThread(() -> onFilterDownLoadError(id, holder));
                        }
                    };
            FilterDownloadHelper.downloadFilter(filter, downloadListener);
        }

        private void onFilterDownLoadComplete(int id, ThisVh holder) {
            mFilterProgressMap.remove(id);
            Log.i(TAG, "onComplete " + id);
            mFilterItemClick.selectFilterItem(holder.getAdapterPosition());
        }

        private void onFilterDownLoadError(int id, ThisVh holder) {
            if (!NetworkUtils.isNetworkConnected(itemView.getContext())) {
                Toast.makeText(itemView.getContext(), R.string.network_failed_tip, Toast.LENGTH_SHORT).show();
            }
            mFilterProgressMap.remove(id);
            holder.mDownloadProgressBar.setVisibility(View.GONE);
            holder.mUndownloadFlag.setVisibility(View.VISIBLE);
        }

        protected void loadImage(KwaiImageView icon, final FilterConfig filteritem) {
            Log.i(TAG, " loadimage " + filteritem.getDisplayName());
            if (TextUtils.isEmpty(filteritem.mThumbImageUrl)) {
                Log.e(TAG, "loadImage error icon" + filteritem.mThumbImageName + " url:" + filteritem.mThumbImageUrl);
                return;
            }
            PipelineDraweeControllerBuilder controllerBuilder = Fresco.newDraweeControllerBuilder()
                    .setOldController(icon.getController())
                    .setFirstAvailableImageRequests(getmageRequest(filteritem.mThumbImageUrl));
            icon.setController(controllerBuilder.build());
            StaticCdnImageUtils.bindUrlToKwaiBindableImageView(icon, filteritem.mThumbImageUrl, true);
        }

        private ImageRequest[] getmageRequest(String url) {
            ImageRequest[] imageRequests = new ImageRequest[1];
            int index = 0;
            ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(SafetyUriCalls.parseUriFromString(url));
            imageRequests[0] = builder.build();
            return imageRequests;
        }
    }

    public interface FilterItemClick {
        void onClickFilterItem(int pos);

        void selectFilterItem(int pos);
    }

}
