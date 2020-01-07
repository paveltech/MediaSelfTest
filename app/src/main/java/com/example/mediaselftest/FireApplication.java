package com.example.mediaselftest;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import timber.log.Timber;

public class FireApplication extends Application {
    public static Context appContext;

    public static Context getInstance() {
        return appContext;
    }

    @Override
    public void onCreate() {
        //FireLog.d(TAG, "(++) onCreate");
        super.onCreate();
        appContext = getApplicationContext();
        MultiDex.install(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
