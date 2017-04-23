package com.campusnavigation.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cc on 23/4/17.
 */

public class Signal {

    @SerializedName("mac")
    private String mac;
    @SerializedName("strength")
    private String strength;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }
}
