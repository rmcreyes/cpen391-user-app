package com.cpen391.userapp.dashboardFragments.history;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Used for GET requests of /api/${userId}
 */
public class historyResult {
    private ArrayList<HashMap<String,String>> previousParkings;

    public int getSize() {
        return previousParkings.size();
    }

    public ArrayList<HashMap<String, String>> getPreviousParkings() {
        return previousParkings;
    }

}



