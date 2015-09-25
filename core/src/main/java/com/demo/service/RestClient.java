package com.demo.service;

import com.demo.models.Installation;
import com.demo.utils.Job;
import com.demo.models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.GsonConverter;
import retrofit.http.Field;

import java.util.ArrayList;

/**
 * Created by Rohit on 06/09/15.
 */
public class RestClient {


    private Service service;

    //TODO change this
    public static String BASE_URL = "http://192.168.0.192:5000/api";


    private static RestClient ourInstance = new RestClient();

    public static RestClient getInstance()
    {
        return ourInstance;
    }

    private RestClient() {

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(BASE_URL)
                .setConverter(new GsonConverter(gson))
                .build();

        service=restAdapter.create(Service.class);

    }


    public void regDeviceGCM(Installation installation,final Callback<Installation> callback){

        service.regDeviceGCM(installation, new Callback<Installation>() {
            @Override
            public void doneReq(Installation installation, RetrofitError retrofitError) {


                callback.doneReq(installation, retrofitError);


            }
        });

    }



    public void signIn(User user,final Callback callback ){

        service.signIn(user, new Callback<User>() {
            @Override
            public void doneReq(User user, RetrofitError retrofitError) {

                //database operation here
                callback.doneReq(user, retrofitError);
            }
        });

    }



    public void requestJob(Job job,final Callback callback ){

        service.requestJob(job, new Callback<Job>() {
            @Override
            public void doneReq(Job job, RetrofitError retrofitError) {

                //database operation here
                callback.doneReq(job, retrofitError);
            }
        });

    }


    public void updateProfile(String userId,User user,final  Callback callback){
        service.updateProfile(userId, user, new Callback<User>() {
            @Override
            public void doneReq(User user, RetrofitError retrofitError) {

                callback.doneReq(user, retrofitError);
            }
        });
    }



    public void partnerListJob(String partnerId, final Callback callback){

        service.partnerListJob(partnerId, new Callback<ArrayList<Job>>() {
            @Override
            public void doneReq(ArrayList<Job> jobs, RetrofitError retrofitError) {
                callback.doneReq(jobs, retrofitError);
            }
        });

    }

    public void userListJob(String userId, final Callback callback){

        service.userListJob(userId, new Callback<ArrayList<Job>>() {
            @Override
            public void doneReq(ArrayList<Job> jobs, RetrofitError retrofitError) {
                callback.doneReq(jobs, retrofitError);
            }
        });

    }

    public void acceptJob(String jobId,String partnerId,final Callback callback){

        service.acceptJob(jobId,partnerId, new Callback<Job>() {
            @Override
            public void doneReq(Job job, RetrofitError retrofitError) {
                callback.doneReq(job, retrofitError);
            }
        });

    }



}
