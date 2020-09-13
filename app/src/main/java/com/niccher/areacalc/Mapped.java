package com.niccher.areacalc;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.tan;
import static java.lang.Math.toRadians;


public class Mapped extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    String locFINE = Manifest.permission.ACCESS_FINE_LOCATION;
    String locCOS = Manifest.permission.ACCESS_COARSE_LOCATION;

    Boolean permAssign = false;

    final int reqcod = 145;
    GoogleMap gMaps;

    float zoomdef = 10f;

    FusedLocationProviderClient floc;

    ImageButton imginfo_clear;
    Switch category;

    int count = 0, area_count = 0;
    LatLng tapped,tapped1;
    LatLng points = null;;

    CalcArea calcArea;
    CalcDistance calcDistance;

    ArrayList<LatLng> locList = new ArrayList<LatLng>();
    ArrayList<LatLng> loc_area = new ArrayList<LatLng>();
    Boolean state = false, area = false, length = true ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapped);

        category=(Switch) findViewById(R.id.status);
        imginfo_clear=(ImageButton) findViewById(R.id.locinfo_clear);

        calcArea = new CalcArea();
        calcDistance = new CalcDistance();

        CheckPermissions();
    }

    @Override
    protected void onStart() {
        super.onStart();
        length = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        length = true;
    }

    private void LocateMe() {
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

        if (permAssign) {
            LocateMe();
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
                    points = latLng;

                    count = count + 1;
                    area_count = area_count + 1;


                    if (area){
                        loc_area.add(latLng);

                        if (loc_area.size() > 1){
                            googleMap.addPolyline((new PolylineOptions()).addAll(loc_area )
                                    .clickable(true)
                                    .width(5)
                                    .color(Color.RED)
                                    .geodesic(false));
                        }

                        if (loc_area.size() > 5){
                            double areas = SphericalUtil.computeArea(loc_area);
                            Toast.makeText(Mapped.this, "Area is "+String.valueOf(areas), Toast.LENGTH_LONG).show();
                            Log.e("AreaComputed", "SphericalUtil areas as: "+areas );

                            List<Location> dope = new ArrayList<Location>();
                            for (int i = 0; i < loc_area.size(); i++) {
                                LatLng here = loc_area.get(i);
                                Location location = new Location("Test");
                                location.setLatitude(here.latitude);
                                location.setLongitude(here.longitude);
                                dope.add(location);
                            }

                            try {

                                double areas2 = calcArea.calculateAreaPolygon(dope);
                                Log.e("AreaComputed", "calculateAreaOfGPSPolygonOnEarthInSquareMeters areas as: "+areas2 );
                            }catch (Exception es){
                                Log.e("AreaComputed", "calculateAreaOfGPSPolygonOnEarthInSquareMeters error "+es.getMessage() );
                            }

                        }

                        markerOptions.position(latLng);
                        markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                        gMaps.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        gMaps.addMarker(markerOptions);
                    }

                    if (length){
                        if (state){
                            locList.clear();
                            gMaps.clear();
                            state = false;
                        }

                        if (count > 2){
                            count = 0;
                            state = true;
                            googleMap.addPolyline((new PolylineOptions()).addAll(locList )
                                    .width(5)
                                    .color(Color.RED)
                                    .geodesic(false));

                            String distance  = String.valueOf(calcDistance.CalculateDistance(tapped, tapped1));;
                            Toast.makeText(Mapped.this, "Distance is "+distance+" Kilometres", Toast.LENGTH_LONG).show();
                        }else {

                            if (count == 1){
                                tapped = latLng;
                                locList.add(latLng);
                            }
                            if (count == 2){
                                tapped1 = latLng;
                                locList.add(latLng);

                                count = 0;
                                state = true;
                                googleMap.addPolyline((new PolylineOptions()).addAll(locList )
                                        .width(5)
                                        .color(Color.RED)
                                        .geodesic(false));

                                String distance  = String.valueOf(calcDistance.CalculateDistance(tapped, tapped1));;
                                Toast.makeText(Mapped.this, "Distance is "+distance+" Kilometres", Toast.LENGTH_LONG).show();
                            }

                            markerOptions.position(latLng);
                            markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                            gMaps.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                            gMaps.addMarker(markerOptions);
                        }
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

            category.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (category.isChecked()){
                        loc_area.add(points);
                        locList.clear();
                        area = true;
                        length = false;
                    }
                    if (!category.isChecked()){
                        loc_area.clear();
                        area = false;
                        length = true;
                    }

                    gMaps.clear();
                }
            });
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("onConnectionFailed", "ConnectionResult connectionResult: " );
    }

}