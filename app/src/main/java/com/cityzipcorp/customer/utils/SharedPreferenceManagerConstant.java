package com.cityzipcorp.customer.utils;

import com.cityzipcorp.customer.BuildConfig;

public interface SharedPreferenceManagerConstant {
    String PREF_NAME = BuildConfig.APPLICATION_ID + ".Prefs";
    String ACCESS_TOKEN = PREF_NAME + ".AccessToken";
    String IMAGE_STORED = PREF_NAME + ".ImageStored";
    String IMAGE_DATA = PREF_NAME + ".ImageData";
    String FCM_TOKEN = PREF_NAME + ".FcmToken";
    String BASE_URL = PREF_NAME + ".BaseUrl";
}
