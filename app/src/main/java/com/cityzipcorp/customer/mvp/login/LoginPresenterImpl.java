package com.cityzipcorp.customer.mvp.login;

import com.cityzipcorp.customer.model.User;

public class LoginPresenterImpl implements LoginPresenter, LoginInteractor.OnUserNotFound {
    private LoginView loginView;
    private LoginInteractorImpl loginPresenter;

    public LoginPresenterImpl(LoginView loginView) {
        this.loginView = loginView;
        this.loginPresenter = new LoginInteractorImpl();
    }

    @Override
    public void validate(String email, String password) {
        loginView.showProgress();
        loginPresenter.submit(email, password, this);
    }

    @Override
    public void onDestroy() {
        loginView = null;
    }

    @Override
    public void onUserNotFound(String error) {
        loginView.hideProgress();
        loginView.setError(error);
    }

    @Override
    public void onValidationError(String error) {
        loginView.hideProgress();
        loginView.setError(error);
    }

    @Override
    public void onSuccess(User user) {
        loginView.hideProgress();
        loginView.success(user);
    }
}
