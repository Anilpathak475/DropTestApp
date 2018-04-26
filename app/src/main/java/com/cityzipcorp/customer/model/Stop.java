package com.cityzipcorp.customer.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anilpathak on 14/01/18.
 */

public class Stop implements Parcelable {
    public static final Parcelable.Creator<Stop> CREATOR = new Parcelable.Creator<Stop>() {
        @Override
        public Stop createFromParcel(Parcel source) {
            return new Stop(source);
        }

        @Override
        public Stop[] newArray(int size) {
            return new Stop[size];
        }
    };
    private String id;
    private String name;
    private String area;
    @SerializedName("location_a")
    private LocationA locationA;

    public Stop() {
    }

    protected Stop(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.area = in.readString();
        this.locationA = in.readParcelable(LocationA.class.getClassLoader());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public LocationA getLocationA() {
        return locationA;
    }

    public void setLocationA(LocationA locationA) {
        this.locationA = locationA;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.area);
        dest.writeParcelable(this.locationA, flags);
    }
}
