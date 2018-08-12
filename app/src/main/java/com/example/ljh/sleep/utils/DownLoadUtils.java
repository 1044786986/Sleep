package com.example.ljh.sleep.utils;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.SparseArrayCompat;

import com.example.ljh.sleep.bean.DownLoadBean;
import com.example.ljh.sleep.callback.DownLoadCallback;

import java.io.File;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DownLoadUtils {
    private static File cacheDir;                               //缓存路径
    private String mDownLoadDir;                                //下载路径

    private boolean mPause = true;                              //标记是否暂停任务
    private boolean isSdCard = false;                           //是否有sd卡
    private boolean isTimer = false;                            //计时器是否开启

    private int mDownLoadSpeedLength = 0;//用来计算下载速度
    private int mDownLoadLength = 0;     //记录已下载大小
    private int mProgress = 0;               //下载进度

    public static int mTaskCount = 0;                            //下载任务数量

    public static final int MAX_TASK_COUNT = 5;                   //最大下载数
    public static final int MEASURE_SPEED_SPACE = 1;

    public static SparseArrayCompat<DownLoadUtils> mTaskMap = new SparseArrayCompat<>();//任务集合

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable mRunnable;                                 //下载任务
    private Timer mTimer;
    private TimerTask mTimerTask;
    private DownLoadBean mDownLoadBean;
    private DownLoadCallback mDownLoadCallback;
    private static DownLoadUtils downLoadUtils;

    public DownLoadUtils(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            isSdCard = true;
            mDownLoadDir = Environment.getExternalStorageDirectory()+"/sleepMusic/";
            checkDir(mDownLoadDir);
        }else{
            isSdCard = false;
        }
    }

    public static DownLoadUtils getInstance(){
        if(downLoadUtils == null){
            synchronized (DownLoadUtils.class){
                downLoadUtils = new DownLoadUtils();
            }
        }
        return downLoadUtils;
    }

    /**
     * 下载任务
     * @param context
     * @param downLoadBean
     * @param callback
     */
    public void downLoad(Context context,final DownLoadBean downLoadBean,final DownLoadCallback callback){
        RetrofitUtils.getRetrofitRx(downLoadBean.getUrl())
                .create(IRetrofit.class)
                .downLoad()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<InputStream>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(InputStream value) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void onFailed(final DownLoadCallback callback, final DownLoadBean downLoadBean){
        handler.post(new Runnable() {
            @Override
            public void run() {
                cancelRunnable(downLoadBean);
                callback.onFailed(downLoadBean,"下载失败");
            }
        });
    }

    /**
     * 开始任务
     */
    public void startThread(){
        if(mRunnable != null){
            synchronized(mRunnable){
                mPause = false;
                mRunnable.notify();
                startTimer();
            }
        }
    }

//    /**
//     * 暂停任务
//     */
//    public void waitThread(){
//        if(mRunnable != null){
//            synchronized (mRunnable){
//                try {
//                    mPause = true;
//                    mRunnable.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    /**
     * 移除任务
     * @return
     */
    public void removeRunnable(DownLoadBean downLoadBean){
        int id = downLoadBean.getId();
        for(int i=0;i<mTaskMap.size();i++){
            if(id == mTaskMap.keyAt(i)){
                CacheThreadPoolUtils.getInstance().remove(mTaskMap.get(i).mRunnable);
                mTaskMap.remove(i);
                break;
            }
        }
    }

    /**
     * 网络出错取消任务
     * @param
     */
    private void cancelRunnable(DownLoadBean downLoadBean){
        int id = downLoadBean.getId();
        for(int i=0;i<mTaskMap.size();i++){
            if(id == mTaskMap.keyAt(i)){
                CacheThreadPoolUtils.getInstance().remove(mTaskMap.get(i).getRunnable());
                mTaskMap.remove(i);
                break;
            }
        }
    }

    /**
     * 开启计时，每秒更新速度
     */
    private void startTimer(){
        if(!isTimer){
            mTimer = new Timer();
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mDownLoadBean.setSpeed(mDownLoadSpeedLength / 1000);
                    mDownLoadBean.setLength(mDownLoadLength);
                    mDownLoadBean.setProgress(mProgress);
                    mDownLoadCallback.onDowning(mDownLoadBean);
                    mDownLoadSpeedLength = 0;
                }
            };
            mTimer.schedule(mTimerTask,0,1000);
            isTimer = true;
        }
    }

    /**
     * 暂停计时
     */
    private void stopTimer(){
        if(mTimer != null && mTimerTask != null){
            mTimerTask.cancel();
            mTimer.cancel();
            isTimer = false;
        }
    }

    /**
     * 获取任务runnable
     */
    private Runnable getRunnable(){
        return mRunnable;
    }


    /**
     * 检查文件是否已存在
     * @param file
     * @return
     */
    public static boolean checkExist(File file){
        if(file.exists()){
            return true;
        }
        return false;
    }

    /**
     * 检查文件夹是否存在
     * @param dir
     */
    private void checkDir(String dir){
        File file = new File(dir);
        if(!file.exists()){
            file.mkdir();
        }
    }

    /**
     * 更新进度监听
     */
    public interface UpdateProgress{
        void updateProgress(DownLoadBean bean);
    }
}
