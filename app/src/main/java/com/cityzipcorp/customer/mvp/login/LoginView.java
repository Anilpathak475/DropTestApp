package com.cityzipcorp.customer.mvp.login;

import com.cityzipcorp.customer.model.User;

public interface LoginView {
    void showProgress();

    void hideProgress();

    void setError(String errorMessage);

    void success(User user);
}
