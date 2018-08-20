package com.example.ljh.sleep.utils;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.SparseArrayCompat;

import com.example.ljh.sleep.bean.DownLoadBean;
import com.example.ljh.sleep.callback.DownLoadCallback;
import com.socks.library.KLog;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class DownLoadUtils {
    private static File cacheDir;                               //缓存路径
    private static String mDownLoadDir = Environment.getExternalStorageDirectory()+"/sleepMusic/";                                //下载路径

    private boolean mPause = true;                              //标记是否暂停任务
    private boolean isSdCard = false;                           //是否有sd卡
    private boolean isTimer = false;                            //计时器是否开启

    private int mDownLoadSpeedLength = 0;//用来计算下载速度
    private int mDownLoadLength = 0;     //记录已下载大小
    private int mProgress = 0;               //下载进度

    public static int mTaskCount = 0;                            //下载任务数量

    public static final int MAX_TASK_COUNT = 5;                   //最大下载数
    public static final int MEASURE_SPEED_SPACE = 1;

    public static SparseArrayCompat<Call> mTaskMap = new SparseArrayCompat<>();//任务集合

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
    public void downLoad(final Context context,final DownLoadBean downLoadBean,final DownLoadCallback callback){
//        Observable.just(downLoadBean)
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .filter(new Predicate<DownLoadBean>() {
//                    @Override
//                    public boolean test(DownLoadBean downLoadBean) throws Exception {
//                        boolean isSdCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
//                        if(!isSdCard){
//                            ShowTipUtils.showAlertDialog(context,"未安装sd卡",1,null);
//                        }
//                        return isSdCard;
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .map(new Function<DownLoadBean, Object>() {
//                    @Override
//                    public Object apply(DownLoadBean downLoadBean) throws Exception {
//                        String filepath = downLoadBean.getFilepath();
//                        String url = downLoadBean.getUrl();
//                        //第一次下载
//                        if(filepath == null) {
//                            KLog.i("第一次下载 = " + downLoadBean.getName());
//                            /**
//                             * 判断后缀名并重命名
//                             */
//                            String filename = downLoadBean.getName();
//                            String lastName = "";
//                            if (url.contains(".mp3")) {
//                                lastName = ".mp3";
//                            } else if (url.contains(".mp4")) {
//                                lastName = ".mp4";
//                            }
//                            filename = filename + lastName + "-" +downLoadBean.getId();
//                            filepath = mDownLoadDir + filename;
//                            /**
//                             * 保存下载路径
//                             */
//                            downLoadBean.setFilepath(filepath);
//                            callback.onDownStart(downLoadBean);
//                    }
//                        return downLoadBean;
//                }})
//                .flatMap(new Function<Object, ObservableSource<?>>() {
//            @Override
//            public ObservableSource<?> apply(Object o) throws Exception {
//                Observable.create()
//            }
//        })
//        KLog.i(downLoadBean.getUrl());
        RetrofitUtils.getRetrofitRx(RetrofitUtils.BASE_URL)
                .create(IRetrofit.class)
                .downLoad()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody value) {
                        try {
                            KLog.i("onNext: "+value.string() +" " +value.contentLength());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        KLog.i("onError: " + e);
                    }

                    @Override
                    public void onComplete() {
                        KLog.i("onComplete");
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
