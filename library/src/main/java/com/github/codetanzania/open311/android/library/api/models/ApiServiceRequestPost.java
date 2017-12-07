package com.github.codetanzania.open311.android.library.api.models;

/**
 * This is an problem that is properly formatted to post to the MajiFix server.
 * To post: JSON Should look like this example:
 *
     * { "reporter": {
     *      "name": "Lally Elias",
     *      "phone": "255714095061"
     *   },
     *   "service": "592029e6e8dd8e00048c1852", // service.id
     *   "address": "Karume Sokoni",
     *   "location" : {
     *      "coordinates" : [0,0] // double lat, double long
     *   },
     *   "description": "For almost a week now we have been without water",
     *   "attachments":[{
     *      "name":"Sample Image",
     *      "caption":"Sample Caption",
     *      "mime":"image/png",
     *      "content":"data:image/png;base64,iVBORw0..."
     *  }]
     * }
 */

public class ApiServiceRequestPost extends ApiServiceRequest {
    private String service;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}
