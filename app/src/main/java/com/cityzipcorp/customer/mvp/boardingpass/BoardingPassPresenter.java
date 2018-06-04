package com.cityzipcorp.customer.mvp.boardingpass;

import android.location.Location;

public interface BoardingPassPresenter {
    void onDestroy();

    void sendSos(String baseUrl, Location location, String passId, String accessToken);

    void getBoardingPass(String baseUrl, String accessToken);

    void getRideDetails(String baseUrl, Location location, String passId, String accessToken);

    void markAttendance(String baseUrl, Location location, String passId, String accessToken);
}
