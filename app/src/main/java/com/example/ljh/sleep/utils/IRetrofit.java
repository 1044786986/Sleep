package com.example.ljh.sleep.utils;

import com.example.ljh.sleep.bean.LauncherBean;
import com.example.ljh.sleep.bean.LoginBean;
import com.example.ljh.sleep.bean.RegisterResponseBean;
import com.example.ljh.sleep.bean.ShowStoryResponseBean;
import com.example.ljh.sleep.bean.UpdateDurationBean;

import java.io.InputStream;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Retrofit服务
 */
public interface IRetrofit {
    @FormUrlEncoded
    @POST("launcher")
    Observable<LauncherBean> getAd(@Field("type") String type);

    @FormUrlEncoded
    @POST("login")
    Observable<LoginBean.LoginResponse> login(@Field("username") String username,
                                              @Field("password") String password);

    @FormUrlEncoded
    @POST("register")
    Observable<RegisterResponseBean> register(@Field("username") String username,
                                              @Field("password") String password,
                                              @Field("email") String email);

    @FormUrlEncoded
    @POST("getStory")
    Observable<ShowStoryResponseBean> getShowStoryData(@Field("type") String type);

    @FormUrlEncoded
    @POST("searchStory")
    Observable<ShowStoryResponseBean> searchShowStoryData(@Field("type") String type,
                                                          @Field("keyWord") String keyWord);

    @FormUrlEncoded
    @POST("loadStory")
    Observable<ShowStoryResponseBean> loadShowStoryData(@Field("type") String type,
                                                        @Field("endId") int endId);

    @FormUrlEncoded
    @POST("updateDuration")
    Call<UpdateDurationBean> updateDuration(@Field("id") int id, @Field("duration") String duration);

    @GET("")
    Observable<InputStream> downLoad();
}
