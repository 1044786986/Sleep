package com.example.ljh.sleep.utils;

import android.content.Context;
import android.util.DisplayMetrics;

import com.example.ljh.sleep.R;

public class ScreenUtils {
    private Context context;
    private DisplayMetrics dm;

    public ScreenUtils(Context context){
        this.context = context;
        this.dm = context.getResources().getDisplayMetrics();
    }

    public static ScreenUtils getInstance(Context context){
        return new ScreenUtils(context);
    }

    /**
     * 获取屏幕宽度
     * @return
     */
    public int getScreenWidth(){
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     * @return
     */
    public int getScreenHeight(){
        return dm.heightPixels;
    }

    /**
     * 获取状态栏高度
     * @return
     */
    public int getStatusBarHeight(){
        int id = context.getResources().getIdentifier("status_bar_height","dimen","android");
        if(id > 0){
            return context.getResources().getDimensionPixelSize(id);
        }
        return 0;
    }

    /**
     * 获取主页底部导航栏高度
     * @return
     */
    public int getBottomBarHeight(){
        int height = (int) context.getResources().getDimension(R.dimen.dp_50);
        return height;
    }
}
