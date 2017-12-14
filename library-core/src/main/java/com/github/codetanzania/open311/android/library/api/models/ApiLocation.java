package com.github.codetanzania.open311.android.library.api.models;

/**
 * This is the location object that is expected by the MajiFix server.
 *
 * "location" : {
 *      "coordinates" : [double long, double lat]
 *  }
 */

public class ApiLocation {
    private double[] coordinates = {0,0};

    public ApiLocation(double latitude, double longitude) {
        // note reversal
        this.coordinates = new double[] {longitude, latitude};
    }

    public double getLongitude() {
        return coordinates[0];
    }

    public double getLatitude() {
        return coordinates[1];
    }
}
