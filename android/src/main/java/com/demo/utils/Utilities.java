package com.demo.utils;

import android.app.Activity;
import android.content.Context;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.demo.mike.R;
import com.demo.models.StrongLoopError;
import com.demo.models.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;
import retrofit.RetrofitError;
import timber.log.Timber;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;

/**
 * Created by Rohit on 06/09/15.
 */
public class Utilities {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 10325;

    private static SweetAlertDialog progressDialog;


    private static Utilities ourInstance = new Utilities();

    public static Utilities getInstance() {
        return ourInstance;
    }

    private Utilities() {
    }


    public boolean checkPlayServices(Context context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {

                GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity) context, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            Timber.v("checkPlayServices: " + false);

            return false;
        }

        Timber.v("checkPlayServices: " + true);
        return true;
    }

    public void showProgress(String title,String message, Context context) {

        if (progressDialog != null) {
            progressDialog.dismiss();

        }
        progressDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.setCancelable(false);
        progressDialog.getProgressHelper().setBarColor(context.getResources().getColor(R.color.primary));
        progressDialog.setContentText(message);
        progressDialog.setTitleText(title);
        progressDialog.show();

    }


    public void dismissProgress() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();

        }

    }


    public void alertNormal(String title, String message, Context context) {

        new SweetAlertDialog(context)
                .setTitleText(title)
                .setContentText(message)
                .show();


    }


    public void alertError(String title, String message, Context context) {

        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setContentText(message)
                .show();


    }


    public void alertWarning(String title, String message, Context context) {

        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(title)
                .setContentText(message)
                .show();


    }


    public void alertSuccess(String title, String message, Context context) {

        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(title)
                .setContentText(message)
                .show();


    }

    public void alertSuccess(String title, String message, Context context, SweetAlertDialog.OnSweetClickListener listener) {


        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(title)
                .setContentText(message)
                .setConfirmClickListener(listener)
                .show();


    }


    public void alertTwo(String title, String content, String message, Context context) {

        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(title)
                .setContentText(content)
                .setCancelText("No")
                .setConfirmText("Confirm")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                })
                .show();


    }


    public boolean isUserSignedIn(Context context) {

        TinyDB tinyDB = new TinyDB(context);
        return tinyDB.getBoolean("IS_SIGNED_IN", false);

    }

    public void setUserSignedIn(Context context, boolean isSignedIn) {
        TinyDB tinyDB = new TinyDB(context);
        tinyDB.putBoolean("IS_SIGNED_IN", isSignedIn);
    }


    public void setUser(User user,Context context){
        TinyDB tinyDB = new TinyDB(context);
        tinyDB.putString("USER", new Gson().toJson(user));


    }

    public User getUser(Context context){
        TinyDB tinyDB = new TinyDB(context);
        String usrString = tinyDB.getString("USER");
        if (usrString.isEmpty()){
            return new User();
        }
        return new Gson().fromJson(usrString,User.class);
    }

    public static String showStrongLoopError( RetrofitError error) {

        String errorMessage = "";

        if (error.getKind().toString().equalsIgnoreCase("NETWORK")) {
            if (error.getCause() instanceof SocketTimeoutException) {
                errorMessage = "Connection timeout.";
            } else {
                errorMessage = "Please check internet connection.";
            }

        } else {

            Gson gson = new Gson();

            //Try to get response body
            BufferedReader reader = null;
            StringBuilder sb = new StringBuilder();

            try {

                reader = new BufferedReader(new InputStreamReader(error.getResponse().getBody().in()));
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            String result = sb.toString();

            StrongLoopError strongLoopError = gson.fromJson(result, StrongLoopError.class);
            errorMessage = strongLoopError.getError().getMessage();

        }
        return errorMessage;

    }


}
