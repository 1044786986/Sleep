package com.example.ljh.sleep.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import com.example.ljh.sleep.application.ErrorTipApp;
import com.example.ljh.sleep.bean.LauncherAdBean;
import com.example.ljh.sleep.bean.LauncherBean;
import com.example.ljh.sleep.callback.MyRetrofitCallback;
import com.example.ljh.sleep.callback.UrlImageCallback;
import com.example.ljh.sleep.contract.LauncherContract;
import com.example.ljh.sleep.utils.CompressUtils;
import com.example.ljh.sleep.utils.GetUrlImageUtils;
import com.example.ljh.sleep.utils.IRetrofit;
import com.example.ljh.sleep.utils.RetrofitUtils;
import com.example.ljh.sleep.utils.ThreadPoolUtils;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LauncherModel implements LauncherContract.ILauncherModel{
    private final String baseUrl = RetrofitUtils.BASE_URL;
    private Runnable runnableTimer;
    private boolean startTimer = false;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void getAd(final Context context, final int width, final int height, final MyRetrofitCallback callBack) {
        RetrofitUtils.getRetrofitRx(baseUrl)
                .create(IRetrofit.class)
                .getAd("getAd")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LauncherBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(final LauncherBean value) {
//                        MainActivity.getPresenter().initMain(); //获取广告页面后初始化主页
                        if(value.isAd()){
                            String img_url = value.getImage_path();
//                            Log.i("aaa","image_url = " + img_url);
                            /**
                             * 获取网络图片
                             */
                            GetUrlImageUtils.getUrlImage(img_url, new UrlImageCallback() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                    bitmap = CompressUtils.getInstance(context).compress(bitmap,width,height);
                                    LauncherAdBean launcherAdBean = new LauncherAdBean();
                                    launcherAdBean.setPath(value.getPath());
                                    launcherAdBean.setBitmapAd(bitmap);
                                    callBack.onSuccess(launcherAdBean);
                                }

                                @Override
                                public void onFailed(String error) {
                                    callBack.onFailed(error);
                                }
                            });
                        }else{
                            callBack.onFailed("notAd");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
//                        MainActivity.getPresenter().initMain();
                        callBack.onFailed(ErrorTipApp.CONN_ERROR);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void startTimer(final LauncherContract.setAdCallBack callBack) {
        startTimer = true;
         runnableTimer = new Runnable() {
            @Override
            public void run() {
                int i = 4;
                while (i > 0 && startTimer) {
                    try {
                        i--;
                        Thread.sleep(1000);
                        final int a = i;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.setAd("跳过广告 " + a);
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (startTimer) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.finish();
                        }
                    });
                }
            }
        };

        ThreadPoolUtils.threadPoolUtils.execute(runnableTimer);
    }

    @Override
    public void stopTimer() {
        startTimer = false;
        ThreadPoolUtils.threadPoolUtils.remove(runnableTimer);
    }


}
