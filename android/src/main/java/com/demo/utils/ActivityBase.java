package com.demo.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewStub;
import butterknife.ButterKnife;
import com.demo.mike.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Rohit on 06/09/15.
 */
public abstract class ActivityBase extends AppCompatActivity{

    protected Context context;
    protected ActionBar actionBar;
    protected Utilities utilities;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        utilities= Utilities.getInstance();

    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }



    @Override
    public void setContentView(int layoutResID) {

        ViewGroup baseLayout= (ViewGroup) LayoutInflater.from(context).inflate(R.layout.baselayout_overlay, null);
        setContentView(baseLayout);
        Toolbar toolbar= (Toolbar) baseLayout.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar= getSupportActionBar();


        //getActionBar().setHomeAsUpIndicator(R.drawable.ic_hamburger);

        ViewStub stub= (ViewStub) baseLayout.findViewById(R.id.container);
        stub.setLayoutResource(layoutResID);
        stub.inflate();
        ButterKnife.bind(this);

    }


    public void setContentViewNoOverlay(int layoutResID) {

        ViewGroup baseLayout= (ViewGroup) LayoutInflater.from(context).inflate(R.layout.baselayout, null);
        setContentView(baseLayout);
        Toolbar toolbar= (Toolbar) baseLayout.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar= getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        //getActionBar().setHomeAsUpIndicator(R.drawable.ic_hamburger);

        ViewStub stub= (ViewStub) baseLayout.findViewById(R.id.container);
        stub.setLayoutResource(layoutResID);
        stub.inflate();
        ButterKnife.bind(this);

    }


    protected void startActivity(Class clazz){

        Intent intent= new Intent(context, clazz);
        startActivity(intent);

    }


        @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
