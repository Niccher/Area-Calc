package com.niccher.areacalc.mod;

/**
 * Created by niccher on 04/06/19.
 */

public class Mod_UserConfig {
    String gUid, gEmail, gUsername, gPhone, gProfile, gCover,gProfilethumb, gCoverthumb;

    public Mod_UserConfig() {
    }

    public Mod_UserConfig(String gUid, String gEmail, String gUsername, String gPhone, String gProfile, String gCover, String gProfilethumb, String gCoverthumb) {
        this.gUid = gUid;
        this.gEmail = gEmail;
        this.gUsername = gUsername;
        this.gPhone = gPhone;
        this.gProfile = gProfile;
        this.gCover = gCover;
        this.gProfilethumb = gProfilethumb;
        this.gCoverthumb = gCoverthumb;
    }

    public String getgUid() {
        return gUid;
    }

    public void setgUid(String gUid) {
        this.gUid = gUid;
    }

    public String getgEmail() {
        return gEmail;
    }

    public void setgEmail(String gEmail) {
        this.gEmail = gEmail;
    }

    public String getgUsername() {
        return gUsername;
    }

    public void setgUsername(String gUsername) {
        this.gUsername = gUsername;
    }

    public String getgPhone() {
        return gPhone;
    }

    public void setgPhone(String gPhone) {
        this.gPhone = gPhone;
    }

    public String getgProfile() {
        return gProfile;
    }

    public void setgProfile(String gProfile) {
        this.gProfile = gProfile;
    }

    public String getgCover() {
        return gCover;
    }

    public void setgCover(String gCover) {
        this.gCover = gCover;
    }

    public String getgProfilethumb() {
        return gProfilethumb;
    }

    public void setgProfilethumb(String gProfilethumb) {
        this.gProfilethumb = gProfilethumb;
    }

    public String getgCoverthumb() {
        return gCoverthumb;
    }

    public void setgCoverthumb(String gCoverthumb) {
        this.gCoverthumb = gCoverthumb;
    }
}