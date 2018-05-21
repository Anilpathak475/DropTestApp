package com.cityzipcorp.customer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.cityzipcorp.customer.R;
import com.cityzipcorp.customer.callbacks.DialogCallback;
import com.cityzipcorp.customer.mvp.forgotpassword.ForgotPasswordPresenter;
import com.cityzipcorp.customer.mvp.forgotpassword.ForgotPasswordPresenterImpl;
import com.cityzipcorp.customer.mvp.forgotpassword.ForgotPasswordView;
import com.cityzipcorp.customer.utils.Constants;
import com.cityzipcorp.customer.utils.NetworkUtils;
import com.cityzipcorp.customer.utils.UiUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgotPassword extends AppCompatActivity implements ForgotPasswordView {

    @BindView(R.id.edt_email_id)
    EditText edtEmail;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    String action;
    private UiUtils uiUtils;
    private ForgotPasswordPresenter forgotPasswordPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        uiUtils = new UiUtils(this);
        getBundleExtra();
        forgotPasswordPresenter = new ForgotPasswordPresenterImpl(this);
    }

    private void getBundleExtra() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("action")) {
            action = bundle.getString("action");
        }

    }

    @OnClick(R.id.btn_submit)
    void onSubmitEmail() {
        String url;
        if (action.equalsIgnoreCase("forgot")) {
            url = Constants.FORGOT_PASSWORD_URL;
        } else {
            url = Constants.NEW_USER_URL;
        }
        if (NetworkUtils.isNetworkAvailable(this)) {
            forgotPasswordPresenter.validateEmail(edtEmail.getText().toString(), url);
        } else {
            setEmailError("No Internet!");
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
    public void setEmailError(String errorMessage) {
        uiUtils.shortToast(errorMessage);
    }

    @Override
    public void success() {
        uiUtils.getAlertDialogForNotify("Conformation email has benn sent to your email : " + edtEmail.getText().toString(), new DialogCallback() {
            @Override
            public void onYes() {
                Intent intent = new Intent(ForgotPassword.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}
