package com.example.ljh.sleep.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class TestService extends Service{
    private final IBinder binder = new MyBindService();

    public class MyBindService extends Binder{
        public TestService getService(){
            return TestService.this;
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("aaa","TestService.onCreate()");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
