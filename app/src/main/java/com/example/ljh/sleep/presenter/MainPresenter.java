package com.example.ljh.sleep.presenter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.example.ljh.sleep.R;
import com.example.ljh.sleep.application.ErrorTipApp;
import com.example.ljh.sleep.bean.MediaPlayBean;
import com.example.ljh.sleep.bean.MusicInfoBean;
import com.example.ljh.sleep.callback.MediaPlayerCallback;
import com.example.ljh.sleep.callback.MediaPlayerListener;
import com.example.ljh.sleep.contract.MainContract;
import com.example.ljh.sleep.fragment.LauncherFragment;
import com.example.ljh.sleep.fragment.LogoFragment;
import com.example.ljh.sleep.fragment.NetWorkStoryFragment;
import com.example.ljh.sleep.model.MainModel;
import com.example.ljh.sleep.service.MediaPlayerService;
import com.example.ljh.sleep.utils.ConvertUtils;
import com.example.ljh.sleep.utils.SharedPreferencesUtils;

import java.util.Timer;
import java.util.TimerTask;

public class MainPresenter implements MainContract.MainPresenter,MediaPlayerListener{
    public static boolean isPlay = false;   //判断是否在播放
    private int curPage = 0;

    private MainContract.MainView mainView;
    private MainContract.MainModel mainModel;

    private Fragment fragment_logo,fragment_launcher;//启动页面
    private Fragment fragment_networkStory; //网络故事页面
    private Fragment fragment_local;    //本地目录页面

