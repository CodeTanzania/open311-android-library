package com.example.majifix311.models;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.example.majifix311.api.ApiModelConverter;
import com.example.majifix311.api.models.ApiAttachment;
import com.example.majifix311.api.models.ApiServiceRequestGet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.text.TextUtils.isEmpty;

/**
 * This is the model used for municipal problems, such as water leakages and/or lack of water.
 */

public class Problem implements Parcelable {
    // TODO add posted status: SAVED, REPORTED

    // for post
    private Reporter mReporter;
    private Category mCategory;
    private Location mLocation;
    private String mAddress;
    private String mDescription;
    private List<Attachment> mAttachments; //TODO Implement

    // for get
    private String mTicketNumber;
    private Status mStatus;
    private Calendar mCreatedAt;
    private Calendar mUpdatedAt;
    private Calendar mResolvedAt;
//    private List<Comment> mComments; //TODO Implement

    private Problem(String username, String phone, String email, String account,
                    Category category, Location location, String address, String description,
                    List<Attachment> attachments) {
        mReporter = new Reporter();
        mReporter.setName(username);
        mReporter.setPhone(phone);
        mReporter.setEmail(email);
        mReporter.setAccount(account);

        mCategory = category;
        mLocation = location;
        mAddress = address;
        mDescription = description;

        mAttachments = attachments;
    }

    private Problem(String username, String phone, String email, String account,
                    Category category, Location location, String address, String description,
                    String ticketNumber, Status status, Calendar createdAt, Calendar updatedAt,
                    Calendar resolvedAt, List<Attachment> attachments) {
        this(username, phone, email, account, category, location, address, description, attachments);

        mTicketNumber = ticketNumber;
        mStatus = status;
        mCreatedAt = createdAt;
        mUpdatedAt = updatedAt;
        mResolvedAt = resolvedAt;
    }

    private Problem(Parcel in) {
        mReporter = in.readParcelable(Reporter.class.getClassLoader());
        mCategory = in.readParcelable(Category.class.getClassLoader());
        mLocation = in.readParcelable(Location.class.getClassLoader());
        mAddress = in.readString();
        mDescription = in.readString();

        mAttachments = new ArrayList<>();
        in.readList(mAttachments, Attachment.class.getClassLoader());

        mTicketNumber = in.readString();
        mStatus = in.readParcelable(Status.class.getClassLoader());

        long mills = in.readLong();
        if (mills != -1) {
            mCreatedAt = Calendar.getInstance();
            mCreatedAt.setTimeInMillis(mills);
        }
        mills = in.readLong();
        if (mills != -1) {
            mUpdatedAt = Calendar.getInstance();
            mUpdatedAt.setTimeInMillis(mills);
        }
        mills = in.readLong();
        if (mills != -1) {
            mResolvedAt = Calendar.getInstance();
            mResolvedAt.setTimeInMillis(mills);
        }
//        mComments = in.readList(Comment.class.getClassLoader());
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

    public Reporter getReporter() {
        return mReporter;
    }

    public String getUsername() {
        return mReporter == null ? null : mReporter.getName();
    }

    public String getPhoneNumber() {
        return mReporter == null ? null : mReporter.getPhone();
    }

    public String getEmail() {
        return mReporter == null ? null : mReporter.getEmail();
    }

    public String getAccount() {
        return mReporter == null ? null : mReporter.getAccount();
    }

    public Category getCategory() {
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

    public String getTicketNumber() {
        return mTicketNumber;
    }

    public Status getStatus() {
        return mStatus;
    }

    public Calendar getCreatedAt() {
        return mCreatedAt;
    }

    public Calendar getUpdatedAt() {
        return mUpdatedAt;
    }

    public Calendar getResolvedAt() {
        return mResolvedAt;
    }

    public List<Attachment> getAttachments() {
        return mAttachments;
    }

//    public List<Comment> getComments() {
//        return mComments;
//    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mReporter, flags);
        dest.writeParcelable(mCategory, flags);
        dest.writeParcelable(mLocation, flags);
        dest.writeString(mAddress);
        dest.writeString(mDescription);

        dest.writeList(mAttachments);
        dest.writeString(mTicketNumber);
        dest.writeParcelable(mStatus, flags);
        dest.writeLong(mCreatedAt == null ? -1 : mCreatedAt.getTimeInMillis());
        dest.writeLong(mUpdatedAt == null ? -1 : mUpdatedAt.getTimeInMillis());
        dest.writeLong(mResolvedAt == null ? -1 : mResolvedAt.getTimeInMillis());
//        dest.writeList(mComments, flags);
    }

    public static class Builder {
        InvalidCallbacks mListener;

        String tempUsername;
        String tempPhone;
        String tempEmail;
        String tempAccount;
        Category tempCategory;
        Location tempLocation;
        String tempAddress;
        String tempDescription;
        List<Attachment> tempAttachments;

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

        public void setEmail(String email) {
            if (email == null) {
                return;
            }
            tempEmail = email.trim();
        }

        public boolean isValidEmail(String email) {
            return !TextUtils.isEmpty(email)
                    && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }

        public void setAccountNumber(String accountNumber) {
            if (accountNumber == null) {
                return;
            }
            tempAccount = accountNumber.trim();
        }

        public void setCategory(Category category) {
            if (category == null) {
                return;
            }
            tempCategory = category;
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

        public void addAttachment(Attachment attachment) {
            if (tempAttachments == null) {
                tempAttachments = new ArrayList<>();
            }
            tempAttachments.add(attachment);
        }

        public Problem build() {
//            return new Problem(tempUsername, tempPhone, tempCategory,
//                    tempLocation, tempAddress, tempDescription);
            return validate() ? new Problem(tempUsername, tempPhone, tempEmail, tempAccount,
                    tempCategory, tempLocation, tempAddress, tempDescription, tempAttachments) : null;
        }

        public Problem buildWithoutValidation(String username, String phone, String email,
                                              String accountNumber, Category category,
                                              Location location, String address, String description,
                                              String ticketNumber, Status status,
                                              Calendar createdAt, Calendar updatedAt, Calendar resolvedAt,
                                              List<Attachment> attachments) {
            return new Problem(username, phone, email, accountNumber,
                    category, location, address, description, ticketNumber, status,
                    createdAt, updatedAt, resolvedAt, attachments);
        }

        // TODO Does this go here?
        public Problem build(ApiServiceRequestGet response) {
            System.out.println("Converting ApiServiceResponseGet into Problem. \n" + response);
            if (response == null) {
                return null;
            }
            tempUsername = response.getReporter().getName();
            tempPhone = response.getReporter().getPhone();
            tempEmail = response.getReporter().getEmail();
            tempAccount = response.getReporter().getAccount();
            tempCategory = ApiModelConverter.convert(response.getService());
            if (response.getLocation() != null) {
                tempLocation = new Location("");
                tempLocation.setLatitude(response.getLocation().getLatitude());
                tempLocation.setLongitude(response.getLocation().getLongitude());
            }
            tempAddress = response.getAddress();
            tempDescription = response.getDescription();

            // TODO: Is there a better way to set these fields?
            String ticketNumber = response.getTicketId();
            Status status = new Status(response.isOpen(),
                    response.getStatus().getName(), response.getStatus().getColor());
            List<Attachment> attachments = ApiModelConverter.convert(response.getAttachments());

            return new Problem(tempUsername, tempPhone, tempEmail, tempAccount, tempCategory,
                    tempLocation, tempAddress, tempDescription, ticketNumber, status,
                    response.getCreatedAt(), response.getUpdatedAt(), response.getResolvedAt(),
                    attachments);
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
            if (tempCategory == null) {
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
