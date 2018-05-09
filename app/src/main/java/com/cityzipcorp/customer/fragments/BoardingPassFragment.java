package com.cityzipcorp.customer.fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cityzipcorp.customer.R;
import com.cityzipcorp.customer.activities.HomeActivity;
import com.cityzipcorp.customer.base.BaseFragment;
import com.cityzipcorp.customer.model.BoardingPass;
import com.cityzipcorp.customer.model.GeoJsonPoint;
import com.cityzipcorp.customer.model.TrackRide;
import com.cityzipcorp.customer.mvp.boardingpass.BoardingPassPresenter;
import com.cityzipcorp.customer.mvp.boardingpass.BoardingPassPresenterImpl;
import com.cityzipcorp.customer.mvp.boardingpass.BoardingPassView;
import com.cityzipcorp.customer.utils.CalenderUtil;
import com.cityzipcorp.customer.utils.Constants;
import com.cityzipcorp.customer.utils.LocationUtils;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by anilpathak on 02/11/17.
 */

public class BoardingPassFragment extends BaseFragment implements BoardingPassView, SwipeRefreshLayout.OnRefreshListener {

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

    private GoogleApiClient googleApiClient;
    private BoardingPass boardingPass;
    private BoardingPassPresenter boardingPassPresenter;
    private LocationUtils locationUtils;
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_borading_pass, container, false);
        ButterKnife.bind(this, view);
        locationUtils = new LocationUtils(activity);
        buildGoogleApiClient();

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
    }

    @OnClick(R.id.btn_track_my_ride)
    void trackMyRide() {
        if (locationUtils.checkLocationPermission()) {
            if (locationUtils.isLocationEnabled()) {
                Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                getRideDetails(location);


            } else {
                locationUtils.enableGps(googleApiClient);
            }
        } else {
            locationUtils.requestLocationPermission();
        }
    }

    private void getRideDetails(Location location) {
        boardingPassPresenter.getRideDetails(location, boardingPass.getId(), sharedPreferenceUtils.getAccessToken());
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(activity).addConnectionCallbacks(activity).addOnConnectionFailedListener(activity).addApi(LocationServices.API).build();
        googleApiClient.connect();
    }

    public void getPassDetails() {
        swipeRefreshLayout.setRefreshing(true);
        boardingPassPresenter.getBoardingPass(sharedPreferenceUtils.getAccessToken());
    }

    private void setPassDetails(BoardingPass boardingPass) {
        try {

            this.boardingPass = boardingPass;
            if (activity != null)
                activity.setTitle("Pass ID :#" + boardingPass.getId().substring(0, 8).toUpperCase());
            txtVehicleName.setText(replaceNull(boardingPass.getVehicleColor() + " " + boardingPass.getVehicleType() + " " + boardingPass.getVehicleModel()));
            txtVehicleNo.setText(boardingPass.getVehicleNumber());
            if (boardingPass.getOtp().equalsIgnoreCase("")) {
                layoutOtp.setVisibility(View.GONE);
            } else {
                layoutOtp.setVisibility(View.VISIBLE);
            }
            txtOtp.setText(boardingPass.getOtp());
            txtAddress.setText(boardingPass.getStopAddress());
            txtAddress.setText(boardingPass.getStopAddress());
            txtDate.setText(CalenderUtil.getPassDateFromDate(boardingPass.getStopTimestamp()));
            txtStartTime.setText(CalenderUtil.getTime(boardingPass.getStopTimestamp()));
            cardBoardingPass.setVisibility(View.VISIBLE);
            scanLayout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
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
    }

    @OnClick(R.id.btn_sos)
    void onClickSos() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        boardingPassPresenter.sendSos(location, boardingPass.getId(), sharedPreferenceUtils.getAccessToken());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                uiUtils.shortToast("Cancelled");
            } else {
                markAttendance();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void markAttendance() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        boardingPassPresenter.markAttendance(location, boardingPass.getId(), sharedPreferenceUtils.getAccessToken());

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
        swipeRefreshLayout.setRefreshing(false);
        cardBoardingPass.setVisibility(View.GONE);
        scanLayout.setVisibility(View.GONE);
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
        swipeRefreshLayout.setRefreshing(false);
        setPassDetails(boardingPass);
    }

    @Override
    public void trackDetailSuccess(TrackRide trackRide) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("track", trackRide);
        bundle.putParcelable("boardingPass", boardingPass);
        MapFragment mapFragment = new MapFragment();
        mapFragment.setArguments(bundle);
        ((HomeActivity) getActivity()).replaceFragment(mapFragment, getString(R.string.map_fragment));
        ((HomeActivity) getActivity()).backAllowed = true;
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
        boolean isAllowed = true;
        if (activity.getPackageName().equalsIgnoreCase("com.cityzipcorp.customer.staging")) {
            isAllowed = false;
        }
        if (activity.getPackageName().equalsIgnoreCase("com.cityzipcorp.customer.preprod")) {
            isAllowed = false;
        }
        return isAllowed;
    }
}