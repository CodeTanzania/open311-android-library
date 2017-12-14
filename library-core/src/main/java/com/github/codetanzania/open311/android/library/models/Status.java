package com.github.codetanzania.open311.android.library.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * This is used to mark issue status.
 */

public class Status implements Parcelable {

//    @Retention(RetentionPolicy.SOURCE)
//    @IntDef({OPEN, CLOSED})
//    public @interface Type {}
//    public static final int OPEN = 0;
//    public static final int CLOSED = 1;

    private String mId;
    private String mName;
    private String mColor;

    public Status(String id, String name, String color){
        this.mId = id;
        this.mName = name;
        this.mColor = color;
    }

    @SuppressWarnings("ResourceType")
    private Status(Parcel in) {
        mId = in.readString();
        mName = in.readString();
        mColor = in.readString();
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getColor() {
        return mColor;
    }

    public static final Parcelable.Creator<Status> CREATOR =
            new Parcelable.Creator<Status>() {
                @Override
                public Status createFromParcel(Parcel in) {
                    return new Status(in);
                }

                @Override
                public Status[] newArray(int size) {
                    return new Status[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mId);
        parcel.writeString(mName);
        parcel.writeString(mColor);
    }
}
