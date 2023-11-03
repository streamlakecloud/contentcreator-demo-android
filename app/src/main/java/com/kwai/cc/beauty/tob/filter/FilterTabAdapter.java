package com.kwai.cc.beauty.tob.filter;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kwai.cc.beauty.util.ScreenUtil;
import com.kwai.feature.post.api.componet.prettify.filter.model.FilterGroup;
import com.streamlake.demo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * author: zhouzhihui
 * created on: 2023/7/24 11:36
 * description:
 */
public class FilterTabAdapter extends RecyclerView.Adapter<FilterTabAdapter.ThisVh> {
    private List<FilterGroup.FilterGroupInfo> datas = new ArrayList<>();
    private int mSelect;
    public int getmSelect() {
        return mSelect;
    }

    public void setDatas(List<FilterGroup.FilterGroupInfo> list) {
        if (list != null && list.size() > 0) {
            datas.clear();
            datas.addAll(list);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ThisVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // int h = parent.getContext().getResources().getDimensionPixelOffset(R.dimen.magic_multi_emoji_tab_title_height);
        TextView tv = new TextView(parent.getContext());
        tv.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return new ThisVh(tv);
    }

    @Override
    public void onBindViewHolder(@NonNull ThisVh holder, int pos) {
        if (pos >=0 && pos < datas.size()) {
            holder.bind(pos);
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class ThisVh extends RecyclerView.ViewHolder {
        public TextView tvDesc;
        public ThisVh(@NonNull View itemView) {
            super(itemView);
            int padding = ScreenUtil.dp2px(itemView.getContext(), 10);
            tvDesc = (TextView) itemView;
            tvDesc.setTextColor(itemView.getContext().getColor(R.color.black));
            tvDesc.setPadding(padding, 0, padding, 0);
            tvDesc.setGravity(Gravity.CENTER);
        }

        public void bind(int pos) {
            FilterGroup.FilterGroupInfo group = datas.get(pos);
            tvDesc.setText(group.mDisplayName);
        }
    }

}
