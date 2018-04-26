package com.cityzipcorp.customer.mvp.setpassword;

public interface SetNewPasswordView {
    void showProgress();

    void hideProgress();

    void setPasswordError(String errorMessage);

    void navigateToLogin();
}
