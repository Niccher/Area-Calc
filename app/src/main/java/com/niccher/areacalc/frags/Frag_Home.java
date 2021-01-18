package com.niccher.areacalc.frags;

import android.Manifest;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class Frag_Home extends Fragment implements OnMapReadyCallback {

    String locFINE = Manifest.permission.ACCESS_FINE_LOCATION;
    String locCOS = Manifest.permission.ACCESS_COARSE_LOCATION;

    MapView mMapView;

    Boolean permAssign = false;

    int count = 0, area_count = 0;
    double length_ = 0;
    LatLng tapped,tapped1;
    LatLng points = null;
    Polygon polygon;
    PolylineOptions polylineOptions;

    double prev,curent;

    ArrayList<LatLng> locList = new ArrayList<LatLng>();
    ArrayList<LatLng> loc_area = new ArrayList<LatLng>();
    ArrayList<LatLng> loc_direction = new ArrayList<LatLng>();
    Boolean state = false, area = false, length = false, clean = false ;

    final int reqcod = 145;
    GoogleMap gMaps;

    float zoomdef = 10f;

    FusedLocationProviderClient floc;

    FloatingActionButton fab_ex, fab_lin, fab_siz;

    TextView txt_peri, txt_area;

    LinearLayout linL, linA, linC, linStatus;

    CalcArea calcArea;
    CalcDistance calcDistance;

    FirebaseAuth mAuth;
    FirebaseUser userf;
    DatabaseReference dref;

    public Frag_Home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fraghome= inflater.inflate(R.layout.frag_home, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Home");

        mAuth = FirebaseAuth.getInstance();
        userf=mAuth.getCurrentUser();
        //fdbas= FirebaseDatabase.getInstance();
        dref = FirebaseDatabase.getInstance().getReference("Area_Calc_Saved");

        CheckPermissions();

        calcArea = new CalcArea();
        calcDistance = new CalcDistance();

        mMapView = (MapView) fraghome.findViewById(R.id.mapView);

        linA = fraghome.findViewById(R.id.linArea);
        linL = fraghome.findViewById(R.id.linLength);
        linC = fraghome.findViewById(R.id.linClear);
        linStatus = fraghome.findViewById(R.id.loc_about);

        fab_ex = fraghome.findViewById(R.id.fab_expand);
        /*fab_lin = fraghome.findViewById(R.id.fab_length);
        fab_siz = fraghome.findViewById(R.id.fab_area);*/

        txt_peri = fraghome.findViewById(R.id.loc_perimeter);
        txt_area = fraghome.findViewById(R.id.loc_area);

        linL.setVisibility(View.INVISIBLE);
        linA.setVisibility(View.INVISIBLE);
        linC.setVisibility(View.INVISIBLE);
        linStatus.setVisibility(View.INVISIBLE);

        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this::onMapReady);

        Animation anim_show = AnimationUtils.loadAnimation(getActivity(),R.anim.fab_show);
        Animation anim_hide = AnimationUtils.loadAnimation(getActivity(),R.anim.fab_hide);

        fab_ex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( linA.getVisibility() == View.VISIBLE && linL.getVisibility() == View.VISIBLE){
                    linC.setVisibility(View.INVISIBLE);
                    linA.setVisibility(View.INVISIBLE);
                    linL.setVisibility(View.INVISIBLE);
                    fab_ex.setAnimation(anim_show);
                }else {
                    linC.setVisibility(View.VISIBLE);
                    linA.setVisibility(View.VISIBLE);
                    linL.setVisibility(View.VISIBLE);
                    fab_ex.setAnimation(anim_hide);
                }
            }
        });

        linL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Please mark two or more points on the map so calculate the distance between them", Toast.LENGTH_LONG).show();

                linA.setVisibility(View.INVISIBLE);
                linL.setVisibility(View.INVISIBLE);
                linC.setVisibility(View.INVISIBLE);

                linStatus.setVisibility(View.VISIBLE);

                txt_area.setVisibility(View.GONE);
                txt_peri.setVisibility(View.VISIBLE);

                txt_peri.setText("Perimeter");
                txt_area.setText("Area");

                fab_ex.setAnimation(anim_show);

                loc_area.clear();
                area = false;
                length = true;
                clean = true;
            }
        });

        linA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Please Mark several points on the map to computer the area by pressing the screen", Toast.LENGTH_LONG).show();

                linA.setVisibility(View.INVISIBLE);
                linL.setVisibility(View.INVISIBLE);
                linC.setVisibility(View.INVISIBLE);

                linStatus.setVisibility(View.VISIBLE);

                txt_peri.setVisibility(View.VISIBLE);
                txt_area.setVisibility(View.VISIBLE);

                txt_peri.setText("Distance");
                txt_area.setText("Area");

                fab_ex.setAnimation(anim_show);

                loc_area.clear();
                locList.clear();
                area = true;
                length = false;
                clean = true;
            }
        });

        linC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                linA.setVisibility(View.INVISIBLE);
                linL.setVisibility(View.INVISIBLE);
                linC.setVisibility(View.INVISIBLE);

                linStatus.setVisibility(View.INVISIBLE);

                txt_peri.setText("");
                txt_area.setText("");

                fab_ex.setAnimation(anim_show);

                loc_area.clear();
                locList.clear();
                area = false;
                length = false;
                gMaps.clear();
                mMapView.onStart();
                count = 0;
                Toast.makeText(getActivity(), "Cleared all markers", Toast.LENGTH_SHORT).show();
            }
        });

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

            gMaps.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    MarkerOptions markerOptions = new MarkerOptions();

                    count = count + 1;
                    area_count = area_count + 1;

                    if (clean){
                        loc_area.clear();
                        locList.clear();
                        gMaps.clear();
                        clean = false;
                    }else if (area){
                        loc_area.add(latLng);

                        if (loc_area.size() > 1){
                            polygon = gMaps.addPolygon(new PolygonOptions()
                                    .addAll(loc_area)
                                    .strokeWidth(0)
                                    .clickable(true)
                                    .fillColor(Color.argb(70,140,70,200)));

                            tapped = loc_area.get(loc_area.size()-2);
                            tapped1 = loc_area.get(loc_area.size()-1);
                            length_ = length_ + calcDistance.CalculateDistance(tapped, tapped1);
                            String distance  = String.format("%.2f", length_);
                            txt_peri.setText("Perimeter : "+distance+" Km");
                        }

                        if (loc_area.size() > 2){
                            ArrayList<LatLng> pev = new ArrayList<>();
                            ArrayList<LatLng> hole = new ArrayList<>();
                            for (int i = 0; i < loc_area.size()-1; i++) {
                                pev.add(loc_area.get(i));
                            }
                            prev = SphericalUtil.computeArea(pev)/1000000;
                            double areas = SphericalUtil.computeArea(loc_area)/1000000;

                            if (prev < areas){
                                Log.e("AreaComputed", "SphericalUtil areas as: "+String.format("%.0f", areas) );
                                Log.e("AreaComputed", "SphericalUtil areas ++: "+String.format("%.0f", prev) );

                                txt_area.setText("Areas as: \n"+String.format("%.2f", areas)+" Sq, Km");

                            }else {
                                //Toast.makeText(getActivity(), "Markers should all be moving to one direction,\nEither clockwise or anticlockwise", Toast.LENGTH_LONG).show();
                                gMaps.clear();
                                hole.add(latLng);
                                polygon = gMaps.addPolygon(new PolygonOptions()
                                        .addAll(loc_area)
                                        .addHole(hole)
                                        .fillColor(Color.LTGRAY));
                            }

                        }

                        markerOptions.position(latLng);
                        markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                        gMaps.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        gMaps.addMarker(markerOptions);
                    } else if (length){
                        locList.add(latLng);
                        int sizes = locList.size();

                        if (state){
                            locList.clear();
                            gMaps.clear();
                            googleMap.clear();
                            state = false;
                        }

                        if (locList.size() > 1){
                            tapped = locList.get(locList.size()-2);
                            tapped1 = locList.get(locList.size()-1);
                            length_ = length_ + calcDistance.CalculateDistance(tapped, tapped1);
                            String distance  = String.format("%.2f", length_);
                            txt_peri.setText("Distance Approximation: "+distance+" Km");
                            Log.e("Distance is ", "Currently as : " + length_);
                        }

                        markerOptions.position(latLng);
                        googleMap.addPolyline((new PolylineOptions()).addAll(locList )
                                .width(5)
                                .color(Color.RED)
                                .geodesic(false));

                        gMaps.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        gMaps.addMarker(markerOptions);
                    }else {
                        Toast.makeText(getActivity(), "Please select either Area or Distance so as to proceed", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else {
            Toast.makeText(getActivity(), "You must allow the application to access location for it to run smoothly", Toast.LENGTH_SHORT).show();
            CheckPermissions();
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_cancel) {
            Toast.makeText(getActivity(), "Cancel Under Active Development", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.action_save) {

            if (locList.isEmpty() && loc_area.isEmpty()){
                Log.e("SaveList", "Type: locList.isEmpty() && loc_area.isEmpty()");
                Toast.makeText(getActivity(), "Ensure you have placed some markers before saving", Toast.LENGTH_SHORT).show();
            }else {
                if (locList.size() ==0){
                    SaveList(loc_area,"Area");
                }else {
                    SaveList(locList,"Length");
                }
            }

            return true;
        }

        if (id == R.id.action_layers) {
            Toast.makeText(getActivity(), "Layer Pressed", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void SaveList(ArrayList<LatLng> selected, String type) {
        String llong= "";
        for (int i = 0; i < (selected.size()); i++) {
            Log.e("SaveList", i+" SaveList: "+selected.get(i) );
            Log.e("SaveList", i+" Type: "+type);
            llong+=selected.get(i);
        }
        Log.e("SaveList", "SaveList String : "+llong);

        Calendar cal= Calendar.getInstance();
        SimpleDateFormat ctime=new SimpleDateFormat("HH:mm");
        SimpleDateFormat cdate=new SimpleDateFormat("dd-MMMM-yyyy");

        final String ctim=ctime.format(cal.getTime());
        final String cdat=cdate.format(cal.getTime());

        String uploadId = "";

        try {
            uploadId= dref.push().getKey();
            Mod_Area upload = new Mod_Area(uploadId,cdat+" "+ctim,llong,String.valueOf(selected.size()));
            dref.child(type).child(userf.getUid()).child(uploadId).setValue(upload);
            Toast.makeText(getActivity(), "Selection saved", Toast.LENGTH_SHORT).show();
        }catch (Exception s){
            Toast.makeText(getActivity(), "Unable to save your selection", Toast.LENGTH_SHORT).show();
            Log.e("SaveList", "SaveList Error : "+s.getMessage());
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.mini, menu);
        //return true;
        super.onCreateOptionsMenu(menu, inflater);
    }


}
