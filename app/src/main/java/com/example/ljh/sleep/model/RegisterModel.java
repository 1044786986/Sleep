package com.example.ljh.sleep.model;

import com.example.ljh.sleep.bean.RegisterRequestBean;
import com.example.ljh.sleep.bean.RegisterResponseBean;
import com.example.ljh.sleep.callback.MyRetrofitCallback;
import com.example.ljh.sleep.contract.RegisterContract;
import com.example.ljh.sleep.utils.IRetrofit;
import com.example.ljh.sleep.utils.RetrofitUtils;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RegisterModel implements RegisterContract.RegisterModel {
    @Override
    public void register(RegisterRequestBean registerRequestBean, final MyRetrofitCallback callBack) {
        RetrofitUtils.getRetrofitRx2Gson(RetrofitUtils.BASE_URL)
                .create(IRetrofit.class)
                .register(registerRequestBean.getUsername(), registerRequestBean.getPassword(), registerRequestBean.getEmail())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RegisterResponseBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RegisterResponseBean value) {
                        callBack.onSuccess(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onFailed(e+"");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
