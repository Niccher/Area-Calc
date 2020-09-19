package com.niccher.areacalc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
            R.drawable.ic_developers
    };

    public String[] heading_slide ={
            "Some Desc",
            "Area Calculator",
            "Length Estimation",
            "Something Here"
    };

    public String[] description_slide ={
            "Some Description here",
            "Estimate the area of a place at your comfort",
            "Know the distance of any any places",
            "Fitness training"
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
        TextView slideDescription = view.findViewById(R.id.tvDescription);

        if ( (heading_slide[position])=="Some Desc"){
            change.setBackgroundResource(R.drawable.background);
        }
        if ( (heading_slide[position])=="Area Calculator"){
            change.setBackgroundResource(R.drawable.background);
        }
        if ( (heading_slide[position])=="Length Estimation"){
            change.setBackgroundResource(R.drawable.background);
        }
        if ( (heading_slide[position])=="Something Here"){
            change.setBackgroundResource(R.drawable.background);
        }

        //slide_imageView.setImageResource(image_slide[position]);
        slideHeading.setText(heading_slide[position]);
        slideDescription.setText(description_slide[position]);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }

}


