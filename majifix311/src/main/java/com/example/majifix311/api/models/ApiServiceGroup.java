package com.example.majifix311.api.models;

import android.support.annotation.VisibleForTesting;

import java.util.List;

/**
 * This is necessary because the server returns an unfortunate format, as demonstrated here:
 *
 *
 */

public class ApiServiceGroup {
    List<ApiService> services;

    public List<ApiService> getServices() {
        return services;
    }

    @VisibleForTesting
    public void setServices(List<ApiService> services) {
        this.services = services;
    }
}
