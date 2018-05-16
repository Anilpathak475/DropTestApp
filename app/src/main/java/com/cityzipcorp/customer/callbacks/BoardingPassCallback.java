package com.cityzipcorp.customer.callbacks;

import com.cityzipcorp.customer.model.BoardingPass;

/**
 * Created by anilpathak on 05/02/18.
 */

public interface BoardingPassCallback {
    void onSuccess(BoardingPass boardingPass);

    void onFailure(Error error);
}
