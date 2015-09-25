package com.demo.utils;

import android.content.Context;
import com.demo.service.Callback;
import retrofit.RetrofitError;

/**
 * Created by Rohit on 06/09/15.
 */
public abstract class AndroidCallback<T> extends Callback<T> {

    // Android specific callback this will have context


    private Context context;

    public AndroidCallback(Context context){
        this.context=context;

    }

    @Override
    public void doneReq(T t, RetrofitError retrofitError) {

        if (retrofitError!=null){
            Utilities.getInstance().dismissProgress();
            if (retrofitError.getKind()==RetrofitError.Kind.NETWORK){

                Utilities.getInstance().alertError("Network", "Some issue with the network, please try again later", context);
                return;
            }

            Utilities.getInstance().alertError(
                    retrofitError.getResponse().getStatus()+"",
                    Utilities.showStrongLoopError(retrofitError),context);

            return;

        }
        done(t);


    }


    public abstract void done(T t);
}
