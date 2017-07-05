package com.quiz.yelp.features.main;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import static com.quiz.yelp.utils.Constant.REQUEST_LOCATION;

/**
 * Created by lamng on 07/05/2017.
 */

class MainPresenter implements MainContract.ActionListener {

    private final Activity activity;
    private MainContract.View mainView;

    MainPresenter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onCreated(MainContract.View view) {
        this.mainView = view;
        askForPermission();
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions) {
        if (ActivityCompat.checkSelfPermission(activity, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case 1:
                    mainView.startMap();
                    break;
            }
        }
    }

    private void askForPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            }
        } else {
            // Location is already granted
            mainView.startMap();
        }
    }
}
