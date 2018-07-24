package com.cityzipcorp.customer.network;


import com.cityzipcorp.customer.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by anilpathak on 05/09/17.
 * Creating instance of retrofit to make url calls
 */

public class ClientGenerator {
    private Retrofit retrofit;
    private Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .create();

    public ClientGenerator(String baseUrl) {
        createRetrofit(baseUrl);
    }

    private void createRetrofit(String baseUrl) {
        retrofit = null;
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(
                        new OkHttpClient.Builder()
                                .retryOnConnectionFailure(true)
                                .addNetworkInterceptor(Utils.getInstance().getUserAgent())
                                .build()
                )
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public <S> S createClient(Class<S> clientClass) {
        return retrofit.create(clientClass);
    }
}
