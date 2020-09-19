package com.niccher.areacalc.frags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.niccher.areacalc.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class Frag_Profile extends Fragment {

    CardView cvcars,cvestate,cvadcar,cvadest,cvprof;

    public Frag_Profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fraghome= inflater.inflate(R.layout.frag_profile, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("My Profile");

        return fraghome;
    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

}
