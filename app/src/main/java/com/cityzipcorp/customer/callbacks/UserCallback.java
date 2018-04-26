package com.cityzipcorp.customer.callbacks;

import com.cityzipcorp.customer.model.User;

/**
 * Created by anilpathak on 14/11/17.
 */

public interface UserCallback {
    void onSuccess(User user);
    void onFailure(Error error);
}
