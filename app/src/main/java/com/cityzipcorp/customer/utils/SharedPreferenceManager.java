package com.cityzipcorp.customer.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.cityzipcorp.customer.BuildConfig;

/**
 * Created by anilpathak on 05/09/17.
 */

public class SharedPreferenceManager {
    //default preferences name
    private static final String PREF_NAME = BuildConfig.APPLICATION_ID + ".Prefs";


    private static final String ACCESS_TOKEN = PREF_NAME + ".AccessToken";
    private static final String IMAGE_STORED = PREF_NAME + ".ImageStored";
    private static final String IMAGE_DATA = PREF_NAME + ".ImageData";
    private static final String FCM_TOKEN = PREF_NAME + ".FcmToken";
    private Context context;

    public SharedPreferenceManager(Context context) {
        this.context = context;
    }

    public void saveAccessToken(String token) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(ACCESS_TOKEN, token);
        editor.apply();
    }

    public void clearAccessToken() {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.remove(ACCESS_TOKEN);
        editor.apply();
    }

    public void isImageStored(boolean status) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(IMAGE_STORED, status);
        editor.apply();
    }

    public void saveImageData(String imageData) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(IMAGE_DATA, imageData);
        editor.apply();
    }

    public Boolean getImageStatus() {
        return getSharedPreferences().getBoolean(IMAGE_STORED, false);
    }

    public void saveFcmToken(String token) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(FCM_TOKEN, token);
        editor.apply();
    }

    public void clearFcmToken() {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.remove(FCM_TOKEN);
        editor.apply();
    }

    public void clearImageData() {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.remove(IMAGE_DATA);
        editor.apply();
    }

    public void clearImageStored() {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.remove(IMAGE_STORED);
        editor.apply();
    }

    public String getFcmToken() {
        return getSharedPreferences().getString(FCM_TOKEN, "");
    }


    public String getImageData() {
        return getSharedPreferences().getString(IMAGE_DATA, "");
    }

    public String getAccessToken() {
        return getSharedPreferences().getString(ACCESS_TOKEN, "");
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
}
