package com.github.codetanzania.open311.android.library.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This is used to contain attachment information.
 */

public class Attachment implements Parcelable {
    private String name;
    private String caption;
    private String mime = "image/png";
    private String content;

    public Attachment(String name, String caption, String mime, String content) {
        this.name = name;
        this.caption = caption;
        this.mime = mime;
        this.content = content;
    }

    private Attachment(Parcel in) {
        name = in.readString();
        caption = in.readString();
        mime = in.readString();
        content = in.readString();
    }

    public static final Creator<Attachment> CREATOR = new Creator<Attachment>() {
        @Override
        public Attachment createFromParcel(Parcel in) {
            return new Attachment(in);
        }

        @Override
        public Attachment[] newArray(int size) {
            return new Attachment[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getCaption() {
        return caption;
    }

    public String getMime() {
        return mime;
    }

    public String getContent() {
        return content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(caption);
        dest.writeString(mime);
        dest.writeString(content);
    }
}
