package com.cpen391.userapp.dashboardFragments.home;
/**
 * Used for GET requests of "/api/car/{user_id}/{car_id}"
 */
public class oneCarResult {
    private String carName;
    private String licensePlate;
    private String userId;
    private String id;


    public String getCarName() {
        return carName;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }


}
