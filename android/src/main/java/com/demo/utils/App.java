package com.demo.utils;

import android.app.Application;
import com.demo.mike.BuildConfig;
import com.demo.mike.R;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Rohit on 06/09/15.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/avenir.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );


        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }



    }

    private static class CrashReportingTree extends Timber.Tree{

        @Override
        protected void log(int priority, String tag, String message, Throwable t) {

        }
    }


}
