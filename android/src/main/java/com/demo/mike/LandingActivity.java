package com.demo.mike;

import android.content.Intent;
import android.os.Bundle;

import android.view.Gravity;
import android.view.View;
import butterknife.OnClick;
import com.demo.push.RegistrationIntentService;
import com.demo.service.RestClient;
import com.demo.utils.ActivityBase;
import com.demo.utils.AndroidCallback;
import com.demo.utils.GoogleSignUp;
import com.demo.models.User;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;


public class LandingActivity extends ActivityBase implements OnClickListener {


    private GoogleSignUp googleSignUp;
    private DialogPlus signInDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        actionBar.hide();


        if (utilities.isUserSignedIn(context)){
            User user = utilities.getUser(context);
            if (user.isPartner()){

                Intent intent = new Intent(context,JobActivity.class);
                startActivity(intent);
                finish();

            }else{
                Intent intent = new Intent(context,CategoryActivity.class);
                intent.putExtra(CategoryActivity.KEY_IS_CONSUMER,true);
                startActivity(intent);
                finish();

            }

            // Do this of registeration missed due to some reason
            if (utilities.checkPlayServices(context)) {
                // Start IntentService to register this application with GCM.
                Intent intent = new Intent(context, RegistrationIntentService.class);
                startService(intent);
            }

        }


        googleSignUp= new GoogleSignUp(context, new GoogleSignUp.ISignUp() {
            @Override
            public void sucess() {
                signInDialog.dismiss();

                User user = utilities.getUser(context);
                user.setIsPartner(true);

                RestClient.getInstance().signIn(user, new AndroidCallback<User>(context) {
                    @Override
                    public void done(User user) {
                        utilities.dismissProgress();
                        if (!user.isPartner()){

                            utilities.alertError("Email already used","This email is already used",context);
                            utilities.setUserSignedIn(context,false);

                            return;
                        }


                        utilities.setUser(user,context);

                        if (utilities.checkPlayServices(context)) {
                            // Start IntentService to register this application with GCM.
                            Intent intent = new Intent(context, RegistrationIntentService.class);
                            startService(intent);
                        }

                        Intent intent = new Intent(context,CategoryActivity.class);
                        intent.putExtra(CategoryActivity.KEY_IS_CONSUMER,false);
                        startActivity(intent);
                        finish();

                    }

                });


            }

            @Override
            public void failed() {
                utilities.dismissProgress();
                signInDialog.dismiss();

            }
        });



    }





    @OnClick(R.id.txtConsumer)
    public void clickConsumer(){
        Intent intent = new Intent(context,CategoryActivity.class);
        intent.putExtra(CategoryActivity.KEY_IS_CONSUMER, true);
        startActivity(intent);
        finish();
    }


    @OnClick(R.id.txtPartner)
    public void clickPartner(){

        signInDialog = DialogPlus.newDialog(context)
                .setContentHolder(new ViewHolder(R.layout.item_signup))
                .setOnClickListener(this)
                .setCancelable(true)
                .setGravity(Gravity.BOTTOM)
                .create();

        signInDialog.show();


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        googleSignUp.onActivityResult(requestCode,resultCode,data);
    }


    @Override
    public void onClick(DialogPlus dialogPlus, View view) {
        if (view.getId()==R.id.sign_in_button){
            utilities.showProgress("Sign In","Please wait",context);
            googleSignUp.signIn();
        }
    }
}
