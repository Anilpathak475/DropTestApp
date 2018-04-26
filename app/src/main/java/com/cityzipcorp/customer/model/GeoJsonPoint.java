package com.cityzipcorp.customer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by anilpathak on 21/11/17.
 */

public class GeoJsonPoint implements Parcelable {

    public static final Parcelable.Creator<GeoJsonPoint> CREATOR = new Parcelable.Creator<GeoJsonPoint>() {
        @Override
        public GeoJsonPoint createFromParcel(Parcel source) {
            return new GeoJsonPoint(source);
        }

        @Override
        public GeoJsonPoint[] newArray(int size) {
            return new GeoJsonPoint[size];
        }
    };
    private String type;
    private double[] coordinates;

    public GeoJsonPoint(double longitude, double latitude) {
        coordinates = new double[]{longitude, latitude};
        type = "point";
    }

    private GeoJsonPoint(Parcel in) {
        this.type = in.readString();
        this.coordinates = in.createDoubleArray();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeDoubleArray(this.coordinates);
    }

    public double[] getCoordinates() {
        return coordinates;
    }
}
