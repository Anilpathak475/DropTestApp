package com.cityzipcorp.customer.activities;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cityzipcorp.customer.base.BaseActivity;
import com.cityzipcorp.customer.callbacks.DialogCallback;
import com.cityzipcorp.customer.callbacks.ProfileStatusCallback;
import com.cityzipcorp.customer.fragments.BoardingPassFragment;
import com.cityzipcorp.customer.fragments.ChangePasswordFragment;
import com.cityzipcorp.customer.fragments.EditProfileFragment;
import com.cityzipcorp.customer.fragments.GroupAndShiftFragment;
import com.cityzipcorp.customer.fragments.ProfileFragment;
import com.cityzipcorp.customer.fragments.ScheduleFragment;
import com.cityzipcorp.customer.model.FcmRegistrationToken;
import com.cityzipcorp.customer.model.ProfileStatus;
import com.cityzipcorp.customer.store.UserStore;
import com.cityzipcorp.customer.utils.Constants;
import com.cityzipcorp.customer.utils.LocationUtils;
import com.cityzipcorp.customer.utils.SharedPreferenceManager;
import com.cityzipcorp.customer.utils.UiUtils;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cityzipcorp.customer.R.id;
import static com.cityzipcorp.customer.R.layout;
import static com.cityzipcorp.customer.R.string;

