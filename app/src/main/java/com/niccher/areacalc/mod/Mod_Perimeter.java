package com.niccher.areacalc.mod;

/**
 * Created by niccher on 04/06/19.
 */

public class Mod_Perimeter {
    String gUid, gTime, gLatlng, gPoints, gPerimeter;

    public Mod_Perimeter() {
    }

    public Mod_Perimeter(String gUid, String gTime, String gLatlng, String gPoints, String gPerimeter) {
        this.gUid = gUid;
        this.gTime = gTime;
        this.gLatlng = gLatlng;
        this.gPoints = gPoints;
        this.gPerimeter = gPerimeter;
    }

    public String getgUid() {
        return gUid;
    }

    public void setgUid(String gUid) {
        this.gUid = gUid;
    }

    public String getgTime() {
        return gTime;
    }

    public void setgTime(String gTime) {
        this.gTime = gTime;
    }

    public String getgLatlng() {
        return gLatlng;
    }

    public void setgLatlng(String gLatlng) {
        this.gLatlng = gLatlng;
    }

    public String getgPoints() {
        return gPoints;
    }

    public void setgPoints(String gPoints) {
        this.gPoints = gPoints;
    }

    public String getgPerimeter() {
        return gPerimeter;
    }

    public void setgPerimeter(String gPerimeter) {
        this.gPerimeter = gPerimeter;
    }
}