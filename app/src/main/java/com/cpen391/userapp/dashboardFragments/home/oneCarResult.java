package com.cpen391.userapp.dashboardFragments.home;
/**
 * Used for GET requests of /user/me
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

    public String getLicensePlate() {
        return licensePlate;
    }
    public String getUserId() {
        return userId;
    }


}
