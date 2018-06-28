package com.cityzipcorp.customer.clients;

import com.cityzipcorp.customer.model.Attendance;
import com.cityzipcorp.customer.model.BoardingPass;
import com.cityzipcorp.customer.model.SosBody;
import com.cityzipcorp.customer.model.TrackRide;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by anilpathak on 19/03/18.
 */

public interface BoardingPassClient {
    @GET("/api/v1/users/active_boarding_pass/")
    Call<BoardingPass> getBoardingPass(@HeaderMap Map<String, String> headers);

    @POST("/api/v1/boarding_passes/{id}/sos/")
    Call<ResponseBody> sosAlert(@Body SosBody sosBody, @Path("id") String id, @HeaderMap Map<String, String> headers);

    @POST("/api/v1/boarding_passes/{id}/mark_attendance/")
    Call<ResponseBody> markAttendance(@Body Attendance attendance, @Path("id") String id, @HeaderMap Map<String, String> headers);

    @GET("/api/v1/boarding_passes/{boardingPassId}/track")
    Call<TrackRide> trackMyRide(@Path("boardingPassId") String boardingPassId,
                                @Query("lat") String lat, @Query("lng") String lng,
                                @HeaderMap Map<String, String> headers);

}
