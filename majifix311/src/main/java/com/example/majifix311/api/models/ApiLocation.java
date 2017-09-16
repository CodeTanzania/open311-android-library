package com.example.majifix311.api.models;

/**
 * This is the location object that is expected by the MajiFix server.
 *
 * "location" : {
 *      "coordinates" : [double lat, double long]
 *  }
 */

public class ApiLocation {
    private double[] coordinates = {0,0};

    public ApiLocation(double latitude, double longitude) {
        this.coordinates = new double[] {latitude, longitude};
    }

    public double getLatitude() {
        return coordinates[0];
    }

    public double getLongitude() {
        return coordinates[1];
    }
}
