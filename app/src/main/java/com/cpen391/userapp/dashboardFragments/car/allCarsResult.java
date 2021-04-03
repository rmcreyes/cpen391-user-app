package com.cpen391.userapp.dashboardFragments.car;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Used for GET requests of "/api/car/{user_id}"
 */
public class allCarsResult {
    private ArrayList<HashMap<String,String>> cars;

    public int getSize() {
        return cars.size();
    }

    public ArrayList<HashMap<String, String>> getCars() {
        return cars;
    }

}



