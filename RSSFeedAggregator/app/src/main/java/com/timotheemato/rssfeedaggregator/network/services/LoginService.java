package com.timotheemato.rssfeedaggregator.network.services;

import com.timotheemato.rssfeedaggregator.network.interfaces.ILoginService;
import com.timotheemato.rssfeedaggregator.network.models.LoginResponse;
import com.timotheemato.rssfeedaggregator.network.models.SimpleResponse;

import retrofit2.Retrofit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by tmato on 1/21/17.
 */

public class LoginService {

    private ILoginService loginService;
    private boolean isRequestingInformation = false;

    public LoginService(Retrofit retrofit) {
        this.loginService = retrofit.create(ILoginService.class);
    }

    public Observable<SimpleResponse> register(String email, String password) {
        return loginService.register(email, password)
                .doOnSubscribe(() -> isRequestingInformation = true)
                .doOnTerminate(() -> isRequestingInformation = false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SimpleResponse> unregister(String authorization) {
        return loginService.unregister(authorization)
                .doOnSubscribe(() -> isRequestingInformation = true)
                .doOnTerminate(() -> isRequestingInformation = false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .ignoreElements();
    }

    public Observable<LoginResponse> login(String email, String password) {
        return loginService.login(email, password)
                .doOnSubscribe(() -> isRequestingInformation = true)
                .doOnTerminate(() -> isRequestingInformation = false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public boolean isRequestingInformation() {
        return isRequestingInformation;
    }
}