public class HomeActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    public boolean backAllowed = false;
    public boolean inSchedule = false;
    public boolean inProfile = false;
    @BindView(id.navigation)
    BottomNavigationView navigationView;
    @BindView(id.toolbar)
    android.support.v7.widget.Toolbar toolbar;
    @BindView(id.btn_back)
    public Button btnBack;
    @BindView(id.txt_title)
    TextView txtTitle;
    @BindView(id.btn_activate_bulk_mode)
    Button btnActivateBulkMode;
    @BindView(id.btn_done_bulk_mode)
    Button btnDoneBulkMode;
    @BindView(id.btn_save_profile)
    Button btnSaveProfile;
    @BindView(id.btn_refresh)
    ImageView imgRefresh;
    @BindView(id.img_logout)
    ImageView imgLogout;
    @BindView(id.btn_update_password)
    Button btnUpdatePassword;

    private int LOCATION_PERMISSION = 101;
    private UiUtils uiUtils;
    boolean bulkModeActivated = false;
    private LocationUtils locationUtils;
    SharedPreferenceManager sharedPreferenceManager;
    private GoogleApiClient googleApiClient;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case id.boarding_pass:
                    clearBackStack();
                    replaceFragment(new BoardingPassFragment(), getString(string.boarding_pass));
                    backAllowed = false;
                    return true;
                case id.schedule:
                    clearBackStack();
                    replaceFragment(new ScheduleFragment(), getString(string.schedule));
                    backAllowed = false;
                    return true;
                case id.profile:
                    clearBackStack();
                    replaceFragment(new ProfileFragment(), getString(string.profile));
                    backAllowed = false;
                    return true;
            }
            return false;
        }
    };

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshDuties();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        logUser();
        setContentView(layout.activity_home);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        buildGoogleApiClient();
        initVariables();
        checkProfileStatus();
        checkLocationStatus();
        updateFcmTokenOnServer();
    }

    private void checkProfileStatus() {
        uiUtils.showProgressDialog();
        UserStore.getInstance().getProfileStatus(sharedPreferenceManager.getAccessToken(), new ProfileStatusCallback() {
            @Override
            public void onSuccess(ProfileStatus profileStatus) {
                uiUtils.dismissDialog();
                validateProfileStatus(profileStatus);
            }

            @Override
            public void onFailure(Error error) {
                uiUtils.dismissDialog();
                setUpBottomNavigationView(0);
                replaceFragment(new BoardingPassFragment(), getString(string.boarding_pass));
            }
        });
    }

    private void validateProfileStatus(ProfileStatus profileStatus) {
        if (!profileStatus.isGenderUpdated()) {
            navigationView.setVisibility(View.GONE);
            onInCompleteProfile(new EditProfileFragment());
            onTrackingStart();
        } else if (!profileStatus.isShiftUpdated()) {
            navigationView.setVisibility(View.GONE);
            onInCompleteProfile(new GroupAndShiftFragment());
            onTrackingStart();
        } else if (!profileStatus.isNodalUpdated() || !profileStatus.isHomeUpdated()) {
            setUpBottomNavigationView(2);
        } else {
            setUpBottomNavigationView(0);
        }
    }

    private void onInCompleteProfile(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.PROFILE_STATUS, Constants.PROFILE_STATUS_IN_COMPLETED);
        fragment.setArguments(bundle);
        replaceFragment(fragment, getString(string.edit_profile));
    }

    private void initVariables() {
        locationUtils = new LocationUtils(this);
        sharedPreferenceManager = new SharedPreferenceManager(this);
        uiUtils = new UiUtils(this);
        registerReceiver(mMessageReceiver, new IntentFilter("fcm_data"));
    }

    public void setUpBottomNavigationView(int index) {
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        addFragmentByIndex(index);
        navigationView.setVisibility(View.VISIBLE);
        navigationView.getMenu().getItem(index).setChecked(true);


    }

    private void addFragmentByIndex(int index) {
        if (index == 0) {
            clearBackStack();
            replaceFragment(new BoardingPassFragment(), getString(string.boarding_pass));
            backAllowed = false;
        } else if (index == 1) {
            clearBackStack();
            replaceFragment(new ScheduleFragment(), getString(string.schedule));
        } else {
            clearBackStack();
            replaceFragment(new ProfileFragment(), getString(string.profile));
            backAllowed = false;
        }
    }

    private void checkLocationStatus() {
        if (!locationUtils.checkLocationPermission()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    locationUtils.requestLocationPermission();
                }
            }, 1000);

        } else {
            if (!locationUtils.isLocationEnabled()) {
                locationUtils.enableGps(googleApiClient);
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        googleApiClient.connect();
    }

    @OnClick(id.btn_back)
    void onProfileBack() {
        onBackPressed();
    }

    @OnClick(id.btn_activate_bulk_mode)
    void onBulkModeActivate() {
        ScheduleFragment scheduleFragment = (ScheduleFragment) getSupportFragmentManager().findFragmentById(id.fragment_container);
        scheduleFragment.startBulkMode();
        bulkModeActivated = true;
        btnActivateBulkMode.setVisibility(View.GONE);
        btnDoneBulkMode.setVisibility(View.VISIBLE);
    }

    @OnClick(id.btn_done_bulk_mode)
    void onBulkModeDone() {
        ScheduleFragment scheduleFragment = (ScheduleFragment) getSupportFragmentManager().findFragmentById(id.fragment_container);
        scheduleFragment.doneBulkDone();
        bulkModeActivated = false;
        btnActivateBulkMode.setVisibility(View.VISIBLE);
        btnDoneBulkMode.setVisibility(View.GONE);
    }

    @OnClick(id.btn_refresh)
    void onRefreshBoardingPass() {
        BoardingPassFragment boardingPassFragment = (BoardingPassFragment) getSupportFragmentManager().findFragmentById(id.fragment_container);
        boardingPassFragment.getPassDetails();
    }


    private void refreshDuties() {

    }

    public void onTrackingStart() {
        navigationView.setVisibility(View.GONE);
    }

    public void onTrackingEnd() {
        navigationView.setVisibility(View.VISIBLE);
    }

    @OnClick(id.btn_save_profile)
    void onProfileUpdate() {
        EditProfileFragment editProfileFragment = (EditProfileFragment) getSupportFragmentManager().findFragmentById(id.fragment_container);
        editProfileFragment.onSave();
    }

    @OnClick(id.btn_update_password)
    void onUpdatePassword() {
        ChangePasswordFragment changePasswordFragment = (ChangePasswordFragment) getSupportFragmentManager().findFragmentById(id.fragment_container);
        changePasswordFragment.onUpdatePassword();
    }

    @OnClick(id.img_logout)
    void onLogout() {
        new UiUtils(this).getAlertDialogWithMessage(" Are you sure you want to logout ?.", new DialogCallback() {
            @Override
            public void onYes() {
                navigateToLogin();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public String getFragmentTag() {
        int index = getSupportFragmentManager().getBackStackEntryCount() - 1;
        FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(index);
        return backEntry.getName();
    }

    public void replaceFragment(Fragment fragment, String tag) {
        inProfile = false;
        inSchedule = false;
        getSupportFragmentManager().beginTransaction().replace(id.fragment_container, fragment, tag).addToBackStack(tag).commit();
        setUpToolbar(tag);
        setTitle(tag);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(getFragmentTag());
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION) {
            if (!locationUtils.isLocationEnabled()) {
                locationUtils.enableGps(googleApiClient);
            }
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(getFragmentTag());
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onBackPressed() {
        setDefaultToolbarState();
        if (backAllowed) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                getSupportFragmentManager().popBackStack();
            }
        } else {
            finish();
        }
    }

    private void clearBackStack() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    private void navigateToLogin() {
        sharedPreferenceManager.clearAccessToken();
        sharedPreferenceManager.saveAccessToken("");
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    private void setUpToolbar(String pageName) {
        setDefaultToolbarState();
        if (pageName.equalsIgnoreCase(getString(string.edit_profile))) {
            btnSaveProfile.setVisibility(View.VISIBLE);
            btnBack.setVisibility(View.VISIBLE);
        }
        /*if (pageName.equalsIgnoreCase(getString(string.change_password))) {
            btnSaveProfile.setVisibility(View.VISIBLE);
            btnBack.setVisibility(View.VISIBLE);
        }*/

        if (pageName.equalsIgnoreCase(getString(string.boarding_pass))) {
            imgRefresh.setVisibility(View.VISIBLE);
        }

        if (pageName.equalsIgnoreCase(getString(string.profile))) {
            imgLogout.setVisibility(View.VISIBLE);
        }

        if (pageName.equalsIgnoreCase(getString(string.map_fragment))) {
            btnBack.setVisibility(View.VISIBLE);
        }
    }

    private void setDefaultToolbarState() {
        btnBack.setVisibility(View.GONE);
        btnActivateBulkMode.setVisibility(View.GONE);
        btnDoneBulkMode.setVisibility(View.GONE);
        btnSaveProfile.setVisibility(View.GONE);
        btnUpdatePassword.setVisibility(View.GONE);
        imgRefresh.setVisibility(View.GONE);
        imgLogout.setVisibility(View.GONE);
    }

    private void logUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserIdentifier("12345");
        Crashlytics.setUserEmail("anil@theprocedure.in");
        Crashlytics.setUserName("Anil Pathak");
    }

    @Override
    public void setTitle(String title) {
        if (txtTitle != null) txtTitle.setText(title);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    private void updateFcmTokenOnServer() {
        FcmRegistrationToken fcmRegistrationToken = new FcmRegistrationToken();
        fcmRegistrationToken.setName("userName");
        fcmRegistrationToken.setCloudMessageType("FCM");
        fcmRegistrationToken.setRegistrationId(sharedPreferenceManager.getFcmToken());
        fcmRegistrationToken.setDeviceId("");
        fcmRegistrationToken.setApplicationId("");
        UserStore.getInstance().registerFcmToken(fcmRegistrationToken, sharedPreferenceManager.getAccessToken());
    }
}
