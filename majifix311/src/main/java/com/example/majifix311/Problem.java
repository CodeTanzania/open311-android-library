package com.example.majifix311;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.majifix311.api.models.ApiServiceRequestGet;

import static android.text.TextUtils.isEmpty;

/**
 * This is the model used for municipal problems, such as water leakages and/or lack of water.
 */

public class Problem implements Parcelable {
    private String mUsername;
    private String mPhone;
    private String mCategory;
    private Location mLocation;
    private String mAddress;
    private String mDescription;

    private Problem(String username, String phone, String category,
                    Location location, String address, String description) {
        mUsername = username;
        mPhone = phone;
        mCategory = category;
        mLocation = location;
        mAddress = address;
        mDescription = description;
    }

    private Problem(Parcel in) {
        mUsername = in.readString();
        mPhone = in.readString();
        mCategory = in.readString();
        mLocation = in.readParcelable(Location.class.getClassLoader());
        mAddress = in.readString();
        mDescription = in.readString();
    }

    public static final Creator<Problem> CREATOR = new Creator<Problem>() {
        @Override
        public Problem createFromParcel(Parcel in) {
            return new Problem(in);
        }

        @Override
        public Problem[] newArray(int size) {
            return new Problem[size];
        }
    };

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

    public void setLocation(Location location) {
        mLocation = location;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getDescription() {
        return mDescription;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUsername);
        dest.writeString(mPhone);
        dest.writeString(mCategory);
        dest.writeParcelable(mLocation, flags);
        dest.writeString(mAddress);
        dest.writeString(mDescription);
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
            //TODO add back the validation when location is added
//            return new Problem(tempUsername, tempPhone, tempCategory,
//                    tempLocation, tempAddress, tempDescription);
            return validate() ? new Problem(tempUsername, tempPhone, tempCategory,
                    tempLocation, tempAddress, tempDescription) : null;
        }

        // TODO Does this go here?
        public Problem build(ApiServiceRequestGet response) {
            System.out.println("Converting ApiServiceResponseGet into Problem. \n"+response);
            if (response == null) {
                return null;
            }
            tempUsername = response.getReporter().getName();
            tempPhone = response.getReporter().getPhone();
            tempCategory = response.getService().getId();
            if (response.getLocation() != null) {
                tempLocation = new Location("");
                tempLocation.setLatitude(response.getLocation().getLatitude());
                tempLocation.setLongitude(response.getLocation().getLongitude());
            }
            tempAddress = response.getAddress();
            tempDescription = response.getDescription();

            return new Problem(tempUsername, tempPhone, tempCategory,
                    tempLocation, tempAddress, tempDescription);
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
