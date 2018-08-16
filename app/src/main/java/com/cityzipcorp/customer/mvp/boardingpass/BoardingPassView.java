package com.cityzipcorp.customer.mvp.boardingpass;

import com.cityzipcorp.customer.model.BoardingPass;
import com.cityzipcorp.customer.model.NextTrip;
import com.cityzipcorp.customer.model.TrackRide;

public interface BoardingPassView {
    void showProgress();

    void hideProgress();

    void setPassError(String error);

    void setTrackError(String error);

    void noTrip();

    void setError(String error);

    void passDetailSuccess(BoardingPass boardingPass);

    void trackDetailSuccess(TrackRide trackRide);

    void nextTripSuccess(NextTrip nextTrip);

    void attendanceSuccess();

    void sosSuccess();
}
