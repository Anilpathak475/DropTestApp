package com.cityzipcorp.customer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cityzipcorp.customer.R;
import com.cityzipcorp.customer.callbacks.AreaCallback;
import com.cityzipcorp.customer.callbacks.DialogCallback;
import com.cityzipcorp.customer.callbacks.UserCallback;
import com.cityzipcorp.customer.model.Address;
import com.cityzipcorp.customer.model.Area;
import com.cityzipcorp.customer.model.GeoLocateAddress;
import com.cityzipcorp.customer.model.User;
import com.cityzipcorp.customer.store.UserStore;
import com.cityzipcorp.customer.utils.NetworkUtils;
import com.cityzipcorp.customer.utils.SharedPreferenceManager;
import com.cityzipcorp.customer.utils.SharedPreferenceManagerConstant;
import com.cityzipcorp.customer.utils.UiUtils;
import com.cityzipcorp.customer.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AddressActivity extends AppCompatActivity {

    @BindView(R.id.edt_society)
    EditText edtSociety;

    @BindView(R.id.edt_street_address)
    EditText edtStreetAddress;

    @BindView(R.id.edt_locality)
    EditText edtLocality;

    @BindView(R.id.edt_landmark)
    EditText edtLandmark;

    @BindView(R.id.act_area)
    AutoCompleteTextView autoCompleteArea;

    @BindView(R.id.txt_city)
    TextView txtCity;

    @BindView(R.id.txt_state)
    TextView txtState;

    @BindView(R.id.txt_pin_code)
    TextView txtPinCode;

    @BindView(R.id.btn_update)
    Button btnUpdateHomeAddress;

    @BindView(R.id.img_refresh)
    public ImageView imgRefresh;
    @BindView(R.id.toolbar)
    android.support.v7.widget.Toolbar toolbar;

    private SharedPreferenceManager sharedPreferenceManager;
    private User user;
    private Address selectedAddress;
    private UiUtils uiUtils;
    private List<String> areaNames = new ArrayList<>();
    private String macId;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        unbinder = ButterKnife.bind(this);
        uiUtils = new UiUtils(this);
        sharedPreferenceManager = new SharedPreferenceManager(this);
        getBundleExtra();
        macId = Utils.getInstance().getMacId(this);
        getAreas();
    }

    private void getAreas() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            uiUtils.showProgressDialog();
            UserStore.getInstance(sharedPreferenceManager.getValue(SharedPreferenceManagerConstant.BASE_URL), macId).
                    getAreas(sharedPreferenceManager.getValue(SharedPreferenceManagerConstant.ACCESS_TOKEN), new AreaCallback() {
                        @Override
                        public void onSuccess(List<Area> areas) {
                            if (areas != null) {
                                if (areas.size() > 0) {
                                    createAddress(areas);
                                } else {
                                    List<String> area = new ArrayList<>();
                                    area.add("Select Area");
                                    setAdapter(areaNames, 0);
                                }
                            }

                        }

                        @Override
                        public void onFailure(Error error) {
                            uiUtils.dismissDialog();
                            uiUtils.notifyDialog("Unable to fetch Areas", new DialogCallback() {
                                @Override
                                public void onYes() {

                                }
                            });
                        }
                    });
        } else {
            uiUtils.noInternetDialog();
        }
    }

    private void createAddress(List<Area> areas) {
        int selectedIndex = -1;
        for (Area area : areas) {
            areaNames.add(area.getAreaName().toUpperCase());
            if (selectedAddress.getArea().equalsIgnoreCase(area.getAreaName())) {
                selectedIndex = areaNames.indexOf(area.getAreaName());
            }
        }
        setAdapter(areaNames, selectedIndex);
        uiUtils.dismissDialog();
    }

    private void setAdapter(List<String> areaNames, int selectedIndex) {
        autoCompleteArea.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, areaNames));
        if (selectedIndex > 0) {
            autoCompleteArea.setSelection(selectedIndex);
        }
    }

    private void getBundleExtra() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("user")) {
            user = bundle.getParcelable("user");
            if (user != null && user.getHomeStop() != null) {
                setValues(user.getHomeStop().getAddress());
            }
        }

    }

    private void setValues(Address address) {
        selectedAddress = address;
        edtLandmark.setText(address.getLandmark());
        edtSociety.setText(address.getSociety());
        edtStreetAddress.setText(address.getStreetAddress());
        edtLocality.setText(address.getLocality());
        txtCity.setText(address.getCity());
        txtState.setText(address.getState());
        txtPinCode.setText(address.getPostalCode());

    }

    @OnClick(R.id.img_refresh)
    void onRefresh() {
        getAreas();
    }

    @OnClick(R.id.btn_update)
    void updateHomeAddress() {
        if (validateAddress()) {
            if (NetworkUtils.isNetworkAvailable(this)) {
                Address address = new Address();
                address.setArea(autoCompleteArea.getText().toString());
                address.setLocality(edtLocality.getText().toString());
                address.setSociety(edtSociety.getText().toString());
                address.setLandmark(edtLandmark.getText().toString());
                address.setStreetAddress(edtStreetAddress.getText().toString());
                address.setCity(txtCity.getText().toString());
                address.setState(selectedAddress.getState());
                address.setCountry(selectedAddress.getCountry());
                address.setPostalCode(txtPinCode.getText().toString());
                User user = new User();
                user.setId(this.user.getId());
                GeoLocateAddress geoLocateAddress = new GeoLocateAddress();
                geoLocateAddress.setAddress(address);
                geoLocateAddress.setPoint(this.user.getHomeStop().getPoint());
                user.setHomeStop(geoLocateAddress);

                UserStore.getInstance(sharedPreferenceManager.
                        getValue(SharedPreferenceManagerConstant.BASE_URL), macId).
                        updateProfileInfo(sharedPreferenceManager.getValue(SharedPreferenceManagerConstant.ACCESS_TOKEN), user, new UserCallback() {
                            @Override
                            public void onSuccess(User user) {
                                navigateToProfile();
                            }

                            @Override
                            public void onFailure(Error error) {
                                new UiUtils(AddressActivity.this).shortToast(error.getMessage());
                            }
                        });
            } else {
                uiUtils.noInternetDialog();
            }
        }

    }


    private boolean validateAddress() {
        if (areaNames.size() == 0) {
            uiUtils.shortToast("Please refresh and select area");
            return false;
        }

        if (autoCompleteArea.getText().toString().equalsIgnoreCase("Select Area")) {
            uiUtils.shortToast("Please select area");
            return false;
        }
        if (edtLandmark.getText().toString().equalsIgnoreCase("")) {
            uiUtils.shortToast("Please enter landmark");
            return false;
        }
        if (edtStreetAddress.getText().toString().equalsIgnoreCase("")) {
            uiUtils.shortToast("Please enter street address");
            return false;
        }
        if (edtSociety.getText().toString().equalsIgnoreCase("")) {
            uiUtils.shortToast("Please enter society");
            return false;
        }
        if (edtLocality.getText().toString().equalsIgnoreCase("")) {
            uiUtils.shortToast("Please enter locality");
            return false;
        }
        if (edtLandmark.getText().toString().equalsIgnoreCase("")) {
            uiUtils.shortToast("Please enter landmark");
            return false;
        }

        if (!areaNames.contains(autoCompleteArea.getText().toString())) {
            uiUtils.shortToast("Please select area");
            autoCompleteArea.getText().clear();
            return false;
        }
        return true;
    }

    private void navigateToProfile() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        sharedPreferenceManager = null;
        uiUtils = null;
        areaNames.clear();
        areaNames = null;
        selectedAddress = null;
    }
}
