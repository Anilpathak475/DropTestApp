package com.cityzipcorp.customer.clients;

import com.cityzipcorp.customer.model.UserCredential;
import com.cityzipcorp.customer.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by anilpathak on 14/11/17.
 */

public interface LoginClient {

    @POST("/api/v1/users/login/")
    Call<User> login(@Body UserCredential userCredential);

}
