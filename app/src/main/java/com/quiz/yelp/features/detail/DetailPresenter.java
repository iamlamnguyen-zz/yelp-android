package com.quiz.yelp.features.detail;

import android.os.Bundle;
import android.text.TextUtils;

import com.quiz.yelp.models.Restaurant;
import com.quiz.yelp.utils.Constant;

import java.util.List;

/**
 * Created by lamng on 07/05/17.
 */

class DetailPresenter implements DetailContract.ActionListener {

    private DetailContract.View detailView;

    DetailPresenter() {
    }

    public void onCreated(DetailContract.View view) {
        detailView = view;
    }

    @Override
    public void onGetResultRestaurant(Bundle bundle) {
        Restaurant restaurant = bundle.getParcelable(Constant.EXTRA_DATA);
        if (restaurant == null) {
            return;
        }
        displayImage(restaurant.getThumb());
        displayNameRestaurant(restaurant.getName());
        displayRating((float) restaurant.getRating());
        displayPhoneNumber(restaurant.getPhone());
        displayReviewsNumber(restaurant.getReview());
        displayCategories(restaurant.getTitleCategoryList());
    }

    private void displayImage(String url) {
        detailView.displayImage(url);
    }

    private void displayNameRestaurant(String name) {
        detailView.displayNameRestaurant(name);
    }

    private void displayRating(float rating) {
        detailView.displayRating(rating);
    }

    private void displayPhoneNumber(String phone) {
        detailView.displayPhoneNumber(phone);
    }

    private void displayReviewsNumber(String reviews) {
        detailView.displayReviewsNumber(reviews);
    }

    private void displayCategories(List<String> categoryList) {
        if (categoryList == null || categoryList.size() <= 0) {
            return;
        }
        String joinedCategory = TextUtils.join(" #", categoryList);
        detailView.displayCategories("#" + joinedCategory);
    }
}
