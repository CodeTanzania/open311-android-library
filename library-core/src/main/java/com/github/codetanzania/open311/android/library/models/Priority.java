package com.github.codetanzania.open311.android.library.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This is used to mark Problem priority.
 */

public class Priority implements Parcelable {
    private String id;
    private String name;
    private String color;
    private int weight;

    public Priority(String id, String name, String color, int weight) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.weight = weight;
    }

    private Priority(Parcel in) {
        id = in.readString();
        name = in.readString();
        color = in.readString();
        weight = in.readInt();
    }

    public static final Creator<Priority> CREATOR = new Creator<Priority>() {
        @Override
        public Priority createFromParcel(Parcel in) {
            return new Priority(in);
        }

        @Override
        public Priority[] newArray(int size) {
            return new Priority[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(color);
        parcel.writeInt(weight);
    }
}
