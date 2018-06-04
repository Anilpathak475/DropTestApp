package com.cityzipcorp.customer.mvp.forgotpassword;

public interface ForgotPasswordPresenter {
    void validateEmail(String baseUrl, String email, String action);

    void onDestroy();
}
