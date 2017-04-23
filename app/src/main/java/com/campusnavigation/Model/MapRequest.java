package com.campusnavigation.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by cc on 23/4/17.
 */

public class MapRequest {

    @SerializedName("latitude")
    private String latitude;
    @SerializedName("longitude")
    private String longitude;
    @SerializedName("signal_entries")
    private ArrayList<Signal> signalEntries= new ArrayList<>();

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public ArrayList<Signal> getSignalEntries() {
        return signalEntries;
    }

    public void setSignalEntries(ArrayList<Signal> signalEntries) {
        this.signalEntries = signalEntries;
    }
}
