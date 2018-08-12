package com.example.ljh.sleep.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ljh.sleep.bean.MusicInfoBean;

public class SharedPreferencesUtils {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    public static final String SHARED_NAME = "cur_music";
    public static final String KEY_NAME = "name";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_TYPE = "type";
    public static final String KEY_URL = "url";
    public static final String KEY_PROGRESS = "progress";
    public static final String KEY_ID = "id";
    public static final String KEY_LOCAL = "local";
    public static final String KEY_POSITION = "position";
    public static final String KEY_DURATION = "duration";

    public static final String PLAY_MODE = "play_mode";                     //播放模式

    public static void setMusicInfo(Context context, MusicInfoBean bean){
        sharedPreferences = context.getSharedPreferences(SHARED_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
//        if(bean != null){
            editor.putString(KEY_NAME,bean.getName());
            editor.putString(KEY_AUTHOR,bean.getAuthor());
            editor.putString(KEY_TYPE,bean.getType());
            editor.putString(KEY_URL,bean.getUrl());
            editor.putString(KEY_DURATION,bean.getDuration());
            editor.putInt(KEY_PROGRESS,bean.getProgress());
            editor.putInt(KEY_ID,bean.getId());
            editor.putInt(KEY_POSITION,bean.getPosition());
            editor.putBoolean(KEY_LOCAL,bean.isLocal());
//        }else{
//            editor.putString(KEY_NAME,"");
//            editor.putString(KEY_AUTHOR,"");
//            editor.putString(KEY_TYPE,"");
//            editor.putString(KEY_URL,"");
//            editor.putString(KEY_DURATION,"");
//            editor.putInt(KEY_PROGRESS,0);
//            editor.putInt(KEY_ID,0);
//            editor.putInt(KEY_POSITION,0);
//            editor.putBoolean(KEY_LOCAL,false);
//        }
        editor.commit();
        editor.clear();
    }

    public static MusicInfoBean getMusicInfo(Context context){
        sharedPreferences = context.getSharedPreferences(SHARED_NAME,Context.MODE_PRIVATE);
        MusicInfoBean bean = new MusicInfoBean();
        bean.setName(sharedPreferences.getString(KEY_NAME,""));
        //判断是否有记录
        if(bean.getName() == "" || bean.getName() == null){
            return null;
        }
        bean.setAuthor(sharedPreferences.getString(KEY_AUTHOR,""));
        bean.setUrl(sharedPreferences.getString(KEY_URL,""));
        bean.setType(sharedPreferences.getString(KEY_TYPE,""));
        bean.setProgress(sharedPreferences.getInt(KEY_PROGRESS,0));
        bean.setId(sharedPreferences.getInt(KEY_ID,0));
        bean.setDuration(sharedPreferences.getString(KEY_DURATION,""));
        bean.setPosition(sharedPreferences.getInt(KEY_POSITION,0));
        bean.setLocal(sharedPreferences.getBoolean(KEY_LOCAL,false));
        return bean;
    }

    public static int getPlayMode(Context context){
        sharedPreferences = context.getSharedPreferences(SHARED_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getInt(PLAY_MODE,0);
    }
}
