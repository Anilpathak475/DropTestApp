package com.cityzipcorp.customer.mvp.boardingpass;

import android.location.Location;

import com.cityzipcorp.customer.model.Attendance;
import com.cityzipcorp.customer.model.BoardingPass;
import com.cityzipcorp.customer.model.NextTrip;
import com.cityzipcorp.customer.model.TrackRide;

public class BoardingPassPresenterImpl implements BoardingPassPresenter, BoardingPassInteractor.BoardingPassCommonCallback, BoardingPassInteractor.BoardingPassDetailsCallback, BoardingPassInteractor.TrackMyRideCallback, BoardingPassInteractor.BoardingPassAttendanceCallback, BoardingPassInteractor.NextTripCallback {

    private BoardingPassView boardingPassView;
    private BoardingPassInteractorImpl boardingPassInteractor;

    public BoardingPassPresenterImpl(BoardingPassView boardingPassView) {
        this.boardingPassView = boardingPassView;
        this.boardingPassInteractor = new BoardingPassInteractorImpl();
    }

    @Override
    public void setError(String error) {
        boardingPassView.hideProgress();
        boardingPassView.setError(error);
    }

    @Override
    public void onTripFound() {
        boardingPassView.noTrip();
    }

    @Override
    public void onNextTripSuccess(NextTrip nextTrip) {
        boardingPassView.nextTripSuccess(nextTrip);
    }

    @Override
    public void onAttendanceSuccess() {
        boardingPassView.hideProgress();
        boardingPassView.attendanceSuccess();
    }

    @Override
    public void onSosSuccess() {
        boardingPassView.hideProgress();
        boardingPassView.sosSuccess();
    }


    @Override
    public void showPassError(String error) {
        boardingPassView.setPassError(error);
    }

    @Override
    public void onSuccess(BoardingPass boardingPass) {
        boardingPassView.passDetailSuccess(boardingPass);
    }

    @Override
    public void onDestroy() {
        boardingPassView = null;
    }

    @Override
    public void sendSos(String baseUrl, Location location, String passId, String accessToken, String macId) {
        boardingPassView.showProgress();
        boardingPassInteractor.sendSos(baseUrl, location, passId, accessToken, macId, this);
    }

    @Override
    public void getBoardingPass(String baseUrl, String accessToken, String macId) {
        boardingPassInteractor.getBoardingPass(baseUrl, accessToken, macId, this);
    }

    @Override
    public void getNextTrip(String baseUrl, String accessToken, String macId) {
        boardingPassInteractor.getNextTrip(baseUrl, accessToken, macId, this);
    }

    @Override
    public void getRideDetails(String baseUrl, Location location, String passId, String accessToken, String macId) {
        boardingPassView.showProgress();
        boardingPassInteractor.getRideDetails(baseUrl, location, passId, accessToken, macId, this);
    }

    @Override
    public void markAttendance(String baseUrl, Attendance attendance, String passId, String accessToken, String macId) {
        boardingPassView.showProgress();
        boardingPassInteractor.markAttendance(baseUrl, attendance, passId, accessToken, macId, this);
    }

    @Override
    public void showTrackError(String error) {
        boardingPassView.hideProgress();
        boardingPassView.setError(error);
    }

    @Override
    public void onSuccess(TrackRide trackRide) {
        boardingPassView.hideProgress();
        boardingPassView.trackDetailSuccess(trackRide);
    }
}
