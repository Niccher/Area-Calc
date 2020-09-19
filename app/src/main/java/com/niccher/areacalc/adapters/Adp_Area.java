package com.niccher.areacalc.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.niccher.areacalc.R;
import com.niccher.areacalc.mod.Mod_Area;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by niccher on 16/09/20.
 */

public class Adp_Area extends RecyclerView.Adapter<Adp_Area.ViewHolder> {

    private Context mContext;
    List<Mod_Area> mLinks;

    FloatingActionButton popclos;
    ImageView popimg;
    Dialog myDialog;

    public Adp_Area(Context mContext, List<Mod_Area> mLinks) {
        this.mContext = mContext;
        this.mLinks = mLinks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.part_item, parent, false);
        myDialog = new Dialog(mContext);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.poly_date.setText(mLinks.get(position).getgTime());
        holder.poly_objects.setText((mLinks.get(position).getgLatlng()).substring(0,50));
        holder.poly_points.setText(mLinks.get(position).getgPoints()+" Points");
    }

    @Override
    public int getItemCount() {
        return mLinks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView poly_date, poly_points, poly_objects;

        public ViewHolder(View itemView) {
            super(itemView);
            poly_date = itemView.findViewById(R.id.txt_dat);
            poly_points = itemView.findViewById(R.id.txt_title);
            poly_objects = itemView.findViewById(R.id.txt_body);
        }
    }

}