package com.cityzipcorp.customer.callbacks;

import com.cityzipcorp.customer.model.Reason;

import java.util.List;

/**
 * Created by anilpathak on 09/11/17.
 */

public interface ReasonCallback {
    void onSuccess(List<Reason> reasonList);

    void onFailure(Error error);
}
