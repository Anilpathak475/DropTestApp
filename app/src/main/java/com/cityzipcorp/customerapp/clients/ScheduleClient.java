package com.cityzipcorp.customerapp.clients;

import com.cityzipcorp.customerapp.model.Schedule;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;

/**
 * Created by anilpathak on 03/11/2017
 */

public interface ScheduleClient {

    @GET("/api/v1/users/schedule/")
    Call<List<Schedule>> getSchedule(@HeaderMap Map<String, String> headers);
}



