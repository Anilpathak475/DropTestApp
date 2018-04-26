package com.cityzipcorp.customer.fcm;


import android.util.Log;

import com.cityzipcorp.customer.utils.SharedPreferenceManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class InstanceRegistrationIdService extends FirebaseInstanceIdService {

    private static final String TAG = "DriverAppFireBase";

    @Override
    public void onTokenRefresh() {

        //Get hold of the registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //Log the token
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);

    }

    private void sendRegistrationToServer(String token) {
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(InstanceRegistrationIdService.this);
        sharedPreferenceManager.saveFcmToken(token);
    }
}