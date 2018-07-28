package com.example.ljh.sleep.presenter;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.ljh.sleep.activity.MainActivity;
import com.example.ljh.sleep.bean.LauncherAdBean;
import com.example.ljh.sleep.callback.MyRetrofitCallback;
import com.example.ljh.sleep.contract.LauncherContract;
import com.example.ljh.sleep.model.LauncherModel;

public class LauncherPresenter implements LauncherContract.ILauncherPresenter{
    private LauncherContract.ILauncherModel launcherModel;
    private LauncherContract.ILauncherView launcherView;
    private LauncherAdBean launcherAdBean;
    public static boolean showAd = false;

    public LauncherPresenter(LauncherContract.ILauncherView launcherView){
        launcherModel = new LauncherModel();
        this.launcherView = launcherView;
    }

    @Override
    public void getAd() {
        launcherModel.getAd(launcherView.getContext(),launcherView.getIvAdWidth(),
                launcherView.getIvAdHeight(),new MyRetrofitCallback() {
            @Override
            public void onSuccess(Object o) {
                showAd = true;
                launcherAdBean = (LauncherAdBean) o;
                getView().showAd(launcherAdBean.getBitmapAd());
                startTimer();
            }

            @Override
            public void onFailed(String error) {
                Log.i("aaa","LauncherPresenter.getAd().error = " + error);
                MainActivity.getPresenter().removeAd();
//                MainActivity.getPresenter().showMain();
            }
        });
    }

    @Override
    public void startTimer() {
        launcherModel.startTimer(new LauncherContract.setAdCallBack() {
            @Override
            public void setAd(String string) {
                launcherView.setTvAd(string);
            }

            @Override
            public synchronized void finish() {
                MainActivity.getPresenter().removeAd();
                MainActivity.getPresenter().showMain();
            }
        });
    }

    @Override
    public void openAd() {
        String path = launcherAdBean.getPath();
        if(path != "" && path != null){
            Uri uri = Uri.parse(path);
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            getView().openAd(intent);
        }
    }

    @Override
    public void stopTimer() {
        launcherModel.stopTimer();
    }

    @Override
    public LauncherContract.ILauncherView getView() {
        if(launcherView == null){
            throw new IllegalStateException("View not attached");
        }else{
            return launcherView;
        }
    }

}
