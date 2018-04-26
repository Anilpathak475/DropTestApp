package com.cityzipcorp.customer.mvp.forgotpassword;

public interface ForgotPasswordView {
    void showProgress();

    void hideProgress();

    void setEmailError(String errorMessage);

    void success();
}
