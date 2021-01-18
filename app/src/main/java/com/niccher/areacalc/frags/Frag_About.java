package com.niccher.areacalc.frags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.niccher.areacalc.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class Frag_About extends Fragment {

    public Frag_About() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View solv= inflater.inflate(R.layout.frag_about, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("About App");

        TextView info = solv.findViewById(R.id.app_info);
        info.setText("Hello welcome to Area Kalc my simple android app for estimating areas of a polygon and perimeters of polygon and distance between pionts");

        info.append("\nThis work by first rendering the map, from the rendered map one can mark point on it by tapping wherever he/she wants, upon pressing a marker is inserted at the selected location");

        return solv;
    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

}
