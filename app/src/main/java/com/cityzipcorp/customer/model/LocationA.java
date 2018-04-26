package com.cityzipcorp.customer.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anilpathak on 14/01/18.
 */

public class LocationA implements Parcelable {
    public static final Parcelable.Creator<LocationA> CREATOR = new Parcelable.Creator<LocationA>() {
        @Override
        public LocationA createFromParcel(Parcel source) {
            return new LocationA(source);
        }

        @Override
        public LocationA[] newArray(int size) {
            return new LocationA[size];
        }
    };
    private String id;
    @SerializedName("point")
    private GeoJsonPoint geoJsonPoint;

    public LocationA() {
    }

    protected LocationA(Parcel in) {
        this.id = in.readString();
        this.geoJsonPoint = in.readParcelable(GeoJsonPoint.class.getClassLoader());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public GeoJsonPoint getGeoJsonPoint() {
        return geoJsonPoint;
    }

    public void setGeoJsonPoint(GeoJsonPoint geoJsonPoint) {
        this.geoJsonPoint = geoJsonPoint;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeParcelable(this.geoJsonPoint, flags);
    }
}
