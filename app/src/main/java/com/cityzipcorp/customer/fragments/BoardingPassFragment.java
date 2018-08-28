package com.cityzipcorp.customer.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cityzipcorp.customer.R;
import com.cityzipcorp.customer.base.BaseFragment;
import com.cityzipcorp.customer.callbacks.DialogCallback;
import com.cityzipcorp.customer.model.Attendance;
import com.cityzipcorp.customer.model.BoardingPass;
import com.cityzipcorp.customer.model.GeoJsonPoint;
import com.cityzipcorp.customer.model.NextTrip;
import com.cityzipcorp.customer.model.TrackRide;
import com.cityzipcorp.customer.mvp.boardingpass.BoardingPassPresenter;
import com.cityzipcorp.customer.mvp.boardingpass.BoardingPassPresenterImpl;
import com.cityzipcorp.customer.mvp.boardingpass.BoardingPassView;
import com.cityzipcorp.customer.utils.CalenderUtil;
import com.cityzipcorp.customer.utils.Constants;
import com.cityzipcorp.customer.utils.LocationUtils;
import com.cityzipcorp.customer.utils.NetworkUtils;
import com.cityzipcorp.customer.utils.SharedPreferenceManagerConstant;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by anilpathak on 02/11/17.
 */

public class
BoardingPassFragment extends BaseFragment implements BoardingPassView, SwipeRefreshLayout.OnRefreshListener, LocationListener {

    @BindView(R.id.layout_vehicle_details)
    LinearLayout layoutVehicleDetails;

    @BindView(R.id.card_boarding_pass)
    CardView cardBoardingPass;

    @BindView(R.id.scan_layout)
    LinearLayout scanLayout;

    @BindView(R.id.txt_date)
    TextView txtDate;

    @BindView(R.id.txt_start_time)
    TextView txtStartTime;

    @BindView(R.id.txt_no_boarding_pass)
    TextView txtNoBoardingPass;

    @BindView(R.id.txt_vehicle_no)
    TextView txtVehicleNo;

    @BindView(R.id.txt_otp)
    TextView txtOtp;

    @BindView(R.id.txt_vehicle_name)
    TextView txtVehicleName;

    @BindView(R.id.img_vehicle)
    ImageView imgVehicle;

    @BindView(R.id.txt_address)
    TextView txtAddress;

    @BindView(R.id.layout_otp)
    LinearLayout layoutOtp;

    @BindView(R.id.layout_attendance)
    LinearLayout layoutAttendance;

    @BindView(R.id.btn_track_my_ride)
    Button btnTrackMyRide;

    @BindView(R.id.btn_sos)
    Button btnSOS;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.card_next_trip)
    CardView cardNextTrip;

    @BindView(R.id.txt_next_trip_details)
    TextView txtNextTripDetails;

    private GoogleApiClient googleApiClient;
    private BoardingPass boardingPass;
    private BoardingPassPresenter boardingPassPresenter;
    private LocationUtils locationUtils;
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean sosRequested = false;
    private boolean attendanceMarked = false;
    private String vehicleNumber;
    private Unbinder unbinder;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getPassDetails();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getScreenShotStatus()) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_borading_pass, container, false);
        unbinder = ButterKnife.bind(this, view);
        locationUtils = new LocationUtils(activity);
        buildGoogleApiClient();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        locationUtils.enableGps(googleApiClient);
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (locationUtils.checkLocationPermission()) {
            assert locationManager != null;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, this);
        }

        init();

        return view;
    }

    private void init() {
        boardingPassPresenter = new BoardingPassPresenterImpl(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        getPassDetails();
                                    }
                                }
        );
        activity.registerReceiver(mMessageReceiver, new IntentFilter("fcm_data"));
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        activity.unregisterReceiver(mMessageReceiver);
        mMessageReceiver = null;
        unbinder.unbind();
        uiUtils = null;
        locationUtils = null;
        boardingPassPresenter = null;
        boardingPass = null;
        googleApiClient.disconnect();
        mFusedLocationClient = null;
        googleApiClient = null;
        vehicleNumber = null;
    }

    public void onMessageEvent() {
        getPassDetails();
    }

    @OnClick(R.id.btn_track_my_ride)
    void trackMyRide() {
        if (NetworkUtils.isNetworkAvailable(activity)) {
            if (locationUtils.checkLocationPermission()) {
                if (locationUtils.isLocationEnabled()) {
                    mFusedLocationClient.getLastLocation()
                            .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations, this can be null.
                                    if (location != null) {
                                        getRideDetails(location);
                                    }
                                }
                            });
                } else {
                    locationUtils.enableGps(googleApiClient);
                }
            } else {
                locationUtils.requestLocationPermission();
            }
        } else {
            uiUtils.noInternetDialog();
        }
    }

    private void getRideDetails(Location location) {
        boardingPassPresenter.getRideDetails(sharedPreferenceUtils.getValue(SharedPreferenceManagerConstant.BASE_URL),
                location, boardingPass.getId(), sharedPreferenceUtils.getValue(SharedPreferenceManagerConstant.ACCESS_TOKEN), activity.macId);
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(activity).
                addConnectionCallbacks(activity).
                addOnConnectionFailedListener(activity).
                addApi(LocationServices.API).build();
        googleApiClient.connect();
    }

    public void getPassDetails() {
        if (NetworkUtils.isNetworkAvailable(activity)) {
            swipeRefreshLayout.setRefreshing(true);
            boardingPassPresenter.getBoardingPass(
                    sharedPreferenceUtils.getValue(SharedPreferenceManagerConstant.BASE_URL),
                    sharedPreferenceUtils.getValue(SharedPreferenceManagerConstant.ACCESS_TOKEN),
                    activity.macId);
        } else {
            uiUtils.noInternetDialog();
        }
    }

    private void setPassDetails(BoardingPass boardingPass) {
        try {
            cardNextTrip.setVisibility(View.GONE);
            this.boardingPass = boardingPass;
            if (activity != null)
                activity.setTitle("Pass ID: #" + boardingPass.getId().substring(0, 8).toUpperCase());
            txtVehicleName.setText(replaceNull(boardingPass.getVehicleColor() + " " + boardingPass.getVehicleType() + " " + boardingPass.getVehicleModel()));
            txtVehicleNo.setText(boardingPass.getVehicleNumber());
            if (boardingPass.isOtp()) {
                layoutOtp.setVisibility(View.VISIBLE);
                scanLayout.setVisibility(View.GONE);
            } else {
                layoutOtp.setVisibility(View.GONE);
                scanLayout.setVisibility(View.VISIBLE);
            }
            txtOtp.setText(String.format(" %s", boardingPass.getOtp()));
            txtAddress.setText(boardingPass.getStopAddress());
            txtAddress.setText(boardingPass.getStopAddress());
            txtDate.setText(CalenderUtil.getPassDateFromDate(boardingPass.getStopTimestamp()));
            txtStartTime.setText(CalenderUtil.getTime(boardingPass.getStopTimestamp()));
            cardBoardingPass.setVisibility(View.VISIBLE);
            startAnimation();
            if (boardingPass.getAttendedAt() != null) {
                attendanceSuccess();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String replaceNull(String value) {
        if (value != null) {
            if (value.contains("null")) {
                return value.replace("null", "");
            } else {
                return value;
            }
        } else {
            return "";
        }
    }

    @OnClick(R.id.scan_layout)
    void onScanPass() {
        new IntentIntegrator(activity).initiateScan();
    }

    @OnClick(R.id.layout_pickup_address)
    void onLayoutAddressClick() {
        if (NetworkUtils.isNetworkAvailable(activity)) {
            if (boardingPass.getTripType().equalsIgnoreCase(Constants.TRIP_TYPE_PICK_UP)) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GeoJsonPoint geoJsonPoint = boardingPass.getStopLocation();
                        Uri intentUri = Uri.parse(getString(R.string.maps_navigation) + geoJsonPoint.getCoordinates()[1] + "," + geoJsonPoint.getCoordinates()[0] + getString(R.string.maps_mode));
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, intentUri);
                        mapIntent.setPackage(getString(R.string.map_package));
                        startActivity(mapIntent);
                    }
                }, 1000);

            }
        } else {
            uiUtils.noInternetDialog();
        }
    }

    @OnClick(R.id.btn_sos)
    void onClickSos() {
        if (locationUtils.checkLocationPermission()) {
            if (locationUtils.isLocationEnabled()) {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations, this can be null.
                                if (location != null) {
                                    if (NetworkUtils.isNetworkAvailable(activity)) {
                                        sosRequested = false;
                                        boardingPassPresenter.sendSos(
                                                sharedPreferenceUtils.getValue(SharedPreferenceManagerConstant.BASE_URL),
                                                location, boardingPass.getId(),
                                                sharedPreferenceUtils.getValue(SharedPreferenceManagerConstant.ACCESS_TOKEN), activity.macId);
                                    } else {
                                        uiUtils.noInternetDialog();
                                    }
                                }
                            }
                        });

            }
        } else {
            uiUtils.notifyDialog("Please enable location to perform sos", new DialogCallback() {
                @Override
                public void onYes() {
                    sosRequested = true;
                    locationUtils.enableGps(googleApiClient);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == locationUtils.LOCATION_REQUEST_CODE) {
            if (sosRequested) {
                onClickSos();
            }
        } else {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() == null) {
                    uiUtils.shortToast("Cancelled");
                } else {
                    scanResult(result.getContents());
                }
            }
        }
    }

    private void scanResult(final String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
        final Attendance attendance = new Attendance();
        attendance.setAttendedAt(Calendar.getInstance().getTime());
        attendance.setAttended(true);
        attendance.setVehicleNumber(vehicleNumber);
        if (NetworkUtils.isNetworkAvailable(activity)) {
            if (locationUtils.checkLocationPermission() && locationUtils.isLocationEnabled()) {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations, this can be null.
                                GeoJsonPoint geoJsonPoint;
                                if (location != null) {
                                    geoJsonPoint = new GeoJsonPoint(location.getLongitude(), location.getLatitude());
                                } else {
                                    geoJsonPoint = new GeoJsonPoint(8.8, 8.8);
                                }
                                attendance.setGeoJsonPoint(geoJsonPoint);
                                markAttendance(attendance);
                            }
                        });
            } else {
                locationUtils.enableGps(googleApiClient);
                attendance.setGeoJsonPoint(new GeoJsonPoint(8.8, 8.8));
                markAttendance(attendance);
            }
        } else {
            uiUtils.noInternetDialog();
        }
    }

    private void markAttendance(Attendance attendance) {
        boardingPassPresenter.markAttendance(sharedPreferenceUtils.getValue(SharedPreferenceManagerConstant.BASE_URL),
                attendance, boardingPass.getId(),
                sharedPreferenceUtils.getValue(SharedPreferenceManagerConstant.ACCESS_TOKEN), activity.macId);
    }


    @Override
    public void showProgress() {
        uiUtils.showProgressDialog();
    }

    @Override
    public void hideProgress() {
        uiUtils.dismissDialog();
    }

    @Override
    public void setPassError(String error) {
        this.boardingPass = null;
        this.clearUI();

        getNextTrip();
    }

    private void clearUI() {
        cardBoardingPass.setVisibility(View.GONE);
        scanLayout.setVisibility(View.GONE);
        layoutAttendance.setVisibility(View.GONE);
        btnSOS.setVisibility(View.GONE);
        btnTrackMyRide.setVisibility(View.GONE);

        if (activity != null)
            activity.setTitle("");

        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void noTrip() {
        cardNextTrip.setVisibility(View.GONE);
        txtNoBoardingPass.setVisibility(View.VISIBLE);
    }

    @Override
    public void setTrackError(String error) {
        uiUtils.shortToast(error);
    }

    @Override
    public void setError(String error) {
        uiUtils.shortToast(error);
    }

    @Override
    public void passDetailSuccess(BoardingPass boardingPass) {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
        setPassDetails(boardingPass);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getPassDetails();
    }

    @Override
    public void trackDetailSuccess(TrackRide trackRide) {
        Date recordedTime = trackRide.getRecordedAt();
        if (recordedTime != null) {
            if (CalenderUtil.getTimeDiff(recordedTime, Calendar.getInstance().getTime()) < 30) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("track", trackRide);
                bundle.putParcelable("boardingPass", boardingPass);
                MapFragment mapFragment = new MapFragment();
                mapFragment.setArguments(bundle);
                activity.replaceFragment(mapFragment, getString(R.string.map_fragment));
                activity.backAllowed = true;
            } else {
                uiUtils.shortToast("Trip not Started yet!");
            }
        } else {
            uiUtils.shortToast("Trip not Started yet!");
        }
    }

    @Override
    public void nextTripSuccess(NextTrip nextTrip) {
        cardNextTrip.setVisibility(View.VISIBLE);
        String tripType = nextTrip.getTripType().equals("pick_up") ? "Pick up" : "Drop";
        String tripDetails = tripType + "," +
                " " + CalenderUtil.getDay(nextTrip.getTimestamp()) +
                " " + CalenderUtil.getMonthAndDate(nextTrip.getTimestamp()) +
                ", " + CalenderUtil.getTime(nextTrip.getTimestamp());
        txtNextTripDetails.setText(tripDetails);
    }

    @Override
    public void attendanceSuccess() {
        scanLayout.setVisibility(View.GONE);
        btnTrackMyRide.setVisibility(View.GONE);
        layoutAttendance.setVisibility(View.VISIBLE);
        btnSOS.setVisibility(View.VISIBLE);
    }

    @Override
    public void sosSuccess() {
        uiUtils.shortToast("SOS request captured successfully! Our team will contact you soon.");
    }

    @Override
    public void onRefresh() {
        getPassDetails();
    }

    private boolean getScreenShotStatus() {
        boolean isAllowed = false;
        if (activity.getPackageName().equalsIgnoreCase("com.cityzipcorp.customer.staging")) {
            isAllowed = true;
        }
        if (activity.getPackageName().equalsIgnoreCase("com.cityzipcorp.customer.preprod")) {
            isAllowed = true;
        }
        return isAllowed;
    }

    private void startAnimation() {
        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(3000);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setRepeatCount(Animation.INFINITE); //Repeat animation indefinitely
        imgVehicle.startAnimation(rotate);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (sosRequested) {
            sosRequested = false;
            onClickSos();
        }
        if (attendanceMarked) {
            attendanceMarked = false;
            final Attendance attendance = new Attendance();
            attendance.setAttendedAt(Calendar.getInstance().getTime());
            attendance.setAttended(true);
            attendance.setVehicleNumber(vehicleNumber);
            attendance.setGeoJsonPoint(new GeoJsonPoint(location.getLongitude(), location.getLatitude()));
            markAttendance(attendance);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {


    }

    @Override
    public void onProviderDisabled(String s) {
    }

    private void getNextTrip() {
        if (NetworkUtils.isNetworkAvailable(activity)) {
            boardingPassPresenter.getNextTrip(sharedPreferenceUtils.getValue(SharedPreferenceManagerConstant.BASE_URL), sharedPreferenceUtils.getValue(SharedPreferenceManagerConstant.ACCESS_TOKEN), activity.macId);
        } else {
            uiUtils.noInternetDialog();

        }
    }


}