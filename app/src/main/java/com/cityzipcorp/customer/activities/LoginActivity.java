package com.cityzipcorp.customer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cityzipcorp.customer.R;
import com.cityzipcorp.customer.base.BaseActivity;
import com.cityzipcorp.customer.model.User;
import com.cityzipcorp.customer.mvp.login.LoginPresenter;
import com.cityzipcorp.customer.mvp.login.LoginPresenterImpl;
import com.cityzipcorp.customer.mvp.login.LoginView;
import com.cityzipcorp.customer.utils.NetworkUtils;
import com.cityzipcorp.customer.utils.SharedPreferenceManager;
import com.cityzipcorp.customer.utils.UiUtils;
import com.crashlytics.android.Crashlytics;
import com.marlonmafra.android.widget.EditTextPassword;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;


public class LoginActivity extends BaseActivity implements LoginView {

    @BindView(R.id.edt_user_name)
    protected EditText edtUserName;

    @BindView(R.id.edt_password)
    protected EditTextPassword edtPassword;

    @BindView(R.id.txt_forgot_password)
    protected TextView txtForgotPassword;

    @BindView(R.id.btn_login)
    protected Button btnLogin;

    @BindView(R.id.btn_register)
    protected TextView btnRegister;

    private UiUtils uiUtils;
    private LoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        init();
    }

    private void init() {
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        checkSession();

        uiUtils = new UiUtils(this);
        loginPresenter = new LoginPresenterImpl(this);
    }

    private void checkSession() {
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(this);
        if (!sharedPreferenceManager.getAccessToken().equalsIgnoreCase("")) {
            startHomeActivity();
        }
    }

    @OnClick(R.id.btn_login)
    void onLogin() {
        String email = edtUserName.getText().toString();
        String password = edtPassword.getText().toString();
        if (NetworkUtils.isNetworkAvailable(this)) {
            loginPresenter.validate(email, password);
        } else {
            uiUtils.shortToast("No Internet!");
        }
    }

    @OnClick(R.id.txt_forgot_password)
    void forgotPassword() {
        Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
        intent.putExtra("action", "forgot");
        startActivity(intent);
    }

    @OnClick(R.id.btn_register)
    void register() {
        Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
        intent.putExtra("action", "register");
        startActivity(intent);
    }

    private void startHomeActivity() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
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
    public void setError(String errorMessage) {
        uiUtils.shortToast(errorMessage);
    }

    @Override
    public void success(User user) {
        saveUserData(user.getToken());
        startHomeActivity();

    }

    private void saveUserData(String accessToken) {
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(this);
        sharedPreferenceManager.saveAccessToken(accessToken);
    }
}
