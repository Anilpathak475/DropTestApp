package com.cityzipcorp.customer.mvp.setpassword;

import android.text.TextUtils;

import com.cityzipcorp.customer.callbacks.StatusCallback;
import com.cityzipcorp.customer.model.SetNewPassword;
import com.cityzipcorp.customer.store.ForgotPasswordStore;

public class SetNewPasswordInteracterImpl implements SetNewPasswordInteractor {

    @Override
    public void submitPassword(String baseUrl, SetNewPassword setNewPassword, String url, final OnPasswordMatchFailedListener listener) {
        if (validate(setNewPassword.getNewPassword(), setNewPassword.getConfirmPassword(), listener)) {
            ForgotPasswordStore.getInstance(baseUrl).setNewPassword(setNewPassword, url, new StatusCallback() {
                @Override
                public void onSuccess() {
                    listener.onSuccess();
                }

                @Override
                public void onFailure(Error error) {
                    listener.onPasswordError(error.getLocalizedMessage());
                }
            });
        }
    }

    private boolean validate(String newPassword, String confirmPassword, OnPasswordMatchFailedListener listener) {
        if (TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(newPassword)) {
            listener.onPasswordError("Password can not be blank");
            return false;
        }
        if (newPassword.length() < 8 || confirmPassword.length() < 8) {
            listener.onPasswordError("Password must be of 8 characters");
            return false;
        }
        if (!newPassword.equalsIgnoreCase(confirmPassword)) {
            listener.onPasswordError("Password do not matched");
            return false;
        }
        return true;
    }


}
