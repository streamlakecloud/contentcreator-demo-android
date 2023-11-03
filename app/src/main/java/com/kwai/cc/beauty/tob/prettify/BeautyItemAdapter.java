package com.kwai.cc.beauty.tob.prettify;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.streamlake.processor.contentcreator.R;
import com.yxcorp.gifshow.prettify.beauty.BeautyFilterItem;

import java.util.ArrayList;
import java.util.List;

/**
 * author: zhouzhihui
 * created on: 2023/7/24 11:36
 * description:
 */
public class BeautyItemAdapter extends RecyclerView.Adapter<BeautyItemAdapter.ThisVh> {
    public BeautyItemAdapter(BeautyItemClick click) {
        mBeautyItemClick = click;
    }
    public List<BeautyFilterItem> datas = new ArrayList<>();
    private BeautyItemClick mBeautyItemClick;
    private int mSelect;

    public int getmSelect() {
        return mSelect;
    }

    public void setDatas(List<BeautyFilterItem> list) {
        if (list != null) {
            datas.clear();
            datas.addAll(list);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ThisVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // int h = parent.getContext().getResources().getDimensionPixelOffset(R.dimen.magic_multi_emoji_tab_title_height);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.live_beauty_filter_list_item_new_ui, parent, false);
        return new ThisVh(view);
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
        public TextView name;
        public ImageView icon;
        public ThisVh(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            icon = itemView.findViewById(R.id.icon);
        }

        public void bind(int pos) {
            BeautyFilterItem item = datas.get(pos);
            name.setText(item.mNameRes);
            name.setTextColor(itemView.getResources().getColor(pos == mSelect ? R.color.red : R.color.white));
            Drawable drawable = itemView.getResources().getDrawable((item.mIcon));
            icon.setImageDrawable(drawable);
            itemView.setOnClickListener(v -> {
                int oldPos = mSelect;
                mSelect = pos;
                notifyItemChanged(oldPos);
                notifyItemChanged(mSelect);
                mBeautyItemClick.onClickBeautyItem(item);
            });
        }
    }

    public interface BeautyItemClick {
        void onClickBeautyItem(BeautyFilterItem item);
    }
}
