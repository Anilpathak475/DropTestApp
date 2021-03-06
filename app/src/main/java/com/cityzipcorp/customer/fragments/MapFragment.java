package com.cityzipcorp.customer.fragments;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cityzipcorp.customer.R;
import com.cityzipcorp.customer.base.BaseFragment;
import com.cityzipcorp.customer.callbacks.TrackRideCallback;
import com.cityzipcorp.customer.model.BoardingPass;
import com.cityzipcorp.customer.model.GeoJsonPoint;
import com.cityzipcorp.customer.model.TrackRide;
import com.cityzipcorp.customer.store.BoardingPassStore;
import com.cityzipcorp.customer.utils.CalenderUtil;
import com.cityzipcorp.customer.utils.LocationUtils;
import com.cityzipcorp.customer.utils.NetworkUtils;
import com.cityzipcorp.customer.utils.SharedPreferenceManagerConstant;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.cityzipcorp.customer.utils.Utils.replaceNull;

public class MapFragment extends BaseFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    final Handler handler = new Handler();
    MapView mMapView;
    GoogleMap googleMap;
    @BindView(R.id.txt_vehicle_no)
    TextView txtVehicleNo;
    @BindView(R.id.txt_otp)
    TextView txtOtp;
    @BindView(R.id.txt_vehicle_name)
    TextView txtVehicleName;
    @BindView(R.id.txt_eta)
    TextView txtEta;
    @BindView(R.id.txt_last_update)
    TextView txtLastUpdated;
    @BindView(R.id.layout_otp)
    LinearLayout layoutOtp;
    @BindView(R.id.center_map)
    ImageButton centerMap;

    private boolean isMarkerRotating = false;
    private Marker vehicleMarker;
    private Marker serviceMarker;
    private LocationUtils locationUtils;
    private GoogleApiClient googleApiClient;
    private TrackRide trackRide;
    private BoardingPass boardingPass;
    private LatLngBounds bounds;
    private LatLngBounds.Builder builder;
    private Runnable locationRequestRunnable = new Runnable() {
        @Override
        public void run() {
            requestLatestTrackLocations();
            handler.postDelayed(this, 10000);
        }
    };
    private FusedLocationProviderClient mFusedLocationClient;
    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(activity);
        activity.setTitle("Your Ride");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        unbinder = ButterKnife.bind(this, view);
        init(savedInstanceState, view);
        return view;
    }

    private void init(@Nullable Bundle savedInstanceState, View view) {
        locationUtils = new LocationUtils(activity);
        buildGoogleApiClient();
        initLocation();
        getBundleExtra();
        mMapView = view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        startTrackingRepeated();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        handler.removeCallbacks(locationRequestRunnable);
    }

    private void startTrackingRepeated() {
        handler.postDelayed(locationRequestRunnable, 10000);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (locationUtils.checkLocationPermission()) {
            if (!locationUtils.isLocationEnabled()) {
                locationUtils.enableGps(googleApiClient);
            }
            googleMap.setMyLocationEnabled(true);
        }
        trackMyRide();
        MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(activity, R.raw.grey_map_style);
        googleMap.setMapStyle(mapStyleOptions);
    }

    private void initLocation() {
        if (!locationUtils.checkLocationPermission()) {
            locationUtils.requestLocationPermission();
        } else if (!locationUtils.isLocationEnabled()) {
            locationUtils.enableGps(googleApiClient);
        }
    }

    @OnClick(R.id.center_map)
    void onRecenterMap() {
        recenterMap();
    }

    private void getBundleExtra() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            trackRide = bundle.getParcelable("track");
            boardingPass = bundle.getParcelable("boardingPass");
        }
    }

    private void requestLatestTrackLocations() {
        if (locationUtils.checkLocationPermission()) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations, this can be null.
                            if (location != null) {
                                Log.d("Location", "my location is " + location.toString());
                                fetchTripDetailsOnMap(location);
                            } else {
                                uiUtils.shortToast("Unable to fetch location");
                            }
                        }
                    });

        } else {
            locationUtils.requestLocationPermission();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(activity).addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        googleApiClient.connect();
    }

    private void fetchTripDetailsOnMap(Location location) {
        if (NetworkUtils.isNetworkAvailable(activity)) {
            BoardingPassStore.getInstance(sharedPreferenceUtils.getValue(SharedPreferenceManagerConstant.BASE_URL), activity.macId).
                    trackMyRide(boardingPass.getId(), String.valueOf(location.getLatitude()),
                            String.valueOf(location.getLongitude()), sharedPreferenceUtils.getValue(SharedPreferenceManagerConstant.ACCESS_TOKEN),
                            new TrackRideCallback() {
                                @Override
                                public void onSuccess(TrackRide trackRide) {
                                    uiUtils.dismissDialog();
                                    if (trackRide != null) {
                                        drawTripDetailsOnMap(trackRide);
                                    }
                                }

                                @Override
                                public void onFailure(Error error) {
                                    uiUtils.dismissDialog();
                                    uiUtils.shortToast(error.getLocalizedMessage());
                                }
                            });
        } else {
            uiUtils.noInternetDialog();
        }
    }

    private void trackMyRide() {

        if ((boardingPass != null) && (trackRide != null)) {
            txtVehicleName.setText(replaceNull(boardingPass.getVehicleColor() + " " + boardingPass.getVehicleType() + " " + boardingPass.getVehicleModel()));
            txtVehicleNo.setText(boardingPass.getVehicleNumber());

            if (boardingPass.isOtp()) {
                layoutOtp.setVisibility(View.VISIBLE);
                txtOtp.setText(String.format(" %s", boardingPass.getOtp()));
            } else {
                layoutOtp.setVisibility(View.GONE);
            }
            if (trackRide.getEta() >= 0) {
                long minutes = TimeUnit.MILLISECONDS.toMinutes(trackRide.getEta());
                txtEta.setText(String.format("%s m", String.valueOf(minutes)));
            }
            drawTripDetailsOnMap(trackRide);
            startTrackingRepeated();
        }
    }

    private void drawTripDetailsOnMap(TrackRide trackRide) {
        if (googleMap != null) {
            builder = new LatLngBounds.Builder();
            if (trackRide.getServiceLocation() != null) {
                GeoJsonPoint geoJsonPoint = trackRide.getServiceLocation();
                LatLng latLng = new LatLng(geoJsonPoint.getCoordinates()[1], geoJsonPoint.getCoordinates()[0]);
                if (serviceMarker == null) {
                    serviceMarker = googleMap.addMarker(new MarkerOptions().
                            position(latLng).
                            title("Service Location").icon(BitmapDescriptorFactory.
                            fromBitmap(getBitmapBySize(R.drawable.service_location_pin))));

                }
                builder.include(latLng);
            }

            if (trackRide.getVehicleLocation() != null) {
                GeoJsonPoint geoJsonPoint = trackRide.getVehicleLocation();
                LatLng latLng = new LatLng(geoJsonPoint.getCoordinates()[1], geoJsonPoint.getCoordinates()[0]);
                if (vehicleMarker == null) {
                    vehicleMarker = googleMap.addMarker(new MarkerOptions().
                            position(latLng).
                            title("Vehicle Location").icon(BitmapDescriptorFactory.
                            fromBitmap(getCarBitmap(R.drawable.track_cab_pin))));

                } else {
                    //   float rotation = (float) bearingBetweenLocations(vehicleMarker.getPosition(), latLng);
                    animateMarker(vehicleMarker, latLng, false);
                }
                builder.include(latLng);
            }
            if (trackRide.getRecordedAt() != null) {
                txtLastUpdated.setText(CalenderUtil.getTime(trackRide.getRecordedAt()));
            }

            if (bounds == null) {
                recenterMap();
            }
        }
    }

    private void recenterMap() {
        bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        googleMap.animateCamera(cu);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            LocationRequest locationRequest = getLocationRequest();
            if (locationUtils.checkLocationPermission()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
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


    private Bitmap getBitmapBySize(int iconResID) {
        Drawable drawable = ContextCompat.getDrawable(activity, iconResID);
        assert drawable != null;
        drawable = (DrawableCompat.wrap(drawable)).mutate();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return Bitmap.createScaledBitmap(bitmap, 80, 120, false);
    }

    private Bitmap getCarBitmap(int iconResID) {
        Drawable drawable = ContextCompat.getDrawable(activity, iconResID);
        assert drawable != null;
        drawable = (DrawableCompat.wrap(drawable)).mutate();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return Bitmap.createScaledBitmap(bitmap, 30, 60, false);
    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection projection = googleMap.getProjection();
        Point startPoint = projection.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = projection.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    private double bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }

    private void rotateMarker(final Marker marker, final float toRotation, final LatLng latLng) {
        if (!isMarkerRotating) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 1000;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;

                    marker.setRotation(-rot > 180 ? rot / 2 : rot);
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        isMarkerRotating = false;
                    }
                    marker.setPosition(latLng);
                }
            });
        }
    }

}