package com.cityzipcorp.customer.mvp.login;

import com.cityzipcorp.customer.model.User;

public interface LoginInteractor {
    void submit(String userName, String password, OnUserNotFound userNotFound);

    interface OnUserNotFound {
        void onUserNotFound(String error);

        void onValidationError(String error);

        void onSuccess(User user);
    }
}
