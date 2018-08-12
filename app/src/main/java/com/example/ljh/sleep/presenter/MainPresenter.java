package com.example.ljh.sleep.presenter;

import android.animation.ObjectAnimator;
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
import android.view.animation.LinearInterpolator;

import com.example.ljh.sleep.R;
import com.example.ljh.sleep.activity.DownLoadActivity;
import com.example.ljh.sleep.application.ErrorTipApp;
import com.example.ljh.sleep.bean.DownLoadBean;
import com.example.ljh.sleep.bean.EventBusBean;
import com.example.ljh.sleep.bean.MediaPlayBean;
import com.example.ljh.sleep.bean.MusicInfoBean;
import com.example.ljh.sleep.callback.DownLoadCallback;
import com.example.ljh.sleep.callback.MediaPlayerCallback;
import com.example.ljh.sleep.callback.MediaPlayerListener;
import com.example.ljh.sleep.contract.MainContract;
import com.example.ljh.sleep.fragment.LauncherFragment;
import com.example.ljh.sleep.fragment.LogoFragment;
import com.example.ljh.sleep.fragment.NetWorkStoryFragment;
import com.example.ljh.sleep.model.MainModel;
import com.example.ljh.sleep.service.MediaPlayerService;
import com.example.ljh.sleep.utils.ConvertUtils;
import com.example.ljh.sleep.utils.DownLoadUtils2;
import com.example.ljh.sleep.utils.SQLiteUtils;
import com.example.ljh.sleep.utils.SharedPreferencesUtils;
import com.example.ljh.sleep.utils.ShowTipUtils;
import com.example.ljh.sleep.utils.UpdateDuration;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainPresenter implements MainContract.MainPresenter,MediaPlayerListener{
    private boolean isDownLoadExist = false;              //判断音频是否已下载或正在下载
    private EventBusBean mEventBusBean;
//    private int curPage = 0;                            //判断是否在当前页面
    public static List<MusicInfoBean> curMusicList = new ArrayList<>();

    private MainContract.MainView mainView;
    private MainContract.MainModel mainModel;

    private Fragment fragment_logo,fragment_launcher;   //启动页面
    private Fragment fragment_networkStory;             //网络故事页面
    private Fragment fragment_local;                    //本地目录页面

    private Timer timer;                                //计时器
    private TimerTask timerTask;                        //计时任务
    private int timerTag = 0;                           //记录计时次数(100毫秒为例,10次则更新一次进度条)
    public static int curDuration = 0;                  //当前播放时长
    public static int music_duration = 0;               //音频总时长
    private boolean isTimer = false;                    //是否正在计时
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if(curDuration == music_duration && curDuration != 0){
                pauseTimer();
            }
            if(isTimer) {
                getCurPosition();
                getView().updateTimer(ConvertUtils.duration2min(curDuration));
                if(music_duration == 0){
                    getView().updateSeekBar(0);
                }else{
                    getView().updateSeekBar(curDuration * 100 / music_duration);
                }
            }
        }
    };

    private ObjectAnimator rotationAnim;    //旋转动画

    /**
     * 音乐服务连接
     */
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
    /**
     *  播放过程监听回调
     */
    public MediaPlayerCallback mediaPlayerCallback = new MediaPlayerCallback() {
        @Override
        public void onStart(int id) {
            String durationString = ConvertUtils.duration2min(music_duration);
            UpdateDuration.updateDuration(id,durationString);
        }

        @Override
        public void onComplete() {
            Log.i("aaa","RvAdapter_ShowStory.mediaPlayerCallback.onComplete()");
            pauseTimer();
            next();
        }

        @Override
        public void onError() {
            pauseTimer();
        }
    };

    ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////

    public MainPresenter(MainContract.MainView mainView){
        this.mainView = mainView;
        mainModel = new MainModel();
    }

    /**
     * 初始化主页
     */
    @Override
    public void initMain() {
        if(getView().getSaveInstanceState() == null){
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragment_networkStory = new NetWorkStoryFragment();
            fragmentTransaction.add(R.id.frameLayoutMain,fragment_networkStory,"fragment_networkStory");
            fragmentTransaction.hide(fragment_networkStory);
            fragmentTransaction.commit();
            setMusicInfo(new MusicInfoBean());
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
        getView().initFloatWindow();    //初始化悬浮窗
        getView().showBottomView();     //显示底部导航栏
//        getView().checkPermission(new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW,
//        Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionResultCallback() {
//            @Override
//            public void onSuccess() {
//                Log.i("aaa","MainPresenter.checkPermission.onSuccess()");
//            }
//
//            @Override
//            public void onFailed() {
//                Log.i("aaa","MainPresenter.checkPermission.onFailed()");
//                getView().finishView();
//            }
//        });
    }

    /**
     * 发送message到音乐服务
     * @param message
     */
    @Override
    public void sendMessageToMediaPlayer(Message message) {
        try {
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 点击底部栏按钮切换页面
     * @param position
     */
    @Override
    public void changeCurPage(int position) {
//        if(position != curPage){
//            position = curPage;
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
//        }
    }

    @Override
    public void play(MusicInfoBean musicInfoBean) {
        setMusicInfo(musicInfoBean);
        if(musicInfoBean == null){
            return;
        }
        getView().setMediaPlayerInfo(musicInfoBean);
        getView().showPauseIcon();
        resetStatus();
        rotationLogo();

        Message message = new Message();
        //判断是网络音频还是本地音频
        message.what =  musicInfoBean.isLocal()?MediaPlayerService.PLAY_PATH:MediaPlayerService.PLAY_URL;
        switch (message.what){
            case MediaPlayerService.PLAY_URL:
                MediaPlayBean bean = new MediaPlayBean();
                bean.setPath(musicInfoBean.getUrl());
                bean.setId(musicInfoBean.getId());
                bean.setListener(mediaPlayerCallback);
                message.obj = bean;
                break;
            case MediaPlayerService.PLAY_PATH:

                break;
        }
        sendMessageToMediaPlayer(message);
    }

    @Override
    public void pause() {
        if(MediaPlayerService.isLoading){
            return;
        }
        getView().showPlayIcon();
        stopRotationLogo();
        pauseTimer();
        Message message = new Message();
        message.what = MediaPlayerService.PAUSE;
        sendMessageToMediaPlayer(message);
    }

    @Override
    public void start() {
        if(MediaPlayerService.isLoading){
            return;
        }
        MusicInfoBean musicInfoBean = SharedPreferencesUtils.getMusicInfo(getView().getContext());
        if(musicInfoBean == null){
            return;
        }
        getView().showPauseIcon();
        rotationLogo();
        startTimer();
        Message message = new Message();
        message.what = MediaPlayerService.START;
        sendMessageToMediaPlayer(message);
    }

    @Override
    public void next() {
        MusicInfoBean bean = SharedPreferencesUtils.getMusicInfo(getView().getContext());
        if(bean == null){
            return;
        }
        int position = bean.getPosition();
        Log.i("aaa","MainPresenter.next().position = " + position);
        position++;
        int mode = SharedPreferencesUtils.getPlayMode(getView().getContext());

        if(bean.isLocal()){         //本地列表才可选择播放模式，网络列表只能顺序播放
            switch (mode){
                case MediaPlayerService.ORDER_PLAY_MODE:
                    if(position >= curMusicList.size()){
                        getView().setMediaPlayerInfo(new MusicInfoBean());
                    }else{
                        play(curMusicList.get(position));
                    }
                    break;
                case MediaPlayerService.LOOP_PLAY_MODE:
                    break;
                case MediaPlayerService.RANDOM_PLAY_MODE:
                    break;
                case MediaPlayerService.SINGLE_PLAY_MODE:
                    break;
            }
        }else{
            if(position < curMusicList.size()){
                play(curMusicList.get(position));
            }
        }

    }

    @Override
    public void prev() {
        MusicInfoBean bean = SharedPreferencesUtils.getMusicInfo(getView().getContext());
        if(bean == null){
            return;
        }
        int position = bean.getPosition();
        Log.i("aaa","MainPresenter.prev().position = " + position);
        position--;
        int mode = SharedPreferencesUtils.getPlayMode(getView().getContext());

        if(bean.isLocal()){         //本地列表才可选择播放模式，网络列表只能顺序播放
            switch (mode){
                case MediaPlayerService.ORDER_PLAY_MODE:
                    if(position < 0){
                        setMusicInfo(new MusicInfoBean());
                    }else{
                        play(curMusicList.get(position));
                    }
                    break;
                case MediaPlayerService.LOOP_PLAY_MODE:
                    break;
                case MediaPlayerService.RANDOM_PLAY_MODE:
                    break;
                case MediaPlayerService.SINGLE_PLAY_MODE:
                    break;
            }
        }else{
            if(position >= 0){
                play(curMusicList.get(position));
            }else{
//                setMusicInfo(new MusicInfoBean());
            }
        }
    }

    @Override
    public void setMusicInfo(MusicInfoBean musicInfo) {
        SharedPreferencesUtils.setMusicInfo(getView().getContext(),musicInfo);
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
                handler.post(timerRunnable);
            }
        };
        timer.schedule(timerTask, 0, 1000);
        isTimer = true;
    }

    /**
     * 暂停计时
     */
    @Override
    public void pauseTimer() {
        if(timer != null && timerTask != null){
            isTimer = false;
            timerTask.cancel();
            timer.purge();
            timer.cancel();
        }
    }

    /**
     * 重新计时
     */
    @Override
    public void resetStatus() {
        music_duration = 0;
        curDuration = 0;
    }

    /**
     * 拖动进度条的时候更新计时显示
     */
    @Override
    public void updateTimer(int progress) {
        if(getView().isTrackingTouch()){
            curDuration = music_duration * progress / 100;
            getView().updateTimer(ConvertUtils.duration2min(curDuration));
        }
    }

    @Override
    public void updateSecondProgress(int progress) {
        getView().updateSecondSeekBar(progress);
    }

    @Override
    public void seekToProgress() {
        startTimer();
        Message message = new Message();
        message.what = MediaPlayerService.SEEK_TO;
        sendMessageToMediaPlayer(message);
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

    /**
     * 跳转到下载页面
     */
    @Override
    public void toDownLoadActivity() {
        Intent intent = new Intent(getView().getContext(), DownLoadActivity.class);
        getView().getContext().startActivity(intent);
    }

    /**
     * 获取播放音频回调
     * @return
     */
    @Override
    public MediaPlayerCallback getMediaPlayerCallback() {
        return mediaPlayerCallback;
    }

    /**
     * 获取当前播放音频的位置
     */
    @Override
    public void getCurPosition() {
        Message message = new Message();
        message.what = MediaPlayerService.GET_CUR_POSITION;
        try {
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载音频
     * @param
     */
    @Override
    public void downLoad(Object o, final int downLoadType) {
        DownLoadBean downLoadBean = null;
        if(downLoadType == DownLoadUtils2.DOWNLOAD){
            final MusicInfoBean bean = (MusicInfoBean)o;
            downLoadBean = new DownLoadBean();
            downLoadBean.setName(bean.getName());
            downLoadBean.setAuthor(bean.getAuthor());
            downLoadBean.setType(bean.getType());
            downLoadBean.setUrl(bean.getUrl());
            downLoadBean.setDuration(bean.getDuration());
            downLoadBean.setId(bean.getId());

            final DownLoadBean downLoadBean1 = downLoadBean;
            /**
             * 检查此音频是否正在下载或已下载
             */
            mainModel.checkDownLoadInfo(getView().getContext(), downLoadBean, new MainContract.CheckDownLoadCallback() {
                @Override
                public void onNoExist() {
                    isDownLoadExist = false;
//                SQLiteUtils sqLiteUtils = new SQLiteUtils(getView().getContext(),null);
//                sqLiteUtils.insertDownLoadInfo(downLoadBean);
                }

                @Override
                public void onDownLoadingExist() {
                    ShowTipUtils.toastShort(getView().getContext(),ErrorTipApp.DOWNLOADING_EXIST);
                    isDownLoadExist = false;
                }

                @Override
                public void onDownLoadedExist() {
                    ShowTipUtils.showAlertDialog(getView().getContext(), ErrorTipApp.FILE_EXIST, 2,
                            new ShowTipUtils.AlertDialogCallback() {
                                @Override
                                public void positive() {
                                    isDownLoadExist = false;
                                    SQLiteUtils utils = new SQLiteUtils(getView().getContext(),null);
                                    utils.removeMusicInfo(bean);
//                                    utils.insertDownLoadInfo(downLoadBean1);
                                }

                                @Override
                                public void negative() {
                                    isDownLoadExist = true;
                                }
                            });
                }
            });
            //取消下载
            if(isDownLoadExist){
                return;
            }
        }else{
            downLoadBean = (DownLoadBean)o;
        }

        mEventBusBean = new EventBusBean();
        DownLoadUtils2.getInstance().downLoad(getView().getContext(), downLoadBean, new DownLoadCallback() {
            //开始下载
            @Override
            public void onDownStart(DownLoadBean bean) {
                //第一次下载，插入下载记录
                if(downLoadType == DownLoadUtils2.DOWNLOAD){
                    ShowTipUtils.toastShort(getView().getContext(), ErrorTipApp.DOWNLOAD_START);
                    SQLiteUtils sqLiteUtils = new SQLiteUtils(getView().getContext(),null);
                    sqLiteUtils.insertDownLoadInfo(bean);
                }
            }

            @Override
            public void onDownWait(DownLoadBean bean) {
                bean.setProgress(-1);
                mEventBusBean.setType("onDownWait");
                mEventBusBean.setObject(bean);
                EventBus.getDefault().post(mEventBusBean);
            }

            @Override
            public void DownPause(DownLoadBean bean) {
                mEventBusBean.setType("onDownPause");
                mEventBusBean.setObject(bean);
                EventBus.getDefault().post(mEventBusBean);
            }

            @Override
            public void DownReStart(DownLoadBean downLoadBean) {

            }

            @Override
            public void onDowning(DownLoadBean bean) {
                SQLiteUtils sqLiteUtils = new SQLiteUtils(getView().getContext(),null);
                sqLiteUtils.updateDownLoadProgress(bean);
                mEventBusBean.setType("onDowning");
                mEventBusBean.setObject(bean);
                EventBus.getDefault().post(mEventBusBean);   //发送到DownLoadActivity更新ui进度
            }

            @Override
            public void onDownFinish(DownLoadBean bean) {
//                DownLoadUtils2.getInstance().removeRunnable(bean);
                DownLoadUtils2.getInstance().stopTask(bean);
                mEventBusBean.setType("onDownFinish");
                mEventBusBean.setObject(bean);
                EventBus.getDefault().post(mEventBusBean);
                SQLiteUtils sqLiteUtils = new SQLiteUtils(mainView.getContext(),null);
                sqLiteUtils.insertMusicInfo(bean);
                sqLiteUtils.updateDownLoadStatus(bean);
            }

            @Override
            public void onFailed(DownLoadBean downLoadBean, String error) {
                DownLoadUtils2.getInstance().stopTask(downLoadBean);
//                DownLoadUtils2.getInstance().removeRunnable(downLoadBean);
                SQLiteUtils sqLiteUtils = new SQLiteUtils(getView().getContext(),null);
                downLoadBean.setStatus(DownLoadBean.DOWNLOAD_ERROR);
                sqLiteUtils.updateDownLoadStatus(downLoadBean);
            }

            @Override
            public boolean onExist() {
                ShowTipUtils.showAlertDialog(getView().getContext(), ErrorTipApp.FILE_EXIST,
                        2, new ShowTipUtils.AlertDialogCallback() {
                            @Override
                            public void positive() {
                                isDownLoadExist = true;
                            }

                            @Override
                            public void negative() {
                                isDownLoadExist = false;
                            }
                        });
                return isDownLoadExist;
            }
        });
    }

    @Override
    public void reLoad(final DownLoadBean downLoadBean) {
//        DownLoadUtils2.getInstance().removeRunnable(downLoadBean);
        DownLoadUtils2.getInstance().stopTask(downLoadBean);
        downLoad(downLoadBean,DownLoadUtils2.DOWNLAD_RELOAD);
    }

    @Override
    public void rotationLogo() {
        if(rotationAnim != null){
            rotationAnim.resume();
        }
    }

    @Override
    public void stopRotationLogo() {
        if(rotationAnim != null){
            rotationAnim.pause();
        }
    }

    @Override
    public void initRotationLogo() {
        rotationAnim = ObjectAnimator.ofFloat(getView().getMediaPlayerLogo(),
                "rotation",getView().getMediaPlayerLogo().getRotation(),360);
        rotationAnim.setDuration(10000);
        rotationAnim.setRepeatCount(-1);
        rotationAnim.setInterpolator(new LinearInterpolator());
        rotationAnim.start();
        rotationAnim.pause();
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
