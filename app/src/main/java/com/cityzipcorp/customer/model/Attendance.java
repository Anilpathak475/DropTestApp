package com.cityzipcorp.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by anilpathak on 19/03/18.
 */

public class Attendance {
    @SerializedName("attended_location")
    GeoJsonPoint geoJsonPoint;
    private boolean attended;
    @SerializedName("attended_at")
    private Date attendedAt;

    @SerializedName("vehicle_id")
    private String vehicleId;

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public boolean isAttended() {
        return attended;
    }

    public void setAttended(boolean attended) {
        this.attended = attended;
    }

    public Date getAttendedAt() {
        return attendedAt;
    }

    public void setAttendedAt(Date attendedAt) {
        this.attendedAt = attendedAt;
    }

    public GeoJsonPoint getGeoJsonPoint() {
        return geoJsonPoint;
    }

    public void setGeoJsonPoint(GeoJsonPoint geoJsonPoint) {
        this.geoJsonPoint = geoJsonPoint;
    }


}
