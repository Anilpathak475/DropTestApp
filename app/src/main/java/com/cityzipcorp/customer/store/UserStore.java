package com.cityzipcorp.customer.store;

import android.support.annotation.NonNull;
import android.util.Log;

import com.cityzipcorp.customer.callbacks.GroupCallBack;
import com.cityzipcorp.customer.callbacks.NodalStopCallback;
import com.cityzipcorp.customer.callbacks.ProfileStatusCallback;
import com.cityzipcorp.customer.callbacks.StatusCallback;
import com.cityzipcorp.customer.callbacks.UserCallback;
import com.cityzipcorp.customer.clients.LoginClient;
import com.cityzipcorp.customer.clients.UserClient;
import com.cityzipcorp.customer.model.ChangePassword;
import com.cityzipcorp.customer.model.FcmRegistrationToken;
import com.cityzipcorp.customer.model.Group;
import com.cityzipcorp.customer.model.NodalStop;
import com.cityzipcorp.customer.model.NodalStopBody;
import com.cityzipcorp.customer.model.ProfileStatus;
import com.cityzipcorp.customer.model.User;
import com.cityzipcorp.customer.model.UserCredential;
import com.cityzipcorp.customer.network.ClientGenerator;
import com.cityzipcorp.customer.utils.Utils;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by anilpathak on 21/11/17.
 */

public class UserStore {

    private static UserStore instance = null;

    private UserStore() {
    }

    public static UserStore getInstance() {
        if (instance == null) {
            instance = new UserStore();
        }

        return instance;
    }

    public void login(UserCredential userCredential, final UserCallback userCallback) {
        LoginClient loginClient = ClientGenerator.createClient(LoginClient.class);
        Call<User> call = loginClient.login(userCredential);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    userCallback.onSuccess(response.body());
                } else if (response.code() == 404 || response.code() == 400) {
                    userCallback.onFailure(new Error("Invalid Credentials!"));
                } else {
                    userCallback.onFailure(new Error("Unable to login!"));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                userCallback.onFailure(new Error("Unable to login!"));
            }
        });

    }

    public void getProfileInfo(String authToken, final UserCallback userCallback) {
        Log.d("Auth token", authToken);
        UserClient userClient = ClientGenerator.createClient(UserClient.class);
        Call<User> call = userClient.profile(Utils.getHeader(authToken));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    userCallback.onSuccess(response.body());
                } else {
                    userCallback.onFailure(new Error(""));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                userCallback.onFailure(new Error(t));
            }
        });
    }

    public void updateProfileInfo(String authToken, User user, final UserCallback userCallback) {
        UserClient userClient = ClientGenerator.createClient(UserClient.class);
        Call<User> call = userClient.updateProfile(Utils.getHeader(authToken), user.getId(), user);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    userCallback.onSuccess(response.body());
                } else {
                    userCallback.onFailure(new Error("Request ended with " + response.code() + " status"));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                userCallback.onFailure(new Error(t));
            }
        });
    }

    public void logout(String autToken, final StatusCallback statusCallback) {
        UserClient userClient = ClientGenerator.createClient(UserClient.class);
        Call<ResponseBody> call = userClient.logout(Utils.getHeader(autToken));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    statusCallback.onSuccess();
                } else {
                    statusCallback.onFailure(new Error("Request ended with " + response.code() + " status"));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                statusCallback.onFailure(new Error(t));
            }
        });
    }

    public void getNodalStopList(String boundingBoxString, String authToken, final NodalStopCallback nodalStopCallback) {
        UserClient userClient = ClientGenerator.createClient(UserClient.class);
        Call<List<NodalStop>> call = userClient.getNodalStopList(boundingBoxString, Utils.getHeader(authToken));
        call.enqueue(new Callback<List<NodalStop>>() {
            @Override
            public void onResponse(Call<List<NodalStop>> call, Response<List<NodalStop>> response) {
                if (response.isSuccessful()) {
                    nodalStopCallback.onSuccess(response.body());
                } else {
                    nodalStopCallback.onFailure(new Error("Request ended with " + response.code() + " status"));
                }
            }

            @Override
            public void onFailure(Call<List<NodalStop>> call, Throwable t) {
                nodalStopCallback.onFailure(new Error(t));
            }
        });
    }

    public void updateNodalStop(String authToken, String id, NodalStopBody nodalStopBody, final StatusCallback statusCallback) {
        UserClient userClient = ClientGenerator.createClient(UserClient.class);
        User user = new User(nodalStopBody);
        Call<User> call = userClient.updateNodalPoint(Utils.getHeader(authToken), id, user);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    statusCallback.onSuccess();
                } else {
                    statusCallback.onFailure(new Error("Request ended with " + response.code() + " status"));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                statusCallback.onFailure(new Error(t));
            }
        });
    }


    public void getShiftByGroup(String authToken, final GroupCallBack groupCallBack) {
        UserClient userClient = ClientGenerator.createClient(UserClient.class);
        Call<List<Group>> call = userClient.getAllGroups(Utils.getHeader(authToken));
        call.enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                if (response.isSuccessful()) {
                    groupCallBack.onSuccess(response.body());
                } else {
                    groupCallBack.onFailure(new Error("Request ended with " + response.code() + " status"));
                }
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                groupCallBack.onFailure(new Error(t.getMessage()));
            }
        });
    }

    public void getProfileStatus(String accessToken, final ProfileStatusCallback profileStatusCallback) {
        UserClient userClient = ClientGenerator.createClient(UserClient.class);
        Call<ProfileStatus> call = userClient.getProfileStatus(Utils.getHeader(accessToken));
        call.enqueue(new Callback<ProfileStatus>() {
            @Override
            public void onResponse(Call<ProfileStatus> call, Response<ProfileStatus> response) {
                if (response.isSuccessful()) {
                    profileStatusCallback.onSuccess(response.body());
                } else {
                    profileStatusCallback.onFailure(new Error("unable to fetch details"));
                }
            }

            @Override
            public void onFailure(Call<ProfileStatus> call, Throwable t) {
                profileStatusCallback.onFailure(new Error("unable to fetch details"));
            }
        });


    }


    public void registerFcmToken(FcmRegistrationToken fcmRegistrationToken, String accessToken) {
        UserClient userClient = ClientGenerator.createClient(UserClient.class);
        Call<ResponseBody> call = userClient.registerDeviceToFcm(fcmRegistrationToken, Utils.getHeader(accessToken));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("FCM Sync Status", "Fcm registration successfully");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d(" FCM Sync Status", "Fcm registration failure");
            }
        });
    }

    public void changePassword(ChangePassword changePassword, String accessToken, final StatusCallback statusCallback) {
        UserClient userClient = ClientGenerator.createClient(UserClient.class);
        Call<ResponseBody> call = userClient.changePassword(changePassword, Utils.getHeader(accessToken));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    statusCallback.onSuccess();
                } else if (response.code() == 400) {
                    statusCallback.onFailure(new Error("Current password do not match"));
                } else {
                    statusCallback.onFailure(new Error("Unable to change password"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                statusCallback.onFailure(new Error("Unable to change password"));
            }
        });
    }


}
