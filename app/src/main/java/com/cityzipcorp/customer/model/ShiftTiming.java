package com.cityzipcorp.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ShiftTiming implements Serializable {

    @SerializedName("in_times")
    private List<String> inTimes;

    @SerializedName("out_times")
    private List<String> outTimes;

    public List<String> getInTimes() {
        return inTimes;
    }

    public List<String> getOutTimes() {
        return outTimes;
    }
}