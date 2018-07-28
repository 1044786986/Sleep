package com.example.ljh.sleep.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.ljh.sleep.R;
import com.example.ljh.sleep.utils.ScreenUtils;

public class FloatMediaPlayer {
    /**
     * 保存于share悬浮球位置的key
     */
    public static final String LOGO_X_KEY = "FloatMenuX";
    public static final String LOGO_Y_KEY = "FloatMenuY";
    public static final String SHAREDPREFERENCES_NAME = "FloatMenu";

    /**
     * 悬浮球在左边还是右边
     */
    public final int LEFT = 0;
    public final int RIGHT = 1;

    /**
     * 悬浮球恢复位置的速度
     * 打开音乐控制器的速度
     */
    public final int RECOVER_TIME = 200;
    public final int OPEN_TIME = 200;

    /**
     * 悬浮球位置x,y
     */
    private int logoX = 0;
    private int logoY = 0;

    /**
     * 判断悬浮球是否有在Y轴移动的参数
     */
    private int oldLogoY = 0;

    /**
     * 音乐控制器位置
     */
    private int mediaPlayerX;
    private int mediaPlayerY;

    /**
     * 手指在悬浮球里的位置
     */
    private float inLogoX;
    private float inLogoY;

    public int screenHeight = 0;       //屏幕高度
    public int screenWidth = 0;        //屏幕宽度
    public int statusBarHeight = 0;    //状态栏高度

    private boolean isMoving = false;     //悬浮球是否在移动
    private boolean isOpen = false;      //菜单是否被打开
    private boolean isOpening = false;
    private boolean isClosing = false;

    private Context context;
    private WindowManager windowManager;
    private WindowManager.LayoutParams logoLayoutParams;
    private WindowManager.LayoutParams mediaPlayerLayoutParams;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ValueAnimator valueAnimator;

    private ImageView logoView;
    private int logoViewHeight;
    private View mediaPlayer;
    private int mediaPlayerWidth;
    private int mediaPlayerHeight;
    private int mediaPlayerValue;

    public FloatMediaPlayer(Builder builder){
        this.context = builder.context;
        this.logoView = builder.logoView;
        this.mediaPlayer = builder.mediaPlayerView;
        this.logoViewHeight = builder.logoViewHeight;
        mediaPlayerWidth = (int) context.getResources().getDimension(R.dimen.dp_250);
//        mediaPlayerWidth = ConvertUtils.dp2px(context,mediaPlayerWidth);
//        Log.i("aaa","mediaPlayerWidth = " + mediaPlayerWidth);
        mediaPlayerHeight = logoViewHeight;
        init();
    }


    private View.OnTouchListener logoViewListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    inLogoX = motionEvent.getX();
                    inLogoY = motionEvent.getY();
//                    Log.i("aaa","down = " + inLogoX + " " + inLogoY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if((isOpen && logoX != 0 && logoX != screenWidth) || (isOpen && oldLogoY != logoY)){
                       closeMediaPlayer();
                    }
                    move(motionEvent.getRawX() - inLogoX,motionEvent.getRawY() - inLogoY);
                    break;
                case MotionEvent.ACTION_UP:
