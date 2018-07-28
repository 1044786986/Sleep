package com.example.ljh.sleep.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ljh.sleep.R;
import com.example.ljh.sleep.bean.LoginBean;
import com.example.ljh.sleep.contract.LoginContract;
import com.example.ljh.sleep.presenter.LoginPresenter;

public class LoginActivity extends AppCompatActivity implements LoginContract.LoginView,View.OnClickListener{
    private TextView tvRegister,tvForget;
    private CheckBox cbRemember,cbAuto;
    private EditText etUsername,etPassword;
    private Button btLogin;
    private ProgressBar progressBar;
    private LoginContract.LoginPresenter loginPresenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        loginPresenter.init();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btLogin:
                loginPresenter.login();
                break;
            case R.id.tvForget:
                toForgetPassWord();
                break;
            case R.id.tvRegister:
                toRegister();
                break;
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public LoginBean getInfo() {
        LoginBean loginBean = new LoginBean();
        loginBean.setUsername(etUsername.getText()+"");
        loginBean.setPassword(etPassword.getText()+"");
        return loginBean;
    }

    @Override
    public LoginBean.LoginCheckBox getCheckBox() {
        LoginBean.LoginCheckBox loginCheckBox = new LoginBean.LoginCheckBox();
        loginCheckBox.setRemember(cbRemember.isChecked());
        loginCheckBox.setAuto(cbAuto.isChecked());
        return loginCheckBox;
    }

    @Override
    public void toRegister() {
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    public void toForgetPassWord() {
        Intent intent = new Intent(LoginActivity.this,ForgetPasswordActivity.class);
        startActivity(intent);
    }

    @Override
    public void toMain() {
        finish();
    }

    @Override
    public void showToast(String string) {
        Toast.makeText(this,string,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressBar(String string) {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void setCbRemember() {
        cbRemember.setChecked(true);
    }

    @Override
    public void setCbAuto() {
        cbAuto.setChecked(true);
    }

    @Override
    public void setUsername(String username) {
        etUsername.setText(username);
    }

    @Override
    public void setPassword(String password) {
        etPassword.setText(password);
    }

    private void init(){
        tvForget = findViewById(R.id.tvForget);
        tvRegister = findViewById(R.id.tvRegister);
        cbAuto = findViewById(R.id.cbAuto);
        cbRemember = findViewById(R.id.cbRemember);
        etUsername = findViewById(R.id.etUsername);
        etPassword= findViewById(R.id.etPassword);
        btLogin = findViewById(R.id.btLogin);
        progressBar = findViewById(R.id.pbLogin);
        tvForget.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        btLogin.setOnClickListener(this);

        loginPresenter = new LoginPresenter(this);
    }
}
