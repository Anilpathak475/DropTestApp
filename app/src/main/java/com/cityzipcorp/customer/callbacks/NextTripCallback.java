package com.cityzipcorp.customer.callbacks;

import com.cityzipcorp.customer.model.NextTrip;

public interface NextTripCallback {
    void onSuccess(NextTrip nextTrip);

    void onFailure(Error error);
}
