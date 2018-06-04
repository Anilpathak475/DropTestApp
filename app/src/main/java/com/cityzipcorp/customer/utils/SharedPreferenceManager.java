package com.cityzipcorp.customer.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by anilpathak on 05/09/17.
 */

public class SharedPreferenceManager {
    //default preferences name
    public Context context;

    public SharedPreferenceManager(Context context) {
        this.context = context;
    }

    public void saveValue(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void clearValue(String key) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.remove(key);
        editor.apply();
    }

    public void clearUserData() {

        getSharedPreferences().getAll().clear();
    }

    public String getValue(String key) {
        return getSharedPreferences().getString(key, "");
    }


    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(SharedPreferenceManagerConstant.PREF_NAME, Context.MODE_PRIVATE);
    }
}
