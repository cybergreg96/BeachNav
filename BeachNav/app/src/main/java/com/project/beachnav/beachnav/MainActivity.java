package com.project.beachnav.beachnav;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(homeIntent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    public static class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

        private GoogleMap mMap;
        private LatLngBounds CSULB_Bounds = new LatLngBounds(
                new LatLng(33.765, -118.124241), new LatLng(33.785, -118.108));

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_maps);

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.BN_map);
            mapFragment.getMapAsync(this);

        }

        /**
         * Will show current location on the map when 'wya?' button is tapped.
         * (Mapped to button from activity_maps -> click the button -> onClick in expanded Properties)
         * (All this needs now is the permissions for the location)
         */
        public void findLocation(View v) {
//            mMap.setMyLocationEnabled(true);
        }

        /**
         * Will find a location that matches the search item as best as possible.
         * (Mapped to search dialog the same way findLocation was to that button)
         * ..we need to be able to handle anything that the search dialog can give
         *  -> auto-suggestions from a database?
         */
        public void onSearch(View v) {
            EditText location_tf = (EditText) findViewById(R.id.editText);
            String location = location_tf.getText().toString();
            List<Address> addressList = null;

            if (location != null || location.equals("")) {
                Geocoder geocoder = new Geocoder(this);
                try {
                    addressList = geocoder.getFromLocationName(location, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
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
            LatLng CSULB = new LatLng(33.782, -118.116);
            GroundOverlayOptions csulbMap = new GroundOverlayOptions()
                    .image(BitmapDescriptorFactory.fromResource(R.drawable.csulb_map2016))
                    .position(CSULB, 1570f, 1520f);
            mMap.addGroundOverlay(csulbMap);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(CSULB));

//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                 != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
//                 Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                mMap.setMyLocationEnabled(true);
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }

        }


    }
}
