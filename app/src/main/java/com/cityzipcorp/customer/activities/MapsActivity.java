package com.cityzipcorp.customer.activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cityzipcorp.customer.R;
import com.cityzipcorp.customer.callbacks.NodalStopCallback;
import com.cityzipcorp.customer.callbacks.StatusCallback;
import com.cityzipcorp.customer.callbacks.UserCallback;
import com.cityzipcorp.customer.model.GeoJsonPoint;
import com.cityzipcorp.customer.model.GeoLocateAddress;
import com.cityzipcorp.customer.model.NodalStop;
import com.cityzipcorp.customer.model.NodalStopBody;
import com.cityzipcorp.customer.model.User;
import com.cityzipcorp.customer.store.UserStore;
import com.cityzipcorp.customer.utils.Constants;
import com.cityzipcorp.customer.utils.CustomClusterRenderer;
import com.cityzipcorp.customer.utils.LocationUtils;
import com.cityzipcorp.customer.utils.SharedPreferenceManager;
import com.cityzipcorp.customer.utils.UiUtils;
import com.etiennelawlor.discreteslider.library.ui.DiscreteSlider;
import com.etiennelawlor.discreteslider.library.utilities.DisplayUtility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private final String TAG = MapsActivity.class.getCanonicalName();
    SupportMapFragment supportMapFragment;
    @BindView(R.id.auto_complete_layout)
    RelativeLayout autoCompleteLayout;
    @BindView(R.id.tap_layout)
    RelativeLayout layoutTap;
    @BindView(R.id.btn_set_home)
    Button btnSetHome;
    @BindView(R.id.image_map_pin)
    AppCompatImageView impMapPin;
    @BindView(R.id.txt_address_home)
    TextView txtAddressHome;
    @BindView(R.id.txt_address_nodal)
    TextView txtAddressNodal;

    @BindView(R.id.layout_seek_bar)
    LinearLayout layoutSeekBar;

    @BindView(R.id.discrete_slider)
    DiscreteSlider discreteSlider;

    @BindView(R.id.tick_mark_labels_rl)
    RelativeLayout tickMarkLabelsRelativeLayout;

    private Marker homeMarker;
    private SharedPreferenceManager sharedPreferenceManager;
    private NodalStop nodalStop;
    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;
    private int ADDRESS_TYPE = -1;
    private LatLng latLng;
    private UiUtils uiUtils;
    private com.cityzipcorp.customer.model.Address address;
    private GeoJsonPoint geoJsonPoint;
    private User user;
    private ClusterManager<NodalStop> mClusterManager;
    private LocationUtils locationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        initVariables();
        buildGoogleApiClient();
        initLocation();
        initializeSupportMapFragment();
    }

    private void initVariables() {
        locationUtils = new LocationUtils(this);
        uiUtils = new UiUtils(this);
        sharedPreferenceManager = new SharedPreferenceManager(this);
        address = new com.cityzipcorp.customer.model.Address();
    }

    private void initLocation() {
        if (!locationUtils.checkLocationPermission()) {
            locationUtils.requestLocationPermission();
        } else if (!locationUtils.isLocationEnabled()) {
            locationUtils.enableGps(googleApiClient);
        }
    }

    private void getBundleExtra() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            user = bundle.getParcelable("user");
            ADDRESS_TYPE = bundle.getInt("address");
            setHomeAddressInMap();
        }
    }

    private void initView() {
        if (ADDRESS_TYPE == 0) {
            autoCompleteLayout.setVisibility(View.VISIBLE);
            layoutTap.setVisibility(View.GONE);
            layoutSeekBar.setVisibility(View.GONE);
            impMapPin.setVisibility(View.VISIBLE);
        } else if (ADDRESS_TYPE == 1) {
            layoutTap.setVisibility(View.VISIBLE);
            autoCompleteLayout.setVisibility(View.GONE);
            impMapPin.setVisibility(View.GONE);
            layoutSeekBar.setVisibility(View.VISIBLE);
            discreteSlider.setOnDiscreteSliderChangeListener(new DiscreteSlider.OnDiscreteSliderChangeListener() {
                @Override
                public void onPositionChanged(int position) {
                    int childCount = tickMarkLabelsRelativeLayout.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        TextView tv = (TextView) tickMarkLabelsRelativeLayout.getChildAt(i);
                        if (i == position) {
                            tv.setTextColor(getResources().getColor(R.color.blue500));
                            String distanceString = tv.getText().toString();
                            if (position == 0) {
                                distanceString = distanceString.replace("m", "");
                            } else {
                                distanceString = distanceString.replace("km", "");
                                distanceString = distanceString + "000";
                            }

                            getNodalStopsList(Integer.parseInt(distanceString));
                        } else {
                            tv.setTextColor(getResources().getColor(R.color.black));
                        }
                    }
                }
            });
            tickMarkLabelsRelativeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    tickMarkLabelsRelativeLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                    addTickMarkTextLabels();
                }
            });
            getNodalStopsList(500);
        }
    }

    private void setHomeAddressInMap() {
        if (user.getHomeStop() != null && user.getHomeStop().getPoint() != null) {
            final GeoLocateAddress geoLocateAddress = user.getHomeStop();
            double[] coordinates = geoLocateAddress.getPoint().getCoordinates();
            latLng = new LatLng(coordinates[1], coordinates[0]);
        }
    }

    private void getNodalStopsList(final int distance) {
        uiUtils.showProgressDialog();
        LatLngBounds latLngBoundsForApi = toBounds(latLng, distance);
        String boundingBounds = String.valueOf(latLngBoundsForApi.southwest.longitude + "," + latLngBoundsForApi.southwest.latitude + "," + latLngBoundsForApi.northeast.longitude + "," + latLngBoundsForApi.northeast.latitude);
        UserStore.getInstance().getNodalStopList(boundingBounds, sharedPreferenceManager.getAccessToken(), new NodalStopCallback() {
            @Override
            public void onSuccess(List<NodalStop> nodalStopList) {
                if (nodalStopList != null) {
                    addNodalMarkersOnMap(nodalStopList);
                    LatLngBounds latLngBounds = toBounds(latLng, distance + 300);
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(latLngBounds, 20);
                    googleMap.animateCamera(cu);
                    uiUtils.dismissDialog();
                }
            }

            @Override
            public void onFailure(Error error) {
                uiUtils.dismissDialog();
                uiUtils.shortToast("Unable to fetch nodal stops");
            }
        });
    }

    @OnClick(R.id.btn_set_home)
    void onSetHome() {
        if (ADDRESS_TYPE == 0) {
            updateHomeAddress();
        } else if (ADDRESS_TYPE == 1) {
            updateNodalPoint();
        }
    }

    private void updateHomeAddress() {
        User user = new User();
        user.setId(this.user.getId());
        GeoLocateAddress geoLocateAddress = new GeoLocateAddress();
        geoLocateAddress.setAddress(address);
        geoLocateAddress.setPoint(geoJsonPoint);
        user.setHomeStop(geoLocateAddress);
       /* Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        Intent intent = new Intent(MapsActivity.this, AddressActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, Constants.REQUEST_CODE_ADDRESS_INTENT);*/


        UserStore.getInstance().updateProfileInfo(sharedPreferenceManager.getAccessToken(), user, new UserCallback() {
            @Override
            public void onSuccess(User user) {
                navigateToProfile();
            }

            @Override
            public void onFailure(Error error) {
                uiUtils.shortToast(error.getMessage());
            }
        });
    }

    private void navigateToProfile() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        this.finish();
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        googleApiClient.connect();
    }

    private void initializeSupportMapFragment() {
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        try {
            if (supportMapFragment.getView() != null) {
                View locationButton = ((View) supportMapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                layoutParams.setMargins(0, 0, 30, 150);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        supportMapFragment.getMapAsync(this);
    }

    @OnClick(R.id.auto_complete_layout)
    void onAutoCompleteClick() {
        Intent intent = new Intent(MapsActivity.this, AutocompletePlaceActivity.class);
        startActivityForResult(intent, 111);
    }

    private void updateNodalPoint() {
        if (nodalStop != null) {
            final NodalStopBody nodalStopBody = new NodalStopBody.Builder().setExternalStopId(nodalStop.getId()).setName(nodalStop.getStop().getName()).setCoordinates(nodalStop.getStop().getLocationA().getGeoJsonPoint()).build();
            UserStore.getInstance().updateNodalStop(sharedPreferenceManager.getAccessToken(), user.getId(), nodalStopBody, new StatusCallback() {
                @Override
                public void onSuccess() {
                    navigateToProfile();
                }

                @Override
                public void onFailure(Error error) {
                    uiUtils.shortToast(error.getMessage());
                }
            });
        } else {
            uiUtils.shortToast("Please select a point ");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        this.googleMap = map;
        getBundleExtra();
        if (latLng != null) {
            homeMarker = googleMap.addMarker(new MarkerOptions().
                    position(latLng).
                    title("Home").icon(BitmapDescriptorFactory.
                    fromBitmap(uiUtils.getBitmapBySize(R.drawable.home_location_pin, 80, 120))));
            moveMap();
        } else {
            if (locationUtils.checkLocationPermission()) {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
        }

        mClusterManager = new ClusterManager<>(this, googleMap);
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                initView();
            }
        });
        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                if (ADDRESS_TYPE == 0) {
                    if (impMapPin.getVisibility() == View.GONE) {
                        impMapPin.setVisibility(View.VISIBLE);
                    }
                    btnSetHome.setVisibility(View.VISIBLE);
                    Log.d("Camera position change" + "", googleMap.getCameraPosition() + "");
                    latLng = googleMap.getCameraPosition().target;
                    try {
                        new FetchAddress().execute(latLng);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.grey_map_style);
        map.setMapStyle(mapStyleOptions);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (latLng == null) {
            try {
                LocationRequest mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(10000);
                mLocationRequest.setFastestInterval(5000);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                if (locationUtils.checkLocationPermission()) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
                } else {
                    initLocation();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                latLng = bundle.getParcelable("address");
                new FetchAddress().execute(latLng);
                moveMap();
            }
        } else if (requestCode == Constants.REQUEST_CODE_ADDRESS_INTENT) {
            if (resultCode == RESULT_OK) {
                navigateToProfile();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (ADDRESS_TYPE == 0) {
                if (location != null) changeMap(location);
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        initLocation();
    }

    private void changeMap(Location location) {
        if (googleMap != null) {
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
        } else {
            Toast.makeText(this, "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
        }

    }

    private void addNodalMarkersOnMap(List<NodalStop> nodalStopList) {
        mClusterManager.clearItems();
        mClusterManager.addItems(nodalStopList);
        String selectedNodalStopId = "";
        if (user.getNodalStop() != null) {
            selectedNodalStopId = user.getNodalStop().getExternalStopId();
        }
        final CustomClusterRenderer renderer = new CustomClusterRenderer(this, googleMap, selectedNodalStopId, mClusterManager);
        mClusterManager.setRenderer(renderer);
        mClusterManager.setOnClusterItemClickListener(
                new ClusterManager.OnClusterItemClickListener<NodalStop>() {
                    @Override
                    public boolean onClusterItemClick(NodalStop clusterItem) {
                        if (btnSetHome.getVisibility() == View.GONE) {
                            btnSetHome.setVisibility(View.VISIBLE);
                            setNodalStopValue(clusterItem);
                        } else {
                            setNodalStopValue(clusterItem);
                        }
                        return false;
                    }
                });

        googleMap.setOnInfoWindowClickListener(mClusterManager);
        googleMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
        googleMap.setOnCameraIdleListener(mClusterManager);
        googleMap.setOnMarkerClickListener(mClusterManager);
        mClusterManager.cluster();
        setMarkerBounce(mClusterManager);
        uiUtils.dismissDialog();

    }

    public void setMarkerBounce(ClusterManager clusterManager) {
        for (final com.google.android.gms.maps.model.Marker m : clusterManager.getMarkerCollection().getMarkers()) {
            final Handler handler = new Handler();
            final long startTime = SystemClock.uptimeMillis();
            final long duration = 2000;
            final Interpolator interpolator = new BounceInterpolator();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    long elapsed = SystemClock.uptimeMillis() - startTime;
                    float t = Math.max(1 - interpolator.getInterpolation((float) elapsed / duration), 0);
                    m.setAnchor(0.5f, 1.0f + t);
                    if (t > 0.0) {
                        handler.postDelayed(this, 16);
                    }
                }
            });
            return;
        }
    }

    void setNodalStopValue(NodalStop nodalStopValue) {
        this.nodalStop = nodalStopValue;
        txtAddressNodal.setText("You selected " + nodalStopValue.getStop().getName());

    }

    //Function to move the map
    private void moveMap() {
        Log.d("Log is in move map", "" + latLng);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
        //Animating the camera
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }


    private String replaceNull(String value) {
        if (value == null) {
            return "";
        }
        return value;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    class FetchAddress extends AsyncTask<LatLng, Void, android.location.Address> {
        android.location.Address coreAddress;

        @Override
        protected Address doInBackground(LatLng... latLngs) {
            Log.d("Log is", "" + latLng);
            Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
            try {
                List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (addressList.size() > 0) {
                    coreAddress = addressList.get(0);
                }
            } catch (IOException e) {
                Log.e(TAG, "Unable connxxxxect to Geocoder", e);
            }

            return coreAddress;
        }

        @Override
        protected void onPostExecute(Address coreAddress) {
            super.onPostExecute(coreAddress);
            if (coreAddress != null) {
                txtAddressHome.setText(replaceNull(coreAddress.getAddressLine(0) + coreAddress.getAddressLine(1)));
                address.setPostalCode(replaceNull(coreAddress.getPostalCode()));
                address.setCity(replaceNull(coreAddress.getLocality()));
                address.setLocality(replaceNull(coreAddress.getFeatureName() + coreAddress.getThoroughfare() + coreAddress.getSubThoroughfare() + coreAddress.getLocality() + coreAddress.getPremises() + coreAddress.getSubLocality()));
                address.setSociety(replaceNull(coreAddress.getFeatureName()));
                address.setState(replaceNull(coreAddress.getAdminArea()));
                address.setLandmark(replaceNull(coreAddress.getLocality() + " , " + coreAddress.getSubLocality()));
                address.setCountry(replaceNull(coreAddress.getCountryName()));
                geoJsonPoint = new GeoJsonPoint(coreAddress.getLongitude(), coreAddress.getLatitude());
            } else {
                txtAddressHome.setText("Unable to fetch address");
            }
        }
    }

    public LatLngBounds toBounds(LatLng center, double radiusInMeters) {
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southwestCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        return new LatLngBounds(southwestCorner, northeastCorner);
    }


    private void addTickMarkTextLabels() {
        int tickMarkCount = discreteSlider.getTickMarkCount();
        float tickMarkRadius = discreteSlider.getTickMarkRadius();
        int width = tickMarkLabelsRelativeLayout.getMeasuredWidth();

        int discreteSliderBackdropLeftMargin = DisplayUtility.dp2px(this, 32);
        int discreteSliderBackdropRightMargin = DisplayUtility.dp2px(this, 32);
        int interval = (width - (discreteSliderBackdropLeftMargin + discreteSliderBackdropRightMargin) - ((int) (tickMarkRadius + tickMarkRadius)))
                / (tickMarkCount - 1);

        String[] tickMarkLabels = {"500m", "1km", "2km", "5km", "10km"};
        int tickMarkLabelWidth = DisplayUtility.dp2px(this, 45);

        for (int i = 0; i < tickMarkCount; i++) {
            TextView tv = new TextView(this);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    tickMarkLabelWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);

            tv.setText(tickMarkLabels[i]);
            tv.setTextSize(16);
            tv.setGravity(Gravity.CENTER);
            if (i == discreteSlider.getPosition())
                tv.setTextColor(getResources().getColor(R.color.blue700));
            else
                tv.setTextColor(getResources().getColor(R.color.black));
            int left = discreteSliderBackdropLeftMargin + (int) tickMarkRadius + (i * interval) - (tickMarkLabelWidth / 2);

            layoutParams.setMargins(left,
                    0,
                    0,
                    0);
            tv.setLayoutParams(layoutParams);

            tickMarkLabelsRelativeLayout.addView(tv);
        }
    }
}