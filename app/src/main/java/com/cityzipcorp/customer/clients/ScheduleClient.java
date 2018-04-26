package com.cityzipcorp.customer.clients;

import com.cityzipcorp.customer.model.Reason;
import com.cityzipcorp.customer.model.Schedule;
import com.cityzipcorp.customer.model.ShiftTiming;
import com.cityzipcorp.customer.utils.Constants;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by anilpathak on 03/11/2017
 */

public interface ScheduleClient {

    @GET("/api/v1/users/schedule/")
    Call<List<Schedule>> getSchedule(@HeaderMap Map<String, String> headers);

    @GET("/api/v1/reasons/")
    Call<List<Reason>> getReason(@HeaderMap Map<String, String> headers);

    @PATCH("/api/v1/schedule_updates/{id}/")
    Call<ResponseBody> updateSchedule(@Header(Constants.HEADER_AUTHORIZATION_KEY) String authKey, @Path("id") String id, @Body Schedule schedule);

    @POST("/api/v1/schedule_updates/")
    Call<ResponseBody> createSchedule(@Header(Constants.HEADER_AUTHORIZATION_KEY) String authKey, @Body Schedule schedule);

    @GET("/api/v1/shifts/times/")
    Call<ShiftTiming> getShiftTimings(@HeaderMap Map<String, String> headers);
}



