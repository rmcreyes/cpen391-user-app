package com.cpen391.userapp.dashboardFragments.home;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Used for GET requests of "/api/parking/{user_id}/current"
 */
public class currParkResult {
    private ArrayList<HashMap<String,String>> currentParkings;

    public int getSize() {
        return currentParkings.size();
    }

    public ArrayList<HashMap<String, String>> getCurrentParkings() {
        return currentParkings;
    }

}



