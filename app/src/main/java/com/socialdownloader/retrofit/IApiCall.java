package com.socialdownloader.retrofit;

import com.socialdownloader.R;
import com.socialdownloader.models.PostResponse;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IApiCall {
    @GET
    Call<PostResponse> getPostDetails(@Url String url);
}
