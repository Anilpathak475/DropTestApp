package com.cityzipcorp.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class NextTrip {
    private Date timestamp;
    @SerializedName("trip_type")
    private String tripType;

    public Date getTimestamp() {
        return timestamp;
    }

    public String getTripType() {
        return tripType;
    }
}
