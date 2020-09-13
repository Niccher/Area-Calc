package com.niccher.areacalc;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class Mapped extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    String locFINE = Manifest.permission.ACCESS_FINE_LOCATION;
    String locCOS = Manifest.permission.ACCESS_COARSE_LOCATION;

    Boolean permAssign = false;

    final int reqcod = 145;
    GoogleMap gMaps;

    float zoomdef = 10f;

    FusedLocationProviderClient floc;

    ImageButton imgbtn,imginfo_clear,imginfo_distance;

    int count = 0;
    LatLng tapped,tapped1;
    boolean twos = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapped);

        imgbtn=(ImageButton) findViewById(R.id.myloc);
        imginfo_clear=(ImageButton) findViewById(R.id.locinfo_clear);
        imginfo_distance=(ImageButton) findViewById(R.id.locinfo_distance);

        CheckPermissions();
    }

    private void LocatMe() {
        floc = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (permAssign) {
                Task tasklocat = floc.getLastLocation();
                tasklocat.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.e("Target", "Location Got ");
                            Location currloc = (Location) task.getResult();

                            try {
                                movCamera(new LatLng(currloc.getLatitude(), currloc.getLongitude()), zoomdef,"Base Location");
                            }catch (Exception es){
                                Toast.makeText(getApplicationContext(), "Error\n"+es, Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(Mapped.this, "Location Not achieved", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException sec) {
            Toast.makeText(Mapped.this, "Get Device Locate, Sec", Toast.LENGTH_SHORT).show();
            Log.e("Target", "Location SEC " + sec.getMessage());
        }
    }

    private void movCamera(LatLng latlong, float zoom,String locname) {
        Toast.makeText(Mapped.this, "Moving Camera to lat" + latlong.latitude + "\nlong" + latlong.longitude, Toast.LENGTH_LONG).show();
        Log.e("Target", "Moving Camera to lat " + latlong.latitude + "\tlong " + latlong.longitude);
        gMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, zoom));

        if (!locname.equals("Base Location")){
            MarkerOptions opt=new MarkerOptions()
                    .position(latlong)
                    .title(locname);
            gMaps.addMarker(opt);
        }
    }

    private void Init() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(Mapped.this);
    }

    private void CheckPermissions() {
        String[] pe = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                locFINE) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    locCOS) == PackageManager.PERMISSION_GRANTED) {
                permAssign = true;
                Init();
            } else {
                ActivityCompat.requestPermissions(this, pe, reqcod);
            }
        } else {
            ActivityCompat.requestPermissions(this, pe, reqcod);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case reqcod: {
                if (grantResults.length > 0) {
                    for (int f = 0; f < grantResults.length; f++) {
                        if (grantResults[f] != PackageManager.PERMISSION_GRANTED) {
                            permAssign = false;
                            return;
                        }
                    }
                    permAssign = true;
                    Init();
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMaps = googleMap;

        ArrayList<String> places = null;

        if (permAssign) {
            LocatMe();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            gMaps.setMyLocationEnabled(true);
            gMaps.getUiSettings().setMyLocationButtonEnabled(true);

            gMaps.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(LatLng latLng) {
                    MarkerOptions markerOptions = new MarkerOptions();

                    count = count + 1;

                    if (count > 2){

                    }else {

                        if (count == 1){
                            tapped = latLng;
                        }
                        if (count == 2){
                            tapped1 = latLng;
                        }

                        markerOptions.position(latLng);
                        markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                        gMaps.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        gMaps.addMarker(markerOptions);
                    }
                }
            });

            imginfo_clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gMaps.clear();
                    Toast.makeText(Mapped.this, "Cleared all markers", Toast.LENGTH_SHORT).show();
                }
            });

            imginfo_distance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    count = 0;
                    String distance  = String.valueOf(CalculationByDistance(tapped, tapped1));;
                    Toast.makeText(Mapped.this, "Distance is "+distance+" Kilometres", Toast.LENGTH_LONG).show();

                    SystemClock.sleep(2000);
                    gMaps.clear();

                }
            });
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("onConnectionFailed", "ConnectionResult connectionResult: " );
    }

    public double CalculationByDistance(LatLng StartPoint, LatLng EndPoint) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartPoint.latitude;
        double lat2 = EndPoint.latitude;
        double lon1 = StartPoint.longitude;
        double lon2 = EndPoint.longitude;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);

        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;

        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.e("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

}