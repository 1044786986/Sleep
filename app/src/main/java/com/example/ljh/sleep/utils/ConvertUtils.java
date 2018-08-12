package com.example.ljh.sleep.utils;

import android.content.Context;

public class ConvertUtils {

    /**
     * dp值转换成px值
     *
     * @param dpValue dp值
     * @return px值
     */
    public static int dp2px(Context context, final float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px值转换成dp值
     *
     * @param pxValue px值
     * @return dp值
     */
    public static int px2dp(Context context, final float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * sp值转换成px值
     *
     * @param spValue sp值
     * @return px值
     */
    public static int sp2px(Context context, final float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * px值转换成sp值
     *
     * @param pxValue px值
     * @return sp值
     */
    public static int px2sp(Context context, final float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static String duration2min(long duration){
        StringBuilder time = new StringBuilder();
        long second = duration / 1000;
        long minute = 0;
        long hour = 0;
        if(second >= 60){
            minute = second / 60;
            second = second % 60;
        }

        if(minute >= 60){
            hour = minute / 60;
            minute = minute % 60;
        }

        if(hour < 10 && hour > 0){
            time.append("0");
            time.append(hour);
            time.append(":");
        }else if(hour >=10){
            time.append(hour);
            time.append(":");
        }

        if(minute < 10){
            time.append("0");
        }
        time.append(minute);
        time.append(":");

        if(second < 10){
            time.append("0");
        }
        time.append(second);
        return time+"";
    }

    public static String duration2min(int duration){
        StringBuilder time = new StringBuilder();
        int second = duration / 1000;
        int minute = 0;
        int hour = 0;
        if(second >= 60){
            minute = second / 60;
            second = second % 60;
        }

        if(minute >= 60){
            hour = minute / 60;
            minute = minute % 60;
        }

        if(hour < 10 && hour > 0){
            time.append("0");
            time.append(hour);
            time.append(":");
        }else if(hour >=10){
            time.append(hour);
            time.append(":");
        }

        if(minute < 10){
            time.append("0");
        }
        time.append(minute);
        time.append(":");

        if(second < 10){
            time.append("0");
        }
        time.append(second);
        return time+"";
    }

    public static long min2duration(String time){
        long duration = 0;
        if(time.length() == 5){
            duration = Integer.parseInt(time.charAt(4)+"")+Integer.parseInt(time.charAt(3)+"")*10
            +Integer.parseInt(time.charAt(1)+"")*60+Integer.parseInt(time.charAt(0)+"")*600;
        }else if(time.length() == 8){
            duration = Integer.parseInt(time.charAt(7)+"")+Integer.parseInt(time.charAt(6)+"")*10
                    +Integer.parseInt(time.charAt(4)+"")*60+Integer.parseInt(time.charAt(3)+"")*600
                    +Integer.parseInt(time.charAt(1)+"")*3600+Integer.parseInt(time.charAt(0)+"")*36000;
        }
        return duration*1000;
    }
}
