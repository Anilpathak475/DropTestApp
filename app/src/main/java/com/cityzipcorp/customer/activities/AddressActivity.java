package com.cityzipcorp.customer.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.cityzipcorp.customer.R;
import com.cityzipcorp.customer.callbacks.AreaCallback;
import com.cityzipcorp.customer.model.Address;
import com.cityzipcorp.customer.model.Area;
import com.cityzipcorp.customer.model.User;
import com.cityzipcorp.customer.store.UserStore;
import com.cityzipcorp.customer.utils.SharedPreferenceManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    private SharedPreferenceManager sharedPreferenceManager;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        ButterKnife.bind(this);
        sharedPreferenceManager = new SharedPreferenceManager(this);
        getAreas();
    }

    private void getAreas() {
        UserStore.getInstance().getAreas(sharedPreferenceManager.getAccessToken(), new AreaCallback() {
            @Override
            public void onSuccess(List<Area> areas) {
                List<String> areaNames = new ArrayList<>();
                for (Area area : areas) {
                    areaNames.add(area.getAreaName());
                }
                setAdapter(areaNames);
            }

            @Override
            public void onFailure(Error error) {

            }
        });
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
        edtSelectedAddress.setText(address.getSociety() + " ," + address.getLocality() + "," + address.getStreetAddress());
        txtCity.setText(address.getCity());
        edtLandmark.setText(address.getLandmark());
        txtState.setText(address.getState());
        txtPinCode.setText(address.getPostalCode());
    }
}
