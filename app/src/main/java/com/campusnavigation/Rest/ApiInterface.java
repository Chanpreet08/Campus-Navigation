package com.campusnavigation.Rest;

import com.campusnavigation.Model.MapRequest;
import com.campusnavigation.Response.MapResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by cc on 23/4/17.
 */

public interface ApiInterface {

    @POST("/location")
    Call<MapResponse> getLatLog(@Body MapRequest mapRequest);
}
