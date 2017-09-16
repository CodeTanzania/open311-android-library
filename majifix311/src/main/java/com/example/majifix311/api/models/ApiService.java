package com.example.majifix311.api.models;

import android.support.annotation.VisibleForTesting;

/**
 * This is the expected service model that is received from the MajiFix server.
 * The full JSON looks like the following:
 *
 * "service": {
        "jurisdiction": {
            "code": "H",
            "name": "HQ",
            "phone": "255714999888",
            "email": "N/A",
            "domain": "dawasco.org",
            "_id": "592029e5e8dd8e00048c184b",
            "longitude": 0,
            "latitude": 0,
            "uri": "https://dawasco.herokuapp.com/jurisdictions/592029e5e8dd8e00048c184b"
        },
        "group": {
            "code": "N",
            "name": "Non Commercial",
            "color": "#960F1E",
            "_id": "592029e6e8dd8e00048c184d",
            "uri": "https://dawasco.herokuapp.com/servicegroups/592029e6e8dd8e00048c184d"
        },
        "code": "LW",
        "name": "Lack of Water",
        "description": "Lack of Water related service request(issue)",
        "color": "#960F1E",
        "_id": "592029e6e8dd8e00048c1852",
        "createdAt": "2017-05-20T11:35:02.299Z",
        "updatedAt": "2017-05-20T11:35:02.299Z",
        "uri": "https://dawasco.herokuapp.com/services/592029e6e8dd8e00048c1852"
    }
 */

public class ApiService {
    private String code;
    private String name;
    private String description;
    private String color;
    private String _id;

    @VisibleForTesting
    ApiService(String id) {
        _id = id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getColor() {
        return color;
    }

    public String getId() {
        return _id;
    }
}
