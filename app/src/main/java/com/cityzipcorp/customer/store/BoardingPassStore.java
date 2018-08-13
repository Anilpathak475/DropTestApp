package com.cityzipcorp.customer.store;

import android.support.annotation.NonNull;

import com.cityzipcorp.customer.callbacks.BoardingPassCallback;
import com.cityzipcorp.customer.callbacks.StatusCallback;
import com.cityzipcorp.customer.callbacks.TrackRideCallback;
import com.cityzipcorp.customer.clients.BoardingPassClient;
import com.cityzipcorp.customer.model.Attendance;
import com.cityzipcorp.customer.model.BoardingPass;
import com.cityzipcorp.customer.model.SosBody;
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
    private ClientGenerator clientGenerator;
    private String macId;

    private BoardingPassStore(String baseUrl, String macId) {
        clientGenerator = new ClientGenerator(baseUrl);
        this.macId = macId;
    }

    public static BoardingPassStore getInstance(String baseUrl, String macId) {
        return new BoardingPassStore(baseUrl, macId);
    }

    public void getBoardingPass(String authToken, final BoardingPassCallback boardingPassCallback) {
        BoardingPassClient boardingPassClient = clientGenerator.createClient(BoardingPassClient.class);
        Call<BoardingPass> call = boardingPassClient.getBoardingPass(Utils.getHeader(authToken, macId));
        call.enqueue(new Callback<BoardingPass>() {
            @Override
            public void onResponse(@NonNull Call<BoardingPass> call, @NonNull Response<BoardingPass> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boardingPassCallback.onSuccess(response.body());
                } else {
                    boardingPassCallback.onFailure(new Error("Unable to get boarding pass"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<BoardingPass> call, @NonNull Throwable t) {
                boardingPassCallback.onFailure(new Error("Unable to get boarding pass"));
            }
        });
    }

    public void sosAlert(SosBody sosBody, String boardingPassId, String accessToken, final StatusCallback statusCallback) {
        BoardingPassClient boardingPassClient = clientGenerator.createClient(BoardingPassClient.class);
        Call<ResponseBody> call = boardingPassClient.sosAlert(sosBody, boardingPassId, Utils.getHeader(accessToken, macId));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    statusCallback.onSuccess();
                } else {
                    statusCallback.onFailure(new Error("Unable to update sos request"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                statusCallback.onFailure(new Error("Unable to update sos request"));
            }
        });
    }

    public void markAttendance(Attendance attendance, String boardingPassId, String accessToken, final StatusCallback statusCallback) {
        BoardingPassClient boardingPassClient = clientGenerator.createClient(BoardingPassClient.class);
        Call<ResponseBody> call = boardingPassClient.markAttendance(attendance, boardingPassId, Utils.getHeader(accessToken, macId));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    statusCallback.onSuccess();
                } else if (response.code() == 404) {
                    statusCallback.onFailure(new Error("Scanned Qr code is invalid. Please try again later!"));
                } else {
                    statusCallback.onFailure(new Error("Unable to mark attendance"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                statusCallback.onFailure(new Error("Unable to mark attendance"));
            }
        });
    }

    public void trackMyRide(String boardingPassId, String lat, String lng, String accessToken, final TrackRideCallback trackRideCallback) {
        BoardingPassClient boardingPassClient = clientGenerator.createClient(BoardingPassClient.class);
        Call<TrackRide> call = boardingPassClient.trackMyRide(boardingPassId, lat, lng, Utils.getHeader(accessToken, macId));
        call.enqueue(new Callback<TrackRide>() {
            @Override
            public void onResponse(@NonNull Call<TrackRide> call, @NonNull Response<TrackRide> response) {
                if (response.isSuccessful()) {
                    trackRideCallback.onSuccess(response.body());
                } else if (response.code() == 412) {
                    trackRideCallback.onFailure(new Error("Trip not Started yet!"));
                } else {
                    trackRideCallback.onFailure(new Error("Unable to Fetch Ride Details"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<TrackRide> call, @NonNull Throwable t) {
                trackRideCallback.onFailure(new Error("Unable to Fetch Ride Details"));
            }
        });
    }
}
