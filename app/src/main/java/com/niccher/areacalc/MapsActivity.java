package com.niccher.areacalc;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.niccher.areacalc.fragments.Frag_About;


public class MapsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Init();
    }

    private void Init(){
        Fragment frags;
        frags=new Frag_About();
        getSupportActionBar().setTitle("Maap");
        FragmentManager frman1=getSupportFragmentManager();
        frman1.beginTransaction().replace(R.id.container,frags).commit();
    }

}
