package com.github.codetanzania.open311.android.library.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * This is used for categories (api calls them 'services').
 */

public class Category implements Parcelable, Comparable<Category> {
    private String mName;
    private String mId;
    private int mPriority;
    private String mCode;

    public Category(String name, String id, int priority, String code) {
        mName = name;
        mId = id;
        mPriority = priority;
        mCode = code;
    }

    protected Category(Parcel in) {
        mName = in.readString();
        mId = in.readString();
        mPriority = in.readInt();
        mCode = in.readString();
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

    public int getPriority() {
        return mPriority;
    }

    public void setPriority(int priority) {
        mPriority = priority;
    }

    public String getCode() {
        return mCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mId);
        dest.writeInt(mPriority);
        dest.writeString(mCode);
    }

    @Override
    public int compareTo(@NonNull Category o) {
        return getPriority() == o.getPriority() ? 0 :
                getPriority() > o.getPriority() ? -1 : 1;
    }
}
