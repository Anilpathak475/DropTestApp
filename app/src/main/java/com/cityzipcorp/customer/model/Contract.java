package com.cityzipcorp.customer.model;

import com.google.gson.annotations.SerializedName;

public class Contract {
    private String host;
    @SerializedName("customer_name")
    private String customerName;
    private String name;

    public String getHost() {
        return host;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getName() {
        return name;
    }
}
