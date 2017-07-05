package com.quiz.yelp.features.main;

/**
 * Created by lamng on 07/05/2017.
 */

interface MainContract {

    interface View {

        void startMap();
    }

    interface ActionListener {

        void onCreated(View view);

        void onRequestPermissionResult(int requestCode, String[] permissions);
    }
}
