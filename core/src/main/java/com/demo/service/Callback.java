package com.demo.service;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Rohit on 06/09/15.
 */
public abstract class Callback<T> implements retrofit.Callback<T>{


    // Do this to have error call back in saame class

    @Override
    public void success(T t, Response response) {
        doneReq(t,null);

    }

    @Override
    public void failure(RetrofitError retrofitError) {
        doneReq(null,retrofitError);


    }

    public abstract void doneReq(T t,RetrofitError retrofitError);


}

