package com.cityzipcorp.customer.mvp.forgotpassword;

public class ForgotPasswordPresenterImpl implements ForgotPasswordPresenter, ForgotPasswordInteractor.OnEmailNotFound {

    private ForgotPasswordView forgotPasswordView;
    private ForgotPasswordInteractorImpl forgotPasswordInteractor;

    public ForgotPasswordPresenterImpl(ForgotPasswordView forgotPasswordView) {
        this.forgotPasswordView = forgotPasswordView;
        this.forgotPasswordInteractor = new ForgotPasswordInteractorImpl();
    }

    @Override
    public void validateEmail(String baseUrl, String email, String action) {
        if (forgotPasswordView != null) {
            forgotPasswordView.showProgress();
        }
        forgotPasswordInteractor.submit(baseUrl, email, action, this);
    }

    @Override
    public void onDestroy() {
        forgotPasswordView = null;
    }

    @Override
    public void onEmailNotFound(String error) {
        forgotPasswordView.hideProgress();
        forgotPasswordView.setEmailError(error);
    }

    @Override
    public void onSuccess() {
        forgotPasswordView.hideProgress();
        forgotPasswordView.success();
    }
}
