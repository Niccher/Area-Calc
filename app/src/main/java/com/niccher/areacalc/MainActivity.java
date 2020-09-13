package com.niccher.areacalc;

import android.*;
import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    final int reqcod = 144;
    FusedLocationProviderClient fcli;
    Location loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myloc);

        fcli = LocationServices.getFusedLocationProviderClient(this);
        LastPlca();

    }

    private void LastPlca() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},reqcod);
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},reqcod);
            return;
        }
        Task<Location> taskl = fcli.getLastLocation();
        taskl.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location !=null){
                    loc=location;
                    Toast.makeText(MainActivity.this,
                            "My Loc##\n"+"Lat -> "+loc.getLatitude()+"\nLong -> "+loc.getLongitude(),
                            Toast.LENGTH_SHORT).show();
                    SupportMapFragment su= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.myloc);
                    su.getMapAsync(MainActivity.this);
                }
                else {
                    Toast.makeText(MainActivity.this, "location is =null", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "onMapReady", Toast.LENGTH_SHORT).show();
        LatLng latlo=new LatLng(loc.getLatitude(),loc.getLongitude());
        MarkerOptions marka=new MarkerOptions().position(latlo).title("Real Nigga");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latlo));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlo,10));
        googleMap.addMarker(marka);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case reqcod:{
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "onRequestPermissionsResult", Toast.LENGTH_SHORT).show();
                    LastPlca();
                }
                break;
            }
        }
    }
}