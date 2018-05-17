package com.cityzipcorp.customer.utils;

import android.os.Build;

import com.cityzipcorp.customer.BuildConfig;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anilpathak on 17/11/17.
 */

public class Utils {

    public static Map<String, String> getHeader(String authToken) {
        Field[] fields = Build.VERSION_CODES.class.getFields();
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(Constants.HEADER_AUTHORIZATION_KEY, Constants.HEADER_AUTHORIZATION_VALUE_PREFIX + authToken);
        headerMap.put(Constants.HEADER_MOBILE_OS, fields[Build.VERSION.SDK_INT + 1].getName());
        headerMap.put(Constants.HEADER_MOBILE_APP_VERSION, BuildConfig.VERSION_NAME);
        headerMap.put(Constants.HEADER_MOBILE_OS_VERSION, String.valueOf(android.os.Build.VERSION.SDK_INT));
        headerMap.put(Constants.HEADER_MOBILE_MANUFACTURER, Build.MANUFACTURER);
        headerMap.put(Constants.HEADER_MOBILE_MANUFACTURER_MODEL, Build.MODEL);
        return headerMap;
    }

    public static String replaceNull(String value) {
        if (value != null) {
            if (value.contains("null")) {
                return value.replace("null", "");
            } else {
                return value;
            }
        } else {
            return "";
        }
    }
}
