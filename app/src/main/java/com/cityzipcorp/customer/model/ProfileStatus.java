package com.cityzipcorp.customer.model;

import com.google.gson.annotations.SerializedName;

public class ProfileStatus {
    @SerializedName("has_specified_gender")
    private boolean isGenderUpdated;

    @SerializedName("has_home_stop")
    private boolean isHomeUpdated;

    @SerializedName("has_specified_shift")
    private boolean isShiftUpdated;

    @SerializedName("has_nodal_stop")
    private boolean isNodalUpdated;

    @SerializedName("has_opted_in")
    private boolean isOptedIn;


    public boolean isOptedIn() {
        return isOptedIn;
    }

    public boolean isGenderUpdated() {
        return isGenderUpdated;
    }

    public boolean isHomeUpdated() {
        return isHomeUpdated;
    }

    public boolean isShiftUpdated() {
        return isShiftUpdated;
    }

    public boolean isNodalUpdated() {
        return isNodalUpdated;
    }
}
