package com.cityzipcorp.customer.mvp.login;


public interface LoginPresenter {
    void validate(String email, String password);

    void onDestroy();
}
