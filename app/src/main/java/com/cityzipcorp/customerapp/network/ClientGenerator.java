package com.cityzipcorp.customerapp.network;


import com.cityzipcorp.customerapp.utils.CalenderUtil;
import com.cityzipcorp.customerapp.utils.Configuration;
import com.cityzipcorp.customerapp.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by anilpathak on 05/09/17.
 * Creating instance of retrofit to make url calls
 */

public class ClientGenerator {

    private static Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'hh:mm:ssZ")
            .create();

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Configuration.BASE_URL)
            .client(
                    new OkHttpClient.Builder()
                            .retryOnConnectionFailure(true)
                            .addInterceptor(new Interceptor() {
                                @Override
                                public Response intercept(Chain chain) throws IOException {
                                    Request original  = chain.request();

                                    Request.Builder builder = original.newBuilder();
                                    builder.addHeader(
                                            "Content-Type",
                                            "application/json"
                                    );
                                    builder.addHeader(Constants.HEADER_TIMEZONE_KEY,
                                            CalenderUtil.getCurrentTimeZone());

                                    return chain.proceed(builder.build());
                                }
                            })
                            .build()
            )
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    public static <S> S createClient(Class<S> clientClass) {
        return retrofit.create(clientClass);
    }
}