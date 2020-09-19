package com.niccher.areacalc.Utils;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

public class CalcDistance {

    int Radius = 6371;

    public double CalculateDistance(LatLng StartPoint,LatLng EndPoint){
        double lat1 = StartPoint.latitude;
        double lat2 = EndPoint.latitude;
        double lon1 = StartPoint.longitude;
        double lon2 = EndPoint.longitude;

        double dLat = toRadians(lat2 - lat1);
        double dLon = toRadians(lon2 - lon1);
        double a = sin(dLat / 2) * sin(dLat / 2)
                + cos(toRadians(lat1))
                * cos(toRadians(lat2)) * sin(dLon / 2)
                * sin(dLon / 2);

        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;

        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.e("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

}
