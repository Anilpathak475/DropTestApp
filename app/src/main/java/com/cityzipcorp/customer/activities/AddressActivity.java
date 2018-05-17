package com.cityzipcorp.customer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.cityzipcorp.customer.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddressActivity extends AppCompatActivity {

    @BindView(R.id.edt_selected_address)
    EditText edtSelectedAddress;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        ButterKnife.bind(this);
        uiUtils = new UiUtils(this);
        sharedPreferenceManager = new SharedPreferenceManager(this);
        getAreas();
    }

    private void getAreas() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            uiUtils.showProgressDialog();
            UserStore.getInstance().getAreas(sharedPreferenceManager.getAccessToken(), new AreaCallback() {
                @Override
                public void onSuccess(List<Area> areas) {
                    List<String> areaNames = new ArrayList<>();
                    for (Area area : areas) {
                        areaNames.add(area.getAreaName());
                    }
                    setAdapter(areaNames);
                    uiUtils.dismissDialog();
                }

                @Override
                public void onFailure(Error error) {
                    uiUtils.dismissDialog();
                    uiUtils.shortToast("Unable to fetch Areas");
                }
            });
        } else {
            uiUtils.shortToast("No Internet!");
        }
    }

    private void setAdapter(List<String> areaNames) {
        autoCompleteArea.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, areaNames));
        getBundleExtra();
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
        String selectedAddress = address.getSociety() + " " + address.getLocality() + " " + address.getStreetAddress();
        selectedAddress = selectedAddress.replace("null", "");
        selectedAddress = selectedAddress.replace(address.getArea(), "");
        selectedAddress = selectedAddress.replace(address.getCity(), "");
        selectedAddress = selectedAddress.replace(address.getCountry(), "");
        selectedAddress = selectedAddress.replace(address.getPostalCode(), "");
        edtSelectedAddress.setText(selectedAddress);
        txtCity.setText(address.getCity());
        edtLandmark.setText(address.getLandmark());
        txtState.setText(address.getState());
        txtPinCode.setText(address.getPostalCode());
    }


    @OnClick(R.id.btn_update)
    void updateHomeAddress() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            Address address = new Address();
            address.setArea(autoCompleteArea.getText().toString());
            address.setCity(txtCity.getText().toString());
            address.setSociety(selectedAddress.getSociety());
            address.setState(selectedAddress.getState());
            address.setLandmark(edtLandmark.getText().toString());
            address.setLocality(selectedAddress.getLocality());
            address.setCountry(selectedAddress.getCountry());
            User user = new User();
            user.setId(this.user.getId());
            GeoLocateAddress geoLocateAddress = new GeoLocateAddress();
            geoLocateAddress.setAddress(address);
            geoLocateAddress.setPoint(this.user.getHomeStop().getPoint());
            user.setHomeStop(geoLocateAddress);

            UserStore.getInstance().updateProfileInfo(sharedPreferenceManager.getAccessToken(), user, new UserCallback() {
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
            uiUtils.shortToast("No Internet!");
        }

    }

    private void navigateToProfile() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        this.finish();
    }
}
