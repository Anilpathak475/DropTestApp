package com.cityzipcorp.customer.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import com.cityzipcorp.customer.model.Group;
import com.cityzipcorp.customer.model.Shift;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by anilpathak on 17/11/17.
 */

public class Utils {

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }


    public static Map<String, String> getHeader(String authToken) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(Constants.HEADER_AUTHORIZATION_KEY, Constants.HEADER_AUTHORIZATION_VALUE_PREFIX + authToken);
        return headerMap;
    }

    public static List<Group> getIndexReplacedListForGroup(List<Group> groupList) {
        List<Group> listGroup= new ArrayList<>();
        Group group = new Group();
        group.setName("Select Group");
        Shift shift = new Shift();
        shift.setName("Select Shift");
        List<Shift> shiftList = new ArrayList<>();
        shiftList.add(shift);
        group.setShifts(shiftList);
        listGroup.add(group);
        listGroup.addAll(groupList);
        return listGroup;
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
