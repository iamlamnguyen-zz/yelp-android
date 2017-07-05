package com.quiz.yelp.features;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.quiz.yelp.R;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.InfoWindowAdapter,
        GoogleMap.OnInfoWindowClickListener, GoogleMap.OnCameraChangeListener {
    private static final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap map;
    private YelpFusionApi yelpFusionApi;

    private Business business;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        thread.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Getting LocationManager object from System Service LOCATION_SERVICE
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map = googleMap;
        map.setMyLocationEnabled(true);
        map.setInfoWindowAdapter(this);
        map.setOnInfoWindowClickListener(this);
        map.setOnCameraChangeListener(this);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
    }

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
                if (response.isSuccessful()) {

                    Marker marker;
                    for (Business item : response.body().getBusinesses()) {
                        marker = map.addMarker(new MarkerOptions()
                                .position(new LatLng(item.getCoordinates().getLatitude(),
                                        item.getCoordinates().getLongitude()))
                                .title(item.getName())
                                .snippet(String.valueOf(item.getLocation().getAddress2())));

                        marker.setTag(item);
                    }
                } else {
                    // error response, no access to resource?
                    Log.e(TAG, response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                // HTTP error happened, do something to handle it.
                Log.e("Error", t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);
        // Showing the current location in Google Map
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        // Zoom in the Google Map
        map.animateCamera(CameraUpdateFactory.zoomTo(10));
        map.getUiSettings().setZoomControlsEnabled(false);

        getListRestaurantsWithLatLong(String.valueOf(latitude), String.valueOf(longitude));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.e(TAG, "onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.e(TAG, "onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.e(TAG, "onProviderDisabled");
    }

    // InfoWindow

    @BindView(R.id.tvName)
    TextView tvName;

    @BindView(R.id.ratingBar)
    RatingBar ratingBar;

    @BindView(R.id.tvAddress)
    TextView tvAddress;

    @SuppressLint("InflateParams")
    @Override
    public View getInfoWindow(Marker marker) {
        View view = getLayoutInflater().inflate(R.layout.custom_info_window, null);
        ButterKnife.bind(this, view);

        renderInfoWindow(marker);
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private void renderInfoWindow(final Marker marker) {
        if (marker == null) {
            return;
        }
        business = (Business) marker.getTag();
        if (business == null) {
            return;
        }
        com.yelp.fusion.client.models.Location location = business.getLocation();
        String address = location.getAddress1()
                + ", " + location.getAddress2()
                + ", " + location.getAddress3()
                + ", " + location.getCity() + ", " + location.getCountry();

        tvName.setText(business.getName());
        ratingBar.setRating((float) business.getRating());
        tvAddress.setText(address);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        business = (Business) marker.getTag();
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

        DetailActivity.toActivity(this, restaurant);
    }

    private List<String> getTitleCategoryList(ArrayList<Category> categoryArrayList) {
        List<String> titleCategoryList = new ArrayList<>();
        for (int i = 0; i < categoryArrayList.size(); i++) {
            titleCategoryList.add(categoryArrayList.get(i).getTitle());
        }
        return titleCategoryList;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        String latitude = String.valueOf(cameraPosition.target.latitude);
        String longitude = String.valueOf(cameraPosition.target.longitude);

        getListRestaurantsWithLatLong(latitude, longitude);
    }
}
