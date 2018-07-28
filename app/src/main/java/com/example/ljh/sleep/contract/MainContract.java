package com.example.ljh.sleep.contract;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.example.ljh.sleep.bean.MusicInfoBean;

public class MainContract {
    public interface MainView{
        void initFloatWindow(); //初始化音乐控制器
        void setMediaPlayerInfo(MusicInfoBean bean);//设置音乐控制器内容
//        void setMusicInfo(MusicInfoBean bean);//记录当前播放的音频信息
        void updateSeekBar(int i);//更新进度条
        void updateTimer(String string);      //更新计时器
        void changePage(int pos);//切换页面
        void showPlayIcon();    //音乐控制器显示播放按钮
        void showPauseIcon();   //音乐控制器显示暂停按钮
        void showBottomView();   //显示底部导航栏
        Context getContext();
        Bundle getSaveInstanceState();//判断页面是否保存过
        FragmentManager getMyFragmentManager(); //获取FragmentManager
    }

    public interface MainPresenter{
        void initMain();//初始化主页
        void showMain();//显示主页
        void changeCurPage(int position);//切换页面
        void setMusicInfo(MusicInfoBean musicInfo);//记录当前播放的音频信息
        void startTimer();
        void pauseTimer();
        void reStartTimer();
        void getCurPosition();//获取当前音乐播放的时长
        void bindMediaPlayService();//绑定播放音乐服务
        void unbindMediaPlayService();//解绑服务
//        void initFloatWindow();
        void initAd();  //初始化广告页面
        void showAd();  //显示广告页面
        void removeAd();
        void showLogo();//显示LOGO页面
        void removeLogo();
        void removeFragment(Fragment fragment);//移除Fragment
        FragmentManager getFragmentManager();  //其他Fragment页面获取fragmentManager
        MainView getView();
    }

    public interface MainModel{
        void startTimer();
        void pauseTimer();
        void reStartTimer();
    }

//    public interface MediaPlayerListener{
//        void play(String string);
//    }
}
