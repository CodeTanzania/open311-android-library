package com.github.codetanzania.open311.android.library.models;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.github.codetanzania.open311.android.library.api.ApiModelConverter;
import com.github.codetanzania.open311.android.library.api.models.ApiServiceRequestGet;
import com.github.codetanzania.open311.android.library.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    // Attachment URLs are saved here
    private List<String> mAttachments;

    // for get
    private String mTicketNumber;
    private Status mStatus;
    private Priority mPriority;
    private Calendar mCreatedAt;
    private Calendar mUpdatedAt;
    private Calendar mResolvedAt;
    private List<ChangeLog> mChangeLog;

    private Problem(String username, String phone, String email, String account,
                    Category category, Location location, String address, String description,
                    List<String> attachments) {
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
                    String ticketNumber, Status status, Priority priority,
                    Calendar createdAt, Calendar updatedAt, Calendar resolvedAt,
                    List<String> attachments, List<ChangeLog> changelog) {
        this(username, phone, email, account, category, location, address, description, attachments);

        mTicketNumber = ticketNumber;
        mStatus = status;
        mPriority = priority;
        mCreatedAt = createdAt;
        mUpdatedAt = updatedAt;
        mResolvedAt = resolvedAt;
        mChangeLog = changelog;
    }

    private Problem(Parcel in) {
        mReporter = in.readParcelable(Reporter.class.getClassLoader());
        mCategory = in.readParcelable(Category.class.getClassLoader());
        mLocation = in.readParcelable(Location.class.getClassLoader());
        mAddress = in.readString();
        mDescription = in.readString();

        mAttachments = new ArrayList<>();
        in.readStringList(mAttachments);

        mTicketNumber = in.readString();
        mStatus = in.readParcelable(Status.class.getClassLoader());
        mPriority = in.readParcelable(Priority.class.getClassLoader());

        mCreatedAt = DateUtils.getCalendarFromParcel(in);
        mUpdatedAt = DateUtils.getCalendarFromParcel(in);
        mResolvedAt = DateUtils.getCalendarFromParcel(in);

        mChangeLog = new ArrayList<>();
        in.readTypedList(mChangeLog, ChangeLog.CREATOR);
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

    public boolean isOpen() {
        return mResolvedAt == null || mResolvedAt.getTime() != new Date(0);
    }

    public Status getStatus() {
        return mStatus;
    }

    public Priority getPriority() {
        return mPriority;
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

    public List<String> getAttachments() {
        return mAttachments;
    }

    public boolean hasAttachments() {
        return mAttachments != null && !mAttachments.isEmpty();
    }

    public List<ChangeLog> getChangeLog() {
        return mChangeLog;
    }

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

        dest.writeStringList(mAttachments);
        dest.writeString(mTicketNumber);
        dest.writeParcelable(mStatus, flags);
        dest.writeParcelable(mPriority, flags);
        DateUtils.setCalendarInParcel(dest, mCreatedAt);
        DateUtils.setCalendarInParcel(dest, mUpdatedAt);
        DateUtils.setCalendarInParcel(dest, mResolvedAt);
        dest.writeTypedList(mChangeLog);
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
        List<String> tempAttachments;

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

        public void addAttachment(String url) {
            if (tempAttachments == null) {
                tempAttachments = new ArrayList<>();
            }
            tempAttachments.add(url);
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
                                              String ticketNumber, Status status, Priority priority,
                                              Calendar createdAt, Calendar updatedAt, Calendar resolvedAt,
                                              List<String> attachments, List<ChangeLog> changeLogs) {
            return new Problem(username, phone, email, accountNumber,
                    category, location, address, description, ticketNumber, status, priority,
                    createdAt, updatedAt, resolvedAt, attachments, changeLogs);
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
