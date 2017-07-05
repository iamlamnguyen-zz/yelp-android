package com.quiz.yelp.features.map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.quiz.yelp.models.Restaurant;
import com.quiz.yelp.utils.Constant;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.Category;
import com.yelp.fusion.client.models.SearchResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lamng on 07/05/2017.
 */

class MapPresenter implements MapContract.ActionListener {
    private static final String TAG = MapPresenter.class.getSimpleName();

    private Business business;
    private Activity activity;
    private MapContract.View mapView;
    private YelpFusionApi yelpFusionApi;

    MapPresenter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onCreated(MapContract.View view) {
        this.mapView = view;
        thread.start();
    }

    @Override
    public void onMapReady() {
        // Getting LocationManager object from System Service LOCATION_SERVICE
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        onLocationChanged(location);
    }

    private Thread thread = new Thread(new Runnable() {
        public void run() {
            try {
                YelpFusionApiFactory yelpFusionApiFactory = new YelpFusionApiFactory();
                yelpFusionApi = yelpFusionApiFactory.createAPI(Constant.CLIENT_ID, Constant.CLIENT_SECRET);
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    });

    private void getListRestaurantsWithLatLong(String lat, String longitude) {
        Map<String, String> params = new HashMap<>();
        params.put("term", "restaurants");
        params.put("latitude", lat);
        params.put("longitude", longitude);

        if (yelpFusionApi == null) {
            return;
        }
        Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);
        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                if (response == null) {
                    return;
                }
                if (response.isSuccessful()) {
                    mapView.onResponseSuccessful(response.body().getBusinesses());
                } else {
                    // error response, no access to resource?
                    if (response.errorBody() == null || response.errorBody().toString() == null) {
                        return;
                    }
                    mapView.onResponseFailure(response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                // HTTP error happened, do something to handle it.
                if (t == null || t.getLocalizedMessage() == null) {
                    return;
                }
                mapView.onFailure(t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        getListRestaurantsWithLatLong(String.valueOf(latitude), String.valueOf(longitude));

        mapView.updateLocation(latLng);
    }

    @Override
    public void renderInfoWindow(Marker marker) {
        if (marker == null) {
            return;
        }
        business = (Business) marker.getTag();
        if (business == null) {
            return;
        }
        displayRestaurantName(business.getName());
        displayRating((float) business.getRating());
        displayRestaurantAddress(business.getLocation());
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        String latitude = String.valueOf(cameraPosition.target.latitude);
        String longitude = String.valueOf(cameraPosition.target.longitude);

        getListRestaurantsWithLatLong(latitude, longitude);
    }

    @Override
    public void onInfoWindowClick(Object tag) {
        business = (Business) tag;
        if (business == null) {
            return;
        }
        String thumb = business.getImageUrl();
        String name = business.getName();
        double rating = business.getRating();
        String phone = business.getDisplayPhone();
        String review = String.valueOf(business.getReviewCount());

        List<String> titleCategoryList = getTitleCategoryList(business.getCategories());
        Restaurant restaurant = new Restaurant(thumb, name, phone, rating, review, titleCategoryList);

        mapView.startDetailRestaurant(restaurant);
    }

    private List<String> getTitleCategoryList(ArrayList<Category> categoryArrayList) {
        List<String> titleCategoryList = new ArrayList<>();
        for (int i = 0; i < categoryArrayList.size(); i++) {
            titleCategoryList.add(categoryArrayList.get(i).getTitle());
        }
        return titleCategoryList;
    }

    private void displayRestaurantName(String name) {
        mapView.displayRestaurantName(name);
    }

    private void displayRating(float rating) {
        mapView.displayRating(rating);
    }

    private void displayRestaurantAddress(com.yelp.fusion.client.models.Location location) {
        String address1 = TextUtils.isEmpty(location.getAddress1()) ? "" : location.getAddress1();
        String address2 = TextUtils.isEmpty(location.getAddress2()) ? "" : ", " + location.getAddress2();
        String address3 = TextUtils.isEmpty(location.getAddress3()) ? "" : ", " + location.getAddress3();
        String city = TextUtils.isEmpty(location.getCity()) ? "" : ", " + location.getCity();
        String country = TextUtils.isEmpty(location.getCountry()) ? "" : ", " + location.getCountry();
        String address = address1 + address2 + address3 + city + country;

        mapView.displayRestaurantAddress(address);
    }
}
