package com.niccher.areacalc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.niccher.areacalc.R;


public class Adp_Slider extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

    public Adp_Slider(Context context) {
        this.context = context;
    }

    public int[] image_slide ={
            R.drawable.ic_home,
            R.drawable.ic_area,
            R.drawable.ic_length,
            R.drawable.ic_location
    };

    public String[] heading_slide ={
            "Welcome to Areal Kalc",
            "Area Calculator",
            "Length Estimation",
            "Save your instances"
    };

    public String[] description_slide ={
            "A geo utility calulator",
            "Estimate the area of a place at your comfort",
            "Know the distance of any any places",
            "Save marked Areas and Distance for later reference"
    };

    @Override
    public int getCount() {
        return heading_slide.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container,false);
        container.addView(view);

        RelativeLayout change = view.findViewById(R.id.changeme);

        TextView slideHeading = view.findViewById(R.id.tvHeading);
        ImageView slide_imageView = view.findViewById(R.id.tvImg);
        TextView slideDescription = view.findViewById(R.id.tvDescription);

        if ( (heading_slide[position])=="Welcome to Areal Kalc"){
            //change.setBackgroundResource(R.drawable.background);
            slide_imageView.setImageResource(R.drawable.ic_home);
        }
        if ( (heading_slide[position])=="Area Calculator"){
            //change.setBackgroundResource(R.drawable.background);
            slide_imageView.setImageResource(R.drawable.ic_area);
        }
        if ( (heading_slide[position])=="Length Estimation"){
            //change.setBackgroundResource(R.drawable.background);
            slide_imageView.setImageResource(R.drawable.ic_length);
        }
        if ( (heading_slide[position])=="Save your instances"){
            //change.setBackgroundResource(R.drawable.background);
            slide_imageView.setImageResource(R.drawable.ic_save);
        }

        //slide_imageView.setImageResource(context.getResources().getResourceName(image_slide[position]));
        slideHeading.setText(heading_slide[position]);
        slideDescription.setText(description_slide[position]);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }

}


