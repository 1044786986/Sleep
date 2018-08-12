package com.example.ljh.sleep.utils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit管理类
 */
public class RetrofitUtils {
    public static final String IP = "192.168.1.105";
    public static final String BASE_URL = "http://" + IP + "/sleep/";
    private static final int READ_TIME = 5;
    private static final int CONN_TIME = 5;
    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(READ_TIME, TimeUnit.SECONDS)
            .connectTimeout(CONN_TIME,TimeUnit.SECONDS)
            .build();
    private static Retrofit retrofit;

    public static Retrofit getRetrofitRx2Gson(String baseUrl){
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit;
    }

    public static Retrofit getRetrofitRx(String baseUrl){
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit;
    }

    public static Retrofit getRetrofitGson(String baseUrl){
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

}
