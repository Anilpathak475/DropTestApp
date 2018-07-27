package com.cityzipcorp.customer.mvp.boardingpass;

import android.location.Location;

import com.cityzipcorp.customer.model.Attendance;
import com.cityzipcorp.customer.model.BoardingPass;
import com.cityzipcorp.customer.model.TrackRide;

public interface BoardingPassInteractor {
    void sendSos(String baseUrl, Location location, String passId, String accessToken, String macId, BoardingPassCommonCallback listener);

    void getBoardingPass(String baseUrl, String accessToken, String macId, BoardingPassDetailsCallback boardingPassDetailsCallback);

    void getRideDetails(String baseUrl, Location location, String passId, String accessToken, String macId, TrackMyRideCallback trackMyRideCallback);

    void markAttendance(String baseUrl, Attendance attendance, String passId, String accessToken, String macId, BoardingPassAttendanceCallback boardingPassCommonCallback);

    interface BoardingPassCommonCallback {
        void setError(String error);

        void onSosSuccess();
    }

    interface BoardingPassAttendanceCallback {
        void setError(String error);

        void onAttendanceSuccess();
    }

    interface BoardingPassDetailsCallback {
        void showPassError(String error);

        void onSuccess(BoardingPass boardingPass);
    }

    interface TrackMyRideCallback {
        void showTrackError(String error);

        void onSuccess(TrackRide trackRide);
    }

}
