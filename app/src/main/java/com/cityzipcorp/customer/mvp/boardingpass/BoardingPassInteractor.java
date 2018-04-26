package com.cityzipcorp.customer.mvp.boardingpass;

import android.location.Location;

import com.cityzipcorp.customer.model.BoardingPass;
import com.cityzipcorp.customer.model.TrackRide;

public interface BoardingPassInteractor {
    void sendSos(Location location, String passId, String accessToken, BoardingPassCommonCallback listener);

    void getBoardingPass(String accessToken, BoardingPassDetailsCallback boardingPassDetailsCallback);

    void getRideDetails(Location location, String passId, String accessToken, TrackMyRideCallback trackMyRideCallback);

    void markAttendance(Location location, String passId, String accessToken, BoardingPassAttendanceCallback boardingPassCommonCallback);

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
