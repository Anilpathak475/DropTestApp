package com.cityzipcorp.customer.mvp.setpassword;

import com.cityzipcorp.customer.model.SetNewPassword;

public interface SetNewPasswordInteractor {

    void submitPassword(String baseUrl, SetNewPassword setNewPassword, String url, OnPasswordMatchFailedListener listener);

    interface OnPasswordMatchFailedListener {
        void onPasswordError(String error);

        void onSuccess();
    }
}
