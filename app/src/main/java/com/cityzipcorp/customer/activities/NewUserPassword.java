package com.cityzipcorp.customer.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.cityzipcorp.customer.R;
import com.cityzipcorp.customer.callbacks.DialogCallback;
import com.cityzipcorp.customer.model.SetNewPassword;
import com.cityzipcorp.customer.mvp.setpassword.SetNewPasswordPresenter;
import com.cityzipcorp.customer.mvp.setpassword.SetNewPasswordPresenterImpl;
import com.cityzipcorp.customer.mvp.setpassword.SetNewPasswordView;
import com.cityzipcorp.customer.utils.Constants;
import com.cityzipcorp.customer.utils.NetworkUtils;
import com.cityzipcorp.customer.utils.SharedPreferenceManager;
import com.cityzipcorp.customer.utils.SharedPreferenceManagerConstant;
import com.cityzipcorp.customer.utils.UiUtils;
import com.marlonmafra.android.widget.EditTextPassword;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewUserPassword extends AppCompatActivity implements SetNewPasswordView {


    @BindView(R.id.edt_new_password)
    EditTextPassword edtNewPassword;

    @BindView(R.id.edt_confirm_password)
    EditTextPassword edtConfirmPassword;

    @BindView(R.id.btn_submit)
    Button btnSubmit;

    private SetNewPasswordPresenter presenter;
    private UiUtils uiUtils;
    private String resetId;
    private String emailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_password);
        ButterKnife.bind(this);
        presenter = new SetNewPasswordPresenterImpl(this);
        uiUtils = new UiUtils(this);
        getExtras();
    }

    private void getExtras() {
        Intent appLinkIntent = getIntent();

        if (appLinkIntent != null && appLinkIntent.getData() != null && (appLinkIntent.getData().getScheme().equals("http"))) {
            Uri data = appLinkIntent.getData();
            if (data != null) {
                resetId = data.getQueryParameter("reset_id");
                emailId = data.getQueryParameter("email");
            }
        }
    }

    @OnClick(R.id.btn_submit)
    void onSubmitPassword() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            SetNewPassword setNewPassword = new SetNewPassword();
            setNewPassword.setResetId(resetId);
            setNewPassword.setNewPassword(edtNewPassword.getText().toString());
            setNewPassword.setConfirmPassword(edtConfirmPassword.getText().toString());
            setNewPassword.setEmail(emailId);
            presenter.validatePassword(new SharedPreferenceManager(this).
                            getValue(SharedPreferenceManagerConstant.BASE_URL),
                    setNewPassword, Constants.NEW_USER_PASSWORD_CHANGE_URL);
        } else {
            uiUtils.shortToast("No Internet!");
        }
    }


    @Override
    public void showProgress() {
        uiUtils.showProgressDialog();
    }

    @Override
    public void hideProgress() {
        uiUtils.dismissDialog();
    }

    @Override
    public void setPasswordError(String errorMessage) {
        uiUtils.shortToast(errorMessage);
    }

    @Override
    public void navigateToLogin() {
        uiUtils.getAlertDialogForNotify("Password has been set successfully. click ok login!", new DialogCallback() {
            @Override
            public void onYes() {
                Intent intent = new Intent(NewUserPassword.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }
}