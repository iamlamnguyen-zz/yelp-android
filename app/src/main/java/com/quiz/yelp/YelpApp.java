package com.quiz.yelp;

import android.app.Application;

/**
 * Created by lamng on 07/04/2017.
 */

public class YelpApp extends Application {

    private static YelpApp instance;

    public static YelpApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
