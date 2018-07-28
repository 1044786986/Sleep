package com.example.ljh.sleep.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.ljh.sleep.R;
import com.example.ljh.sleep.bean.RegisterRequestBean;
import com.example.ljh.sleep.contract.RegisterContract;
import com.example.ljh.sleep.presenter.RegisterPresenter;

public class RegisterActivity extends AppCompatActivity implements RegisterContract.RegisterView,View.OnClickListener{
    private EditText etUsername,etPassword,etEmail;
    private Button btRegister;
    private ProgressBar progressBar;
    private RegisterPresenter registerPresenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btRegister:
                registerPresenter.register();
                break;
        }
    }

    private void init(){
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        btRegister = findViewById(R.id.btRegister);
        progressBar = findViewById(R.id.pbRegister);
        btRegister.setOnClickListener(this);
        registerPresenter = new RegisterPresenter(this);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showToast(String string) {

    }

    @Override
    public void finishThis() {
        synchronized(this){
            finish();
        }
    }

    @Override
    public RegisterRequestBean getRegisterInfo() {
        RegisterRequestBean registerRequestBean = new RegisterRequestBean();
        registerRequestBean.setUsername(etUsername.getText()+"");
        registerRequestBean.setPassword(etPassword.getText()+"");
        registerRequestBean.setEmail(etEmail.getText()+"");
        return registerRequestBean;
    }
}
