package com.niccher.areacalc.frags;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.SphericalUtil;
import com.niccher.areacalc.R;
import com.niccher.areacalc.Utils.CalcArea;
import com.niccher.areacalc.Utils.CalcDistance;
import com.niccher.areacalc.mod.Mod_Area;
import com.niccher.areacalc.mod.Mod_Perimeter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 */
public class Frag_Render_History extends Fragment implements OnMapReadyCallback {

    String locFINE = Manifest.permission.ACCESS_FINE_LOCATION;
    String locCOS = Manifest.permission.ACCESS_COARSE_LOCATION;

    MapView mMapView;

    Boolean permAssign = false;

    Polygon polygon;
    PolylineOptions polylineOptions;


    ArrayList<LatLng> locList = new ArrayList<LatLng>();
    ArrayList<LatLng> loc_area = new ArrayList<LatLng>();
    ArrayList<LatLng> loc_init = new ArrayList<LatLng>();

    final int reqcod = 145;
    GoogleMap gMaps;

    float zoomdef = 10f;

    FusedLocationProviderClient floc;

    FloatingActionButton fab_ex;

    TextView txt_peri, txt_area;

    LinearLayout linL, linA, linC, linStatus;

    CalcArea calcArea;
    CalcDistance calcDistance;

    LatLng new_point;

    Intent getit;
    Bundle getbun;

    String Object_time,Object_uid,Object_latlong,Object_points,Object_perimeter,Object_area;

    public Frag_Render_History() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fraghome= inflater.inflate(R.layout.frag_home, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("History");

        CheckPermissions();

        calcArea = new CalcArea();
        calcDistance = new CalcDistance();

        mMapView = (MapView) fraghome.findViewById(R.id.mapView);

        linA = fraghome.findViewById(R.id.linArea);
        linL = fraghome.findViewById(R.id.linLength);
        linC = fraghome.findViewById(R.id.linClear);
        linStatus = fraghome.findViewById(R.id.loc_about);

        fab_ex = fraghome.findViewById(R.id.fab_expand);
        fab_ex.setVisibility(View.INVISIBLE);

        txt_peri = fraghome.findViewById(R.id.loc_perimeter);
        txt_area = fraghome.findViewById(R.id.loc_area);

        linC.setVisibility(View.INVISIBLE);

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        getit= getActivity().getIntent();
        getbun = getArguments();

        Object_time=getbun.getString("Object_time");
        Object_uid=getbun.getString("Object_uid");
        Object_latlong=getbun.getString("Object_latlong");
        Object_points=getbun.getString("Object_points");
        Object_perimeter=getbun.getString("Object_perimeter");
        Object_area=getbun.getString("Object_area");

        /*Log.e("Passed --- ", "Object_time: "+Object_time);
        Log.e("Passed --- ", "Object_uid: "+Object_uid);
        Log.e("Passed --- ", "Object_latlong: "+Object_latlong);
        Log.e("Passed --- ", "Object_points: "+Object_points);
        Log.e("Passed --- ", "Object_perimeter: "+Object_perimeter);
        Log.e("Passed --- ", "Object_area: "+Object_area);*/

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this::onMapReady);

        return fraghome;
    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
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

