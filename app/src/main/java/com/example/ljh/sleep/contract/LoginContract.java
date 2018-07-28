package com.example.ljh.sleep.contract;

import android.content.Context;

import com.example.ljh.sleep.bean.LoginBean;
import com.example.ljh.sleep.callback.MyRetrofitCallback;

public class LoginContract {
    public interface LoginView{
        Context getContext();
        LoginBean getInfo();    //获取用户名和密码
        LoginBean.LoginCheckBox getCheckBox();//获取记住密码和自动登录checkbox的状态
        void toRegister();      //注册
        void toForgetPassWord();//忘记密码
        void toMain();          //主页
        void showToast(String string);
        void showProgressBar(String string);
        void hideProgressBar();
        void setCbRemember();   //初始化checkbox的状态
        void setCbAuto();
        void setUsername(String username);     //初始化用户名和密码
        void setPassword(String password);
    }

    public interface LoginPresenter{
        void login();
        void loginCheckbox(LoginBean loginBean);
        void init();
    }

    public interface LoginModel{
        void login(LoginBean loginBean, MyRetrofitCallback callBack);   //登录
        void loginCheckbox(Context context, LoginBean.LoginCheckBox loginCheckBox,LoginBean loginBean);//成功登录后更新checkbox状态
    }

}
