package com.cityzipcorp.customer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.cityzipcorp.customer.BuildConfig;
import com.cityzipcorp.customer.R;
import com.cityzipcorp.customer.callbacks.ContractCallback;
import com.cityzipcorp.customer.callbacks.DialogCallback;
import com.cityzipcorp.customer.model.Contract;
import com.cityzipcorp.customer.model.User;
import com.cityzipcorp.customer.model.UserCredential;
import com.cityzipcorp.customer.mvp.login.LoginPresenter;
import com.cityzipcorp.customer.mvp.login.LoginPresenterImpl;
import com.cityzipcorp.customer.mvp.login.LoginView;
import com.cityzipcorp.customer.store.UserStore;
import com.cityzipcorp.customer.utils.LocationUtils;
import com.cityzipcorp.customer.utils.NetworkUtils;
import com.cityzipcorp.customer.utils.SharedPreferenceManager;
import com.cityzipcorp.customer.utils.SharedPreferenceManagerConstant;
import com.cityzipcorp.customer.utils.UiUtils;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.marlonmafra.android.widget.EditTextPassword;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;


public class LoginActivity extends AppCompatActivity implements LoginView {

    @BindView(R.id.edt_user_name)
    protected EditText edtUserName;

    @BindView(R.id.spn_contract)
    protected Spinner spnContract;

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
    private List<Contract> contracts;
    private SharedPreferenceManager sharedPreferenceManager;
    private LocationUtils locationUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        init();
        locationUtils = new LocationUtils(this);
        if (!locationUtils.checkReadPhoneStatePermission()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    locationUtils.requestPhoneStatePermission();
                }
            }, 1000);
        }
    }

    private void getContracts() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            uiUtils.noInternetDialog();
        } else {
            UserStore.getInstance(BuildConfig.BASE_URL_CONTRACT).
                    getContracts(new ContractCallback() {
                        @Override
                        public void onSuccess(List<Contract> contracts) {
                            if (contracts.size() > 0) {
                                setContractAdapter(contracts);
                            } else {
                                uiUtils.notifyDialog("Unable to get contacts, please try again later ", new DialogCallback() {
                                    @Override
                                    public void onYes() {
                                        finish();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Error error) {
                            uiUtils.notifyDialog("Unable to get contacts, please try again later ", new DialogCallback() {
                                @Override
                                public void onYes() {
                                    finish();
                                }
                            });
                        }
                    });
        }
    }

    private void setContractAdapter(List<Contract> contracts) {
        this.contracts = contracts;
        List<String> contractNames = new ArrayList<>();
        contractNames.add("Select");
        for (Contract contract : contracts) {
            contractNames.add(String.format("%s - %s", contract.getCustomerName(), contract.getName()));
        }
        spnContract.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, contractNames));
        spnContract.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.white));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void init() {
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        uiUtils = new UiUtils(this);
        loginPresenter = new LoginPresenterImpl(this);
        sharedPreferenceManager = new SharedPreferenceManager(this);
        checkSession();
        getContracts();
    }

    private void checkSession() {
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(this);
        if (!sharedPreferenceManager.getValue(SharedPreferenceManagerConstant.ACCESS_TOKEN).equalsIgnoreCase("")) {
            startHomeActivity();
        }
    }

    @OnClick(R.id.btn_login)
    void onLogin() {
        if (spnContract.getSelectedItemPosition() > 0) {
            validateCaptcha();
        } else {
            uiUtils.shortToast("Please select contract");
        }
    }

    private void performLogin(String captchaToken) {

        Contract contract = contracts.get(spnContract.getSelectedItemPosition() - 1);
        sharedPreferenceManager.saveValue(SharedPreferenceManagerConstant.BASE_URL, contract.getHost());

        String email = edtUserName.getText().toString();
        String password = edtPassword.getText().toString();
        UserCredential userCredential = new UserCredential();
        userCredential.setEmail(email);
        userCredential.setPassword(password);
        userCredential.setCaptchaResuktToken(captchaToken);
        if (NetworkUtils.isNetworkAvailable(this)) {
            loginPresenter.validate(sharedPreferenceManager.getValue(SharedPreferenceManagerConstant.BASE_URL), userCredential);
        } else {
            uiUtils.noInternetDialog();
        }

    }


    private void validateCaptcha() {
        SafetyNet.getClient(this).verifyWithRecaptcha("6LeCs1wUAAAAAOUJqfa0AbeYWwfyrUiwEX-a-nza")
                .addOnSuccessListener(this, new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                        Log.d("Login", "onSuccess");
                        if (!response.getTokenResult().isEmpty()) {
                            performLogin(response.getTokenResult());

                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            Log.d("Login", "Error message: " +
                                    CommonStatusCodes.getStatusCodeString(apiException.getStatusCode()));
                        } else {
                            Log.d("Login", "Unknown type of error: " + e.getMessage());
                        }
                    }
                });
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
        sharedPreferenceManager.saveValue(SharedPreferenceManagerConstant.ACCESS_TOKEN, accessToken);
    }
}
