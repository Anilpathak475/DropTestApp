package com.cityzipcorp.customer.utils;

/**
 * Created by anilpathak on 06/11/17.
 */

public interface Constants {
    String HEADER_AUTHORIZATION_KEY = "Authorization";
    String HEADER_MOBILE_OS = "mob_os";
    String HEADER_MOBILE_OS_VERSION = "mob_os_version";
    String HEADER_MOBILE_MANUFACTURER = "mob_manufacturer";
    String HEADER_MOBILE_MANUFACTURER_MODEL = "mob_manufacturer_model";
    String HEADER_MOBILE_APP_VERSION = "mob_app_version";
    String HEADER_TIMEZONE_KEY = "Timezone";
    String HEADER_AUTHORIZATION_VALUE_PREFIX = "JWT ";
    String EDIT_INTENT_EXTRA_DATA = "data ";
    String EDIT_INTENT_EXTRA_TIME = "timeToDisplay";
    String TRIP_TYPE_PICK_UP = "pick_up";
    String API_NOT_CONNECTED = "Google API not connected";
    String PlacesTag = "Google Places Auto Complete";
    String MAP_URL = "https://maps.googleapis.com/maps/api/directions/";
    int LOCATION_PERMISSION_TRACK = 1001;
    String PROFILE_STATUS = "profileStatus";
    String PROFILE_STATUS_IN_COMPLETED = "incomplete";
    String FORGOT_PASSWORD_URL = "/api/v1/users/reset_password/";
    String NEW_USER_URL = "/api/v1/users/send_invite/";
    String FORGOT_PASSWORD_CHANGE_URL = "/api/v1/users/set_new_password/";
    String NEW_USER_PASSWORD_CHANGE_URL = "/api/v1/users/register/";
    int REQUEST_CODE_ADDRESS_INTENT = 186;
}
