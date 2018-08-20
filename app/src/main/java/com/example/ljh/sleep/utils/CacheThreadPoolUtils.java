package com.example.ljh.sleep.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CacheThreadPoolUtils {
    private final int MAX_POOL_SIZE = 5;
    private final int CORE_POOL_SIZE = 5;
    private int time = 60;
    private TimeUnit timeUnit = TimeUnit.SECONDS;
    private ThreadPoolExecutor threadPoolExecutor;
    public static final CacheThreadPoolUtils cacheThreadPoolUtils = new CacheThreadPoolUtils();

    public CacheThreadPoolUtils(){
        threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE,MAX_POOL_SIZE,
                time,timeUnit,new LinkedBlockingQueue<Runnable>());
    }

    public static CacheThreadPoolUtils getInstance(){
        return cacheThreadPoolUtils;
    }

    public void execute(Runnable runnable){
        if(runnable != null){
            threadPoolExecutor.execute(runnable);
        }
    }

    public void remove(Runnable runnable){
        if(runnable != null){
            threadPoolExecutor.remove(runnable);
        }
    }

    public Thread getThread(Runnable runnable){
        return threadPoolExecutor.getThreadFactory().newThread(runnable);
    }

    public int getPoolSize(){
        return threadPoolExecutor.getPoolSize();
    }

    public int getCorePoolSize(){
        return threadPoolExecutor.getCorePoolSize();
    }

    public int getQueue(){
        return threadPoolExecutor.getQueue().size();
    }

    public BlockingQueue getQueueList(){
        return threadPoolExecutor.getQueue();
    }

    public int  getActiveCount(){
        return threadPoolExecutor.getActiveCount();
    }

    public int getCompletedTaskCount(){
        return (int) threadPoolExecutor.getCompletedTaskCount();
    }

    public int getLargestPoolSize(){
        return threadPoolExecutor.getLargestPoolSize();
    }

    public int getMaximumPoolSize(){
        return threadPoolExecutor.getMaximumPoolSize();
    }

    public int getTaskCount(){
        return (int) threadPoolExecutor.getTaskCount();
    }
}
