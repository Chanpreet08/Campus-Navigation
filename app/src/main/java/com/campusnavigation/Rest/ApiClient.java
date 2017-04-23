package com.campusnavigation.Rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by cc on 23/4/17.
 */

public class ApiClient {

    public static final String  BASE_URL="http://fierce-eyrie-90451.herokuapp.com/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofitClient()
    {
        if(retrofit==null)
        {
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
}
