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
import android.widget.TextView;
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
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
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
import java.util.Collections;
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
    TextView det_info;
    Switch category;

    int count = 0, area_count = 0;
    double length_ = 0;
    LatLng tapped,tapped1;
    LatLng points = null;
    Polygon polygon;
    PolylineOptions polylineOptions;

    double prev,curent;

    CalcArea calcArea;
    CalcDistance calcDistance;

    ArrayList<LatLng> locList = new ArrayList<LatLng>();
    ArrayList<LatLng> loc_area = new ArrayList<LatLng>();
    ArrayList<LatLng> loc_direction = new ArrayList<LatLng>();
    Boolean state = false, area = false, length = true ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapped);

        category=(Switch) findViewById(R.id.status);
        imginfo_clear=(ImageButton) findViewById(R.id.locinfo_clear);
        det_info=(TextView) findViewById(R.id.loc_details);

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
        Toast.makeText(Mapped.this, "Locating lat " + latlong.latitude + "\nlong " + latlong.longitude, Toast.LENGTH_LONG).show();
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
                    det_info.setVisibility(View.VISIBLE);

                    count = count + 1;
                    area_count = area_count + 1;


                    if (area){
                        loc_area.add(latLng);

                        if (loc_area.size() > 1){

                            polygon = googleMap.addPolygon(new PolygonOptions()
                                    .addAll(loc_area)
                                    .strokeWidth(0)
                                    .clickable(true)
                                    .fillColor(Color.GRAY));
                        }

                        if (loc_area.size() > 2){
                            ArrayList<LatLng> pev = new ArrayList<>();
                            for (int i = 0; i < loc_area.size()-1; i++) {
                                pev.add(loc_area.get(i));
                            }
                            prev = SphericalUtil.computeArea(pev)/1000000;
                            double areas = SphericalUtil.computeArea(loc_area)/1000000;

                            if (prev < areas){
                                //Toast.makeText(Mapped.this, "Area is "+String.format("%.2f", areas)+" Square Kilometers", Toast.LENGTH_LONG).show();
                                Log.e("AreaComputed", "SphericalUtil areas as: "+String.format("%.0f", areas) );
                                Log.e("AreaComputed", "SphericalUtil areas ++: "+String.format("%.0f", prev) );

                                det_info.setText("Areas Approximated as: \n"+String.format("%.2f", areas)+" Square Kilometres");

                            }else {
                                det_info.setText("");
                                det_info.setVisibility(View.INVISIBLE);
                                Toast.makeText(Mapped.this, "Markers should all be moving to one direction,\nEither clockwise or anticlockwise", Toast.LENGTH_LONG).show();
                                loc_area.clear();
                                locList.clear();
                                gMaps.clear();
                                googleMap.clear();
                                loc_area.add(latLng);
                            }

                            /*List<Location> dope = new ArrayList<Location>();
                            for (int i = 0; i < loc_area.size(); i++) {
                                LatLng here = loc_area.get(i);
                                Location location = new Location("Test");
                                location.setLatitude(here.latitude);
                                location.setLongitude(here.longitude);
                                dope.add(location);
                            }

                            /*try {
                                double areas2 = calcArea.calculateAreaPolygon(dope);
                                Log.e("AreaComputed", "calculateAreaPolygon areas as: "+areas2 );
                            }catch (Exception es){
                                Log.e("AreaComputed", "calculateAreaPolygon error "+es.getMessage() );
                            }*/

                        }

                        markerOptions.position(latLng);
                        markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                        gMaps.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        gMaps.addMarker(markerOptions);
                    }

                    if (length){
                        locList.add(latLng);
                        int sizes = locList.size();

                        if (state){
                            locList.clear();
                            gMaps.clear();
                            googleMap.clear();
                            state = false;
                        }

                        if (locList.size() > 1){
                            /*Log.e("Size as", "onMapClick: " + locList.size());
                            Log.e("Varrrr 1", "onMapClick: " + locList.get(locList.size()-2));
                            Log.e("Varrrr 2", "onMapClick: " + locList.get(locList.size()-1));*/
                            tapped = locList.get(locList.size()-2);
                            tapped1 = locList.get(locList.size()-1);
                            length_ = length_ + calcDistance.CalculateDistance(tapped, tapped1);
                            String distance  = String.format("%.2f", length_);
                            det_info.setText("Distance Approximated as: \n"+distance+" Kilometres");
                            Log.e("Distance is ", "Currently as : " + length_);
                        }

                        markerOptions.position(latLng);
                        googleMap.addPolyline((new PolylineOptions()).addAll(locList )
                                .width(5)
                                .color(Color.RED)
                                .geodesic(false));

                        gMaps.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        gMaps.addMarker(markerOptions);
                    }

                }
            });

            imginfo_clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loc_area.clear();
                    locList.clear();
                    gMaps.clear();
                    googleMap.clear();
                    count = 0;
                    det_info.setText("");
                    det_info.setVisibility(View.INVISIBLE);
                    Toast.makeText(Mapped.this, "Cleared all markers", Toast.LENGTH_SHORT).show();
                }
            });

            category.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (category.isChecked()){
                        loc_area.clear();
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
                    googleMap.clear();

                    det_info.setText("");
                    det_info.setVisibility(View.INVISIBLE);
                    //det_info.setText("Areas Approximated as: ");
                }
            });
        }
    }

    public boolean isClockwise(ArrayList<LatLng> region) {
        final int size = region.size();
        LatLng a = region.get(size - 1);
        double aux = 0;
        for (int i = 0; i < size; i++) {
            LatLng b = region.get(i);
            aux += (b.latitude - a.latitude) * (b.longitude + a.longitude);
            a = b;
        }
        return aux <= 0;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("onConnectionFailed", "ConnectionResult connectionResult: " );
    }

}