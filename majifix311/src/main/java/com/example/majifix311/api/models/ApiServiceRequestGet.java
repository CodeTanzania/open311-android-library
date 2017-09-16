package com.example.majifix311.api.models;

import android.location.Location;
import android.support.annotation.VisibleForTesting;

/**
 * This is returned from the server after submitting a new issue.
 */

public class ApiServiceRequestGet extends ApiServiceRequest {
    private ApiService service;

    @VisibleForTesting
    public ApiServiceRequestGet(String username, String phone, String serviceId,
                                double latitude, double longitude, String address,
                                String description) {
        setReporter(new ApiReporter(username, phone));
        service = new ApiService(serviceId);
        setLocation(new ApiLocation(latitude, longitude));
        setAddress(address);
        setDescription(description);
    }

    public ApiService getService() {
        return service;
    }

    @Override
    public String toString() {
        String locationString = getLocation() == null
                ? "0,0" : getLocation().getLatitude()+", "+getLocation().getLongitude();

        return "ApiServiceRequest { " +
            "\n  tempUsername ="+ getReporter().getName() +
            "\n  tempPhone = "+getReporter().getPhone()+
            "\n  tempCategory = "+getService().getName()+
            "\n  tempLocation = "+locationString+
            "\n  tempAddress = "+getAddress()+
            "\n  tempDescription = "+getDescription()+
            "}";
    }
}
