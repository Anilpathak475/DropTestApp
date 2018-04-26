package com.cityzipcorp.customer.callbacks;

import com.cityzipcorp.customer.model.ProfileStatus;

public interface ProfileStatusCallback {
    void onSuccess(ProfileStatus profileStatus);

    void onFailure(Error error);
}
