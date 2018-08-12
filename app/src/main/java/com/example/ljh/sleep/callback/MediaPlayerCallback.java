package com.example.ljh.sleep.callback;

public interface MediaPlayerCallback {
    void onStart(int id);
    void onComplete();
    void onError();
}
