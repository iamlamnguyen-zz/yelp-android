package com.quiz.yelp.features.map;

import android.location.Location;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.quiz.yelp.models.Restaurant;
import com.yelp.fusion.client.models.Business;

import java.util.ArrayList;

/**
 * Created by lamng on 07/05/2017.
 */

interface MapContract {

    interface View {

        void updateLocation(LatLng latLng);

        void onResponseSuccessful(ArrayList<Business> businesses);

        void onFailure(String localizedMessage);

        void onResponseFailure(String errorResponseMsg);

        void displayRestaurantName(String name);

        void displayRating(float rating);

        void displayRestaurantAddress(String address);

        void startDetailRestaurant(Restaurant restaurant);
    }

    interface ActionListener {

        void onCreated(View view);

        void onMapReady();

        void onLocationChanged(Location location);

        void renderInfoWindow(Marker marker);

        void onCameraChange(CameraPosition cameraPosition);

        void onInfoWindowClick(Object tag);
    }
}
