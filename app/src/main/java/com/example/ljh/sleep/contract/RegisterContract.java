package com.example.ljh.sleep.contract;

import android.content.Context;

import com.example.ljh.sleep.bean.RegisterRequestBean;
import com.example.ljh.sleep.callback.MyRetrofitCallback;

public class RegisterContract {

    public interface RegisterView{
        Context getContext();
        void showProgressBar();
        void hideProgressBar();
        void showToast(String string);
        void finishThis();                      //关闭页面
        RegisterRequestBean getRegisterInfo();  //获取注册信息
    }

    public interface RegisterPresenter{
        void register();
    }

    public interface RegisterModel{
        void register(RegisterRequestBean registerRequestBean, MyRetrofitCallback callBack);
    }
}
