package com.example.majifix311.api.models;

import java.util.List;

/**
 * Created by Dave - Work on 11/18/2017.
 */

public class ApiServiceRequestGetMany {
    List<ApiServiceRequestGet> servicerequests;
    int pages;

    public List<ApiServiceRequestGet> getServicerequests() {
        return servicerequests;
    }

    public int getPages() {
        return pages;
    }

    public void setServicerequests(List<ApiServiceRequestGet> servicerequests) {
        this.servicerequests = servicerequests;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }
}
