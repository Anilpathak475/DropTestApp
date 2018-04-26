package com.cityzipcorp.customer.mvp.forgotpassword;

public interface ForgotPasswordPresenter {
    void validateEmail(String email, String action);

    void onDestroy();
}
