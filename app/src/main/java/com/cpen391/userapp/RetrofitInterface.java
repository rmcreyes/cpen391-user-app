package com.cpen391.userapp;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
/*
* Interface to connect with backend API
*/
public interface RetrofitInterface {
    @POST("/api/user/login")
    Call<loginResult> executeLogin(@Body HashMap<String, String> map);

    @POST("/api/user/signup")
    Call<loginResult> executeSignup (@Body HashMap<String, String> map);
}
