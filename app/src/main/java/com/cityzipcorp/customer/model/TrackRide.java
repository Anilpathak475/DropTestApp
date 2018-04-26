package com.cityzipcorp.customer.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class TrackRide implements Serializable, Parcelable {

    public static final Parcelable.Creator<TrackRide> CREATOR = new Parcelable.Creator<TrackRide>() {
        @Override
        public TrackRide createFromParcel(Parcel source) {
            return new TrackRide(source);
        }

        @Override
        public TrackRide[] newArray(int size) {
            return new TrackRide[size];
        }
    };
    @SerializedName("service_location")
    private GeoJsonPoint serviceLocation;
    @SerializedName("eta")
    private int eta;
    @SerializedName("vehicle_location")
    private GeoJsonPoint vehicleLocation;

    @SerializedName("updated_at")
    private Date recordedAt;

    public TrackRide() {
    }

    protected TrackRide(Parcel in) {
        this.serviceLocation = in.readParcelable(GeoJsonPoint.class.getClassLoader());
        this.eta = in.readInt();
        this.vehicleLocation = in.readParcelable(GeoJsonPoint.class.getClassLoader());
    }

    public GeoJsonPoint getServiceLocation() {
        return serviceLocation;
    }

    public int getEta() {
        return eta;
    }

    public GeoJsonPoint getVehicleLocation() {
        return vehicleLocation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.serviceLocation, flags);
        dest.writeInt(this.eta);
        dest.writeParcelable(this.vehicleLocation, flags);
    }

    public Date getRecordedAt() {
        return recordedAt;
    }

}