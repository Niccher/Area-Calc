package com.niccher.areacalc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.niccher.areacalc.frags.Frag_About;
import com.niccher.areacalc.frags.Frag_Home;
import com.niccher.areacalc.frags.Frag_Profile;
import com.niccher.areacalc.frags.Frag_Setting;
import com.niccher.areacalc.frags.Frag_View_Area;
import com.niccher.areacalc.frags.Frag_View_Length;
import com.niccher.areacalc.menu.DrawerAdapter;
import com.niccher.areacalc.menu.DrawerItem;
import com.niccher.areacalc.menu.SimpleItem;
import com.niccher.areacalc.menu.SpaceItem;
import com.squareup.picasso.Picasso;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class Casa extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {

    private String[] screenTitles;
    private Drawable[] screenIcons;

    FirebaseAuth mAuth;
    FirebaseUser userf;
    DatabaseReference dref1;

    CircleImageView usr_prof;
    TextView usr_handle,usr_email;

    private SlidingRootNav slidingRootNav;


    private static final int POS_DASHBOARD = 0;
    private static final int POS_View_Area = 1;
    private static final int POS_View_Length = 2;
    private static final int POS_Profile = 3;
    private static final int POS_About_App = 4;
    private static final int POS_Setting = 5;

    private static final int POS_WEBSITE = 6;
    private static final int POS_LOG = 7;
    private static final int POS_EXIT = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_casa);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth= FirebaseAuth.getInstance();
        userf=mAuth.getCurrentUser();

        LoadData();

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        usr_prof = findViewById(R.id.nav_usrimg);
        usr_handle = findViewById(R.id.nav_usrhandle);
        usr_email = findViewById(R.id.nav_usremail);

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_DASHBOARD).setChecked(true),
                createItemFor(POS_View_Area),
                createItemFor(POS_View_Length),
                createItemFor(POS_Profile),
                createItemFor(POS_About_App),
                createItemFor(POS_Setting),
                new SpaceItem(32),
                createItemFor(POS_WEBSITE),
                createItemFor(POS_LOG),
                createItemFor(POS_EXIT)));
        adapter.setListener(Casa.this);

        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setSelected(POS_DASHBOARD);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GetState();
        LoadUsa();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetState();
        LoadUsa();
    }

    private void GetState(){
        FirebaseUser fuse=mAuth.getCurrentUser();
        if (fuse!=null){
            //
        }else {
            startActivity(new Intent(Casa.this,UserLogin.class));
            finish();
        }
    }

    public void LoadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("Area_Calc_Prefs", MODE_PRIVATE);
        String intro = sharedPreferences.getString("intro_slide","has_not");
        Log.e("LoadData", "intro_slide is set as " + intro);

        if (intro =="has_not"){
            Intent gog=new Intent(this,ActivitySlider.class);
            gog.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(gog);
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (intro =="has_seen") {}

        try {
            dref1= FirebaseDatabase.getInstance().getReference("Area_Calc").child(userf.getUid());
            dref1.keepSynced(true);

            dref1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String gUsername,gEmail,gimgProfile;

                    gUsername= (String) dataSnapshot.child("gUsername").getValue();
                    gEmail= (String) dataSnapshot.child("gEmail").getValue();
                    gimgProfile= (String) dataSnapshot.child("gProfilethumb").getValue();

                    usr_email.setText(gEmail);
                    usr_handle.setText(gUsername);

                    try {
                        Picasso.get().load(gimgProfile).into(usr_prof);

                    }catch (Exception ex){
                        Picasso.get().load(R.drawable.ic_defuser).into(usr_prof);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (Exception ex){
            Log.e("Casa ", "LoadUsa Data ********************: \n" +ex.getMessage());
        }
    }


    private void LoadUsa() {
        try {
            usr_email.setText("Your Email");
            usr_handle.setText("Sample Username");
            Picasso.get().load(R.mipmap.eye11).into(usr_prof);
        }catch (Exception ex){
            Log.e("Casa ", "LoadUsa: \n" +ex.getMessage());
        }
    }

    //@Override
    public void onItemSelected(int position) {

        Fragment frags;

        if (position == POS_DASHBOARD) {
            frags=new Frag_Home();
            FragmentManager frman1=getSupportFragmentManager();
            frman1.beginTransaction().replace(R.id.container,frags).commit();
        }
        if (position == POS_View_Area) {
            frags=new Frag_View_Area();
            FragmentManager frman1=getSupportFragmentManager();
            frman1.beginTransaction().replace(R.id.container,frags).commit();
        }
        if (position == POS_View_Length) {
            frags=new Frag_View_Length();
            FragmentManager frman1=getSupportFragmentManager();
            frman1.beginTransaction().replace(R.id.container,frags).commit();
        }
        if (position == POS_Profile) {
            frags=new Frag_Profile();
            FragmentManager frman1=getSupportFragmentManager();
            frman1.beginTransaction().replace(R.id.container,frags).commit();
        }
        if (position == POS_About_App) {
            frags=new Frag_About();
            FragmentManager frman1=getSupportFragmentManager();
            frman1.beginTransaction().replace(R.id.container,frags).commit();
        }
        if (position == POS_Setting) {
            frags=new Frag_Setting();
            FragmentManager frman1=getSupportFragmentManager();
            frman1.beginTransaction().replace(R.id.container,frags).commit();
        }

        if (position == POS_WEBSITE) {
            Toast.makeText(this, "Target Website Not Set, Redirecting to Google", Toast.LENGTH_LONG).show();;
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("www.google.com"));
                startActivity(browserIntent);
            } catch (Exception ex){
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
                startActivity(browserIntent);
            }
        }
        if (position == POS_LOG) {
            mAuth.signOut();
            GetState();
        }
        if (position == POS_EXIT) {
            Toast.makeText(this, "Exiting", Toast.LENGTH_LONG).show();;
            Intent intt=new Intent(Intent.ACTION_MAIN);
            intt.addCategory(Intent.CATEGORY_HOME);
            intt.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intt);

            finish();
            System.gc();
            System.exit(0);
        }

        slidingRootNav.closeMenu();

    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }


    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.color_secondary))
                .withTextTint(color(R.color.color_primary))
                .withSelectedIconTint(color(R.color.colorAccent))
                .withSelectedTextTint(color(R.color.colorAccent));
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }

}
