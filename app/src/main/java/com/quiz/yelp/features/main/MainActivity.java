package com.quiz.yelp.features.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.quiz.yelp.R;
import com.quiz.yelp.features.map.MapsActivity;

public class MainActivity extends AppCompatActivity implements MainContract.View {
    private static final String TAG = MainActivity.class.getSimpleName();

    private MainContract.ActionListener mainPresenter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainPresenter = new MainPresenter(this);
        mainPresenter.onCreated(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mainPresenter.onRequestPermissionResult(requestCode, permissions);
    }

    @Override
    public void startMap() {
        startActivity(new Intent(this, MapsActivity.class));
        finish();
    }
}
