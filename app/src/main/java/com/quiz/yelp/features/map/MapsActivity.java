package com.quiz.yelp.features.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.quiz.yelp.R;
import com.quiz.yelp.features.detail.DetailActivity;
import com.quiz.yelp.models.Restaurant;
import com.yelp.fusion.client.models.Business;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressWarnings("deprecation")
public class MapsActivity extends FragmentActivity implements MapContract.View, OnMapReadyCallback, LocationListener, GoogleMap.InfoWindowAdapter,
        GoogleMap.OnInfoWindowClickListener, GoogleMap.OnCameraChangeListener {
    private static final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap map;
    private MapContract.ActionListener mapPresenter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapPresenter = new MapPresenter(this);
        mapPresenter.onCreated(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map = googleMap;
        map.setMyLocationEnabled(true);
        map.setInfoWindowAdapter(this);
        map.setOnCameraChangeListener(this);
        map.setOnInfoWindowClickListener(this);

        mapPresenter.onMapReady();
    }

    @Override
    public void onLocationChanged(Location location) {
        mapPresenter.onLocationChanged(location);
    }

    @Override
    public void updateLocation(LatLng latLng) {
        // Showing the current location in Google Map
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        // Zoom in the Google Map
        map.animateCamera(CameraUpdateFactory.zoomTo(10));
        map.getUiSettings().setZoomControlsEnabled(false);
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

        mapPresenter.renderInfoWindow(marker);
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        mapPresenter.onInfoWindowClick(marker.getTag());
    }

    @Override
    public void startDetailRestaurant(Restaurant restaurant) {
        DetailActivity.toActivity(this, restaurant);
    }

    @Override
    public void onResponseSuccessful(ArrayList<Business> businesses) {
        Marker marker;
        for (Business item : businesses) {
            marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(item.getCoordinates().getLatitude(),
                            item.getCoordinates().getLongitude()))
                    .title(item.getName())
                    .snippet(String.valueOf(item.getLocation().getAddress2())));

            marker.setTag(item);
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        mapPresenter.onCameraChange(cameraPosition);
    }

    @Override
    public void onFailure(String localizedMessage) {
        Toast.makeText(this, localizedMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponseFailure(String errorResponseMsg) {
        Toast.makeText(this, errorResponseMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayRestaurantName(String name) {
        tvName.setText(name);
    }

    @Override
    public void displayRating(float rating) {
        ratingBar.setRating(rating);
    }

    @Override
    public void displayRestaurantAddress(String address) {
        tvAddress.setText(address);
    }


}
