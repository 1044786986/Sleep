package com.example.ljh.sleep.utils;

import android.content.Context;
import android.support.v4.util.SparseArrayCompat;
import android.util.Log;

import com.example.ljh.sleep.bean.DownLoadBean;
import com.example.ljh.sleep.callback.DownLoadCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownLoadUtils2 {
    public static File cacheDir;                               //缓存路径
    public static String mDownLoadDir;                                //下载路径

    public static int mTaskCount = 0;                            //下载任务数量

    public static final int MAX_TASK_COUNT = 5;                   //最大下载数
    public static final int MEASURE_SPEED_SPACE = 1;              //更新下载速度的间隔
    public static final int DOWNLOAD = 0;
    public static final int DOWNLOAD_CONTINUE = 1;
    public static final int DOWNLAD_RELOAD = 2;
    public static final int READ_TIME_OUT = 5000;
    public static final int CONN_TIME_OUT = 5000;

    public static SparseArrayCompat<DownLoadTask> mTaskMap = new SparseArrayCompat<>();//任务集合
    public static List<DownLoadTask> mTaskMap1 = new ArrayList<>();
    private static DownLoadUtils2 downLoadUtils2;

    public static DownLoadUtils2 getInstance(){
        if(downLoadUtils2 == null){
            synchronized (DownLoadUtils2.class){
                downLoadUtils2 = new DownLoadUtils2();
            }
        }
        return downLoadUtils2;
    }

    /**
     * 下载任务
     * @param context
     * @param downLoadBean
     * @param callback
     */
    public synchronized void downLoad(Context context, final DownLoadBean downLoadBean, final DownLoadCallback callback){
//        DownLoadTask downLoadTask = new DownLoadTask(context,downLoadBean,callback);
        DownLoadTask downLoadTask = new DownLoadTask();
//        mTaskMap1.add(downLoadTask);
        mTaskMap.put(downLoadBean.getId(),downLoadTask);
//        downLoadTask.start();
        downLoadTask.downLoad(context,downLoadBean,callback);
    }

    /**
     * 终止下载后继续下载
     * @param context
     * @param downLoadBean
     * @param callback
     */
    public synchronized void downLoadContinue(Context context,DownLoadBean downLoadBean,DownLoadCallback callback){
        DownLoadTask downLoadTask = new DownLoadTask();
        downLoadTask.setDownLoadType(DOWNLOAD_CONTINUE);
        mTaskMap.put(downLoadBean.getId(),downLoadTask);
        downLoadTask.downLoadContinue(context,downLoadBean,callback);
    }

    /**
     * 重新下载任务
     * @param context
     * @param downLoadBean
     * @param callback
     */
    public synchronized void reLoad(Context context,DownLoadBean downLoadBean,DownLoadCallback callback){
        for(int i=0;i<mTaskMap.size();i++){
            if(downLoadBean.getId() == mTaskMap.valueAt(i).getDownLoadBean().getId()){
                CacheThreadPoolUtils.getInstance().remove(mTaskMap.valueAt(i).getRunnable());
                mTaskMap.remove(i);
                Log.i("aaa","DownLoadUtils2.移除任务");
                break;
            }
        }
        DownLoadTask downLoadTask = new DownLoadTask();
        downLoadTask.setDownLoadType(DOWNLAD_RELOAD);
        mTaskMap.put(downLoadBean.getId(),downLoadTask);
        Log.i("aaa","DownLoadUtils2.任务存入集合");
        downLoadTask.downLoad(context,downLoadBean,callback);
    }

    /**
     * 移除任务
     * @return
     */
    public synchronized void removeRunnable(DownLoadBean downLoadBean){
        int id = downLoadBean.getId();
        for(int i=0;i<DownLoadUtils2.mTaskMap.size();i++){
            if(id == DownLoadUtils2.mTaskMap.keyAt(i)){
//                CacheThreadPoolUtils.getInstance().remove(mTaskMap.valueAt(i).getRunnable());
                mTaskMap.valueAt(i).stopTask();
                Log.i("aaa","DownLoadTask.移除任务 " + mTaskMap.valueAt(i).getDownLoadBean().getName());
                mTaskMap.remove(i);
                break;
            }
        }
//        for(int i=0;i<DownLoadUtils2.mTaskMap1.size();i++){
//            if(id == DownLoadUtils2.mTaskMap1.get(i).getDownLoadBean().getId()){
////                CacheThreadPoolUtils.getInstance().remove(mTaskMap.valueAt(i).getRunnable());
////                mTaskMap1.get(i).stopTask();
//                Log.i("aaa","DownLoadTask.移除任务 " + mTaskMap1.get(i).getDownLoadBean().getName());
////                mTaskMap1.remove(i);
//                break;
//            }
        }


//    public void stopTask(DownLoadBean downLoadBean){
//        for(int i=0;i<DownLoadUtils2.mTaskMap1.size();i++){
//            if(downLoadBean.getId() == DownLoadUtils2.mTaskMap1.get(i).getDownLoadBean().getId()){
////                CacheThreadPoolUtils.getInstance().remove(mTaskMap.valueAt(i).getRunnable());
//                mTaskMap1.get(i).stopTask();
////                if(mTaskMap1.get(i) != null && !mTaskMap1.get(i).isInterrupted()){
////                    mTaskMap1.get(i).interrupt();
////                }
//                Log.i("aaa","DownLoadTask.终止线程 " + mTaskMap1.get(i).getDownLoadBean().getName());
//                mTaskMap1.remove(i);
//                break;
//            }
//        }
//    }

    public void stopTask(DownLoadBean downLoadBean){
        for(int i=0;i<DownLoadUtils2.mTaskMap.size();i++){
            if(downLoadBean.getId() == DownLoadUtils2.mTaskMap.valueAt(i).getDownLoadBean().getId()){
                mTaskMap.valueAt(i).stopTask();
                Log.i("aaa","DownLoadTask.终止线程 " + mTaskMap.valueAt(i).getDownLoadBean().getName());
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
//                if(DownLoadUtils2.mTaskMap.valueAt(i).getRunnable() != null){
                CacheThreadPoolUtils.getInstance().remove(mTaskMap.valueAt(i).getRunnable());
//                }
                mTaskMap.remove(i);
                break;
            }
        }
    }

    /**
     * 更新进度监听
     */
    public interface UpdateProgress{
        void updateProgress(DownLoadBean bean);
    }
}
