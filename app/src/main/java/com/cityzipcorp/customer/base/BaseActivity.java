package com.cityzipcorp.customer.base;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.cityzipcorp.customer.utils.Constants;
import com.cityzipcorp.customer.utils.SharedPreferenceManager;

/**
 * Created by anilpathak on 17/11/17.
 */

public abstract class BaseActivity extends AppCompatActivity  implements Constants{


    protected Activity activity;
    public SharedPreferenceManager sharedPreferenceManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        activity = this;
        sharedPreferenceManager = new SharedPreferenceManager(activity);

    }

    public abstract void setTitle(String title);
}
