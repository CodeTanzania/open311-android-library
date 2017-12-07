package com.github.codetanzania.open311.android.library.api.models;

import android.support.annotation.VisibleForTesting;

/**
 * This is the expected service model that is received from the MajiFix server.
 * The full JSON looks like the following:
 * {
    "jurisdiction": {
        "code": "GRZ",
        "name": "Gerezani",
        "phone": "255743451864",
        "email": "callcenter@dawasco.com",
        "domain": "gerezani.dawasco.com",
        "_id": "5968b64148dfc224bb47747e",
        "longitude": 0,
        "latitude": 0,
        "uri": "https://dawasco.herokuapp.com/jurisdictions/5968b64148dfc224bb47747e"
    },
    "group": {
        "jurisdiction": {
             "code": "GRZ",
            "name": "Gerezani",
            "phone": "255743451864",
            "email": "callcenter@dawasco.com",
            "domain": "gerezani.dawasco.com",
            "_id": "5968b64148dfc224bb47747e",
            "longitude": 0,
            "latitude": 0,
            "uri": "https://dawasco.herokuapp.com/jurisdictions/5968b64148dfc224bb47747e"
        },
        "code": "C",
        "name": "Commercial",
        "color": "#06C947",
        "_id": "5968b64148dfc224bb47747f",
        "uri": "https://dawasco.herokuapp.com/servicegroups/5968b64148dfc224bb47747f"
    },
    "code": "BL",
    "name": "Billing",
    "description": "Billing Enquiry related service request(issue)",
    "color": "#0D47A1",
    "priority": {
        "name": "Normal",
        "weight": 0,
        "color": "#4CAF50",
        "_id": "5968b63d48dfc224bb477444",
        "createdAt": "2017-07-14T12:17:01.167Z",
        "updatedAt": "2017-07-14T12:17:01.167Z",
        "uri": "https://dawasco.herokuapp.com/priorities/5968b63d48dfc224bb477444"
    },
    "isExternal": false,
    "_id": "5968b64248dfc224bb477496",
    "createdAt": "2017-07-14T12:17:06.063Z",
    "updatedAt": "2017-07-14T12:17:06.063Z",
    "uri": "https://dawasco.herokuapp.com/services/5968b64248dfc224bb477496"
  },
 */

public class ApiService {
    private String code;
    private String name;
    private String description;
    private String color;
    private String _id;
    private ApiPriority priority;

    @VisibleForTesting
    public ApiService(String id, String name, int priority, String code) {
        _id = id;
        this.name = name;
        this.priority = new ApiPriority();
        this.priority.setWeight(priority);
        this.code = code;
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

    public int getPriority() {
        if (priority == null) {
            return 0;
        }
        return priority.getWeight();
    }
}
