package com.github.codetanzania.open311.android.library.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This contains relevant reporter information, such as name and phone number.
 */

public class Reporter implements Parcelable {
    private String name;
    private String phone;
    private String accountNo;
    private String email;

    public Reporter() {}

    protected Reporter(Parcel in) {
        name = in.readString();
        phone = in.readString();
        accountNo = in.readString();
        email = in.readString();
    }

    public static final Creator<Reporter> CREATOR = new Creator<Reporter>() {
        @Override
        public Reporter createFromParcel(Parcel in) {
            return new Reporter(in);
        }

        @Override
        public Reporter[] newArray(int size) {
            return new Reporter[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAccount() {
        return accountNo;
    }

    public void setAccount(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(accountNo);
        dest.writeString(email);
    }
}
