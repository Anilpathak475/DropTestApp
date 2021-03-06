package com.cityzipcorp.customer.mvp.forgotpassword;

public interface ForgotPasswordInteractor {
    void submit(String baseUrl, String userName, String action, OnEmailNotFound emailNotFound);

    interface OnEmailNotFound {
        void onEmailNotFound(String error);

        void onSuccess();
    }
}
