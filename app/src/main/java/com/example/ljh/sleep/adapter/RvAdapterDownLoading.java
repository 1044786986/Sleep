package com.example.ljh.sleep.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ljh.sleep.R;
import com.example.ljh.sleep.bean.DownLoadBean;
import com.example.ljh.sleep.contract.DownLoadContract;
import com.example.ljh.sleep.presenter.DownLoadPresenter;

import java.util.List;

public class RvAdapterDownLoading extends RecyclerView.Adapter<RvAdapterDownLoading.ViewHolder>{
    private List<DownLoadBean> datalist;
    private LayoutInflater mLayoutInflater;
    private DownLoadContract.showPopupListener mShowPopupListener;
    private boolean mMultiSelection = false;

    public RvAdapterDownLoading(Context context, DownLoadPresenter presenter, List<DownLoadBean> list){
        datalist = list;
        mLayoutInflater = LayoutInflater.from(context);
        mShowPopupListener = presenter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_downloading,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final DownLoadBean bean = datalist.get(position);
        bean.setPosition(position);
        holder.tvName.setText(bean.getName());
        holder.tvAuthor.setText(bean.getAuthor());
        holder.progressBar.setProgress(bean.getProgress());
        /**
         * 显示下载状态
         */
        if(bean.getStatus() == DownLoadBean.DOWNLOAD_WIAT){
            holder.tvSpeed.setText("等待下载");
        }else if(bean.getStatus() == DownLoadBean.DOWNLOAD_PAUSE){
            holder.tvSpeed.setText("暂停下载");
        }else if(bean.getStatus() == DownLoadBean.DOWNLOAD_ERROR){
            holder.tvSpeed.setText("下载失败");
        }else{
            /**
             * 显示下载速度
             */
            int speed = bean.getSpeed();
            String speedString = speed >= 1000? String.format("%.1f",(double)speed / 1000) + "m/s": speed + "kb/s";
            holder.tvSpeed.setText(speedString);
        }
        holder.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mShowPopupListener.showPopupWindow(holder.ivMore,bean);
            }
        });
        /**
         * 多选选择功能
         */
        if(!mMultiSelection){
            holder.checkBox.setVisibility(View.GONE);
        }else{
            holder.checkBox.setVisibility(View.VISIBLE);

        }
        /**
         * 长按触发多选
         */
        holder.parentView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvName;
        private TextView tvAuthor;
        private TextView tvSpeed;
        private CheckBox checkBox;
        private ImageView ivMore;
        private ProgressBar progressBar;
        private LinearLayout parentView;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvSpeed = itemView.findViewById(R.id.tvSpeed);
            checkBox = itemView.findViewById(R.id.cbSelect);
            ivMore = itemView.findViewById(R.id.ivMore);
            progressBar = itemView.findViewById(R.id.progress);
            parentView = itemView.findViewById(R.id.linearLayout_downloading);
        }
    }
}
