package com.example.majifix311;

import android.location.Location;

import static android.text.TextUtils.isEmpty;

/**
 * This is the model used for municipal problems, such as water leakages and/or lack of water.
 */

public class Problem {
    private String mUsername;
    private String mPhone;
    private String mCategory;
    private Location mLocation;
    private String mAddress;
    private String mDescription;

    private Problem(String username, String phone, String category, Location location, String address, String description) {
        mUsername = username;
        mPhone = phone;
        mCategory = category;
        mLocation = location;
        mAddress = address;
        mDescription = description;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getPhoneNumber() {
        return mPhone;
    }

    public String getCategory() {
        return mCategory;
    }

    public Location getLocation() {
        return mLocation;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getDescription() {
        return mDescription;
    }

    public static class Builder {
        InvalidCallbacks mListener;

        String tempUsername;
        String tempPhone;
        String tempCategory;
        Location tempLocation;
        String tempAddress;
        String tempDescription;

        public Builder(InvalidCallbacks listener) {
            mListener = listener;
        }

        public void setUsername(String username) {
            if (username == null) {
                return;
            }
            tempUsername = username.trim();
        }

        public void setPhoneNumber(String phoneNumber) {
            if (phoneNumber == null) {
                return;
            }
            tempPhone = phoneNumber.trim();
        }

        public void setCategory(String category) {
            if (category == null) {
                return;
            }
            tempCategory = category.trim();
        }

        public void setLocation(Location location) {
            if (location == null) {
                return;
            }
            tempLocation = location;
        }

        public void setAddress(String address) {
            if (address == null) {
                return;
            }
            tempAddress = address.trim();
        }

        public void setDescription(String description) {
            if (description == null) {
                return;
            }
            tempDescription = description.trim();
        }

        public Problem build() {
            return validate() ? new Problem(tempUsername, tempPhone, tempCategory,
                    tempLocation, tempAddress, tempDescription) : null;
        }

        private boolean validate() {
            boolean isValid = true;
            if (isEmpty(tempUsername)) {
                mListener.onInvalidUsername();
                isValid = false;
            }
            if (isEmpty(tempPhone)) {
                mListener.onInvalidPhoneNumber();
                isValid = false;
            }
            if (isEmpty(tempCategory)) {
                mListener.onInvalidCategory();
                isValid = false;
            }
            if (tempLocation == null) {
                mListener.onInvalidLocation();
                isValid = false;
            }
            if (isEmpty(tempAddress)) {
                mListener.onInvalidAddress();
                isValid = false;
            }
            if (isEmpty(tempDescription)) {
                mListener.onInvalidDescription();
                isValid = false;
            }
            return isValid;
        }

        public interface InvalidCallbacks {
            void onInvalidUsername();
            void onInvalidPhoneNumber();
            void onInvalidCategory();
            void onInvalidLocation();
            void onInvalidAddress();
            void onInvalidDescription();
        }

    }
}