    private Timer timer;            //计时器
    private TimerTask timerTask;    //计时任务
    private int timerTag = 0;       //记录计时次数(100毫秒为例,10次则更新一次进度条)
    private int curDuration = 0;        //当前播放时长
    public int duration = 0;       //音频总时长
    public int timerDuration = 0;  //计时时长
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if(timerDuration == duration){
                pauseTimer();
            }
            getView().updateTimer(ConvertUtils.duration2min(timerDuration));
//            Log.i("aaa","100/1000 = " + ConvertUtils.min2duration("12:19") + "   " +duration);
            timerTag++;
            if(timerTag % 10 == 0){
                timerTag = 0;
                mainView.updateSeekBar((int) (timerDuration * 100/ duration));
            }
        }
    };

    private Handler handler = new Handler(Looper.getMainLooper());
    private Messenger messenger;
    private ServiceConnection serviceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Log.i("aaa","MainPresenter.onServiceConnected()");
        messenger = new Messenger(iBinder);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        Log.i("aaa","MainPresenter.onServiceDisconnected()");
    }
};

    public MainPresenter(MainContract.MainView mainView){
        this.mainView = mainView;
        mainModel = new MainModel();
    }

    @Override
    public void initMain() {
        if(getView().getSaveInstanceState() == null){
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragment_networkStory = new NetWorkStoryFragment();
            fragmentTransaction.add(R.id.frameLayoutMain,fragment_networkStory,"fragment_networkStory");
            fragmentTransaction.hide(fragment_networkStory);
            fragmentTransaction.commit();
        }
    }

    /**
     * 显示主页
     */
    @Override
    public void showMain() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        if(fragment_networkStory != null){
            fragmentTransaction.show(fragment_networkStory);
            fragmentTransaction.commit();
        }
        getView().initFloatWindow();
        getView().showBottomView();
    }

    /**
     * 点击底部栏按钮切换页面
     * @param position
     */
    @Override
    public void changeCurPage(int position) {
        if(position != curPage){
            position = curPage;
            FragmentTransaction fragmentTransaction = mainView.getMyFragmentManager().beginTransaction();
            switch (position){
                case 0:
                    fragmentTransaction.hide(fragment_local);
                    fragmentTransaction.show(fragment_networkStory);
                    break;
                case 1:
                    fragmentTransaction.hide(fragment_networkStory);
                    fragmentTransaction.show(fragment_local);
                    break;
            }
            fragmentTransaction.commit();
        }
    }

    @Override
    public void play(MusicInfoBean musicInfoBean, MediaPlayerCallback callback) {
        setMusicInfo(musicInfoBean);
        getView().setMediaPlayerInfo(musicInfoBean);
        getView().showPauseIcon();

        Message message = new Message();
        //判断是网络音频还是本地音频
        message.what =  musicInfoBean.isLocal()?MediaPlayerService.PLAY_PATH:MediaPlayerService.PLAY_URL;
//        message.what = type;
        switch (message.what){
            case MediaPlayerService.PLAY_URL:
                MediaPlayBean bean = new MediaPlayBean();
                bean.setPath(musicInfoBean.getUrl());
                bean.setId(musicInfoBean.getId());
                bean.setListener(callback);
                message.obj = bean;
                break;
        }
        try {
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pause() {
        getView().showPlayIcon();
        pauseTimer();
        try {
            Message message = new Message();
            message.what = MediaPlayerService.PAUSE;
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        getView().showPauseIcon();
        startTimer();
        try {
            Message message = new Message();
            message.what = MediaPlayerService.START;
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void next() {

    }

    @Override
    public void prev() {

    }

    @Override
    public void setMusicInfo(MusicInfoBean musicInfo) {
        SharedPreferencesUtils.setMusicInfo(getView().getContext(),musicInfo);
//        mainView.setMusicInfo(bean);
    }

    /**
     * 暂停后开始计时
     */
    @Override
    public void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                timerDuration+=100;
                handler.post(timerRunnable);
            }
        };
        timer.schedule(timerTask, 100, 100);
    }

    /**
     * 暂停计时
     */
    @Override
    public void pauseTimer() {
        if(timerTask != null && !timerTask.cancel()){
            timerTask.cancel();
            timer.cancel();
        }
    }

    /**
     * 重新计时
     */
    @Override
    public void reStartTimer() {
        pauseTimer();
        getView().updateTimer("00:00");
//        timer = new Timer();
        timerDuration = 0;
        startTimer();
//        timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                timerDuration+=100;
//                handler.post(timerRunnable);
//            }
//        };
//        timer.schedule(timerTask,100,100);
    }

    @Override
    public void bindMediaPlayService() {
        /**
         * 绑定播放音频服务
         */
        Intent intent = new Intent(getView().getContext(), MediaPlayerService.class);
        getView().getContext().bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void unbindMediaPlayService() {
        getView().getContext().unbindService(serviceConnection);
    }

    /**
     * 初始化广告页面，但不显示
     */
    @Override
    public void initAd() {
        FragmentTransaction fragmentTransaction = getView().getMyFragmentManager().beginTransaction();
        fragment_launcher = new LauncherFragment();
        fragmentTransaction.add(R.id.frameLayoutMain,fragment_launcher);
        fragmentTransaction.hide(fragment_launcher);
        fragmentTransaction.commit();
    }

    /**
     * 显示广告页面
     */
    @Override
    public void showAd() {
        FragmentTransaction fragmentTransaction = getView().getMyFragmentManager().beginTransaction();
        if(fragment_launcher != null && LauncherPresenter.showAd){  //显示广告
            fragmentTransaction.show(fragment_launcher);
            fragmentTransaction.commit();
        }else{      //不显示广告则立刻显示主页
            showMain();
        }
    }

    /**
     * 移除广告页面
     */
    @Override
    public void removeAd() {
        removeFragment(fragment_launcher);
        fragment_launcher = null;
    }

    /**
     * 显示logo页面
     */
    @Override
    public void showLogo() {
        FragmentTransaction fragmentTransaction = getView().getMyFragmentManager().beginTransaction();
        if(fragment_logo == null) {
            fragment_logo = new LogoFragment();
            fragmentTransaction.add(R.id.frameLayoutMain, fragment_logo);
            fragmentTransaction.show(fragment_logo);
        }else{
            fragmentTransaction.show(fragment_logo);
        }
        fragmentTransaction.commit();
    }

    /**
     * 移除Logo页面
     */
    @Override
    public void removeLogo() {
        removeFragment(fragment_logo);
    }

    /**
     * 移除fragment
     * @param fragment
     */
    @Override
    public void removeFragment(Fragment fragment) {
        if(fragment != null){
            FragmentTransaction fragmentTransaction = getView().getMyFragmentManager().beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void getCurPosition() {
        Message message = new Message();
        message.what = MediaPlayerService.GET_CUR_POSITION;
    }

    @Override
    public FragmentManager getFragmentManager() {
        return getView().getMyFragmentManager();
    }

    @Override
    public MainContract.MainView getView() {
        if(mainView == null){
            throw new IllegalStateException(ErrorTipApp.VIEW_NULL);
        }else{
            return mainView;
        }
    }

}
