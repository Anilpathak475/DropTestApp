package com.cityzipcorp.customer.clients;

import com.cityzipcorp.customer.model.Area;
import com.cityzipcorp.customer.model.ChangePassword;
import com.cityzipcorp.customer.model.FcmRegistrationToken;
import com.cityzipcorp.customer.model.Group;
import com.cityzipcorp.customer.model.NodalStop;
import com.cityzipcorp.customer.model.ProfileStatus;
import com.cityzipcorp.customer.model.User;
import com.cityzipcorp.customer.model.UserCredential;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by anilpathak on 21/11/17.
 */

public interface UserClient {

    @POST("/api/v1/users/login/")
    Call<User> login(@Body UserCredential userCredential);

    @GET("/api/v1/users/profile_status/")
    Call<ProfileStatus> getProfileStatus(@HeaderMap Map<String, String> headers);

    @POST("/api/v1/users/logout/")
    Call<ResponseBody> logout(@HeaderMap Map<String, String> headers);

    @GET("/api/v1/users/profile/")
    Call<User> profile(@HeaderMap Map<String, String> headers);

    @GET("/api/v1/users/groups/")
    Call<List<Group>> getAllGroups(@HeaderMap Map<String, String> headers);

    // TODO: Change this to pascal case
    @PATCH("/api/v1/users/{id}/")
    Call<User> updateProfile(@HeaderMap Map<String, String> headers, @Path("id") String id, @Body User user);

    @GET("/api/v1/stops/nearby/")
    Call<List<NodalStop>> getNodalStopList(@Query("in_bbox") String boundingBox, @HeaderMap Map<String, String> headers);

    // TODO: change this to pascal case
    @PATCH("/api/v1/users/{id}/")
    Call<User> updateNodalPoint(@HeaderMap Map<String, String> headers, @Path("id") String id, @Body User user);

    @POST("/api/v1/gcm_devices/")
    Call<ResponseBody> registerDeviceToFcm(@Body FcmRegistrationToken fcmRegistrationToken, @HeaderMap Map<String, String> headers);

    @POST("/api/v1/users/change_password/")
    Call<ResponseBody> changePassword(@Body ChangePassword changePassword, @HeaderMap Map<String, String> headers);

    @GET("/api/v1/areas/")
    Call<List<Area>> getAreas(@HeaderMap Map<String, String> headers);


}
