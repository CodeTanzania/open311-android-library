package com.example.majifix311.api.models;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the response object returned from the server when querying for my reported issues.
 */

public class ApiServiceRequestList {
    private List<ApiServiceRequestGet> mRequests;
    private int mPages;

    public ApiServiceRequestList(ArrayList<ApiServiceRequestGet> requests, int pages) {
        mRequests = requests;
        mPages = pages;
    }

    public List<ApiServiceRequestGet> getRequests() {
        return mRequests;
    }

    public int getPages() {
        return mPages;
    }
}
