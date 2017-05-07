package com.example.javo.loginstest;

import android.app.Application;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by javo on 5/2/17.
 */

public class TodayApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }
}
