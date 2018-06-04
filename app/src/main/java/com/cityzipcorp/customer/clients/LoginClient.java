package com.cityzipcorp.customer.clients;

import com.cityzipcorp.customer.model.Contract;
import com.cityzipcorp.customer.model.User;
import com.cityzipcorp.customer.model.UserCredential;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

/**
 * Created by anilpathak on 14/11/17.
 */

public interface LoginClient {

    @POST("/api/v1/users/login/")
    Call<User> login(@Body UserCredential userCredential, @HeaderMap Map<String, String> headers);

    @GET("/api/v1/customer_contracts/summary/")
    Call<List<Contract>> contract(@HeaderMap Map<String, String> headers);


}
