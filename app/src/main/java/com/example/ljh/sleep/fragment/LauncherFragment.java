package com.example.ljh.sleep.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ljh.sleep.R;
import com.example.ljh.sleep.activity.MainActivity;
import com.example.ljh.sleep.contract.LauncherContract;
import com.example.ljh.sleep.presenter.LauncherPresenter;
import com.example.ljh.sleep.utils.MyApplication;

public class LauncherFragment extends Fragment implements LauncherContract.ILauncherView,
        View.OnClickListener{
    private ImageView ivAd;
    private TextView tvAd;
    private LauncherPresenter launcherPresenter;
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_launcher);
//        getSupportActionBar().hide();
//        initView();
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_launcher,null);
        initView(view);
        return view;
    }

    @Override
    public Context getContext() {
        if(getActivity() != null){
            return getActivity();
        }
        return MyApplication.getInstance();
    }

    @Override
    public void showAd(Bitmap bitmap) {
        ivAd.setImageBitmap(bitmap);
        ivAd.setVisibility(View.VISIBLE);
        tvAd.setVisibility(View.VISIBLE);
    }

    @Override
    public void skipAd() {
        launcherPresenter.stopTimer();
        toMainActivity();
    }

    @Override
    public void showLogo() {

    }

    @Override
    public void setTvAd(String string) {
        tvAd.setText(string);
    }

    @Override
    public void openAd(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void toMainActivity() {
        launcherPresenter.stopTimer();
        MainActivity.getPresenter().removeAd();
        MainActivity.getPresenter().showMain();
    }

    @Override
    public int getIvAdWidth() {
        return ivAd.getWidth();
    }

    @Override
    public int getIvAdHeight() {
        return ivAd.getHeight();
    }

    private void initView(View view){
        ivAd = view.findViewById(R.id.ivAd);
        tvAd = view.findViewById(R.id.tvAd);
        ivAd.setOnClickListener(this);
        tvAd.setOnClickListener(this);
        launcherPresenter = new LauncherPresenter(this);
        launcherPresenter.getAd();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ivAd:
                launcherPresenter.openAd();
                break;
            case R.id.tvAd:
                skipAd();
                break;
        }
    }
}
