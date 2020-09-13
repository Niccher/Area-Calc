package com.niccher.areacalc.fragments;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.location.FusedLocationProviderClient;

import com.niccher.areacalc.R;

public class Frag_About extends Fragment implements OnMapReadyCallback {

    MapView mMapView;
    private GoogleMap googleMap;

    final int reqcod = 144;
    FusedLocationProviderClient fcli;
    Location loc;

    public Frag_About() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View solv = inflater.inflate(R.layout.frag_about, container, false);

        mMapView = (MapView) solv.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //googleMap = mMapView.getMapAsync(this::onMapReady);
        mMapView.getMapAsync(this::onMapReady);

        return solv;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng shule = new LatLng(-1.0529029, 37.0947193);

        /*googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        googleMap.getUiSettings().setTiltGesturesEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);

        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(shule, 15));
        googleMap.addMarker(new MarkerOptions().position(shule).title("Gretsa University"));*/

        Toast.makeText(getActivity(), "onMapReady", Toast.LENGTH_SHORT).show();
        LatLng latlo=new LatLng(loc.getLatitude(),loc.getLongitude());
        MarkerOptions marka=new MarkerOptions().position(latlo).title("Real Nigga");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latlo));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlo,10));
        googleMap.addMarker(marka);
    }
}