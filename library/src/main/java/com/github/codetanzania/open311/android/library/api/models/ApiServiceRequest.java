package com.github.codetanzania.open311.android.library.api.models;

/**
 * The MajiFix server expects a certain format (in a Post), that is different
 * than what it sends (in a Get). ApiServiceRequest is an abstract class that holds
 * logic that is found in both Get and Post objects.
 */

public abstract class ApiServiceRequest {
    private ApiReporter reporter;
    private String address;
    private ApiLocation location;
    private String description;
    private ApiAttachment[] attachments;

    public ApiReporter getReporter() {
        return reporter;
    }

    public void setReporter(ApiReporter reporter) {
        this.reporter = reporter;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ApiLocation getLocation() {
        return location;
    }

    public void setLocation(ApiLocation location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ApiAttachment[] getAttachments() {
        return attachments;
    }

    public void setAttachments(ApiAttachment[] attachments) {
        this.attachments = attachments;
    }
}
