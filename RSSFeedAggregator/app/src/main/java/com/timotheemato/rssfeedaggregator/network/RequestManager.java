package com.timotheemato.rssfeedaggregator.network;

import android.content.Context;

import com.timotheemato.rssfeedaggregator.BuildConfig;
import com.timotheemato.rssfeedaggregator.network.models.ErrorResponse;
import com.timotheemato.rssfeedaggregator.network.models.LoginResponse;
import com.timotheemato.rssfeedaggregator.network.models.SimpleResponse;
import com.timotheemato.rssfeedaggregator.network.services.FeedService;
import com.timotheemato.rssfeedaggregator.network.services.LoginService;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by tmato on 1/21/17.
 */

public class RequestManager {
    private static RequestManager instance;

    private LoginService loginService;
    private FeedService feedService;

    private Retrofit retrofit;
    private Converter<ResponseBody, ErrorResponse> errorConverter;

    private RequestManager(Context context) {
        retrofit = getAdapter();
        errorConverter =
                retrofit.responseBodyConverter(ErrorResponse.class, new Annotation[0]);

        this.loginService = new LoginService(retrofit);
        this.feedService = new FeedService(retrofit);
    }

    public static RequestManager getInstance(Context context) {
        synchronized (RequestManager.class) {
            if (instance == null) {
                instance = new RequestManager(context);
            }
            return instance;
        }
    }

    public boolean isRequestingInformation() {
        return loginService.isRequestingInformation() ||
                feedService.isRequestingInformation();
    }

    private Retrofit getAdapter() {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();

        return new Retrofit.Builder()
                .baseUrl(BuildConfig.URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    public Observable<SimpleResponse> register(String email, String password) {
        return loginService.register(email, password);
    }

    public Observable<SimpleResponse> unregister(String authorization) {
        return loginService.unregister(authorization);
    }

    public Observable<LoginResponse> login(String email, String password) {
        return loginService.login(email, password);
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public void setRetrofit(Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    public ErrorResponse getError(Response response) {
        try {
            return errorConverter.convert(response.errorBody());
        } catch (IOException e) {
            return null;
        }
    }
}