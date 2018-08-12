package com.example.ljh.sleep.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.ljh.sleep.R;
import com.example.ljh.sleep.bean.MusicInfoBean;
import com.example.ljh.sleep.callback.PermissionResultCallback;
import com.example.ljh.sleep.contract.MainContract;
import com.example.ljh.sleep.presenter.MainPresenter;
import com.example.ljh.sleep.service.MediaPlayerService;
import com.example.ljh.sleep.ui.CircleImageDrawable;
import com.example.ljh.sleep.ui.FloatMediaPlayer;

public class MainActivity extends PermissionManagerActivity implements MainContract.MainView,View.OnClickListener{
    private View mediaPlayerView;       //音乐控制器视图
    private ImageView mediaPlayerLogo;  //音乐悬浮球logo
    private CircleImageDrawable circleImageDrawable;
    private ImageView ivNext,ivPrev,ivPlay;//上一首、下一首、暂停
    private TextView tvName,tvAuthor;   //名字、作者
    private TextView tvDuration,tvTimer;//总时长、计时器
    private SeekBar seekBar;
    private NavigationView navigationView;

    private LinearLayout linearLayout_home,linearLayout_folder,linearLayout_bottom;//底部导航栏
    private ImageView ivCloud,ivFolder; //底部导航栏图片
    private TextView tvCloud,tvFolder;  //底部导航栏文字

    private FloatMediaPlayer floatMediaPlayer;
    private Bitmap mediaPlayerLogoBitmap;

    private FragmentManager fragmentManager;
    private static MainPresenter mainPresenter;

