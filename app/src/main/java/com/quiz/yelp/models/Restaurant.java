package com.quiz.yelp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by lamng on 07/05/17.
 */

public class Restaurant implements Parcelable {

    private String thumb;
    private String name;
    private String phone;
    private double rating;
    private String review;
    private List<String> titleCategoryList;

    public Restaurant(String thumb, String name, String phone, double rating, String review, List<String> titleCategoryList) {
        this.thumb = thumb;
        this.name = name;
        this.phone = phone;
        this.rating = rating;
        this.review = review;
        this.titleCategoryList = titleCategoryList;
    }

    public String getThumb() {
        return thumb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public double getRating() {
        return rating;
    }

    public String getReview() {
        return review;
    }

    public List<String> getTitleCategoryList() {
        return titleCategoryList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.thumb);
        dest.writeString(this.name);
        dest.writeString(this.phone);
        dest.writeDouble(this.rating);
        dest.writeString(this.review);
        dest.writeStringList(this.titleCategoryList);
    }

    private Restaurant(Parcel in) {
        this.thumb = in.readString();
        this.name = in.readString();
        this.phone = in.readString();
        this.rating = in.readDouble();
        this.review = in.readString();
        this.titleCategoryList = in.createStringArrayList();
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel source) {
            return new Restaurant(source);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };
}
