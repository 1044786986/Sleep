package com.example.ljh.sleep.model;

import com.example.ljh.sleep.contract.MainContract;

import java.util.Timer;
import java.util.TimerTask;

public class MainModel implements MainContract.MainModel{
    private Timer timer = new Timer();
    private TimerTask timerTask;

    @Override
    public void startTimer() {

    }

    @Override
    public void pauseTimer() {

    }

    @Override
    public void reStartTimer() {

    }
}
