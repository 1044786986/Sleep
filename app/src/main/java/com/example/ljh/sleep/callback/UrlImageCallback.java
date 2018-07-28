package com.example.ljh.sleep.callback;

public interface UrlImageCallback {
    void onSuccess(byte[] byteArray);
    void onFailed(String error);
}
