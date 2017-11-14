package com.project.beachnav.beachnav.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.project.beachnav.beachnav.R;
import com.project.beachnav.beachnav.other.Node;

/**
 * Created by Austin on 11/12/2017.
 */

public class BNMapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private LatLngBounds CSULB_Bounds =
            new LatLngBounds(new LatLng(33.765, -118.124241), new LatLng(33.785, -118.108));

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupMapIfNeeded();
    }

    private void setupMapIfNeeded() {
        if (mMap == null) {
            SupportMapFragment mapFragment =
                    (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.BN_mapF);
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings sett = mMap.getUiSettings();
        sett.setMyLocationButtonEnabled(true);
        sett.setZoomControlsEnabled(false);
        sett.setScrollGesturesEnabled(true);
        mMap.setMinZoomPreference(15.0f);
        mMap.setLatLngBoundsForCameraTarget(CSULB_Bounds);
        LatLng CSULB = new LatLng(33.782, -118.116);
        GroundOverlayOptions csulbMap = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.csulb_map2016))
                .position(CSULB, 1570f, 1520f);
        mMap.addGroundOverlay(csulbMap);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(CSULB));
    }

    private Marker m;
    public void dropLocationMarker(Node location) {
        if (m != null) {m.remove();}
        LatLng latLng = new LatLng(location.getX(),location.getY());
        m = mMap.addMarker(new MarkerOptions().position(latLng).title(location.getLabel()));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
