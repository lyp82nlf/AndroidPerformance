package com.dsg.androidperformance.adapter;

import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.dsg.androidperformance.R;
import com.dsg.androidperformance.bean.NewsItem;
import com.dsg.androidperformance.utils.LaunchTimer;
import com.dsg.androidperformance.utils.LogUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private List<NewsItem> mItems;
    private boolean mHasRecorded;
    private OnFeedShowCallBack mCallBack;

    public NewsAdapter(List<NewsItem> items) {
        this.mItems = items;
    }

    public void setItems(List<NewsItem> items) {
        this.mItems = items;
        notifyDataSetChanged();
    }

    public void setOnFeedShowCallBack(OnFeedShowCallBack callBack) {
        this.mCallBack = callBack;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_constrainlayout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (position == 0 && !mHasRecorded) {
            mHasRecorded = true;
            holder.layout.getViewTreeObserver()
                    .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            holder.layout.getViewTreeObserver().removeOnPreDrawListener(this);
                            LogUtils.i("FeedShow");
                            LaunchTimer.endRecord("FeedShow");
                            if (mCallBack != null) {
                                mCallBack.onFeedShow();
                            }
                            return true;
                        }
                    });
        }

        NewsItem newsItem = mItems.get(position);

        // 以下代码是为了演示字符串的拼接
        String msgOld = newsItem.title + newsItem.targetId;// 原有方式

        StringBuilder builder = new StringBuilder();
        builder.append(newsItem.title)
                .append(newsItem.targetId);// 建议使用方式，不要小看这点优化
        String msgNew = builder.toString();

        holder.textView.setText(newsItem.title);
        Uri uri = Uri.parse(newsItem.imgurl);
        holder.imageView.setImageURI(uri);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        SimpleDraweeView imageView;
        ConstraintLayout layout;

        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.tv_title);
            imageView = view.findViewById(R.id.iv_news);
            layout = view.findViewById(R.id.ll_out);
        }
    }

}
