package com.cityzipcorp.customerapp.store;

import android.support.annotation.NonNull;

import com.cityzipcorp.customerapp.callbacks.ScheduleCallback;
import com.cityzipcorp.customerapp.clients.ScheduleClient;
import com.cityzipcorp.customerapp.model.Schedule;
import com.cityzipcorp.customerapp.network.ClientGenerator;
import com.cityzipcorp.customerapp.utils.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by anilpathak on 06/11/17.
 */

public class ScheduleStore {

    private static ScheduleStore instance = null;

    private ScheduleStore() {
    }

    public static ScheduleStore getInstance() {
        if (instance == null) {
            instance = new ScheduleStore();
        }

        return instance;
    }

    public void getSchedule(String authToken, final ScheduleCallback scheduleCallback) {
        ScheduleClient scheduleClient= ClientGenerator.createClient(ScheduleClient.class);
        Call<List<Schedule>> call = scheduleClient.getSchedule(getHeader(authToken));
        call.enqueue(new Callback<List<Schedule>>() {
            @Override
            public void onResponse(@NonNull Call<List<Schedule>> call,
                                   @NonNull Response<List<Schedule>> response) {
                if(response.isSuccessful()) {
                    scheduleCallback.onSuccess(response.body());
                } else {
                    scheduleCallback.onFailure(new Error(response.errorBody().toString()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Schedule>> call, @NonNull Throwable t) {
                scheduleCallback.onFailure(new Error(t));
            }
        });

    }

    private Map<String, String> getHeader(String authToken) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(Constants.HEADER_AUTHORIZATION_KEY,
                Constants.HEADER_AUTHORIZATION_VALUE_PREFIX + authToken);
        return headerMap;
    }
}
