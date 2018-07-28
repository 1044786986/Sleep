package com.example.ljh.sleep.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ljh.sleep.R;
import com.example.ljh.sleep.activity.MainActivity;
import com.example.ljh.sleep.utils.ThreadPoolUtils;

public class LogoFragment extends Fragment{
    private Handler handler = new Handler(Looper.getMainLooper());
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logo,null);
        startTimer();
        return view;
    }

    private void startTimer(){
//        MainActivity.getPresenter().initMain();
        ThreadPoolUtils.threadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                int i = 2;
                while (i > 0){
                    try {
                        Thread.sleep(1000);
                        i--;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.getPresenter().removeLogo();
                        MainActivity.getPresenter().showAd();
                    }
                });
            }
        });
    }
}
