package com.asisdroid.drawfun;

import android.app.Application;

import com.bumptech.glide.request.target.ViewTarget;

/**
 * Created by ashishkumarpolai on 3/7/2018.
 */

public class DrawFunApplication extends Application {
    public static DrawFunApplication appInstance;

    public static DrawFunApplication getInstance()
    {
        return appInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
    }
}
