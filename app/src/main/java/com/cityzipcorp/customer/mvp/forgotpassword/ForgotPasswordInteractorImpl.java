package com.cityzipcorp.customer.mvp.forgotpassword;

import com.cityzipcorp.customer.callbacks.StatusCallback;
import com.cityzipcorp.customer.store.ForgotPasswordStore;

public class ForgotPasswordInteractorImpl implements ForgotPasswordInteractor {
    @Override
    public void submit(final String email, String action, final OnEmailNotFound listener) {
        ForgotPasswordStore.getInstance().forgotPassword(email, action, new StatusCallback() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onFailure(Error error) {
                listener.onEmailNotFound(error.getLocalizedMessage());
            }
        });
    }
}
