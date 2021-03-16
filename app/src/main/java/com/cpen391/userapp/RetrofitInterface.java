package com.cpen391.userapp;

import com.cpen391.userapp.dashboardFragments.car.allCarsResult;
import com.cpen391.userapp.dashboardFragments.history.historyResult;
import com.cpen391.userapp.dashboardFragments.home.currParkResult;
import com.cpen391.userapp.dashboardFragments.home.meResult;
import com.cpen391.userapp.dashboardFragments.home.meterResult;
import com.cpen391.userapp.dashboardFragments.home.oneCarResult;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

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

    @GET("/api/car/{user_id}")
    Call<allCarsResult> getAllCars (@Path(value = "user_id", encoded = true) String userId, @Header("Authorization") String authHeader);

    @POST("/api/car/{user_id}")
    Call<Void> postCar (@Path(value = "user_id", encoded = true) String userId, @Header("Authorization") String authHeader, @Body HashMap<String, String> map);

    @PUT("/api/car/{user_id}/{car_id}")
    Call<Void> updateCar (@Path(value = "user_id", encoded = true) String userId, @Path(value = "car_id", encoded = true) String carId, @Header("Authorization") String authHeader, @Body HashMap<String, String> map);

    @DELETE("/api/car/{user_id}/{car_id}")
    Call<Void> deleteCar (@Path(value = "user_id", encoded = true) String userId, @Path(value = "car_id", encoded = true) String carId, @Header("Authorization") String authHeader);

    @GET("/api/car/{user_id}/{car_id}")
    Call<oneCarResult> getOneCar (@Path(value = "user_id", encoded = true) String userId, @Path(value = "car_id", encoded = true) String carId, @Header("Authorization") String authHeader);

    @GET("/api/meter/all")
    Call<List<meterResult>> getMeter ();

    @GET("/api/parking/{user_id}/current")
    Call<currParkResult> getCurrParking (@Path(value = "user_id", encoded = true) String userId, @Header("Authorization") String authHeader);
    @GET("/api/parking/{user_id}/previous")
    Call<historyResult> getPastParking (@Path(value = "user_id", encoded = true) String userId, @Header("Authorization") String authHeader);

}
