package com.campusnavigation.Response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cc on 23/4/17.
 */

public class MapResponse {

    @SerializedName("latitude")
    private String resLatitude;
    @SerializedName("longitude")
    private String resLongitude;
    @SerializedName("message")
    private String message;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResLatitude() {
        return resLatitude;
    }

    public void setResLatitude(String resLatitude) {
        this.resLatitude = resLatitude;
    }

    public String getResLongitude() {
        return resLongitude;
    }

    public void setResLongitude(String resLongitude) {
        this.resLongitude = resLongitude;
    }
}
