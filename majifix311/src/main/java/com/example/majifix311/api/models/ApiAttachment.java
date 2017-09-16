package com.example.majifix311.api.models;

/**
 * This is the attachment format expected by the MajiFix server.
 *
 * "attachments":[{
 *      "name":"Sample Image",
 *      "caption":"Sample Caption",
 *      "mime":"image/png",
 *      "content":"data:image/png;base64,iVBORw0..."
 *  }]
 *
 *  Where content is a Base64String.
 */

public class ApiAttachment {
    private String name;
    private String caption;
    private String mime = "image/png";
    private String content;

    public ApiAttachment(String name, String caption, String content) {
        this.name = name;
        this.caption = caption;
        this.content = content;
    }
}
