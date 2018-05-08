package com.cityzipcorp.customer.model;

import com.google.gson.annotations.SerializedName;

public class Area {

    @SerializedName("name")
    String areaName;

    public String getAreaName() {
        return areaName;
    }


}
