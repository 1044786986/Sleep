package com.example.ljh.sleep.bean;

import com.example.ljh.sleep.callback.MediaPlayerCallback;

public class MediaPlayBean {
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public MediaPlayerCallback getListener() {
        return listener;
    }

    public void setListener(MediaPlayerCallback listener) {
        this.listener = listener;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private MediaPlayerCallback listener;
    private int id;
}
