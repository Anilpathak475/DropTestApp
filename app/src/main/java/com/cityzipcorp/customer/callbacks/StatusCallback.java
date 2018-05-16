package com.cityzipcorp.customer.callbacks;

/**
 * Created by anilpathak on 25/11/17.
 */

public interface StatusCallback {
    void onSuccess();

    void onFailure(Error error);
}
