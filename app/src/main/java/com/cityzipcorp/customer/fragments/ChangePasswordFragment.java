package com.cityzipcorp.customer.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cityzipcorp.customer.R;
import com.cityzipcorp.customer.base.BaseFragment;
import com.cityzipcorp.customer.callbacks.StatusCallback;
import com.cityzipcorp.customer.model.ChangePassword;
import com.cityzipcorp.customer.store.UserStore;
import com.cityzipcorp.customer.utils.NetworkUtils;
import com.marlonmafra.android.widget.EditTextPassword;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by anilpathak on 24/11/17.
 */

public class ChangePasswordFragment extends BaseFragment {

    @BindView(R.id.edt_current_password)
    EditTextPassword edtCurrentPassword;

    @BindView(R.id.edt_new_password)
    EditTextPassword edtNewPassword;

    @BindView(R.id.edt_confirm_password)
    EditTextPassword edtConfirmPassword;

    @BindView(R.id.btn_submit)
    Button btnSubmit;

    public static ChangePasswordFragment getInstance() {
        return new ChangePasswordFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //    if (getActivity() != null) getActivity().setTitle(getString(R.string.change_password));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.btn_submit)
    public void onUpdatePassword() {
        if (NetworkUtils.isNetworkAvailable(activity)) {
            if (validate()) {
                uiUtils.showProgressDialog();
                ChangePassword changePassword = new ChangePassword();
                changePassword.setCurrentPassword(edtCurrentPassword.getText().toString());
                changePassword.setNewPassword(edtNewPassword.getText().toString());
                UserStore.getInstance().changePassword(changePassword, sharedPreferenceUtils.getAccessToken(), new StatusCallback() {
                    @Override
                    public void onSuccess() {
                        uiUtils.dismissDialog();
                        uiUtils.shortToast("Password changed successfully");
                        activity.onBackPressed();
                    }

                    @Override
                    public void onFailure(Error error) {
                        uiUtils.dismissDialog();
                        uiUtils.shortToast(error.getMessage());
                    }
                });
            }
        } else {
            uiUtils.shortToast("No Internet!");
        }
    }


    private boolean validate() {
        if (TextUtils.isEmpty(edtCurrentPassword.getText().toString())) {
            uiUtils.shortToast("Please enter current password");
            return false;
        }

        if (TextUtils.isEmpty(edtNewPassword.getText().toString())) {
            uiUtils.shortToast("Please enter new password");
            return false;
        }
        if (TextUtils.isEmpty(edtConfirmPassword.getText().toString())) {
            uiUtils.shortToast("Please enter confirm password");
            return false;
        }
        if (!edtNewPassword.getText().toString().equalsIgnoreCase(edtConfirmPassword.getText().toString())) {
            uiUtils.shortToast("New and Confirm Password do not match");
            return false;
        }
        return true;
    }
}
