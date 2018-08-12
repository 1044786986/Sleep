package com.example.ljh.sleep.contract;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.example.ljh.sleep.callback.MyRetrofitCallback;

public class LauncherContract {

    public interface ILauncherView{
        Context getContext();
        void showAd(Bitmap bitmap);     //显示广告
        void skipAd();                  //跳过广告
        void showLogo();                //显示APP的启动LOGO
        void setTvAd(String string);    //跳过广告倒计时
        void openAd(Intent intent);
        void toMainActivity();          //跳转到主页
        int getIvAdWidth();             //获取广告页ImageView的宽度和高度
        int getIvAdHeight();
    }

    public interface ILauncherPresenter{
        void getAd();                   //获取广告
        void openAd();                  //打开广告页面
        void startTimer();              //开始倒计时
        void stopTimer();               //停止倒计时
        ILauncherView getView();
    }

    public interface ILauncherModel{
        /**
         *
         * @param context
         * @param width ImageView宽
         * @param height ImageView高
         * @param callBack
         */
        void getAd(Context context,int width,int height,MyRetrofitCallback callBack);

        /**
         * 开始计时
         * @param callBack
         */
        void startTimer(LauncherContract.setAdCallBack callBack);

        /**
         * 终止计时
         */
        void stopTimer();
    }

    /**
     * 更改倒计时时间
     */
    public interface setAdCallBack{
        void setAd(String string);
        void finish();
    }
}
