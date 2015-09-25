package com.demo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import com.demo.models.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import timber.log.Timber;

/**
 * Created by Rohit on 07/09/15.
 */
public class GoogleSignUp implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Context context;
    private GoogleApiClient googleApiClient;
    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;
    private int RC_SIGN_IN= 321;


    private Utilities utilities;
    private ISignUp iSignUp;



    public  GoogleSignUp(Context context,ISignUp signUp){
        this.context= context;
        this.iSignUp=signUp;

        utilities=Utilities.getInstance();
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }



    public void signIn(){

        if (!googleApiClient.isConnected()&&!googleApiClient.isConnecting()){
            mShouldResolve = true;
            googleApiClient.connect();
        }
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != Activity.RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            googleApiClient.connect();
        }


    }


    private void resolve(ConnectionResult connectionResult){

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult((Activity)context, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Timber.e("Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    googleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                iSignUp.failed();
                utilities.alertError("Sign Up", connectionResult.toString(), context);
            }
        } else {
            // Show the signed-out UI
            // showSignedOutUI();
            iSignUp.failed();
        }

    }


    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(googleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(googleApiClient);
                User user= new User();

                user.setName(currentPerson.getDisplayName());
                user.setPhotoUrl(currentPerson.getImage().getUrl());
                user.setEmailId(Plus.AccountApi.getAccountName(googleApiClient));

                utilities.setUser(user,context);
                utilities.setUserSignedIn(context,true);

                iSignUp.sucess();

                // Use piccaso here
                //new LoadProfileImage(image).execute(personPhotoUrl);

                // update profile frame with new info about Google Account
                // profile
               // updateProfile(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            iSignUp.failed();

        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        Timber.v("onConnected");
        getProfileInformation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        resolve(connectionResult);

    }


    public interface ISignUp{

        public void sucess();
        public void failed();


    }


}
