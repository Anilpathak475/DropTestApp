package com.cityzipcorp.customer.mvp.setpassword;

import com.cityzipcorp.customer.model.SetNewPassword;

public class SetNewPasswordPresenterImpl implements SetNewPasswordPresenter, SetNewPasswordInteractor.OnPasswordMatchFailedListener {

    private SetNewPasswordView setNewPasswordView;
    private SetNewPasswordInteractor setNewPasswordInteractor;

    public SetNewPasswordPresenterImpl(SetNewPasswordView setNewPasswordView) {
        this.setNewPasswordView = setNewPasswordView;
        this.setNewPasswordInteractor = new SetNewPasswordInteracterImpl();
    }

    @Override
    public void onPasswordError(String errorMessage) {
        if (setNewPasswordView != null) {
            setNewPasswordView.hideProgress();
            setNewPasswordView.setPasswordError(errorMessage);
        }
    }

    @Override
    public void onSuccess() {
        setNewPasswordView.hideProgress();
        setNewPasswordView.navigateToLogin();
    }


    @Override
    public void validatePassword(SetNewPassword setNewPassword, String url) {
        setNewPasswordView.showProgress();
        setNewPasswordInteractor.submitPassword(setNewPassword, url, this);
    }

    @Override
    public void onDestroy() {
        setNewPasswordView = null;
    }
}
