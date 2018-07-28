package com.example.ljh.sleep.bean;

import android.graphics.Bitmap;

public class LauncherAdBean {
    private Bitmap bitmapAd;

    public Bitmap getBitmapAd() {
        return bitmapAd;
    }

    public void setBitmapAd(Bitmap bitmapAd) {
        this.bitmapAd = bitmapAd;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private String path;
}
