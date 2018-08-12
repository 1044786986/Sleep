package com.example.ljh.sleep.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.ljh.sleep.application.ErrorTipApp;
import com.example.ljh.sleep.application.KeyApp;
import com.example.ljh.sleep.bean.LoginBean;
import com.example.ljh.sleep.callback.MyRetrofitCallback;
import com.example.ljh.sleep.contract.LoginContract;
import com.example.ljh.sleep.utils.EncodeUtils;
import com.example.ljh.sleep.utils.IRetrofit;
import com.example.ljh.sleep.utils.RetrofitUtils;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LoginModel implements LoginContract.LoginModel{

    @Override
    public void login(LoginBean loginBean, final MyRetrofitCallback callBack) {
        String username = loginBean.getUsername();
        String password = loginBean.getPassword();
        if(username == "" || username == null){
            callBack.onFailed(ErrorTipApp.USERNAME_NULL);
        }else if(password == "" || password == null){
            callBack.onFailed(ErrorTipApp.PASSWORD_NULL);
        }else{
            username = EncodeUtils.ShaEncode(username);
            RetrofitUtils.getRetrofitRx2Gson(RetrofitUtils.BASE_URL)
                    .create(IRetrofit.class)
                    .login(username,password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<LoginBean.LoginResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(LoginBean.LoginResponse value) {
                            callBack.onSuccess(value);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i("aaa","error = " + e);
                            callBack.onFailed(ErrorTipApp.CONN_ERROR);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    @Override
    public void loginCheckbox(Context context, LoginBean.LoginCheckBox checkBox,LoginBean loginBean) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(KeyApp.name,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KeyApp.LOGIN_NAME,loginBean.getUsername());
        if(checkBox.isAuto()){
            checkBox.setRemember(true);
            editor.putBoolean(KeyApp.AUTO_LOGIN,true);
            editor.putBoolean(KeyApp.REMEMBER_PASSWORD,true);
        }else{
            editor.putBoolean(KeyApp.AUTO_LOGIN,false);
        }

        if(checkBox.isRemember()){
            editor.putBoolean(KeyApp.REMEMBER_PASSWORD,true);
            editor.putString(KeyApp.LOGIN_PASSWORD,loginBean.getPassword());
        }else{
            editor.putBoolean(KeyApp.REMEMBER_PASSWORD,false);
            editor.putString(KeyApp.LOGIN_PASSWORD,"");
        }
        editor.commit();
    }

}
