package com.niccher.areacalc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.niccher.areacalc.adapters.Adp_Slider;


public class ActivitySlider extends AppCompatActivity {

    private Button btnSkip, btnNext;
    private ViewPager sViewPager;
    private LinearLayout dotsLayout;
    private TextView dots[];

    Adp_Slider adpSlider;

    SharedPreferences shpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);

        sViewPager = (ViewPager) findViewById(R.id.sViewPager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSkipClick();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnNextClick();
            }
        });

        adpSlider = new Adp_Slider(this);
        sViewPager.setAdapter(adpSlider);
        sViewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        addBottomDots(0);

    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            if (position == adpSlider.image_slide.length - 1) {
                btnNext.setText("Start");
                btnSkip.setVisibility(View.GONE);
            } else {
                btnNext.setText("Next");
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {}

        @Override
        public void onPageScrollStateChanged(int arg0) {}
    };

    public void btnSkipClick() {
        WritePref();
    }

    public  void btnNextClick() {
        int current = getItem(1);
        if (current < adpSlider.image_slide.length) {
            sViewPager.setCurrentItem(current);
        } else {
            WritePref();
        }
    }

    private void launchHomeScreen() {
        Intent gog=new Intent(this,UserLogin.class);
        gog.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(gog);
        ActivitySlider.this.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private int getItem(int i) {
        return sViewPager.getCurrentItem() + i;
    }

    // add dot indicator
    public void addDotIndicator(){
        dots = new TextView[3];
        for (int i=0; i<dots.length; i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8266;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.colortWhite));

            dotsLayout.addView(dots[i]);
        }
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[4];
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.colortWhite));  // dot_inactive
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(getResources().getColor(R.color.colorAccent)); // dot_active
    }

    private void WritePref(){
        shpref = getSharedPreferences("Area_Calc_Prefs", MODE_PRIVATE);
        SharedPreferences.Editor PrefEdit=shpref.edit();
        PrefEdit.putString("intro_slide","has_seen");
        PrefEdit.apply();

        launchHomeScreen();
    }
}
