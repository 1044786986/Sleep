package com.example.ljh.sleep.model;

import android.util.Log;

import com.example.ljh.sleep.application.ErrorTipApp;
import com.example.ljh.sleep.bean.ShowStoryRequestBean;
import com.example.ljh.sleep.bean.ShowStoryResponseBean;
import com.example.ljh.sleep.callback.MyRetrofitCallback;
import com.example.ljh.sleep.contract.ShowStoryContract;
import com.example.ljh.sleep.utils.IRetrofit;
import com.example.ljh.sleep.utils.RetrofitUtils;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ShowStoryModel implements ShowStoryContract.ShowStoryModel{
    @Override
    public void getData(ShowStoryRequestBean bean, final MyRetrofitCallback callBack) {
        RetrofitUtils.getRetrofitRx2Gson(RetrofitUtils.BASE_URL)
                .create(IRetrofit.class)
                .getShowStoryData(bean.getType())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ShowStoryResponseBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ShowStoryResponseBean value) {
//                        Log.i("aaa","ShowStoryModel.value.status = " + value.isStatus());
                        Log.i("aaa","ShowStoryModel.value.data = " + value.getData().get(0).getId());
                        callBack.onSuccess(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("aaa","ShowStoryModel.getData().error = " + e.getMessage());
                        callBack.onFailed(ErrorTipApp.LOAD_FAILED);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}
