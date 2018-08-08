package com.cityzipcorp.customer.store;

import android.support.annotation.NonNull;

import com.cityzipcorp.customer.callbacks.StatusCallback;
import com.cityzipcorp.customer.clients.ForgotPasswordClient;
import com.cityzipcorp.customer.model.SetNewPassword;
import com.cityzipcorp.customer.network.ClientGenerator;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordStore {

    private ClientGenerator clientGenerator;

    private ForgotPasswordStore(String baseUrl) {
        clientGenerator = new ClientGenerator(baseUrl);
    }

    public static ForgotPasswordStore getInstance(String baseUrl) {

        return new ForgotPasswordStore(baseUrl);
    }

    public void forgotPassword(String email, String url, final StatusCallback statusCallback) {
        ForgotPasswordClient forgotPasswordClient = clientGenerator.createClient(ForgotPasswordClient.class);
        SetNewPassword setNewPassword = new SetNewPassword();
        setNewPassword.setEmail(email);
        Call<ResponseBody> call = forgotPasswordClient.forgotPassword(url, setNewPassword);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    statusCallback.onSuccess();
                } else if (response.code() == 404) {
                    statusCallback.onFailure(new Error("User Not found"));
                } else if (response.code() == 400) {
                    statusCallback.onFailure(new Error("Email not valid or already exists"));
                } else {
                    statusCallback.onFailure(new Error("Unable to register"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                statusCallback.onFailure(new Error("Something went wrong"));
            }
        });
    }

    public void setNewPassword(SetNewPassword setNewPassword, String url, final StatusCallback statusCallback) {
        ForgotPasswordClient forgotPasswordClient = clientGenerator.createClient(ForgotPasswordClient.class);
        Call<ResponseBody> call = forgotPasswordClient.setNewPassword(url, setNewPassword);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    statusCallback.onSuccess();
                } else {
                    statusCallback.onFailure(new Error("Unable to set new Password!"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                statusCallback.onFailure(new Error("Unable to set new Password!"));
            }
        });

    }

}
