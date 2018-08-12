package com.example.ljh.sleep.utils;

import android.util.Log;

import com.example.ljh.sleep.bean.UpdateDurationBean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UpdateDuration {
    public static void updateDuration(final int id,final String duration){
        Log.i("aaa","id = " + id);
        ThreadPoolUtils.threadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                Retrofit retrofit = RetrofitUtils.getRetrofitGson(RetrofitUtils.BASE_URL);
                IRetrofit iRetrofit = retrofit.create(IRetrofit.class);
                Call<UpdateDurationBean> call = iRetrofit.updateDuration(id,duration);
                call.enqueue(new Callback<UpdateDurationBean>() {
                    @Override
                    public void onResponse(Call<UpdateDurationBean> call, Response<UpdateDurationBean> response) {
//                        Log.i("aaa","UpdateDuration.onResponse = " + response);
                    }

                    @Override
                    public void onFailure(Call<UpdateDurationBean> call, Throwable t) {
                        Log.i("aaa","UpdateDuration.onFailure() = " + t);
                    }
                });
            }
        });
    }
}
