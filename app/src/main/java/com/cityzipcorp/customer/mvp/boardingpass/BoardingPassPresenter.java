package com.cityzipcorp.customer.mvp.boardingpass;

import android.location.Location;

public interface BoardingPassPresenter {
    void onDestroy();

    void sendSos(Location location, String passId, String accessToken);

    void getBoardingPass(String accessToken);

    void getRideDetails(Location location, String passId, String accessToken);

    void markAttendance(Location location, String passId, String accessToken);
}
