package com.cityzipcorp.customer.fragments;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cityzipcorp.customer.R;
import com.cityzipcorp.customer.activities.HomeActivity;
import com.cityzipcorp.customer.base.BaseFragment;
import com.cityzipcorp.customer.callbacks.TrackRideCallback;
import com.cityzipcorp.customer.model.BoardingPass;
import com.cityzipcorp.customer.model.GeoJsonPoint;
import com.cityzipcorp.customer.model.TrackRide;
import com.cityzipcorp.customer.store.BoardingPassStore;
import com.cityzipcorp.customer.utils.CalenderUtil;
import com.cityzipcorp.customer.utils.LocationUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    private Marker vehicleMarker;
    private Marker serviceMarker;
    private Marker userLocationMarker;
    private LocationUtils locationUtils;
    private List<LatLng> markers = new ArrayList<>();

    private GoogleApiClient googleApiClient;
    private TrackRide trackRide;
    private BoardingPass boardingPass;

    private Runnable locationRequestRunnable = new Runnable() {
        @Override
        public void run() {
            //Do Something
            requestLatestTrackLocations();
            handler.postDelayed(this, 10000);

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(activity);
        ((HomeActivity) activity).setTitle("Your Ride");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);
        init(savedInstanceState, view);
        return view;
    }

    private void init(@Nullable Bundle savedInstanceState, View view) {
        locationUtils = new LocationUtils(activity);
        buildGoogleApiClient();
        mMapView = view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getString(R.string.boarding_pass));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
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
        getBundleExtra();

        MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(activity, R.raw.grey_map_style);
        googleMap.setMapStyle(mapStyleOptions);

    }

    private boolean checkLocationPermission() {
        return ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

    private void getBundleExtra() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            trackRide = bundle.getParcelable("track");
            boardingPass = bundle.getParcelable("boardingPass");
            drawTripDetailsOnMap(trackRide);
            trackMyRide();
        }
    }

    private void requestLatestTrackLocations() {

        if (checkLocationPermission()) return;
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            Log.d("Location", "my location is " + location.toString());
            fetchTripDetailsOnMap(location);
        } else {
            uiUtils.shortToast("Unable to fetch location");
        }
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(activity).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        googleApiClient.connect();
    }

    private void fetchTripDetailsOnMap(Location location) {
        BoardingPassStore.getInstance().trackMyRide(boardingPass.getId(), String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), sharedPreferenceUtils.getAccessToken(),
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
    }

    private void trackMyRide() {

        if ((boardingPass != null) && (trackRide != null)) {
            txtVehicleName.setText(replaceNull(boardingPass.getVehicleColor() + " " + boardingPass.getVehicleType() + " " + boardingPass.getVehicleModel()));
            txtVehicleNo.setText(boardingPass.getVehicleNumber());

            if (boardingPass.getOtp().equalsIgnoreCase("")) {
                layoutOtp.setVisibility(View.GONE);
            } else {
                layoutOtp.setVisibility(View.VISIBLE);
                txtOtp.setText(boardingPass.getOtp());
            }
            if (trackRide.getEta() >= 0) {
                long minutes = TimeUnit.MILLISECONDS.toMinutes(trackRide.getEta());
                txtEta.setText(String.valueOf(minutes) + " m");
            }

            startTrackingRepeated();
        }
    }

    private void drawTripDetailsOnMap(TrackRide trackRide) {
        markers.clear();
        if (googleMap != null) {
            if (trackRide.getServiceLocation() != null) {
                GeoJsonPoint geoJsonPoint = trackRide.getServiceLocation();
                LatLng latLng = new LatLng(geoJsonPoint.getCoordinates()[1], geoJsonPoint.getCoordinates()[0]);
                if (serviceMarker == null) {
                    serviceMarker = googleMap.addMarker(new MarkerOptions().
                            position(latLng).
                            title("Service Location").icon(BitmapDescriptorFactory.
                            fromBitmap(getBitmapBySize(R.drawable.service_location_pin, 70, 100))));
                    markers.add(latLng);
                }
            }

            if (trackRide.getVehicleLocation() != null) {
                GeoJsonPoint geoJsonPoint = trackRide.getVehicleLocation();
                LatLng latLng = new LatLng(geoJsonPoint.getCoordinates()[1], geoJsonPoint.getCoordinates()[0]);
                if (vehicleMarker == null) {
                    vehicleMarker = googleMap.addMarker(new MarkerOptions().
                            position(latLng).
                            title("Vehicle Location").icon(BitmapDescriptorFactory.
                            fromBitmap(getBitmapBySize(R.drawable.ic_car_placeholder, 70, 100))));
                    markers.add(latLng);

                } else {
                    animateMarker(vehicleMarker, latLng, false);
                    markers.add(latLng);
                }
            }
            drawRootAndCenterBoundMap();
            if (trackRide.getRecordedAt() != null) {
                txtLastUpdated.setText(CalenderUtil.getTime(trackRide.getRecordedAt()));
            }
        }
    }


    private void drawRootAndCenterBoundMap() {
        try {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            builder.include(markers.get(0));
            builder.include(markers.get(1));

            LatLngBounds bounds = builder.build();

            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.50); // offset from edges of the map 10% of screen

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            googleMap.animateCamera(cu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            LocationRequest locationRequest = getLocationRequest();
            if (checkLocationPermission()) return;
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

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
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (userLocationMarker == null) {
            userLocationMarker = googleMap.addMarker(new MarkerOptions().
                    position(latLng).
                    title("Service Location").icon(BitmapDescriptorFactory.
                    fromBitmap(getBitmapBySize(R.drawable.home_pin, 70, 100))));
        } else {
            animateMarker(vehicleMarker, latLng, false);
        }
    }


    private Bitmap getBitmapBySize(int iconResID, int width, int height) {
        Drawable drawable = ContextCompat.getDrawable(activity, iconResID);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

    private LatLng getCurrentLocation() {
        //Creating a location object
        LatLng latLng = null;

        if (checkLocationPermission()) return latLng;
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (googleApiClient == null) {
            buildGoogleApiClient();
        }
        if (location != null) {
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
        }
        return latLng;
    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = googleMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
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
}