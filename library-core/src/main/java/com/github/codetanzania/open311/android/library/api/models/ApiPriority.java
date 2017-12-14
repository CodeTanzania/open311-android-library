package com.github.codetanzania.open311.android.library.api.models;

import android.support.annotation.VisibleForTesting;

/**
 * The server response looks like this:
 * "priority": {
        "name": "Normal",
        "weight": 0,
        "color": "#4CAF50",
        "_id": "5968b63d48dfc224bb477444",
        "createdAt": "2017-07-14T12:17:01.167Z",
        "updatedAt": "2017-07-14T12:17:01.167Z",
        "uri": "https://dawasco.herokuapp.com/priorities/5968b63d48dfc224bb477444"
   }
 */

public class ApiPriority {
    private String _id;
    private String name;
    private String color;
    private int weight;

    public String getId() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public int getWeight() {
        return weight;
    }

    @VisibleForTesting
    void setWeight(int weight) {
        this.weight = weight;
    }
}
