package com.project.beachnav.beachnav;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class Main1Activity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        new Handler().postDelayed(new Runnable(){
    @Override
            public void run(){
        Intent homeIntent = new Intent(Main1Activity.this, MapsActivity.class);
        startActivity(homeIntent);
        finish();
    }
        },SPLASH_TIME_OUT);
    }

    public static class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

        private GoogleMap mMap;
        private LatLngBounds CSULB_Bounds = new LatLngBounds(
                new LatLng(33.77, -118.122), new LatLng(33.79, -118.108));

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_maps);
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

        }
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we just add a marker within CSULB.
         * If Google Play services is not installed on the device, the user will be prompted to install
         * it inside the SupportMapFragment. This method will only be triggered once the user has
         * installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            UiSettings sett = mMap.getUiSettings();
            sett.isMyLocationButtonEnabled();
            sett.setZoomControlsEnabled(true);
            sett.setScrollGesturesEnabled(true);
            mMap.setMinZoomPreference(15.0f);
            mMap.setLatLngBoundsForCameraTarget(CSULB_Bounds);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CSULB_Bounds.getCenter(), 0));

        }


    }
}
