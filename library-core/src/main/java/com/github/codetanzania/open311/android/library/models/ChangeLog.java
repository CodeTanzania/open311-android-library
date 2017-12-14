package com.github.codetanzania.open311.android.library.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import com.github.codetanzania.open311.android.library.utils.DateUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;

/**
 * This is a comment on a Problem.
 */

public class ChangeLog implements Parcelable {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({COMMENT, STATUS, PRIORITY, ASSIGNMENT})
    @interface Type {}

    static final int COMMENT = 0;
    static final int STATUS = 1;
    static final int PRIORITY = 2;
    static final int ASSIGNMENT = 3;

    @Type private int mChangeType;
    private Party mChanger;
    private Priority mPriority;
    private Status mStatus;
    private Party mAssignee;
    private String mComment;
    private String mCreatedAt;
    private boolean isPublic;

    public ChangeLog(Party changer, Status status, String createdAt, boolean isPublic) {
        this(changer, createdAt, isPublic);
        mStatus = status;
        mChangeType = STATUS;
    }

    public ChangeLog(Party changer, Party assignee, String createdAt, boolean isPublic) {
        this(changer, createdAt, isPublic);
        mAssignee = assignee;
        mChangeType = ASSIGNMENT;
    }

    public ChangeLog(Party changer, Priority priority, String createdAt, boolean isPublic) {
        this(changer, createdAt, isPublic);
        mPriority = priority;
        mChangeType = PRIORITY;
    }

    public ChangeLog(Party changer, String comment, String createdAt, boolean isPublic) {
        this(changer, createdAt, isPublic);
        mComment = comment;
        mChangeType = COMMENT;
    }

    private ChangeLog(Party changer, String createdAt, boolean isVisible) {
        mChanger = changer;
        mCreatedAt = createdAt;
        this.isPublic = isVisible;
    }

    private ChangeLog(Parcel in) {
        mChangeType = in.readInt();
        mChanger = (Party) in.readSerializable();
        mStatus = in.readParcelable(Status.class.getClassLoader());
        mComment = in.readString();
        mPriority = in.readParcelable(Priority.class.getClassLoader());
        mAssignee = (Party) in.readSerializable();
        mCreatedAt = in.readString();
        isPublic = in.readInt() > 0;
    }

    public static final Creator<ChangeLog> CREATOR = new Creator<ChangeLog>() {
        @Override
        public ChangeLog createFromParcel(Parcel in) {
            return new ChangeLog(in);
        }

        @Override
        public ChangeLog[] newArray(int size) {
            return new ChangeLog[size];
        }
    };

    public int getType() {
        return mChangeType;
    }

    public void setType(int mChangeType) {
        this.mChangeType = mChangeType;
    }

    public String getLog() {
        switch (mChangeType) {
            case COMMENT :
                return mComment;
            case STATUS :
                return "Status was changed to: "+mStatus.getName();
            case PRIORITY :
                return "Priority was changed to: "+mPriority.getName();
            case ASSIGNMENT :
                return "Ticket was re-assigned to: "+mAssignee.getName();
        }
        return null;
    }

    public Party getChanger() {
        return mChanger;
    }

    public void setChanger(Party mChanger) {
        this.mChanger = mChanger;
    }

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status mStatus) {
        this.mStatus = mStatus;
    }

    public Party getAssignee() {
        return mAssignee;
    }

    public void setAssignee(Party mAssignee) {
        this.mAssignee = mAssignee;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String mComment) {
        this.mComment = mComment;
    }

    public Priority getPriority() {
        return mPriority;
    }

    public void setPriority(Priority priority) {
        mPriority = priority;
    }

    public Calendar getCreatedAt() {
        return DateUtils.getCalendarFromMajiFixApiString(mCreatedAt);
    }

    public String getCreatedAtString() {
        return mCreatedAt;
    }

    public void setCreatedAt(String mCreatedAt) {
        this.mCreatedAt = mCreatedAt;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setIsVisibile(boolean isVisibile) {
        this.isPublic = isVisibile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mChangeType);
        parcel.writeSerializable(mChanger);
        parcel.writeParcelable(mStatus, flags);
        parcel.writeString(mComment);
        parcel.writeParcelable(mPriority, flags);
        parcel.writeSerializable(mAssignee);
        parcel.writeString(mCreatedAt);
        parcel.writeInt(isPublic ? 1 : 0);
    }
}