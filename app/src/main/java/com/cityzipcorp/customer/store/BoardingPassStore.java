package com.cityzipcorp.customer.store;

import com.cityzipcorp.customer.callbacks.BoardingPassCallback;
import com.cityzipcorp.customer.callbacks.StatusCallback;
import com.cityzipcorp.customer.callbacks.TrackRideCallback;
import com.cityzipcorp.customer.clients.BoardingPassClient;
import com.cityzipcorp.customer.model.Attendance;
import com.cityzipcorp.customer.model.BoardingPass;
import com.cityzipcorp.customer.model.GeoJsonPoint;
import com.cityzipcorp.customer.model.TrackRide;
import com.cityzipcorp.customer.network.ClientGenerator;
import com.cityzipcorp.customer.utils.Utils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by anilpathak on 19/03/18.
 */

public class BoardingPassStore {
    private static BoardingPassStore instance = null;
    private ClientGenerator clientGenerator;

    private BoardingPassStore(String baseUrl) {
        clientGenerator = new ClientGenerator(baseUrl);
    }

    public static BoardingPassStore getInstance(String baseUrl) {
        instance = new BoardingPassStore(baseUrl);
        return instance;
    }

    public void getBoardingPass(String authToken, final BoardingPassCallback boardingPassCallback) {
        BoardingPassClient boardingPassClient = clientGenerator.createClient(BoardingPassClient.class);
        Call<BoardingPass> call = boardingPassClient.getBoardingPass(Utils.getHeader(authToken));
        call.enqueue(new Callback<BoardingPass>() {
            @Override
            public void onResponse(Call<BoardingPass> call, Response<BoardingPass> response) {
                if (response.isSuccessful()) {
                    boardingPassCallback.onSuccess(response.body());
                } else {
                    boardingPassCallback.onFailure(new Error(String.valueOf(response.code())));
                }
            }

            @Override
            public void onFailure(Call<BoardingPass> call, Throwable t) {
                boardingPassCallback.onFailure(new Error(t.getMessage()));
            }
        });
    }

    public void sosAlert(GeoJsonPoint geoJsonPoint, String boardingPassId, String accessToken, final StatusCallback statusCallback) {
        BoardingPassClient boardingPassClient = clientGenerator.createClient(BoardingPassClient.class);
        Call<ResponseBody> call = boardingPassClient.sosAlert(geoJsonPoint, boardingPassId, Utils.getHeader(accessToken));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    statusCallback.onSuccess();
                } else {
                    statusCallback.onFailure(new Error(String.valueOf(response.code())));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                statusCallback.onFailure(new Error(t.getMessage()));
            }
        });
    }

    public void markAttendance(Attendance attendance, String boardingPassId, String accessToken, final StatusCallback statusCallback) {
        BoardingPassClient boardingPassClient = clientGenerator.createClient(BoardingPassClient.class);
        Call<ResponseBody> call = boardingPassClient.markAttendance(attendance, boardingPassId, Utils.getHeader(accessToken));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    statusCallback.onSuccess();
                } else {
                    statusCallback.onFailure(new Error(String.valueOf(response.code())));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                statusCallback.onFailure(new Error(t));
            }
        });
    }

    public void trackMyRide(String boardingPassId, String lat, String lng, String accessToken, final TrackRideCallback trackRideCallback) {
        BoardingPassClient boardingPassClient = clientGenerator.createClient(BoardingPassClient.class);
        Call<TrackRide> call = boardingPassClient.trackMyRide(boardingPassId, lat, lng, Utils.getHeader(accessToken));
        call.enqueue(new Callback<TrackRide>() {
            @Override
            public void onResponse(Call<TrackRide> call, Response<TrackRide> response) {
                if (response.isSuccessful()) {
                    trackRideCallback.onSuccess(response.body());
                } else if (response.code() == 412) {
                    trackRideCallback.onFailure(new Error("Trip not Started yet!"));
                } else {
                    trackRideCallback.onFailure(new Error("Unable to Fetch Ride Details"));
                }
            }

            @Override
            public void onFailure(Call<TrackRide> call, Throwable t) {
                trackRideCallback.onFailure(new Error("Unable to Fetch Ride Details"));
            }
        });
    }
}
