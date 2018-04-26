package com.cityzipcorp.customer.clients;

import com.cityzipcorp.customer.model.SetNewPassword;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ForgotPasswordClient {

    @POST
    Call<ResponseBody> forgotPassword(@Url String url, @Body SetNewPassword setNewPassword);

    @POST
    Call<ResponseBody> setNewPassword(@Url String url, @Body SetNewPassword setNewPassword);
}
