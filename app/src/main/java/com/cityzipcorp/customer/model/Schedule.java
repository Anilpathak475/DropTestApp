package com.cityzipcorp.customer.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.cityzipcorp.customer.utils.CalenderUtil;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by anilpathak on 06/11/17.
 */

public class Schedule implements Parcelable {

    public static final Parcelable.Creator<Schedule> CREATOR = new Parcelable.Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel source) {
            return new Schedule(source);
        }

        @Override
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };
    private String id = "";
    private Date currentDate;
    @SerializedName("date")
    private String dateString;
    @SerializedName("out_time_update")
    private TimeUpdate outTimeUpdate;
    @SerializedName("in_time_update")
    private TimeUpdate inTimeUpdate;

    public Schedule() {

    }

    protected Schedule(Parcel in) {
        this.id = in.readString();
        this.dateString = in.readString();
        this.currentDate = (java.util.Date) in.readSerializable();
        this.outTimeUpdate = in.readParcelable(TimeUpdate.class.getClassLoader());
        this.inTimeUpdate = in.readParcelable(TimeUpdate.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.dateString);
        dest.writeSerializable(this.currentDate);
        dest.writeParcelable(this.outTimeUpdate, flags);
        dest.writeParcelable(this.inTimeUpdate, flags);
    }

    public String getId() {
        return id;
    }

    public Date getDate() {
        if (currentDate == null) {
            currentDate = CalenderUtil.getDateFromString(this.dateString);
        }
        return currentDate;
    }

    public void setDate(Date date) {
        this.currentDate = date;
    }

    public TimeUpdate getOutTimeUpdate() {
        return outTimeUpdate;
    }

    public void setOutTimeUpdate(TimeUpdate outTimeUpdate) {
        this.outTimeUpdate = outTimeUpdate;
    }

    public TimeUpdate getInTimeUpdate() {
        return inTimeUpdate;
    }

    public void setInTimeUpdate(TimeUpdate inTimeUpdate) {
        this.inTimeUpdate = inTimeUpdate;
    }
}
