package com.quiz.yelp.features;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.quiz.yelp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng sydney = new LatLng(-34, 151);

        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(false);

        // Add a marker in Sydney and move the camera
        map.getUiSettings().setZoomControlsEnabled(false);
        map.addMarker(new MarkerOptions().position(sydney));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        map.setInfoWindowAdapter(new CustomInfoWindowAdapter());
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
            render(marker, view);
            return view;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

        private void render(Marker marker, View view) {
            tvName.setText("Name");
            tvRatting.setText("Ratting");
            tvAddress.setText("Address");
        }
    }
}
