package com.cityzipcorp.customer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
import com.cityzipcorp.customer.mvp.forgotpassword.ForgotPasswordPresenter;
import com.cityzipcorp.customer.mvp.forgotpassword.ForgotPasswordPresenterImpl;
import com.cityzipcorp.customer.mvp.forgotpassword.ForgotPasswordView;
import com.cityzipcorp.customer.store.UserStore;
import com.cityzipcorp.customer.utils.Constants;
import com.cityzipcorp.customer.utils.NetworkUtils;
import com.cityzipcorp.customer.utils.SharedPreferenceManager;
import com.cityzipcorp.customer.utils.SharedPreferenceManagerConstant;
import com.cityzipcorp.customer.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgotPassword extends AppCompatActivity implements ForgotPasswordView {

    @BindView(R.id.edt_email_id)
    EditText edtEmail;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.spn_contract)
    protected Spinner spnContract;
    String action;
    private UiUtils uiUtils;
    private ForgotPasswordPresenter forgotPasswordPresenter;
    private List<Contract> contracts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        uiUtils = new UiUtils(this);
        getBundleExtra();
        getContracts();
        forgotPasswordPresenter = new ForgotPasswordPresenterImpl(this);
    }

    private void getBundleExtra() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("action")) {
            action = bundle.getString("action");
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
                                uiUtils.getAlertDialogForNotify("Unable to get contacts, please try again later ", new DialogCallback() {
                                    @Override
                                    public void onYes() {
                                        finish();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Error error) {
                            uiUtils.getAlertDialogForNotify("Unable to get contacts, please try again later ", new DialogCallback() {
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

    @OnClick(R.id.btn_submit)
    void onSubmitEmail() {
        if (spnContract.getSelectedItemPosition() > 0) {
            performSubmit();
        } else {
            uiUtils.shortToast("Please Select Location");
        }
    }

    private void performSubmit() {
        String url;
        if (action.equalsIgnoreCase("forgot")) {
            url = Constants.FORGOT_PASSWORD_URL;
        } else {
            url = Constants.NEW_USER_URL;
        }
        if (NetworkUtils.isNetworkAvailable(this)) {
            Contract contract = contracts.get(spnContract.getSelectedItemPosition() - 1);
            new SharedPreferenceManager(this).saveValue(SharedPreferenceManagerConstant.BASE_URL, contract.getHost());
            forgotPasswordPresenter.validateEmail(contract.getHost(), edtEmail.getText().toString(), url);
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
