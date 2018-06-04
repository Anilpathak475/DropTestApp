package com.cityzipcorp.customer.mvp.login;

import com.cityzipcorp.customer.model.User;
import com.cityzipcorp.customer.model.UserCredential;

public interface LoginInteractor {
    void submit(String baseUrl, UserCredential userCredential, OnUserNotFound userNotFound);

    interface OnUserNotFound {
        void onUserNotFound(String error);

        void onValidationError(String error);

        void onSuccess(User user);
    }
}
