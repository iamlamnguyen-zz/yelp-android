package com.quiz.yelp.features.detail;

import android.os.Bundle;

/**
 * Created by lamng on 07/05/17.
 */

interface DetailContract {

    interface View {

        void displayImage(String url);

        void displayNameRestaurant(String name);

        void displayRating(float rating);

        void displayPhoneNumber(String phone);

        void displayReviewsNumber(String reviews);

        void displayCategories(String categories);

    }

    interface ActionListener {

        void onCreated(DetailContract.View view);

        void onGetResultRestaurant(Bundle bundle);
    }
}
