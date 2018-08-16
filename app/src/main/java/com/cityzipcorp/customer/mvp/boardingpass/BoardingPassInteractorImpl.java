package com.cityzipcorp.customer.mvp.boardingpass;

import android.location.Location;

import com.cityzipcorp.customer.callbacks.BoardingPassCallback;
import com.cityzipcorp.customer.callbacks.StatusCallback;
import com.cityzipcorp.customer.callbacks.TrackRideCallback;
import com.cityzipcorp.customer.model.Attendance;
import com.cityzipcorp.customer.model.BoardingPass;
import com.cityzipcorp.customer.model.GeoJsonPoint;
import com.cityzipcorp.customer.model.NextTrip;
import com.cityzipcorp.customer.model.SosBody;
import com.cityzipcorp.customer.model.TrackRide;
import com.cityzipcorp.customer.store.BoardingPassStore;
import com.cityzipcorp.customer.store.UserStore;

public class BoardingPassInteractorImpl implements BoardingPassInteractor {

    @Override
    public void sendSos(String baseUrl, Location location, String passId, String accessToken, String macId, final BoardingPassCommonCallback listener) {
        GeoJsonPoint geoJsonPoint;
        if (location == null) {
            geoJsonPoint = new GeoJsonPoint(0, 0);
        } else {
            geoJsonPoint = new GeoJsonPoint(location.getLongitude(), location.getLatitude());
        }

        BoardingPassStore.getInstance(baseUrl, macId).sosAlert(new SosBody(geoJsonPoint), passId, accessToken, new StatusCallback() {
            @Override
            public void onSuccess() {
                listener.onSosSuccess();
            }

            @Override
            public void onFailure(Error error) {
                listener.setError(error.getLocalizedMessage());
            }
        });
    }

    @Override
    public void getBoardingPass(String baseUrl, String accessToken, String macId, final BoardingPassDetailsCallback boardingPassDetailsCallback) {
        BoardingPassStore.getInstance(baseUrl, macId).getBoardingPass(accessToken, new BoardingPassCallback() {
            @Override
            public void onSuccess(BoardingPass boardingPass) {
                if (boardingPass != null) {
                    boardingPassDetailsCallback.onSuccess(boardingPass);
                }
            }

            @Override
            public void onFailure(Error error) {
                boardingPassDetailsCallback.showPassError(error.getLocalizedMessage());
            }
        });
    }

    @Override
    public void getRideDetails(String baseUrl, Location location, String passId, String accessToken, String macId, final TrackMyRideCallback trackMyRideCallback) {
        String lat;
        String lng;
        if (null == location) {
            lat = "0.0";
            lng = "0.0";
        } else {
            lat = String.valueOf(location.getLatitude());
            lng = String.valueOf(location.getLatitude());
        }
        BoardingPassStore.getInstance(baseUrl, macId).trackMyRide(passId, lat, lng, accessToken,
                new TrackRideCallback() {
                    @Override
                    public void onSuccess(TrackRide trackRide) {
                        if (trackRide != null) {
                            trackMyRideCallback.onSuccess(trackRide);
                        }
                    }

                    @Override
                    public void onFailure(Error error) {
                        trackMyRideCallback.showTrackError(error.getLocalizedMessage());
                    }
                });
    }

    @Override
    public void getNextTrip(String baseUrl, String accessToken, String macId, final NextTripCallback nextTripCallback) {
        UserStore.getInstance(baseUrl, macId).getNextTrip(accessToken, new com.cityzipcorp.customer.callbacks.NextTripCallback() {
            @Override
            public void onSuccess(NextTrip nextTrip) {
                nextTripCallback.onNextTripSuccess(nextTrip);
            }

            @Override
            public void onFailure(Error error) {
                nextTripCallback.onTripFound();
            }
        });
    }

    @Override
    public void markAttendance(String baseUrl, Attendance attendance, String passId, String accessToken, String macId, final BoardingPassAttendanceCallback boardingPassCommonCallback) {

        BoardingPassStore.getInstance(baseUrl, macId).markAttendance(attendance, passId, accessToken, new StatusCallback() {
            @Override
            public void onSuccess() {
                boardingPassCommonCallback.onAttendanceSuccess();
            }

            @Override
            public void onFailure(Error error) {
                boardingPassCommonCallback.setError(error.getLocalizedMessage());
            }
        });
    }

}
