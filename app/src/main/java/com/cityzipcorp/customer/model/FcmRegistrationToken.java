package com.cityzipcorp.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FcmRegistrationToken implements Serializable {

    @SerializedName("device_id")
    private String deviceId;

    @SerializedName("name")
    private String name;

    @SerializedName("registration_id")
    private String registrationId;

    @SerializedName("cloud_message_type")
    private String cloudMessageType;

    @SerializedName("application_id")
    private String applicationId;

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public void setCloudMessageType(String cloudMessageType) {
        this.cloudMessageType = cloudMessageType;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
}