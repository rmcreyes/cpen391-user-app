package com.cpen391.userapp.dashboardFragments.home;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Used for GET requests of /meter/all
 */
import java.util.List;

public class meterResult {

    private String id;
    private String isOccupied;
    private String unitPrice;
    private String updated;

    public String getMeterNo() {
        return id;
    }

    public String getIsOccupied() {
        return isOccupied;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public String getUpdated() {
        return updated;
    }


}


