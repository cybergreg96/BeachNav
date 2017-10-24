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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.HashMap;
import java.util.Map;

public class Main1Activity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
        @Override
            public void run() {
                Intent homeIntent = new Intent(Main1Activity.this, MapsActivity.class);
                startActivity(homeIntent);
            finish();
            }
        },SPLASH_TIME_OUT);
    }

    public static class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

        private GoogleMap mMap;
        private LatLngBounds CSULB_Bounds = new LatLngBounds(
                new LatLng(33.765, -118.124241), new LatLng(33.785, -118.108));
        private Map<String, LatLng> mapPlaces = new HashMap<>();

        /**
         * Instantiates the map
         * this method hardcodes all the keys for all the places in CSULB
         */
        protected void instantiateMap(){
            //ECS
            mapPlaces.put("ECS", new LatLng(33.783529, -118.110287) );
            mapPlaces.put("Computer Science", new LatLng(33.783529, -118.110287));
            mapPlaces.put("CECS", new LatLng(33.783529, -118.110287));
            mapPlaces.put("Computer Engineering", new LatLng(33.783529, -118.110287));
            mapPlaces.put("Computer Engineering Computer Science", new LatLng(33.783529, -118.110287));
            //EN2
            mapPlaces.put("EN2", new LatLng(33.783215, -118.110925));
            mapPlaces.put("Engineering 2", new LatLng(33.783215, -118.110925));
            //EN3
            mapPlaces.put("EN3", new LatLng(33.783694, -118.111157));
            mapPlaces.put("Engineering 3", new LatLng(33.783694, -118.111157));
            //EN4
            mapPlaces.put("EN4", new LatLng(33.783681, -118.110674));
            mapPlaces.put("Engineering 4", new LatLng(33.783681, -118.110674));

            //VEC
            mapPlaces.put("VEC", new LatLng(33.782818, -118.110636));
            mapPlaces.put("Vivian Engineering Center", new LatLng(33.782818, -118.110636));
            //Outpost
            mapPlaces.put("Outpost", new LatLng(33.782340, -118.110410));
            mapPlaces.put("The Outpost", new LatLng(33.782340, -118.110410));
            mapPlaces.put("OP", new LatLng(33.782340, -118.110410));
            //SSPA
            mapPlaces.put("SPA", new LatLng(33.782017, -118.110383));
            mapPlaces.put("SSPA", new LatLng(33.782017, -118.110383));
            mapPlaces.put("SS/PA", new LatLng(33.782017, -118.110383));
            mapPlaces.put("School of Social Work", new LatLng(33.782017, -118.110383));
            mapPlaces.put("School of Social Work/Public Affairs", new LatLng(33.782017, -118.110383));
            //Horn Center
            mapPlaces.put("HC", new LatLng(33.783444, -118.113983));
            mapPlaces.put("Horn Center", new LatLng(33.783444, -118.113983));
            mapPlaces.put("Horn", new LatLng(33.783444, -118.113983));
            mapPlaces.put("Computer Lab", new LatLng(33.783444, -118.113983));
            mapPlaces.put("Open Access Computer Lab", new LatLng(33.783444, -118.113983));
            //UAM University Art Museum
            mapPlaces.put("UAM",new LatLng(33.783425, -118.114629));
            mapPlaces.put("University Art Museum",new LatLng(33.783425, -118.114629));
            mapPlaces.put("Museum",new LatLng(33.783425, -118.114629));
            //KIN
            mapPlaces.put("KIN", new LatLng(33.782898, -118.112586));
            mapPlaces.put("Kinesiology", new LatLng(33.782898, -118.112586));
            //SRWC
            mapPlaces.put("SRWC", new LatLng(33.785038, -118.109484));
            mapPlaces.put("Student Recreation and Wellness Center", new LatLng(33.785038, -118.109484));
            mapPlaces.put("Recreation and Wellness Center", new LatLng(33.785038, -118.109484));

            //HHS1 (Health and Human Services)
            mapPlaces.put("HHS1", new LatLng(33.782388, -118.112801));
            mapPlaces.put("Health and Human Services 1", new LatLng(33.782388, -118.112801));
            mapPlaces.put("Health Human Services 1", new LatLng(33.782388, -118.112801));
            mapPlaces.put("Human Services 1", new LatLng(33.782388, -118.112801));
            //HHS2
            mapPlaces.put("HHS2", new LatLng(33.782384, -118.112125));
            mapPlaces.put("Health and Human Services 2", new LatLng(33.782384, -118.112125));
            mapPlaces.put("Health Human Services 2", new LatLng(33.782384, -118.112125));
            mapPlaces.put("Human Services 2", new LatLng(33.782384, -118.112125));
            //Health Human Services (in general)
            mapPlaces.put("Health and Human Services", new LatLng(33.782326, -118.112490));
            mapPlaces.put("HHS",new LatLng(33.782326, -118.112490));
            mapPlaces.put("Human Services",new LatLng(33.782326, -118.112490));
            mapPlaces.put("Health Human Services", new LatLng(33.782326, -118.112490));
            //TODO: add other names
            //BH
            mapPlaces.put("Brotman Hall", new LatLng(33.782659, -118.115339));
            mapPlaces.put("BH", new LatLng(33.782659, -118.115339));
            //UPPER CAMPUS
            //USU
            mapPlaces.put("USU", new LatLng(33.781281, -118.113450));
            //CP Central Plant (Lego)
            mapPlaces.put("CP", new LatLng(33.781316, -118.112386));
            //CAFE
            mapPlaces.put("CAFE", new LatLng(33.780574, -118.114071));
            //BKS Book Store
            mapPlaces.put("BKS", new LatLng(33.779974, -118.114158));
            //MLSC Molecular Science
            mapPlaces.put("MLSC", new LatLng(33.780301, -118.112488));
            //HSCI Hall of Science
            mapPlaces.put("HSCI", new LatLng(33.779830, -118.112639));
            //MIC Microbiology
            mapPlaces.put("MIC", new LatLng(33.779429, -118.111720));
            //PH1
            mapPlaces.put("PH1", new LatLng(33.778898, -118.112505));
            //PH2 Peterson Hall 2
            mapPlaces.put("PH2", new LatLng(33.779272, -118.112482));
            //FA 1
            mapPlaces.put("FA1", new LatLng(33.777211, -118.112557));
            //FA 2
            mapPlaces.put("FA2", new LatLng(33.777474, -118.112361));
            //FA 3
            mapPlaces.put("FA3", new LatLng(33.777935, -118.112311));
            //FA4 fine arts 4
            mapPlaces.put("FA4", new LatLng(33.778336, -118.112741));

            //UT University Theatre?
            mapPlaces.put("UT", new LatLng(33.776733, -118.112113));

            //UTC University Theatre Center?
            mapPlaces.put("UTC", new LatLng(33.776735, -118.111652));

            //TA Theatre Arts Building
            mapPlaces.put("TA", new LatLng(33.776510, -118.112639));

            //MHB Macintosh Building, The Toaster?
            mapPlaces.put("MHB", new LatLng(33.776882, -118.113202));

            //AS ??
            mapPlaces.put("AS", new LatLng(33.777009, -118.114096));

            //LIB Library
            mapPlaces.put("LIB", new LatLng(33.777207, -118.114842));

            //LA1
            mapPlaces.put("LA1", new LatLng(33.777664, -118.114713));

            //LA2
            mapPlaces.put("LA2", new LatLng(33.777987, -118.114547));

            //LA3
            mapPlaces.put("LA3", new LatLng(33.778292, -118.114440));

            //LA4
            mapPlaces.put("LA4", new LatLng(33.778566, -118.114338));

            //LA5
            mapPlaces.put("LA5", new LatLng(33.778898, -118.114241));

            //LH Lecture Hall
            mapPlaces.put("LH", new LatLng(33.778187, -118.113976));

            //CLA ???
            mapPlaces.put("CLA", new LatLng(33.777815, -118.114132));

            //PSY Psychology
            mapPlaces.put("PSY", new LatLng(33.779318, -118.114439));

            //ED2 the outer edge of the campus
            mapPlaces.put("ED2", new LatLng(33.775727, -118.114354));

            //EED
            mapPlaces.put("EED", new LatLng(33.776224, -118.114156));

            //MMC Multimedia Center
            mapPlaces.put("MMC", new LatLng(33.776768, -118.114561));

            //ANNEX (???)
            mapPlaces.put("ANNEX", new LatLng(33.777081, -118.111909));

            //LAB Language Arts Building
            mapPlaces.put("LAB", new LatLng(33.776887, -118.112687));

            //FO2
            mapPlaces.put("FO2", new LatLng(33.778497, -118.113910));

            //FO3
            mapPlaces.put("FO3", new LatLng(33.779128, -118.113688));

            //FO4 Faculty Office 4
            mapPlaces.put("FO4", new LatLng(33.778202, -118.111990));

            //FO5 Faculty Office 5
            mapPlaces.put("FO5", new LatLng(33.779103, -118.112462));

        }
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
            LatLng CSULB = new LatLng(33.7816, -118.1155);
            GroundOverlayOptions CSULB_Map = new GroundOverlayOptions()
                    .image(BitmapDescriptorFactory.fromResource(R.drawable.csulb_map2016))
                    .position(CSULB, 1570f, 1520f);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(CSULB));
            mMap.addGroundOverlay(CSULB_Map);

        }


    }
}