    private void LocateMe() {
        floc = LocationServices.getFusedLocationProviderClient(getActivity());
        try {
            if (permAssign) {
                Task tasklocat = floc.getLastLocation();
                tasklocat.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            //Log.e("Target", "Location Got ");
                            Location currloc = (Location) task.getResult();

                            try {
                                movCamera(new LatLng(currloc.getLatitude(), currloc.getLongitude()), zoomdef);
                            }catch (Exception es){
                                Toast.makeText(getActivity(), "Error\n"+es, Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(getActivity(), "Location Not achieved", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException sec) {
            Toast.makeText(getActivity(), "Get Device Locate, Sec", Toast.LENGTH_SHORT).show();
            Log.e("Target", "Location SEC " + sec.getMessage());
        }
    }

    private void movCamera(LatLng latlong, float zoom) {
        gMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, zoom));
    }

    private void CheckPermissions() {
        String[] pe = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(getContext(),
                locFINE) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    locCOS) == PackageManager.PERMISSION_GRANTED) {
                permAssign = true;
            } else {
                ActivityCompat.requestPermissions(getActivity(), pe, reqcod);
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(), pe, reqcod);
        }
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (permAssign){
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            googleMap.getUiSettings().setScrollGesturesEnabled(true);
            googleMap.getUiSettings().setTiltGesturesEnabled(true);
            googleMap.getUiSettings().setRotateGesturesEnabled(true);

            gMaps = googleMap;
            gMaps.setMyLocationEnabled(true);
            gMaps.getUiSettings().setMyLocationButtonEnabled(true);
            LocateMe();

            MarkerOptions markerOptions = new MarkerOptions();
            locList.clear();

            LatLng point_one = null, point_last = null;

            try {
                if (isNumeric(Object_area)){
                    String[] dd = Object_latlong.split("lat/lng:");
                    for ( int sizes = 1; sizes< dd.length; sizes++) {
                        String pointone = dd[sizes].replace("(","").replace(")","").trim();
                        Log.e((dd.length-1)+"- Final As - ", "************* are : "+pointone );
                        String[] new_latlong =  pointone.split(",");
                        double new_lat = Double.parseDouble(new_latlong[0]);
                        double new_long = Double.parseDouble(new_latlong[1]);
                        new_point = new LatLng(new_lat, new_long);
                        locList.add(new_point);
                        markerOptions.position(new_point);

                        if(sizes == 1){
                            point_one = new_point;
                        }

                        gMaps.addMarker(markerOptions);
                    }

                    locList.add(point_one);

                    try {
                        polygon = gMaps.addPolygon(new PolygonOptions()
                                .addAll(locList)
                                .strokeWidth(0)
                                .clickable(true)
                                .fillColor(Color.argb(70,140,70,200)));

                        googleMap.addPolyline((new PolylineOptions()).addAll(locList )
                                .width(5)
                                .color(Color.RED)
                                .geodesic(false));
                    }catch (Exception mas){
                        //Log.e("- Caught As - ", "/////////////////// "+mas );
                    }

                    txt_peri.setText(Object_perimeter+" Km");
                    txt_area.setText( String.format("%.3f", Double.parseDouble(Object_area))+" Sq. Km");

                    linStatus.setVisibility(View.VISIBLE);
                    txt_peri.setVisibility(View.VISIBLE);
                    txt_area.setVisibility(View.VISIBLE);

                }
            }catch (Exception es){
                String[] dd = Object_latlong.split("lat/lng:");
                for ( int sizes = 1; sizes< dd.length; sizes++) {
                    String pointone = dd[sizes].replace("(","").replace(")","").trim();
                    String[] new_latlong =  pointone.split(",");
                    double new_lat = Double.parseDouble(new_latlong[0]);
                    double new_long = Double.parseDouble(new_latlong[1]);
                    new_point = new LatLng(new_lat, new_long);
                    locList.add(new_point);
                    markerOptions.position(new_point);

                    try {
                        googleMap.addPolyline((new PolylineOptions()).addAll(locList )
                                .width(5)
                                .color(Color.RED)
                                .geodesic(false));
                    }catch (Exception mas){
                        //Log.e("- Caught As - ", "/////////////////// "+mas );
                    }

                    gMaps.addMarker(markerOptions);
                }
                txt_peri.setText(Object_perimeter+" Kilometers");

                linStatus.setVisibility(View.VISIBLE);
                txt_peri.setVisibility(View.VISIBLE);
                txt_area.setVisibility(View.GONE);
            }

        }else {
            Toast.makeText(getActivity(), "You must allow the application to access location for it to run smoothly", Toast.LENGTH_SHORT).show();
            CheckPermissions();
        }
    }
}
