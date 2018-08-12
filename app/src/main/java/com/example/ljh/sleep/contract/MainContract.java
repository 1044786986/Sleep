package com.example.ljh.sleep.contract;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.ImageView;

import com.example.ljh.sleep.bean.DownLoadBean;
import com.example.ljh.sleep.bean.MusicInfoBean;
import com.example.ljh.sleep.callback.MediaPlayerCallback;
import com.example.ljh.sleep.callback.PermissionResultCallback;

public class MainContract {
    public interface MainView{
        void initFloatWindow();                         //初始化音乐控制器
        void setMediaPlayerInfo(MusicInfoBean bean);    //设置音乐控制器内容
//      void setMusicInfo(MusicInfoBean bean);          //记录当前播放的音频信息
        void resetSeekBar();                            //重置进度条
        void updateSeekBar(int i);                      //更新进度条
        void updateSecondSeekBar(int i);                //更新缓冲进度条
        void updateTimer(String string);                //更新计时器
        void changePage(int pos);                       //切换页面
        void showPlayIcon();                            //音乐控制器显示播放按钮
        void showPauseIcon();                           //音乐控制器显示暂停按钮
        void showBottomView();                          //显示底部导航栏
        void checkPermission(String permissions[], PermissionResultCallback callback);
        void finishView();
        boolean isTrackingTouch();                      //判断是否在拖动进度条
        Context getContext();
        Bundle getSaveInstanceState();                  //判断页面是否保存过
        FragmentManager getMyFragmentManager();         //获取FragmentManager
        ImageView getMediaPlayerLogo();
    }

    public interface MainPresenter{
        void initMain();                                //初始化主页
        void showMain();                                //显示主页
        void sendMessageToMediaPlayer(Message message); //发送message到音乐服务
        void changeCurPage(int position);               //切换页面
        void setMusicInfo(MusicInfoBean musicInfo);     //记录当前播放的音频信息
        void startTimer();                              //开始计时
        void pauseTimer();                              //停止计时
        void resetStatus();                             //重置控制器状态
        void updateTimer(int progress);                 //拖拉进度条时更新计时
        void updateSecondProgress(int progress);        //加载缓冲进度条
        void seekToProgress();                          //拖动进度条播放音乐
        void getCurPosition();                          //获取当前音乐播放的时长
        void downLoad(Object o,int downLoadType);       //下载音频
        void reLoad(DownLoadBean downLoadBean);         //重新下载音频
        void rotationLogo();                            //开始旋转悬浮Logo
        void stopRotationLogo();                        //暂停旋转悬浮Logo
        void initRotationLogo();                        //初始化悬浮Logo动画
        void bindMediaPlayService();                    //绑定播放音乐服务
        void unbindMediaPlayService();                  //解绑服务
        void initAd();                                  //初始化广告页面
        void showAd();                                  //显示广告页面
        void removeAd();                                //移除广告页面
        void showLogo();                                //显示LOGO页面
        void removeLogo();                              //移除Logo页面
        void removeFragment(Fragment fragment);         //移除Fragment
        void toDownLoadActivity();
        MediaPlayerCallback getMediaPlayerCallback();
        FragmentManager getFragmentManager();           //其他Fragment页面获取fragmentManager
        MainView getView();
    }

    public interface MainModel{
        void insertDownLoadInfo(Context context,DownLoadBean bean);
        void updateDownLoadInfo(Context context,DownLoadBean bean);
        void checkDownLoadInfo(Context context,DownLoadBean bean,CheckDownLoadCallback callback);
        void insertMusicInfo(Context context,DownLoadBean downLoadBean);

//        List<DownLoadBean> getDownLoadInfo(Context context);
    }

    /**
     * 检查音频是否已下载回调
     */
    public interface CheckDownLoadCallback{
        void onNoExist();
        void onDownLoadingExist();
        void onDownLoadedExist();
    }
}
