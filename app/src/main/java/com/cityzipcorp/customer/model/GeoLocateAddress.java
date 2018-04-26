package com.cityzipcorp.customer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by anilpathak on 21/11/17.
 */

public class GeoLocateAddress implements Parcelable {

    public static final Parcelable.Creator<GeoLocateAddress> CREATOR = new Parcelable.Creator<GeoLocateAddress>() {
        @Override
        public GeoLocateAddress createFromParcel(Parcel source) {
            return new GeoLocateAddress(source);
        }

        @Override
        public GeoLocateAddress[] newArray(int size) {
            return new GeoLocateAddress[size];
        }
    };
    private GeoJsonPoint point;
    private Address address;

    public GeoLocateAddress() {
    }

    private GeoLocateAddress(Parcel in) {
        this.point = in.readParcelable(GeoJsonPoint.class.getClassLoader());
        this.address = in.readParcelable(Address.class.getClassLoader());
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.point, flags);
        dest.writeParcelable(this.address, flags);
    }

    public GeoJsonPoint getPoint() {
        return point;
    }

    public void setPoint(GeoJsonPoint point) {
        this.point = point;
    }
}
