package com.example.ljh.sleep.presenter;

import android.content.Context;
import android.util.Log;

import com.example.ljh.sleep.application.ErrorTipApp;
import com.example.ljh.sleep.application.RegexApp;
import com.example.ljh.sleep.application.TipApp;
import com.example.ljh.sleep.bean.RegisterRequestBean;
import com.example.ljh.sleep.bean.RegisterResponseBean;
import com.example.ljh.sleep.callback.MyRetrofitCallback;
import com.example.ljh.sleep.contract.RegisterContract;
import com.example.ljh.sleep.model.RegisterModel;
import com.example.ljh.sleep.utils.EncodeUtils;
import com.example.ljh.sleep.utils.ShowTipUtils;

public class RegisterPresenter implements RegisterContract.RegisterPresenter{
    private RegisterContract.RegisterView registerView;
    private RegisterContract.RegisterModel registerModel;

    public RegisterPresenter(RegisterContract.RegisterView registerView){
        this.registerView = registerView;
        registerModel = new RegisterModel();
    }

    /**
     * 注册
     */
    @Override
    public void register() {
        RegisterRequestBean registerRequestBean = registerView.getRegisterInfo();
        final Context context = registerView.getContext();
        String username = registerRequestBean.getUsername();
        String password = registerRequestBean.getPassword();
        String email = registerRequestBean.getEmail();
        /**
         * 检查输入格式
         */
        if(username == "" || username == null){
            ShowTipUtils.toastShort(context, ErrorTipApp.USERNAME_NULL);
        }else if(password == "" || password == null){
            ShowTipUtils.toastShort(context, ErrorTipApp.PASSWORD_NULL);
        }else if(email == "" || email == null){
            ShowTipUtils.toastShort(context, ErrorTipApp.EMAIL_NULL);
        }else if(!username.matches(RegexApp.USERNAME)){
            ShowTipUtils.toastShort(context, ErrorTipApp.USERNAME_FORMAT);
        }else if(!password.matches(RegexApp.PASSWORD)){
            ShowTipUtils.toastShort(context, ErrorTipApp.PASSWORD_FORMAT);
        }else if(!email.matches(RegexApp.EMAIL)){
            ShowTipUtils.toastShort(context, ErrorTipApp.EMAIL_FORMAT);
        }else{
            registerView.showProgressBar();
            registerRequestBean.setPassword(EncodeUtils.ShaEncode(password));//对密码进行加密
            registerModel.register(registerRequestBean, new MyRetrofitCallback() {
                @Override
                public void onSuccess(Object o) {
                    registerView.hideProgressBar();
                    RegisterResponseBean bean = (RegisterResponseBean) o;
                    //注册成功
                    if(bean.isStatus()){
                        ShowTipUtils.showAlertDialog(context, TipApp.REGISTER_SUCCESS, 1,
                                new ShowTipUtils.AlertDialogCallback() {
                                    @Override
                                    public void positive() {
                                        registerView.finishThis();
                                    }
                                    @Override
                                    public void negative() {
                                    }
                                });
                    }
                    //注册失败
                    else{
                        int responseCode = bean.getResponseCode();
                        if(responseCode == 0){  //用户名已被注册
                            ShowTipUtils.toastShort(context,ErrorTipApp.USERNAME_EXIST);
                        }else if(responseCode == 1){//邮箱已被注册
                            ShowTipUtils.toastShort(context,ErrorTipApp.EMAIL_EXIST);
                        }
                    }
                }

                @Override
                public void onFailed(String error) {
                    Log.i("aaa","error = " + error);
                    registerView.hideProgressBar();
                    ShowTipUtils.toastShort(context,ErrorTipApp.CONN_ERROR);
                }
            });
        }

    }
}
