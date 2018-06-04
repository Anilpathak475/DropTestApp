package com.cityzipcorp.customer.mvp.login;


import com.cityzipcorp.customer.model.UserCredential;

public interface LoginPresenter {
    void validate(String baseUrl, UserCredential credential);

    void onDestroy();
}
