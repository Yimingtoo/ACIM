package com.yiming.acimapplication.Tutorial;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yiming.acimapplication.R;

public class TutorialAdapter extends RecyclerView.Adapter<TutorialAdapter.ViewPagerViewHolder> {
    @NonNull
    @Override
    public ViewPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewPagerViewHolder((ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pager, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerViewHolder holder, int position) {
        switch (position){
            case 0:
                holder.imageView.setImageResource(R.drawable.t1);
                holder.textView.setText("下拉菜单栏，点击编辑");
                break;
            case 1:
                holder.imageView.setImageResource(R.drawable.t2);
                holder.textView.setText("向上滑动");
                break;
            case 2:
                holder.imageView.setImageResource(R.drawable.t3);
                holder.textView.setText("选择“切换输入法”，并点击完成");
                break;
            case 3:
                holder.imageView.setImageResource(R.drawable.t4);
                holder.textView.setText("快捷开关设置完成(◍•ᴗ•◍)");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    class ViewPagerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        public ViewPagerViewHolder(@NonNull ViewGroup itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_item);
            textView = itemView.findViewById(R.id.tv_item);
        }
    }
}
