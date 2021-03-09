package com.cpen391.userapp.dashboardFragments.home;
/**
 * Used for GET requests of /user/me
 */
public class meResult {
    private String email;
    private String id;
    private String firstName;
    private String lastName;
    private String licensePlate;
    private Boolean isAdmin = false;


    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getLicensePlate() {
        return licensePlate;
    }
    public Boolean getAdmin() {
        return isAdmin;
    }
}
