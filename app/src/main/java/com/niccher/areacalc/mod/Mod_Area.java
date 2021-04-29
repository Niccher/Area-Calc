package com.niccher.areacalc.mod;

/**
 * Created by niccher on 04/06/19.
 */

public class Mod_Area {
    String gUid, gTime, gLatlng, gPoints, gArea, gPerimeter;

    public Mod_Area() {
    }

    public Mod_Area(String gUid, String gTime, String gLatlng, String gPoints, String gArea, String gPerimeter) {
        this.gUid = gUid;
        this.gTime = gTime;
        this.gLatlng = gLatlng;
        this.gPoints = gPoints;
        this.gArea = gArea;
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

    public String getgArea() {
        return gArea;
    }

    public void setgArea(String gArea) {
        this.gArea = gArea;
    }

    public String getgPerimeter() {
        return gPerimeter;
    }

    public void setgPerimeter(String gPerimeter) {
        this.gPerimeter = gPerimeter;
    }
}