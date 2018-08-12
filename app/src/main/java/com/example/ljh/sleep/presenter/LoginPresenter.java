package com.example.ljh.sleep.presenter;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ljh.sleep.application.ErrorTipApp;
import com.example.ljh.sleep.application.KeyApp;
import com.example.ljh.sleep.bean.LoginBean;
import com.example.ljh.sleep.callback.MyRetrofitCallback;
import com.example.ljh.sleep.contract.LoginContract;
import com.example.ljh.sleep.model.LoginModel;

public class LoginPresenter implements LoginContract.LoginPresenter{
    private LoginContract.LoginView loginView;
    private LoginContract.LoginModel loginModel;

    public LoginPresenter(LoginContract.LoginView loginView){
        this.loginView = loginView;
        loginModel = new LoginModel();
    }

    @Override
    public void login() {
        loginView.showProgressBar("");
        final LoginBean loginBean = loginView.getInfo();
        loginModel.login(loginBean,new MyRetrofitCallback() {
            @Override
            public void onSuccess(Object o) {
                loginView.hideProgressBar();
                LoginBean.LoginResponse value = (LoginBean.LoginResponse)o;
                if (value.isStatus()) {
                    loginCheckbox(loginBean);
                    loginView.toMain();
                } else {
                    int responseCode = value.getReasonCode();
                    if (responseCode == 0) {
                        loginView.showToast(ErrorTipApp.USERNAME_ERROR);
                    } else if (responseCode == 1) {
                        loginView.showToast(ErrorTipApp.PASSWORD_ERROR);
                    }
                }
            }

            @Override
            public void onFailed(String error) {
                loginView.hideProgressBar();
                loginView.showToast(error);
            }
        });
    }

    @Override
    public void loginCheckbox(LoginBean loginBean) {
        loginModel.loginCheckbox(loginView.getContext(),loginView.getCheckBox(),loginBean);
    }

    /**
     * 初始化复选框和用户名、密码的状态
     */
    @Override
    public void init() {
        SharedPreferences sharedPreferences =
                loginView.getContext().getSharedPreferences(KeyApp.name, Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean(KeyApp.REMEMBER_PASSWORD,false)){
            loginView.setCbRemember();
        }
        if(sharedPreferences.getBoolean(KeyApp.AUTO_LOGIN,false)){
            loginView.setCbAuto();
        }
        loginView.setUsername(sharedPreferences.getString(KeyApp.LOGIN_NAME,""));
        loginView.setUsername(sharedPreferences.getString(KeyApp.LOGIN_PASSWORD,""));
    }

}
