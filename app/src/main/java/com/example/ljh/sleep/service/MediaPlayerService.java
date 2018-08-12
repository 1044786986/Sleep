package com.example.ljh.sleep.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.ljh.sleep.activity.MainActivity;
import com.example.ljh.sleep.bean.MediaPlayBean;
import com.example.ljh.sleep.callback.MediaPlayerCallback;
import com.example.ljh.sleep.presenter.MainPresenter;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MediaPlayerService extends Service{
    private MediaPlayer mediaPlayer;
    private final Messenger messenger = new Messenger(new MediaPlayHandler());
    public static boolean isPlay = false;               //是否正在播放
    public static boolean isLoading = false;             //是否正在加载
    public final static int PLAY_PATH = 0;              //播放本地连接
    public final static int PLAY_URL = 1;               //播放网络连接
    public final static int START = 2;                  //暂停播放后开始播放
    public final static int PAUSE = 3;                  //暂停播放
    public final static int NEXT = 4;                   //下一首
    public final static int PREV = 5;                   //上一首
    public final static int GET_CUR_POSITION = 6;       //获取当前播放进度
    public final static int SEEK_TO = 7;                //播放指定位置
    /**
     * 播放模式
     */
    public final static int ORDER_PLAY_MODE = 8;       //顺序播放
    public final static int LOOP_PLAY_MODE = 9;         //循环播放
    public final static int RANDOM_PLAY_MODE = 10;      //随机播放
    public final static int SINGLE_PLAY_MODE = 11;      //单曲循环

    public class MediaPlayHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            Log.i("aaa","mes.what = " + msg.what);
            switch (msg.what){
                //播放网络音频
                case PLAY_URL:
                    MediaPlayBean bean = (MediaPlayBean) msg.obj;
                    PlayUrl(bean.getId(),bean.getPath(),bean.getListener());
//                    MainActivity.getPresenter().resetStatus(); //重置计时
                    break;
                case PLAY_PATH:
                    break;
                    //暂停后开始播放
                case START:
                    if(!isPlay && !isLoading && mediaPlayer != null){
                        isPlay = true;
                        mediaPlayer.start();
                    }
                    break;
                    //暂停
                case PAUSE:
                    if(isPlay && !isLoading && mediaPlayer != null){
                        isPlay = false;
                        mediaPlayer.pause();
                    }
                    break;
                    //获取当前播放位置
                case GET_CUR_POSITION:
                    MainPresenter.curDuration = getCurPosition();
                    break;
                    //拖拉进度条
                case SEEK_TO:
                    if(mediaPlayer != null){
                        mediaPlayer.seekTo(MainPresenter.curDuration);
                    }
                    break;
            }
        }
    }

    /**
     * 播放本地音频
     * @param filePath
     * @param callback
     */
    private synchronized void PlayPath(final int id,String filePath, final MediaPlayerCallback callback){
        isPlay = false;
        isLoading = true;
        checkMediaPlayer();
        try {
            File file = new File(filePath);
            FileInputStream fs = new FileInputStream(file);
            FileDescriptor fd = fs.getFD();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(fd);
            setMediaPlayerListener(callback);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                    MainActivity.getPresenter().updateSecondProgress(i);
                }
            });
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    isPlay = true;
                    isLoading = false;
                    MainActivity.getPresenter().music_duration = mediaPlayer.getDuration();//获得当前音频的时长
                    MainActivity.getPresenter().startTimer();
                    mediaPlayer.start();                        //开始播放
                    callback.onStart(id);
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放网络音频
     * @param url
     * @param callback
     */
    private synchronized void PlayUrl(final int id,String url, final MediaPlayerCallback callback){
        isPlay = false;
        isLoading = true;
        checkMediaPlayer();
//        isPlay = true;
        try {
            mediaPlayer.setDataSource(url);
            setMediaPlayerListener(callback);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                    MainActivity.getPresenter().updateSecondProgress(i);
                }
            });
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    isPlay = true;
                    isLoading = false;
                    mediaPlayer.start();                        //开始播放
                    MainActivity.getPresenter().music_duration = mediaPlayer.getDuration();//获得当前音频的时长
                    MainActivity.getPresenter().startTimer();
                    callback.onStart(id);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param listener
     */
    private void setMediaPlayerListener(final MediaPlayerCallback listener){
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                mediaPlayer.reset();
                isPlay = false;
                isLoading = false;
                listener.onError();
                return false;
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                isPlay = false;
                isLoading = false;
                listener.onComplete();
            }
        });
    }

    /**
     * 获取当前播放进度
     * @return
     */
    private int getCurPosition(){
        if(mediaPlayer != null){
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    private boolean isPlaying(){
        if(mediaPlayer != null){
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    /**
     *
     */
    private void checkMediaPlayer(){
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        } else {
            if(isPlay || isPlaying()){
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("aaa","MediaPlayerService.onDestroy()");
    }
}
