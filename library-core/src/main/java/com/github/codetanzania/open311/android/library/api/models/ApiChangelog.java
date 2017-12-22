package com.github.codetanzania.open311.android.library.api.models;

import com.github.codetanzania.open311.android.library.models.Party;

/**
 * The object sent from the server looks something like this:
 *
 * "changelogs": [
 {
    "changer": {
        "name": "Lally Elias",
        "phone": "255714095061",
        "_id": "5968b64248dfc224bb4774a5",
        "email": "lallyelias87@gmail.com"
    },
    "priority": {
        "name": "Low",
        "weight": -5,
        "color": "#1B5E20",
        "_id": "5968b63c48dfc224bb477443"
    },
    "status": {
        "name": "Open",
        "weight": -5,
        "color": "#0D47A1",
        "_id": "5968b633617399248a4307b9"
    },
    "createdAt": "2017-12-05T12:58:11.325Z",
    "_id": "5a2697e33a2d6b00045e15e3",
    "visibility": "Public",
    "wasNotificationSent": false,
    "shouldNotify": false,
    "id": "5a2697e33a2d6b00045e15e3"
 }
 ],
 */

public class ApiChangelog {
    private Party changer;
    private ApiPriority priority;
    private ApiStatus status;
    private Party assignee;
    private String comment;
    private String createdAt;
    private boolean isPublic;

    public Party getChanger() {
        return changer;
    }

    public ApiPriority getPriority() {
        return priority;
    }

    public ApiStatus getStatus() {
        return status;
    }

    public Party getAssignee() {
        return assignee;
    }

    public String getComment() {
        return comment;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public boolean isPublic() {
        return isPublic;
    }
}
