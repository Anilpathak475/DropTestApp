package com.cityzipcorp.customer.mvp.setpassword;

import com.cityzipcorp.customer.model.SetNewPassword;

public interface SetNewPasswordPresenter {
    void validatePassword(SetNewPassword setNewPassword, String url);

    void onDestroy();
}
