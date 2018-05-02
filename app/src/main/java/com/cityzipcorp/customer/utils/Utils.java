package com.cityzipcorp.customer.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by anilpathak on 17/11/17.
 */

public class Utils {

    public static Map<String, String> getHeader(String authToken) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(Constants.HEADER_AUTHORIZATION_KEY, Constants.HEADER_AUTHORIZATION_VALUE_PREFIX + authToken);
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
