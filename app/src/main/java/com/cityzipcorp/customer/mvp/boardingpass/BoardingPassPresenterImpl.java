package com.cityzipcorp.customer.mvp.boardingpass;

import android.location.Location;

import com.cityzipcorp.customer.model.BoardingPass;
import com.cityzipcorp.customer.model.TrackRide;

public class BoardingPassPresenterImpl implements BoardingPassPresenter, BoardingPassInteractor.BoardingPassCommonCallback, BoardingPassInteractor.BoardingPassDetailsCallback, BoardingPassInteractor.TrackMyRideCallback, BoardingPassInteractor.BoardingPassAttendanceCallback {

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
    public void sendSos(Location location, String passId, String accessToken) {
        boardingPassView.showProgress();
        boardingPassInteractor.sendSos(location, passId, accessToken, this);
    }

    @Override
    public void getBoardingPass(String accessToken) {
        boardingPassInteractor.getBoardingPass(accessToken, this);
    }

    @Override
    public void getRideDetails(Location location, String passId, String accessToken) {
        boardingPassView.showProgress();
        boardingPassInteractor.getRideDetails(location, passId, accessToken, this);
    }

    @Override
    public void markAttendance(Location location, String passId, String accessToken) {
        boardingPassView.showProgress();
        boardingPassInteractor.markAttendance(location, passId, accessToken, this);
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
