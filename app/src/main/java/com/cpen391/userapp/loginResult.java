package com.cpen391.userapp;

/**
 * Used for POST requests of /user/signup an /user/login
 */
public class loginResult {
    private String email;
    private String userId;
    private String token;

    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }
}

