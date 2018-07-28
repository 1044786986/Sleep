package com.example.ljh.sleep.callback;

public interface MyRetrofitCallback<T>{
    void onSuccess(T t);
    void onFailed(String error);
}
