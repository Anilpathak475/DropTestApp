package com.cityzipcorp.customer.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.cityzipcorp.customer.utils.Constants;
import com.cityzipcorp.customer.utils.SharedPreferenceManager;
import com.cityzipcorp.customer.utils.UiUtils;

/**
 * Created by Anil .
 */

public class BaseFragment extends Fragment implements Constants{

    protected SharedPreferenceManager sharedPreferenceUtils;
    protected Activity activity;
    protected UiUtils uiUtils;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        sharedPreferenceUtils = new SharedPreferenceManager(activity);
        uiUtils = new UiUtils(activity);
    }

}
