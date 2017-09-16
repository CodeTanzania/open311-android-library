package com.example.majifix311.api.models;

import android.location.Location;
import android.support.annotation.VisibleForTesting;

/**
 * This is returned from the server after submitting a new issue.
 * It's a massive block that looks something like this:
 *
 * {
     "jurisdiction": {
         "code": "H",
         "name": "HQ",
         "phone": "255714999888",
         "email": "N/A",
         "domain": "dawasco.org",
         "about": "Main office for Dar es salaam Water Supply Company(DAWASCO)",
         "address": "N/A",
         "location": {
             "type": "Point",
             "coordinates": [0,0]
         },
         "color": "#143B7F",
         "_id": "592029e5e8dd8e00048c184b",
         "createdAt": "2017-05-20T11:35:02.007Z",
         "updatedAt": "2017-06-16T12:04:10.893Z",
         "longitude": 0,
         "latitude": 0,
         "uri": "https://dawasco.herokuapp.com/jurisdictions/592029e5e8dd8e00048c184b"
     },
     "group": {...},
     "service": {
         "jurisdiction": {...},
         "group": {...},
         "code": "LW",
         "name": "Lack of Water",
         "description": "Lack of Water related service request(issue)",
         "color": "#960F1E",
         "_id": "592029e6e8dd8e00048c1852",
         "createdAt": "2017-05-20T11:35:02.299Z",
         "updatedAt": "2017-05-20T11:35:02.299Z",
         "uri": "https://dawasco.herokuapp.com/services/592029e6e8dd8e00048c1852"
    },
    "call": {...},
     "reporter": {
         "name": "Lally Elias",
         "phone": "255714095061"
     },
     "operator": {...},
     "code": "HLW170078",
     "description": "For almost a week now we have been without water",
     "method": "Call",
     "status": {
         "name": "Open",
         "weight": -5,
         "color": "#0D47A1",
         "_id": "592029e5e8dd8e00048c180d",
         "createdAt": "2017-05-20T11:35:01.059Z",
         "updatedAt": "2017-05-20T11:35:01.059Z",
         "uri": "https://dawasco.herokuapp.com/statuses/592029e5e8dd8e00048c180d"
    },
    "priority": {
         "name": "Low",
         "weight": 0,
         "color": "#1B5E29",
         "_id": "592029e5e8dd8e00048c1816",
         "createdAt": "2017-05-20T11:35:01.586Z",
         "updatedAt": "2017-07-29T19:12:40.178Z",
         "uri": "https://dawasco.herokuapp.com/priorities/592029e5e8dd8e00048c1816"
     },
     "attachments": [{
         "updatedAt": "2017-09-14T16:57:13.006Z",
         "createdAt": "2017-09-14T16:57:13.006Z",
         "name": "Sample Image",
         "caption": "Sample Caption",
         "content": "data:image/png;base64,iVBORw0KGg...",
         "_id": "59bab4e889efab0004700021",
         "storage": "Local",
         "mime": "image/png",
         "uploadedAt": "2017-09-14T16:37:09.807Z",
         "id": "59bab4e889efab0004700021",
         "uri": ""
     }],
     "wasTicketSent": true,
     "_id": "59bab4e889efab0004700020",
     "createdAt": "2017-09-14T16:57:13.008Z",
     "updatedAt": "2017-09-14T16:57:13.310Z",
     "longitude": 0,
     "latitude": 0,
     "uri": "https://dawasco.herokuapp.com/servicerequests/59bab4e889efab0004700020"
 }
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
