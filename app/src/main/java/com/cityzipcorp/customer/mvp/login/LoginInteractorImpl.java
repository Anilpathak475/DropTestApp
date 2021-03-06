package com.cityzipcorp.customer.mvp.login;

import android.text.TextUtils;

import com.cityzipcorp.customer.callbacks.UserCallback;
import com.cityzipcorp.customer.model.User;
import com.cityzipcorp.customer.model.UserCredential;
import com.cityzipcorp.customer.store.UserStore;

public class LoginInteractorImpl implements LoginInteractor {
    @Override
    public void submit(String baseUrl, UserCredential userCredential, final OnUserNotFound listener) {
        if (validate(userCredential.getEmail(), userCredential.getPassword())) {
            UserStore.getInstance(baseUrl, "").login(userCredential, new UserCallback() {
                @Override
                public void onSuccess(User user) {
                    listener.onSuccess(user);
                }

                @Override
                public void onFailure(Error error) {
                    listener.onUserNotFound(error.getLocalizedMessage());
                }
            });
        } else {
            listener.onValidationError("Please enter proper details");
        }
    }

    private boolean validate(String userName, String password) {
        if (TextUtils.isEmpty(userName)) {

            return false;
        }
        return !TextUtils.isEmpty(password);
    }
}
