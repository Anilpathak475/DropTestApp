package com.cityzipcorp.customer.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.cityzipcorp.customer.R;
import com.cityzipcorp.customer.base.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by anilpathak on 24/11/17.
 */

public class ChangePasswordFragment extends BaseFragment {

    @BindView(R.id.edt_current_password)
    EditText edtCurrentPassword;

    @BindView(R.id.edt_new_password)
    EditText edtNewPassword;

    @BindView(R.id.edt_confirm_password)
    EditText edtConfirmPassword;

    @BindView(R.id.btn_submit)
    Button btnSubmit;

    String currentPassword;
    public static ChangePasswordFragment getInstance() {
        return new ChangePasswordFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) getActivity().setTitle(getString(R.string.change_password));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        if(bundle != null) {
            if(bundle.containsKey("password")) {
                currentPassword = bundle.getString("password");
            }
        }
        return view;
    }

    @OnClick(R.id.btn_submit)
    public void onUpdatePassword() {
        if(validate()) {
            uiUtils.shortToast("Password updated successfully");
            activity.onBackPressed();
        }
    }

    private boolean validate() {
        if(TextUtils.isEmpty(edtCurrentPassword.getText().toString())) {
            uiUtils.shortToast("Please enter current password");
            return false;
        }

        if(TextUtils.isEmpty(edtNewPassword.getText().toString())) {
            uiUtils.shortToast("Please enter new password");
            return false;
        }
        if(TextUtils.isEmpty(edtConfirmPassword.getText().toString())) {
            uiUtils.shortToast("Please enter confirm password");
            return false;
        }
        if(!edtNewPassword.getText().toString().equalsIgnoreCase(edtConfirmPassword.getText().toString())) {
            uiUtils.shortToast("New and Confirm Password do not match");
            return false;
        }
        return true;
    }
}
