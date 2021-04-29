package com.niccher.areacalc.frags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.niccher.areacalc.R;
import com.niccher.areacalc.adapters.Adp_Perimeter;
import com.niccher.areacalc.adapters.Adp_Perimeter;
import com.niccher.areacalc.mod.Mod_Perimeter;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Frag_View_Length extends Fragment {

    RecyclerView recyl;
    Adp_Perimeter adp;
    List<Mod_Perimeter> mod_list;

    FirebaseAuth mAuth;
    FirebaseUser userf;

    public Frag_View_Length() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View solv= inflater.inflate(R.layout.frag_view_area, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Saved Polylines");

        mAuth= FirebaseAuth.getInstance();
        userf=mAuth.getCurrentUser();

        recyl=solv.findViewById(R.id.rec_gallery);
        recyl.setHasFixedSize(true);

        LinearLayoutManager lim = new LinearLayoutManager(getActivity());
        lim.setReverseLayout(true);
        lim.setStackFromEnd(true);
        recyl.setLayoutManager(lim);

        mod_list=new ArrayList<>();

        FetchEm();


        return solv;
    }


    private void FetchEm() {
        DatabaseReference dref= FirebaseDatabase.getInstance().getReference("Area_Calc_Saved/Length").child(userf.getUid());
        dref.keepSynced(true);

        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mod_list.clear();

                for (DataSnapshot ds1: dataSnapshot.getChildren()){
                    Mod_Perimeter ug=ds1.getValue(Mod_Perimeter.class);

                    mod_list.add(ug);

                    adp=new Adp_Perimeter(getActivity(),mod_list);

                    recyl.setAdapter(adp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
}