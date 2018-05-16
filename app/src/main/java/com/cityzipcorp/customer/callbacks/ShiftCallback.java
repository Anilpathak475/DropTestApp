package com.cityzipcorp.customer.callbacks;

import com.cityzipcorp.customer.model.ShiftTiming;

/**
 * Created by anilpathak on 15/02/18.
 */

public interface ShiftCallback {
    void onSuccess(ShiftTiming shiftTiming);

    void onFailure(Error error);
}
