package com.cityzipcorp.customer.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by anilpathak on 06/11/17.
 */

public class TimeUpdate implements Parcelable {

    public static final Parcelable.Creator<TimeUpdate> CREATOR = new Parcelable.Creator<TimeUpdate>() {
        @Override
        public TimeUpdate createFromParcel(Parcel source) {
            return new TimeUpdate(source);
        }

        @Override
        public TimeUpdate[] newArray(int size) {
            return new TimeUpdate[size];
        }
    };
    private Date timestamp;
    private String id;
    private String reason;
    @SerializedName("is_cancelled")
    private boolean isCancelled;

    protected TimeUpdate(Parcel in) {
        this.timestamp = (Date) in.readSerializable();
        this.id = in.readString();
        this.isCancelled = in.readByte() != 0;
        this.reason = in.readString();
    }

    public TimeUpdate() {

    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean is_cancelled) {
        this.isCancelled = is_cancelled;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.timestamp);
        dest.writeString(this.id);
        dest.writeByte(this.isCancelled ? (byte) 1 : (byte) 0);
        dest.writeString(this.reason);
    }
}
