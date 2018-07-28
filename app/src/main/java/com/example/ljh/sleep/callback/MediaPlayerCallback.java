package com.example.ljh.sleep.callback;

public interface MediaPlayerCallback {
    void onStart(int id,long duration);
    void onComplete();
    void onError();
}
