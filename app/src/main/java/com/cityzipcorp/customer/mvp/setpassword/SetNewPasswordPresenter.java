package com.cityzipcorp.customer.mvp.setpassword;

import com.cityzipcorp.customer.model.SetNewPassword;

public interface SetNewPasswordPresenter {
    void validatePassword(String baseUrl, SetNewPassword setNewPassword, String url);

    void onDestroy();
}
