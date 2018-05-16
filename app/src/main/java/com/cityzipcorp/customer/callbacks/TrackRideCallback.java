package com.cityzipcorp.customer.callbacks;

import com.cityzipcorp.customer.model.TrackRide;

/**
 * Created by anilpathak on 20/03/18.
 */

public interface TrackRideCallback {
    void onSuccess(TrackRide trackRide);

    void onFailure(Error error);
}