//                    Log.i("aaa","up");
                    /**
                     * 判断是否需要恢复位置
                     */
                    if((isMoving && logoX != 0 && logoX != screenWidth) || (isMoving && oldLogoY != logoY)){
                        recoverLogoView();
                    }else{
                        isMoving = false;
                        if (isOpen && !isOpening) {
                            closeMediaPlayer();
                        } else if(!isOpen && !isClosing){
                            openMediaPlayer();
                        }
                    }
                    break;
            }
            return true;
        }
    };

    /**
     * 打开音乐控制器
     */
    private synchronized void openMediaPlayer(){
        if(!isOpen && !isMoving) {
//            Log.i("aaa","openMediaPlayer()");
            windowManager.addView(mediaPlayer,mediaPlayerLayoutParams);
            isOpen = true;
            valueAnimator = getCurLocation() == LEFT ?
                    ValueAnimator.ofInt(mediaPlayerX,mediaPlayerX + mediaPlayerWidth + logoViewHeight):
                    ValueAnimator.ofInt(mediaPlayerX,mediaPlayerX - mediaPlayerWidth - logoViewHeight);
            valueAnimator.setDuration(200);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    mediaPlayerValue = (int) valueAnimator.getAnimatedValue();
                    updateMediaPlayerLocation(mediaPlayerValue);
                }
            });
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    isOpening = true;
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    isOpening = false;
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            valueAnimator.start();
        }
    }

    /**
     * 关闭音乐控制器
     */
    private synchronized void closeMediaPlayer(){
        if (isOpen) {
//            Log.i("aaa","closeMediaPlayer()");
            isOpen = false;
            valueAnimator = getCurLocation() == LEFT ?
                    ValueAnimator.ofInt(mediaPlayerX, mediaPlayerX - mediaPlayerWidth - logoViewHeight) :
                    ValueAnimator.ofInt(mediaPlayerX, mediaPlayerX + mediaPlayerWidth + logoViewHeight);
            valueAnimator.setDuration(200);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    mediaPlayerValue = (int) valueAnimator.getAnimatedValue();
                    updateMediaPlayerLocation(mediaPlayerValue);
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    windowManager.removeView(mediaPlayer);
                    isClosing = false;
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    isClosing = true;
                }
            });
            valueAnimator.start();
        }
    }

    /**
     * 移动悬浮球和音乐控制器
     */
    private void move(float x,float y){
//        if (Math.abs(x - logoX) > 10|| Math.abs(y - logoY) > 10){
            isMoving = true;
//            Log.i("aaa","move");
            if(y <= statusBarHeight){
                y = statusBarHeight;
            }
            logoX = (int) x;
            logoY = (int) y;
            if(logoX < 0){
                logoX = 0;
            }
            if(logoX >= screenWidth - logoViewHeight){
                logoX = screenWidth;
            }
            updateLogoView();
//        }

    }

    /**
     * 恢复悬浮球位置
     */
    private void recoverLogoView(){
        oldLogoY = logoY;
        /**
         * 判断悬浮球在左半区还是右半区
         */
        final int location = getCurLocation();
        if(location == LEFT){
            valueAnimator = ValueAnimator.ofInt(logoX,0);
        }else if(location == RIGHT){
            valueAnimator = ValueAnimator.ofInt(logoX,screenWidth);
        }
        valueAnimator.setDuration(RECOVER_TIME);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            int value;
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                value = (int) valueAnimator.getAnimatedValue();
                logoX = value;
                updateLogoView();
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                logoView.setOnTouchListener(null);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isMoving = false;
                logoView.setOnTouchListener(logoViewListener);
                updateMediaPlayerLp();
                updateSharedPreferences();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                isMoving = false;
                logoView.setOnTouchListener(logoViewListener);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        valueAnimator.start();
    }

    /**
     * 更新音乐悬浮logo的位置
     */
    private void updateLogoView(){
//        Log.i("aaa","logoX,logoY = " + logoX + "    " + logoY);
        logoLayoutParams.x = logoX;
        logoLayoutParams.y = logoY;
        windowManager.updateViewLayout(logoView,logoLayoutParams);
    }

    /**
     * 记录悬浮logo的位置
     */
    private void updateSharedPreferences(){
        editor.putInt(LOGO_X_KEY,logoX);
        editor.putInt(LOGO_Y_KEY,logoY);
        editor.commit();
    }

    /**
     * 更新MediaPlayerLayoutParams
     */
    private void updateMediaPlayerLp(){
        final int location = getCurLocation();
        if(location == LEFT){
            mediaPlayerX = logoX - mediaPlayerWidth;
        }else if(location == RIGHT){
            mediaPlayerX = logoX;
        }
        mediaPlayerY = (logoY > screenHeight - mediaPlayerHeight?logoY - mediaPlayerHeight:logoY)- statusBarHeight;
        mediaPlayerLayoutParams.x = mediaPlayerX;
        mediaPlayerLayoutParams.y = mediaPlayerY;
    }

    /**
     * 更新音乐控制器的位置
     * @param mediaPlayerValue
     */
    private void updateMediaPlayerLocation(int mediaPlayerValue){
        mediaPlayerX = mediaPlayerValue;
        mediaPlayerLayoutParams.x = mediaPlayerX;
        windowManager.updateViewLayout(mediaPlayer,mediaPlayerLayoutParams);
    }

    private void init(){
        /**
         * 获取屏幕信息
         */
        screenWidth = ScreenUtils.getInstance(context).getScreenWidth();
        screenHeight = ScreenUtils.getInstance(context).getScreenHeight();
        statusBarHeight = ScreenUtils.getInstance(context).getStatusBarHeight();

        /**
         * 获取sharePreferences的信息
         */
        sharedPreferences  = context.getSharedPreferences(SHAREDPREFERENCES_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        logoX = sharedPreferences.getInt(LOGO_X_KEY,screenWidth);
        logoY = sharedPreferences.getInt(LOGO_Y_KEY,screenHeight / 2);
        oldLogoY = logoY;
        mediaPlayerX = getCurLocation() == LEFT?logoX - mediaPlayerWidth : logoX;
//        Log.i("aaa",mediaPlayerX+"");
        mediaPlayerY = (logoY > screenHeight - mediaPlayerHeight?logoY - mediaPlayerHeight:logoY)- statusBarHeight;
        Log.i("aaa","FloatMediaPlayer.logoY = " + logoY);
        Log.i("aaa","FloatMediaPlayer.mediaPlayerY = " + mediaPlayerY);

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        /**
         * 初始化悬浮球Logo
         */
        logoLayoutParams = new WindowManager.LayoutParams();
        logoLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        logoLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        logoLayoutParams.format = PixelFormat.TRANSLUCENT;
        logoLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        logoLayoutParams.flags =
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        logoLayoutParams.alpha = 1;
        logoLayoutParams.x = logoX;
        logoLayoutParams.y = logoY;
        Log.i("aaa","FloatMediaPlayer.logoLayoutParams.y = " + logoLayoutParams.y);
        logoView.setOnTouchListener(logoViewListener);

        /**
         * 初始化音乐悬浮窗
         */
        mediaPlayerLayoutParams = new WindowManager.LayoutParams();
//        mediaPlayerLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mediaPlayerLayoutParams.width = mediaPlayerWidth;
        mediaPlayerLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mediaPlayerLayoutParams.alpha = 1;
        mediaPlayerLayoutParams.format = PixelFormat.TRANSLUCENT;
        mediaPlayerLayoutParams.x = mediaPlayerX;
        mediaPlayerLayoutParams.y = mediaPlayerY;
        mediaPlayerLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        mediaPlayerLayoutParams.flags =
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mediaPlayerLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mediaPlayerLayoutParams.token = logoView.getWindowToken();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            if(Build.VERSION.SDK_INT >= 23){
                logoLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
//                mediaPlayerLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }else{
                logoLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
//                mediaPlayerLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            }
        }else{
            logoLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
//            mediaPlayerLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
//        windowManager.addView(mediaPlayer,mediaPlayerLayoutParams);
        windowManager.addView(logoView,logoLayoutParams);
    }

    private int getCurLocation(){
        if(logoX < screenWidth/2){
            return LEFT;
        }
        return RIGHT;
    }

    public static class Builder{
        private Context context;
        private ImageView logoView;
        private View mediaPlayerView;
        private int logoViewHeight;

        public Builder(){}

        public Builder setContext(Context context){
            this.context = context;
            return this;
        }

        public Builder setLogoView(ImageView imageView){
            this.logoView = imageView;
            return this;
        }

        public Builder setMediaPlayerView(View view){
            this.mediaPlayerView = view;
            return this;
        }

        public Builder setLogoViewHeight(int height){
            this.logoViewHeight = height;
            return this;
        }

        public FloatMediaPlayer build(){
            return new FloatMediaPlayer(this);
        }
    }
}
