package com.cpen391.userapp;

import com.cpen391.userapp.dashboardFragments.home.meResult;
import com.cpen391.userapp.loginResult;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
* Define the interface to connect with backend API
**/
public interface RetrofitInterface {
    @POST("/api/user/login")
    Call<loginResult> executeLogin(@Body HashMap<String, String> map);

    @POST("/api/user/signup")
    Call<loginResult> executeSignup (@Body HashMap<String, String> map);

    @GET("/api/user/me")
    Call<meResult> executeMe (@Header("Authorization") String authHeader);

    @PUT("/api/user/me")
    Call<Void> updateProfile (@Body HashMap<String, String> map, @Header("Authorization") String authHeader);
}
