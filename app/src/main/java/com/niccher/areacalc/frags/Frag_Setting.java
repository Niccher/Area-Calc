package com.niccher.areacalc.frags;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.niccher.areacalc.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class Frag_Setting extends Fragment {

    static Context cnt;
    static SwitchPreference themstate;
    SwitchCompat switch1,switch2,switch3,switch4;
    boolean shswitch1;

    SharedPreferences shpref;

    public Frag_Setting() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View solv= inflater.inflate(R.layout.frag_setting, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("App Setting");

        shpref=getActivity().getSharedPreferences("Gretsa_Prefs",Context.MODE_PRIVATE);
        shswitch1=shpref.getBoolean("themes",false);

        switch1=solv.findViewById(R.id.themes);

        switch1.setChecked(shswitch1);

        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shswitch1=!shswitch1;
                switch1.setChecked(shswitch1);

                SharedPreferences.Editor PrefEdit=shpref.edit();
                PrefEdit.putBoolean("themes",shswitch1);
                PrefEdit.apply();

                String states=Boolean.toString(shswitch1);
                Log.e("Phone", "States: Checked"+states);

                if (Boolean.toString(shswitch1)=="true"){
                    Log.e("True", "States: Is"+states);
                    ((AppCompatActivity) getActivity()).getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }

                if (Boolean.toString(shswitch1)=="false"){
                    Log.e("Null", "States: Is"+states);
                    ((AppCompatActivity) getActivity()).getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }

            }
        });

        return solv;
    }

}

