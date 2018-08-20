package com.example.ljh.sleep.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ljh.sleep.R;
import com.example.ljh.sleep.activity.MainActivity;
import com.example.ljh.sleep.bean.DownLoadBean;
import com.example.ljh.sleep.bean.MusicInfoBean;
import com.example.ljh.sleep.callback.MediaPlayerListener;
import com.example.ljh.sleep.presenter.ShowStoryPresenter;
import com.example.ljh.sleep.utils.DownLoadUtils2;

import java.util.List;

public class RvAdapter_ShowStory extends RecyclerView.Adapter<RvAdapter_ShowStory.ViewHolder>{
    private List<MusicInfoBean> dataList;
    private LayoutInflater layoutInflater;
    private Context context;
    private ShowStoryPresenter showStoryPresenter;
    private MediaPlayerListener mediaPlayerListener;    //控制播放、停止等监听（MainActivity）
//    private MediaPlayerCallback mediaPlayerCallback = new MediaPlayerCallback() {   //播放过程监听回调
//        @Override
//        public void onStart(int id,long duration) {
//            String durationString = ConvertUtils.duration2min(duration);
//            UpdateDuration.updateDuration(id,durationString);
//        }
//
//        @Override
//        public void onComplete() {
//            Log.i("aaa","RvAdapter_ShowStory.mediaPlayerCallback.onComplete()");
//            MainActivity.getPresenter().pauseTimer();
//            MainActivity.getPresenter().next();
//        }
//
//        @Override
//        public void onError() {
//            MainActivity.getPresenter().pauseTimer();
//        }
//    };

    public RvAdapter_ShowStory(Context context, List<MusicInfoBean> dataList, ShowStoryPresenter showStoryPresenter){
        this.dataList = dataList;
        this.context = context;
        this.showStoryPresenter = showStoryPresenter;
//        mediaPlayerListener = (MediaPlayerListener) context;
        mediaPlayerListener =  MainActivity.getPresenter();
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_showstory,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        MusicInfoBean bean = dataList.get(position);
        bean.setPosition(position);
        final MusicInfoBean musicInfoBean = bean;
        holder.tvName.setText(bean.getName());
        holder.tvDuration.setText(bean.getDuration());
        /**
         * 播放
         */
        holder.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayerListener.play(musicInfoBean);
            }
        });
        /**
         * 下载
         */
        holder.ivDownLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownLoadBean downLoadBean = new DownLoadBean();
                downLoadBean.setName(musicInfoBean.getName());
                downLoadBean.setAuthor(musicInfoBean.getAuthor());
                downLoadBean.setType(musicInfoBean.getType());
                downLoadBean.setUrl(musicInfoBean.getUrl());
                downLoadBean.setDuration(musicInfoBean.getDuration());
                downLoadBean.setId(musicInfoBean.getId());
                MainActivity.getPresenter().downLoad(downLoadBean, DownLoadUtils2.DOWNLOAD);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvName,tvDuration;
        private ImageView ivPlay,ivDownLoad;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            ivPlay = itemView.findViewById(R.id.ivPlay);
            ivDownLoad = itemView.findViewById(R.id.ivDownLoad);
        }
    }
}
