package com.cpen391.userapp.dashboardFragments.home;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Used for GET requests of /meter/all
 */
import java.util.List;
/**
 * Used for requests of "/api/meter/all" and "/api/meter/{meter_id}/reset"
 */
public class meterResult {

    private String id;
    private String isOccupied;
    private String unitPrice;
    private String isConfirmed;
    private String updatedAt;

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
        return updatedAt;
    }

    public String getIsConfirmed() {
        return isConfirmed;
    }
}


