package com.quiz.yelp.features;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
import com.quiz.yelp.YelpApp;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    private static final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap map;
    private YelpFusionApi yelpFusionApi;

    private List<Marker> markerList = new ArrayList<>();
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        thread.start();
        getListRestaurantsWithLatLong("37.422", "-122.084");
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
                    CustomInfoWindowAdapter customInfoWindowAdapter = new CustomInfoWindowAdapter();

                    SearchResponse result = response.body();
                    for (Business item : result.getBusinesses()) {
                        Log.e(TAG, item.getName() + " ==== " + item.getRating());
                        map.addMarker(new MarkerOptions()
                                .position(new LatLng(item.getCoordinates().getLatitude(),
                                        item.getCoordinates().getLongitude()))
                                .title(item.getName())
                                .snippet(String.valueOf(item.getRating())));
                        map.setInfoWindowAdapter(customInfoWindowAdapter);
                        map.setOnInfoWindowClickListener(customInfoWindowAdapter);
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
        latitude = location.getLatitude();
        longitude = location.getLongitude();
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

    @SuppressWarnings("WeakerAccess")
    public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener {

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
            renderInfoWindow(marker);
            return view;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

        private void renderInfoWindow(final Marker marker) {
            tvName.setText(marker.getTitle());
            tvRatting.setText(marker.getSnippet());
            tvAddress.setText("Address");
        }

        @Override
        public void onInfoWindowClick(Marker marker) {
            Toast.makeText(YelpApp.getInstance(),
                    marker.getTitle() + " === " + marker.getId(), Toast.LENGTH_SHORT).show();
        }
    }
}
