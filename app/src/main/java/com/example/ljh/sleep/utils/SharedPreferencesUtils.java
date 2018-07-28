package com.example.ljh.sleep.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ljh.sleep.bean.MusicInfoBean;

public class SharedPreferencesUtils {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static final String SHARED_NAME = "cur_music";
    private static final String KEY_NAME = "name";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_TYPE = "type";
    private static final String KEY_URL = "url";
    private static final String KEY_PROGRESS = "progress";
    private static final String KEY_ID = "id";
    private static final String KEY_LOCAL = "local";
    private static final String KEY_DURATION = "duration";

    public static void setMusicInfo(Context context, MusicInfoBean bean){
        sharedPreferences = context.getSharedPreferences(SHARED_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(KEY_NAME,bean.getName());
        editor.putString(KEY_AUTHOR,bean.getAuthor());
        editor.putString(KEY_TYPE,bean.getType());
        editor.putString(KEY_URL,bean.getUrl());
        editor.putString(KEY_DURATION,bean.getDuration());
        editor.putInt(KEY_PROGRESS,bean.getProgress());
        editor.putInt(KEY_ID,bean.getId());
        editor.putBoolean(KEY_LOCAL,bean.isLocal());
        editor.commit();
        editor.clear();
    }

    public static MusicInfoBean getMusicInfo(Context context){
        sharedPreferences = context.getSharedPreferences(SHARED_NAME,Context.MODE_PRIVATE);
        MusicInfoBean bean = new MusicInfoBean();
        bean.setName(sharedPreferences.getString(KEY_NAME,""));
        bean.setAuthor(sharedPreferences.getString(KEY_AUTHOR,""));
        bean.setUrl(sharedPreferences.getString(KEY_URL,""));
        bean.setType(sharedPreferences.getString(KEY_TYPE,""));
        bean.setProgress(sharedPreferences.getInt(KEY_PROGRESS,0));
        bean.setId(sharedPreferences.getInt(KEY_ID,0));
        bean.setDuration(sharedPreferences.getString(KEY_DURATION,""));
        bean.setLocal(sharedPreferences.getBoolean(KEY_LOCAL,false));
        return bean;
    }
}
