package com.cityzipcorp.customer.model;

public class SosBody {
    private GeoJsonPoint location;

    public SosBody(GeoJsonPoint location) {
        this.location = location;
    }
}
