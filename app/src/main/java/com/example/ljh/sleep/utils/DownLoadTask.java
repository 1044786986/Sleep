package com.example.ljh.sleep.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.ljh.sleep.bean.DownLoadBean;
import com.example.ljh.sleep.callback.DownLoadCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class DownLoadTask extends Thread{
    private boolean mPause = true;                              //标记是否暂停任务
    private boolean isInterrupt = false;
    private boolean isSdCard = false;                           //是否有sd卡
    private boolean isTimer = false;                            //计时器是否开启

    private int mDownLoadSpeedLength = 0;//用来计算下载速度
    private int mDownLoadLength = 0;     //记录已下载大小
    private int mFileLength = 0;
    private int mProgress = 0;               //下载进度
    public int mDownLoadType = 0;

//    private Handler handler = new Handler(Looper.getMainLooper());
//    private File file;
    private Context mContext;
    private Thread mThread;
    private Runnable mRunnable = null;                                 //下载任务
    private Timer mTimer;
    private TimerTask mTimerTask;
    private DownLoadBean mDownLoadBean;
    private DownLoadCallback mDownLoadCallback;

    DownLoadTask(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            isSdCard = true;
            DownLoadUtils2.mDownLoadDir = Environment.getExternalStorageDirectory()+"/sleepMusic/";
            checkDir(DownLoadUtils2.mDownLoadDir);
        }else{
            isSdCard = false;
        }
    }

    public DownLoadTask(Context context, final DownLoadBean downLoadBean, final DownLoadCallback callback){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            isSdCard = true;
            DownLoadUtils2.mDownLoadDir = Environment.getExternalStorageDirectory()+"/sleepMusic/";
            checkDir(DownLoadUtils2.mDownLoadDir);
            mDownLoadBean = downLoadBean;
            mDownLoadCallback = callback;
            mContext = context;
        }else{
            isSdCard = false;
        }
    }

    @Override
    public void run() {
        super.run();
        Log.i("aaa","是不是一定执行呢");
        if(mDownLoadType == DownLoadUtils2.DOWNLOAD || mDownLoadType == DownLoadUtils2.DOWNLAD_RELOAD){
//            downLoad(mContext,mDownLoadBean,mDownLoadCallback);
        }else if(mDownLoadType == DownLoadUtils2.DOWNLOAD_CONTINUE){

        }
    }

    /**
     * 下载任务
     * @param context
     * @param downLoadBean
     * @param callback
     */
    public void downLoad(Context context, final DownLoadBean downLoadBean, final DownLoadCallback callback){
        /**
         * 判断手机是否有安装sd卡
         */
        if(isSdCard == false){
            ShowTipUtils.showAlertDialog(context,"未安装sd卡",1,null);
            return;
        }
        mDownLoadBean = downLoadBean;
        mDownLoadCallback = callback;
        final String url = downLoadBean.getUrl();
        String filepath = downLoadBean.getFilepath();
        //第一次下载
       if(filepath == null) {
           Log.i("aaa","DownLoadTask.任务第一次下载且filepath == null" + mDownLoadType + "  " + filepath);
           /**
            * 判断后缀名并重命名
            */
           String filename = downLoadBean.getName();
           String lastName = "";
           if (url.contains(".mp3")) {
               lastName = ".mp3";
           } else if (url.contains(".mp4")) {
               lastName = ".mp4";
           }
           filename = filename + lastName;
           filepath = DownLoadUtils2.mDownLoadDir + filename;
           /**
            * 保存下载路径
            */
           mDownLoadBean.setFilepath(filepath);
           callback.onDownStart(mDownLoadBean);
       }else{
           Log.i("aaa","DownLoadTask任务不是第一下载");
       }
        final File file = new File(filepath);

        mRunnable = new Runnable() {
            @Override
            public void run() {
                /**
                 * 下载任务达到最大限制，暂停下载
                 */

                if(DownLoadUtils2.mTaskCount >= DownLoadUtils2.MAX_TASK_COUNT){
                    try {
                        synchronized (mRunnable){
                            mPause = true;
                            callback.onDownWait(downLoadBean);
                            mRunnable.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                DownLoadUtils2.mTaskCount++;   //任务计数+1
                mPause = false; //标记正在下载
                InputStream inputStream = null;
                OutputStream outputStream = null;
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
                    httpURLConnection.setReadTimeout(5000);
                    inputStream = httpURLConnection.getInputStream();
                    outputStream = new FileOutputStream(file);
                    byte bytes[] = new byte[4*1024];
                    int n;
                    mFileLength = httpURLConnection.getContentLength();        //总大小

                    /**
                     * 网络连接成功
                     */
                    if(mFileLength > 0){
                        while ((n = inputStream.read(bytes)) != -1){
                            if(isInterrupt){
                                InterruptTask();
                                return;
                            }
                            outputStream.write(bytes,0,n);
                            mDownLoadLength += n;                               //用来计算下载进度
                            mDownLoadSpeedLength += n;                          //用来计算下载速度
                            mDownLoadBean.setStatus(DownLoadBean.DOWNLOAD_ING); //标记正在下载
                            startTimer();
                            /**
                             * 暂停任务
                             */
                            if(mPause){
                                synchronized (mRunnable){
                                    mPause = true;
                                    stopTimer();
                                    callback.onDownWait(mDownLoadBean);
                                    mRunnable.wait();
                                }
                            }
                        }
                        //网络连接失败
                    }else{
                        Log.i("aaa","音频数据获取失败--DownLoadUtils.fileLength<0");
                        onFailed(callback,mDownLoadBean);
                        return;
                    }
                    /**
                     * 下载完成
                     */
                    callback.onDownFinish(mDownLoadBean);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    onFailed(callback,mDownLoadBean);
                } catch (IOException e) {
                    e.printStackTrace();
                    onFailed(callback,mDownLoadBean);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    stopTimer();
                    DownLoadUtils2.mTaskCount--;
                    try {
                        if(outputStream != null){
                            outputStream.close();
                        }
                        if(inputStream != null){
                            inputStream.close();
                        }
                        InterruptTask();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        CacheThreadPoolUtils.getInstance().execute(mRunnable);
//        mThread = CacheThreadPoolUtils.getInstance().getThread(mRunnable);
//        mThread = new Thread(mRunnable);
//        mThread.start();
    }

    /**
     * 终止下载后继续下载
     * @param context
     * @param downLoadBean
     * @param callback
     */
    public void downLoadContinue(Context context, final DownLoadBean downLoadBean, final DownLoadCallback callback){
        /**
         * 判断手机是否有安装sd卡
         */
        if(isSdCard == false){
            ShowTipUtils.showAlertDialog(context,"未安装sd卡",1,null);
            return;
        }

        mDownLoadBean = downLoadBean;
        mDownLoadCallback = callback;
        final String url = downLoadBean.getUrl();
        final String filePth = downLoadBean.getFilepath();
        if(filePth == null){
            downLoad(context,downLoadBean,callback);
            return;
        }

        mRunnable = new Runnable() {
            @Override
            public void run() {
                /**
                 * 下载任务达到最大限制，暂停下载
                 */

                if(DownLoadUtils2.mTaskCount >= DownLoadUtils2.MAX_TASK_COUNT){
                    try {
                        synchronized (mRunnable){
                            mPause = true;
                            callback.onDownWait(downLoadBean);
                            mRunnable.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                DownLoadUtils2.mTaskCount++;   //任务计数+1
                mPause = false; //标记正在下载
                InputStream inputStream = null;
                OutputStream outputStream = null;
                try {
                    File file = new File(filePth);
                    RandomAccessFile randomAccessFile = new RandomAccessFile(file,"rwd");
                    randomAccessFile.seek(downLoadBean.getLength());

                    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
                    httpURLConnection.setReadTimeout(DownLoadUtils2.READ_TIME_OUT);
                    httpURLConnection.setConnectTimeout(DownLoadUtils2.CONN_TIME_OUT);
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setRequestProperty
                            ("Range","bytes=" + mDownLoadBean.getLength() + "-" + mDownLoadBean.getFilelength());
                    /**
                     * 网络连接成功
                     */
                    if (httpURLConnection.getResponseCode() == 206) {
                        inputStream = httpURLConnection.getInputStream();
//                    outputStream = new FileOutputStream(randomAccessFile);
                        byte bytes[] = new byte[4 * 1024];
                        int n;
                        while ((n = inputStream.read(bytes)) != -1){
                            if(isInterrupt){
                                Thread.currentThread().interrupt();
                                return;
                            }
                            randomAccessFile.write(bytes,0,n);
                            mDownLoadLength += n;                               //用来计算下载进度
                            mDownLoadSpeedLength += n;                          //用来计算下载速度
                            mDownLoadBean.setStatus(DownLoadBean.DOWNLOAD_ING); //标记正在下载
                            startTimer();
                            /**
                             * 暂停任务
                             */
                            if(mPause){
                                synchronized (mRunnable){
                                    mPause = true;
                                    stopTimer();
                                    callback.onDownWait(mDownLoadBean);
                                    mRunnable.wait();
                                }
                            }
                        }
                        //网络连接失败
                    }else{
                        Log.i("aaa","DownLoadUtils.fileLength<0");
                        onFailed(callback,mDownLoadBean);
                        return;
                    }

                    callback.onDownFinish(mDownLoadBean);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    onFailed(callback,mDownLoadBean);
                } catch (IOException e) {
                    e.printStackTrace();
                    onFailed(callback,mDownLoadBean);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    onFailed(callback,mDownLoadBean);
                } finally {
                    DownLoadUtils2.mTaskCount--;
                    stopTimer();
                    try {
                        outputStream.flush();
                        outputStream.close();
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
//        CacheThreadPoolUtils.getInstance().execute(mRunnable);
    }

    private void onFailed(final DownLoadCallback callback, final DownLoadBean downLoadBean){
        callback.onFailed(downLoadBean, "下载失败");
        Thread.currentThread().interrupt();
    }

    /**
     * 开始任务
     */
    public void startTask(){
        if(mRunnable != null){
            synchronized(mRunnable){
                mPause = false;
                mRunnable.notify();
                startTimer();
            }
        }
    }

    public void stopTask(){
        isInterrupt = true;
//        if(mThread != null && mThread.isInterrupted()){
//            mThread.interrupt();
////            mThread = null;
//            String a = "a";
//        }
    }

    public void InterruptTask(){
        Thread.currentThread().interrupt();
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
     * 开启计时，每秒更新速度
     */
    private void startTimer(){
        if(!isTimer){
            mTimer = new Timer();
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if(!mPause){
                        mProgress = mDownLoadLength *100 / mFileLength;         //计算进度
                        mDownLoadBean.setSpeed(mDownLoadSpeedLength / 1000);//计算速度
                        mDownLoadBean.setLength(mDownLoadLength);               //保存已下载大小
                        mDownLoadBean.setProgress(mProgress);                   //保存已下载进度
                        mDownLoadCallback.onDowning(mDownLoadBean);
                        mDownLoadSpeedLength = 0;                               //重置每秒进度
                    }
                    if(isInterrupt){
                        InterruptTask();
                    }
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

    public Thread getThread(){
        return mThread;
    }

    /**
     * 获取任务runnable
     */
    public Runnable getRunnable(){
        return mRunnable;
    }

    /**
     * 获取下载信息
     */
    public DownLoadBean getDownLoadBean(){
        return mDownLoadBean;
    }

    public void setDownLoadType(int DownLoadType){
        mDownLoadType = DownLoadType;
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
