package com.socialdownloader.retrofit;


import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitApiCall {
    private static Retrofit instance;

    public static Retrofit getInstance(){
        return instance==null ?new Retrofit.Builder()
                .baseUrl("https://www.instagram.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build() :instance;
    }
}
