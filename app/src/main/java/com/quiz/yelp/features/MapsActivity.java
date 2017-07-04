package com.quiz.yelp.features;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.quiz.yelp.R;
import com.quiz.yelp.services.GPSTracker;
import com.quiz.yelp.utils.Constant;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap map;
    private Thread thread;
    private YelpFusionApiFactory apiFactory;
    private YelpFusionApi yelpFusionApi;

    private List<Marker> markerList = new ArrayList<Marker>();
    private double latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initAPIFactory();
        getListRestaurantsWithLatLong("37.774929", "-122.419416");
    }

    private void initAPIFactory() {
        new Thread(new Runnable(){
            public void run() {
                apiFactory = new YelpFusionApiFactory();
                try {
                    yelpFusionApi = apiFactory.createAPI(Constant.CLIENT_ID, Constant.CLIENT_SECRET);
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
        }).start();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GPSTracker gps = new GPSTracker(this);

        // Check if GPS enabled
        if(gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            // Can't get location.
        }

        LatLng sydney = new LatLng(latitude, longitude);

        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(false);

        // Add a marker in Sydney and move the camera
        map.getUiSettings().setZoomControlsEnabled(false);
        map.addMarker(new MarkerOptions().position(sydney));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        map.setInfoWindowAdapter(new CustomInfoWindowAdapter());
    }

    private void getListRestaurantsWithLatLong(String lat, String longitude) {
        Map<String, String> params = new HashMap<>();
        params.put("term", "restaurants");
        params.put("latitude", lat );
        params.put("longitude", longitude);

        Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);
        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                if (response.isSuccessful()) {
                    // tasks available
                    SearchResponse result = response.body();
                    for (Business item : result.getBusinesses()) {
                        Log.e(TAG, item.getName() + " ==== " + item.getRating());
                    }
                } else {
                    // error response, no access to resource?
                    Log.e(TAG, response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                Log.e("Error", t.getLocalizedMessage());
            }
        });
    }

    @SuppressWarnings("WeakerAccess")
    public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View view;

        @BindView(R.id.tvName)
        TextView tvName;

        @BindView(R.id.tvRatting)
        TextView tvRatting;

        @BindView(R.id.tvAddress)
        TextView tvAddress;

        CustomInfoWindowAdapter() {
            view = getLayoutInflater().inflate(R.layout.custom_info_window, null);
            ButterKnife.bind(this, view);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            renderInfoWindow(marker, view);
            return view;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

        private void renderInfoWindow(Marker marker, View view) {
            tvName.setText("Name");
            tvRatting.setText("Ratting");
            tvAddress.setText("Address");
        }
    }
}
