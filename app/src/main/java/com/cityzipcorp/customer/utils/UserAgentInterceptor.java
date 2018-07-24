package com.cityzipcorp.customer.utils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class UserAgentInterceptor implements Interceptor {

    private final String userAgent;

    UserAgentInterceptor(String osVersion, String manufacture, String model, String uuId, String macId, String appVersion) {
        userAgent = "Android/" + osVersion + " (" + manufacture + "; " + model + "; " + uuId + "; " + macId + ") CustomerApp/" + appVersion;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request requestWithUserAgent = originalRequest.newBuilder()
                .header("User-Agent", userAgent)
                .header("Content-Type", "application/json")
                .build();
        return chain.proceed(requestWithUserAgent);
    }
}