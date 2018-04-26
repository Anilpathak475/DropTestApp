package com.cityzipcorp.customer.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by anilpathak on 25/11/17.
 */

public class NodalStop implements ClusterItem {

    private String id;
    private Stop stop;

    @Override
    public LatLng getPosition() {
        double[] coordinates = stop.getLocationA().getGeoJsonPoint().getCoordinates();
        return new LatLng(coordinates[1], coordinates[0]);
    }

    public Stop getStop() {
        return stop;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getTitle() {
        return stop.getName();
    }

    @Override
    public String getSnippet() {
        return null;
    }
}
