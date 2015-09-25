package com.demo.service;

import com.demo.models.Installation;
import com.demo.utils.Job;
import com.demo.models.User;
import retrofit.http.*;

import java.util.ArrayList;

/**
 * Created by Rohit on 06/09/15.
 */
public interface Service {


    @POST("/installations")
    public void regDeviceGCM(@Body Installation installation, Callback<Installation> callback);


    @POST("/serviceusers/signIn")
    public void signIn(@Body User user,Callback<User> cb);

    @POST("/jobs/request")
    public void requestJob(@Body Job job,Callback<Job> cb);


    @PUT("/serviceusers/{id}")
    public void updateProfile(@Path("id") String userId,@Body User user,Callback<User> cb);

    @FormUrlEncoded
    @POST("/jobs/list")
    public void partnerListJob(@Field("partnerId") String partnerId,Callback<ArrayList<Job>> cb);

    @FormUrlEncoded
    @POST("/jobs/users/list")
    public void userListJob(@Field("userId") String userId,Callback<ArrayList<Job>> cb);


    @FormUrlEncoded
    @POST("/jobs/accept")
    public void acceptJob(@Field("jobId") String jobId,@Field("partnerId") String partnerId,Callback<Job> cb);



}
