package com.cityzipcorp.customer.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cityzipcorp.customer.R;
import com.cityzipcorp.customer.callbacks.AreaCallback;
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
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    private SharedPreferenceManager sharedPreferenceManager;
    private User user;
    private Address selectedAddress;
    private UiUtils uiUtils;
    private List<String> areaNames = new ArrayList<>();
    private String macId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        ButterKnife.bind(this);
        uiUtils = new UiUtils(this);
        sharedPreferenceManager = new SharedPreferenceManager(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setTitle((Html.fromHtml("<font color=\"#111111\">Set Home Address</font>")));
        }

        getBundleExtra();
        getAreas();
        macId = Utils.getInstance().getMacId(this);
    }

    private void getAreas() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            uiUtils.showProgressDialog();
            UserStore.getInstance(sharedPreferenceManager.getValue(SharedPreferenceManagerConstant.BASE_URL), macId).
                    getAreas(sharedPreferenceManager.getValue(SharedPreferenceManagerConstant.ACCESS_TOKEN), new AreaCallback() {
                        @Override
                        public void onSuccess(List<Area> areas) {
                            createAddress(areas);
                        }

                        @Override
                        public void onFailure(Error error) {
                            uiUtils.dismissDialog();
                            uiUtils.shortToast("Unable to fetch Areas");
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
        txtCity.setText(address.getCity());
        txtState.setText(address.getState());
        txtPinCode.setText(address.getPostalCode());
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
}
