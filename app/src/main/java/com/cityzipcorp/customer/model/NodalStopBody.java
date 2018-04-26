package com.cityzipcorp.customer.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anilpathak on 25/11/17.
 */

public class NodalStopBody implements Parcelable {
    public static final Parcelable.Creator<NodalStopBody> CREATOR = new Parcelable.Creator<NodalStopBody>() {
        @Override
        public NodalStopBody createFromParcel(Parcel source) {
            return new NodalStopBody(source);
        }

        @Override
        public NodalStopBody[] newArray(int size) {
            return new NodalStopBody[size];
        }
    };
    @SerializedName("external_stop_id")
    private String externalStopId;
    private String name;
    private GeoJsonPoint coordinates;

    private NodalStopBody(String externalStopId, String name, GeoJsonPoint coordinates) {
        this.externalStopId = externalStopId;
        this.name = name;
        this.coordinates = coordinates;
    }

    protected NodalStopBody(Parcel in) {
        this.externalStopId = in.readString();
        this.name = in.readString();
        this.coordinates = in.readParcelable(GeoJsonPoint.class.getClassLoader());
    }

    public String getExternalStopId() {
        return externalStopId;
    }

    public String getName() {
        return name;
    }

    public GeoJsonPoint getCoordinates() {
        return coordinates;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.externalStopId);
        dest.writeString(this.name);
        dest.writeParcelable(this.coordinates, flags);
    }

    public static class Builder {
        private String externalStopId;
        private String name;
        private GeoJsonPoint coordinates;

        public Builder setExternalStopId(String externalStopId) {
            this.externalStopId = externalStopId;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setCoordinates(GeoJsonPoint coordinates) {
            this.coordinates = coordinates;
            return this;
        }

        public NodalStopBody build() {
            return new NodalStopBody(externalStopId, name, coordinates);
        }
    }
}
