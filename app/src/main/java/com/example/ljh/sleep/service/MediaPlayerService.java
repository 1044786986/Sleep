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

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MediaPlayerService extends Service{
    private MediaPlayer mediaPlayer;
    private final Messenger messenger = new Messenger(new MediaPlayHandler());
    public static boolean isPlay = false;
    public final static int PLAY_PATH = 0;
    public final static int PLAY_URL = 1;
    public final static int START = 2;
    public final static int PAUSE = 3;
    public final static int NEXT = 4;
    public final static int PREV = 5;
    public final static int GET_CUR_POSITION = 6;

    public class MediaPlayHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i("aaa","mes.what = " + msg.what);
            switch (msg.what){
                case PLAY_URL:
                    MediaPlayBean bean = (MediaPlayBean) msg.obj;
                    PlayUrl(bean.getId(),bean.getPath(),bean.getListener());
                    break;
                case START:
                    if(!isPlay && !isPlaying()){
                        isPlay = true;
                        mediaPlayer.start();
                    }
                    break;
                case PAUSE:
                    if(isPlay && isPlaying()){
                        isPlay = false;
                        mediaPlayer.pause();
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
        checkMediaPlayer();
        try {
            File file = new File(filePath);
            FileInputStream fs = new FileInputStream(file);
            FileDescriptor fd = fs.getFD();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(fd);
            setMediaPlayerListener(callback);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();                        //开始播放
                    MainActivity.getPresenter().reStartTimer(); //开始计时
                    isPlay = true;
                    callback.onStart(id,mediaPlayer.getDuration());
                    MainActivity.getPresenter().duration = mediaPlayer.getDuration();//获得当前音频的时长
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
        checkMediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            setMediaPlayerListener(callback);
            mediaPlayer.prepareAsync();
//            mediaPlayer.seekTo(5);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();                        //开始播放
                    MainActivity.getPresenter().reStartTimer(); //开始计时
                    isPlay = true;
                    callback.onStart(id,mediaPlayer.getDuration());
                    MainActivity.getPresenter().duration = mediaPlayer.getDuration();//获得当前音频的时长
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
                listener.onError();
                return false;
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                isPlay = false;
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
