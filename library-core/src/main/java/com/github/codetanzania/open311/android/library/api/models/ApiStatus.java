package com.github.codetanzania.open311.android.library.api.models;

/**
 * This is the status object returned from the server. For example:
 *
 *   "status": {
 *      "name": "Open",
 *      "weight": -5,
 *      "color": "#0D47A1",
 *      "_id": "5968b633617399248a4307b9",
 *      "createdAt": "2017-07-14T12:16:51.788Z",
 *      "updatedAt": "2017-07-14T12:16:51.788Z",
 *      "uri": "https://dawasco.herokuapp.com/statuses/5968b633617399248a4307b9"
 *   }
 */

public class ApiStatus {
    private String _id;
    private String name;
    private String color;

    public String getId() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
