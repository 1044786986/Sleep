package com.example.ljh.sleep.utils;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池工具
 */
public class ThreadPoolUtils {
    private final int maxPoolSize = 0;
    private final int corePoolSize = 5;
    private int time = 60;
    private TimeUnit timeUnit = TimeUnit.SECONDS;
    private ThreadPoolExecutor threadPoolExecutor;
    public static final ThreadPoolUtils threadPoolUtils = new ThreadPoolUtils();

    ThreadPoolUtils(){
        threadPoolExecutor = new ThreadPoolExecutor(corePoolSize,Integer.MAX_VALUE,
                time,timeUnit,new SynchronousQueue<Runnable>());
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
}