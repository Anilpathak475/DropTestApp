package com.cityzipcorp.customer.callbacks;

import com.cityzipcorp.customer.model.NodalStop;

import java.util.List;

/**
 * Created by anilpathak on 25/11/17.
 */

public interface NodalStopCallback {
    void onSuccess(List<NodalStop> nodalStopList);

    void onFailure(Error error);
}
