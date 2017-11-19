package com.example.majifix311.api.models;

import java.util.List;

/**
 * Created by Dave - Work on 11/18/2017.
 */

public class ApiServiceRequestGetMany {
    List<ApiServiceRequestGet> servicerequests;

    public List<ApiServiceRequestGet> getServicerequests() {
        return servicerequests;
    }

    public void setServicerequests(List<ApiServiceRequestGet> servicerequests) {
        this.servicerequests = servicerequests;
    }
}
