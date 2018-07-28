package com.example.ljh.sleep.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

public class MyApplication extends Application{
    private static Application application;
    private static int activityCount = 0;
    private ActivityLifecycleCallbacks activityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            activityCount++;
            Log.i("aaa","MyApplication.onActivityStart()");
        }

        @Override
        public void onActivityResumed(Activity activity) {
            Log.i("aaa","MyApplication.onActivityResumed()");
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            Log.i("aaa","MyApplication.onActivityStop()");
            activityCount--;
            if(activityCount == 0){

            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

//    public static boolean isForeground1(){
//        return isForeground;
//    }

    /**
     * 防止fragment中getContext返回null对象，导致程序崩溃
     * 此方法有内存溢出的风险
     */
    public static Application getInstance(){
        return application;
    }
}
