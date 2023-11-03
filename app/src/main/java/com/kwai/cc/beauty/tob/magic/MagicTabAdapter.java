package com.kwai.cc.beauty.tob.magic;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kwai.cc.beauty.util.ScreenUtil;
import com.streamlake.processor.contentcreator.R;
import com.yxcorp.gifshow.model.MagicEmoji;
import com.yxcorp.gifshow.model.response.MagicEmojiResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * author: zhouzhihui
 * created on: 2023/7/24 11:36
 * description:
 */
public class MagicTabAdapter extends RecyclerView.Adapter<MagicTabAdapter.ThisVh> {
    public MagicTabAdapter(MagicTabClick click) {
        mMagicTabClick = click;
    }
    public List<MagicEmoji> mBriefEmojis = new ArrayList<>();
    private MagicTabClick mMagicTabClick;
    private int mSelect;

    public void setmBriefEmojis(MagicEmojiResponse rsp) {
        if (rsp != null) {
            mBriefEmojis.clear();
            for (int i = 0; i < rsp.mBriefEmojis.size(); i++) {
                mBriefEmojis.add(rsp.mBriefEmojis.get(i));
                if (rsp.mBriefEmojis.get(i).mName.equals(rsp.mDefaultTabId)) {
                    mSelect = i;
                }
            }
            notifyDataSetChanged();
        }
    }

    public int getmSelect() {
        return mSelect;
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
        if (pos >= 0 && pos < mBriefEmojis.size()) {
            final int posFinal = pos;
            holder.tvDesc.setText(mBriefEmojis.get(pos).mName);
            holder.tvDesc.setTextColor(holder.tvDesc.getContext().getResources().getColor(pos == mSelect ? R.color.red :R.color.black));
            holder.tvDesc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int preIndex = mSelect;
                    mSelect = posFinal;
                    mMagicTabClick.onClickMagicTab(mBriefEmojis.get(posFinal));
                    notifyItemChanged(preIndex);
                    notifyItemChanged(mSelect);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mBriefEmojis.size();
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
    }

    public interface MagicTabClick {
        void onClickMagicTab(MagicEmoji magicEmoji);
    }
}
