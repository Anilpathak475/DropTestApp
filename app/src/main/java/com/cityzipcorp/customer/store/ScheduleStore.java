package com.cityzipcorp.customer.store;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.cityzipcorp.customer.callbacks.ReasonCallback;
import com.cityzipcorp.customer.callbacks.ScheduleCallback;
import com.cityzipcorp.customer.callbacks.ShiftCallback;
import com.cityzipcorp.customer.clients.ScheduleClient;
import com.cityzipcorp.customer.model.Reason;
import com.cityzipcorp.customer.model.Schedule;
import com.cityzipcorp.customer.model.ShiftTiming;
import com.cityzipcorp.customer.network.ClientGenerator;
import com.cityzipcorp.customer.utils.Constants;
import com.cityzipcorp.customer.utils.NetworkUtils;
import com.cityzipcorp.customer.utils.UiUtils;
import com.cityzipcorp.customer.utils.Utils;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by anilpathak on 06/11/17.
 */

public class ScheduleStore {


    private ClientGenerator clientGenerator;

    private ScheduleStore(String baseUrl) {
        clientGenerator = new ClientGenerator(baseUrl);
    }

    public static ScheduleStore getInstance(String baseUrl) {
        return new ScheduleStore(baseUrl);
    }

    public void getSchedule(String authToken, final ScheduleCallback scheduleCallback) {
        ScheduleClient scheduleClient = clientGenerator.createClient(ScheduleClient.class);
        Call<List<Schedule>> call = scheduleClient.getSchedule(Utils.getHeader(authToken));
        call.enqueue(new Callback<List<Schedule>>() {
            @Override
            public void onResponse(@NonNull Call<List<Schedule>> call,
                                   @NonNull Response<List<Schedule>> response) {
                if (response.isSuccessful()) {
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

    public void getReason(String accessToken, final ReasonCallback reasonCallback) {
        ScheduleClient scheduleClient = clientGenerator.createClient(ScheduleClient.class);
        Call<List<Reason>> call = scheduleClient.getReason(Utils.getHeader(accessToken));
        call.enqueue(new Callback<List<Reason>>() {
            @Override
            public void onResponse(Call<List<Reason>> call, Response<List<Reason>> response) {
                if (response.isSuccessful()) {
                    reasonCallback.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Reason>> call, Throwable t) {
                reasonCallback.onFailure(new Error(t));
            }
        });

    }

    public void updateSchedule(final Activity activity, Schedule schedule, String authToken) {
        UiUtils uiUtils = new UiUtils(activity);
        if (NetworkUtils.isNetworkAvailable(activity)) {

            ScheduleClient scheduleClient = clientGenerator.createClient(ScheduleClient.class);
            Call<ResponseBody> call;
            if (!TextUtils.isEmpty(schedule.getId())) {
                call = scheduleClient.updateSchedule(Constants.HEADER_AUTHORIZATION_VALUE_PREFIX + authToken, schedule.getId(), schedule);
            } else {
                call = scheduleClient.createSchedule(Constants.HEADER_AUTHORIZATION_VALUE_PREFIX + authToken, schedule);
            }
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        activity.setResult(Activity.RESULT_OK, new Intent().putExtra("status", "success"));
                        activity.finish();
                    } else {
                        Toast.makeText(activity, "Error code is " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(activity, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            uiUtils.shortToast("No Internet!");
        }
    }


    public void getShiftTimings(String accessToken, final ShiftCallback shiftCallback) {
        ScheduleClient scheduleClient = clientGenerator.createClient(ScheduleClient.class);
        Call<ShiftTiming> call = scheduleClient.getShiftTimings(Utils.getHeader(accessToken));
        call.enqueue(new Callback<ShiftTiming>() {
            @Override
            public void onResponse(Call<ShiftTiming> call, Response<ShiftTiming> response) {
                if (response.isSuccessful()) {
                    shiftCallback.onSuccess(response.body());
                } else {
                    shiftCallback.onFailure(new Error("Unable to get Shift timings"));
                }
            }

            @Override
            public void onFailure(Call<ShiftTiming> call, Throwable t) {
                shiftCallback.onFailure(new Error("Unable to get Shift timings"));
            }
        });

    }

}