    private Bundle saveInstanceState;
    private int curPos = 0; //记录在第几个页面
    private boolean isTrackingTouch = false;//是否在拖动进度条

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.saveInstanceState = saveInstanceState;
        setContentView(R.layout.activity_main);
//        getSupportActionBar().hide();
        init();
        mainPresenter.bindMediaPlayService();
        mainPresenter.initAd();     //初始化广告页面
        mainPresenter.showLogo();   //显示Logo页面
        mainPresenter.initMain();   //初始化主页
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.linearLayout_cloud:
                changePage(0);
                break;
            case R.id.linearLayout_folder:
                changePage(1);
                break;
            case R.id.ivPlay:
                if(MediaPlayerService.isPlay){
                    mainPresenter.pause();
                }else{
                    mainPresenter.start();
                }
                break;
            case R.id.ivPrev:
                mainPresenter.prev();
                break;
            case R.id.ivNext:
                mainPresenter.next();
                break;
        }
    }

    /**
     * 音乐进度条
     */
    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            mainPresenter.updateTimer(i);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isTrackingTouch = true;
            mainPresenter.pauseTimer();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.i("aaa","onStopTrackingTouch()");
            isTrackingTouch = false;
            mainPresenter.seekToProgress();
        }
    };

    /**
     * 初始化音乐控制器
     */
    @Override
    public void initFloatWindow() {
        mediaPlayerLogoBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.cd);
        circleImageDrawable = new CircleImageDrawable(this,mediaPlayerLogoBitmap);
        mediaPlayerLogo = new ImageView(this);
        mediaPlayerLogo.setImageDrawable(circleImageDrawable);
        //重置imageView的高度
        ViewGroup.LayoutParams layoutParams =
                new ViewGroup.LayoutParams(circleImageDrawable.getIntrinsicWidth(),circleImageDrawable.getIntrinsicHeight());
        mediaPlayerLogo.setLayoutParams(layoutParams);
        mediaPlayerLogo.setBackgroundResource(R.drawable.bg_float_media_player);

        //音乐控制器
        mediaPlayerView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.float_media_player,null);
        ivPrev = mediaPlayerView.findViewById(R.id.ivPrev);
        ivPlay = mediaPlayerView.findViewById(R.id.ivPlay);
        ivNext = mediaPlayerView.findViewById(R.id.ivNext);
        ivPrev.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        tvName = mediaPlayerView.findViewById(R.id.tvName);
        tvName.setSelected(true);
        tvAuthor = mediaPlayerView.findViewById(R.id.tvAuthor);
        tvDuration = mediaPlayerView.findViewById(R.id.tvDuration);
        tvTimer = mediaPlayerView.findViewById(R.id.tvTimer);
        seekBar = mediaPlayerView.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);

        floatMediaPlayer = new FloatMediaPlayer.Builder()
                .setContext(this)
                .setLogoView(mediaPlayerLogo)
                .setMediaPlayerView(mediaPlayerView)
                .setLogoViewHeight(circleImageDrawable.getIntrinsicHeight())
                .build();
        mainPresenter.initRotationLogo();
    }

    /**
     * 修改音乐控制器的信息
     */
    @Override
    public void setMediaPlayerInfo(MusicInfoBean bean) {
        tvName.setText(bean.getName());
        tvAuthor.setText(bean.getAuthor());
        tvDuration.setText(bean.getDuration());
    }

    @Override
    public void resetSeekBar() {
        seekBar.setProgress(0);
        seekBar.setSecondaryProgress(0);
    }

    @Override
    public void updateSeekBar(int i) {
        seekBar.setProgress(i);
    }

    @Override
    public void updateSecondSeekBar(int i) {
        seekBar.setSecondaryProgress(i);
    }

    @Override
    public void updateTimer(String string) {
        tvTimer.setText(string);
    }

    /**
     * 切换页面
     * @param pos
     */
    @Override
    public void changePage(int pos) {
        if(curPos != pos){  //判断是否是在当前页面
            curPos = pos;
            switch (pos){
                case 0:
                    ivCloud.setImageResource(R.drawable.home_select);
                    tvCloud.setTextColor(getResources().getColor(R.color.bottom_text_selected));
                    break;
                case 1:
                    ivFolder.setImageResource(R.drawable.home_select);
                    tvFolder.setTextColor(getResources().getColor(R.color.bottom_text_selected));
                    break;
            }
            mainPresenter.changeCurPage(pos);
        }
    }


    @Override
    public void showPlayIcon() {
        ivPlay.setImageResource(R.drawable.play);
    }

    @Override
    public void showPauseIcon() {
        ivPlay.setImageResource(R.drawable.pause);
    }

    private void init(){
        linearLayout_bottom = findViewById(R.id.linearLayout_bottom);
        linearLayout_home = findViewById(R.id.linearLayout_cloud);
        linearLayout_home.setOnClickListener(this);
        linearLayout_folder = findViewById(R.id.linearLayout_folder);
        linearLayout_folder.setOnClickListener(this);

        ivCloud = findViewById(R.id.ivCloud);
        ivFolder = findViewById(R.id.ivFolder);
        tvCloud = findViewById(R.id.tvCloud);
        tvFolder = findViewById(R.id.tvFolder);

        navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_my:
                        break;
                    case R.id.nav_download:
                        mainPresenter.toDownLoadActivity();
                        break;
                    case R.id.nav_problem:
                        break;
                    case R.id.nav_setting:
                        break;
                }
                return false;
            }
        });

        fragmentManager = getSupportFragmentManager();
        mainPresenter = new MainPresenter(this);
    }

    @Override
    public void showBottomView() {
        linearLayout_bottom.setVisibility(View.VISIBLE);
    }

    @Override
    public void checkPermission(String[] permissions, PermissionResultCallback callback) {
        applyPermission(permissions,callback);
    }

    @Override
    public void finishView() {
        finish();
    }


    @Override
    public boolean isTrackingTouch() {
        return isTrackingTouch;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public Bundle getSaveInstanceState() {
        return saveInstanceState;
    }

    //    @Override
    public static MainPresenter getPresenter() {
        return mainPresenter;
    }

    @Override
    public FragmentManager getMyFragmentManager() {
        return fragmentManager;
    }

    @Override
    public ImageView getMediaPlayerLogo() {
        return mediaPlayerLogo;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainPresenter.unbindMediaPlayService();
        mainPresenter = null;
    }
}
