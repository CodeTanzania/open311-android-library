package com.example.majifix311.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This is used for categories (api calls them 'services').
 */

public class Category implements Parcelable {
    private String mName;
    private String mId;

    public Category(String name, String id) {
        mName = name;
        mId = id;
    }

    protected Category(Parcel in) {
        mName = in.readString();
        mId = in.readString();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mId);
    }
}
