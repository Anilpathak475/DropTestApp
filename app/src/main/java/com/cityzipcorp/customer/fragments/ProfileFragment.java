package com.cityzipcorp.customer.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cityzipcorp.customer.R;
import com.cityzipcorp.customer.activities.MapsActivity;
import com.cityzipcorp.customer.base.BaseFragment;
import com.cityzipcorp.customer.callbacks.UserCallback;
import com.cityzipcorp.customer.model.Address;
import com.cityzipcorp.customer.model.User;
import com.cityzipcorp.customer.store.UserStore;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by anilpathak on 02/11/17.
 */

public class ProfileFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.img_edit_profile)
    ImageView imgEditProfile;

    @BindView(R.id.card_home_address)
    CardView cardAddress;

    @BindView(R.id.card_group_shifts)
    CardView cardGroupAndShifts;

    @BindView(R.id.card_nodal_address)
    CardView cardNodal;

    @BindView(R.id.img_profile)
    ImageView imgProfile;

    @BindView(R.id.txt_home_address)
    TextView txtHomeAddress;

    @BindView(R.id.txt_nodal_address)
    TextView txtNodalAddress;

    @BindView(R.id.txt_comp_name)
    TextView txtCompanyName;
    @BindView(R.id.txt_emp_id)
    TextView txtEmpId;

    @BindView(R.id.txt_grp)
    TextView txtGroupName;

    @BindView(R.id.txt_shift)
    TextView txtShiftName;

    @BindView(R.id.txt_no_group_shift)
    TextView txtNoGrpShift;

    @BindView(R.id.main_layout_group)
    LinearLayout mainLayoutGroup;

    @BindView(R.id.layout_main)
    LinearLayout layoutMain;

    @BindView(R.id.no_data_layout)
    LinearLayout noDataLayout;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    User user;

    boolean isAllowedToCLickOnGroup = false;
    int ADDRESS_CODE_HOME = 101;
    int ADDRESS_CODE_NODAL = 102;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        activity.backAllowed = false;
        activity.inProfile = true;
        init();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //   if (getActivity() != null) getActivity().setTitle(getString(R.string.profile));
    }

    private void init() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        getProfileInfo();
                                    }
                                }
        );
    }

    @OnClick(R.id.img_edit_profile)
    void editProfileClick() {
        EditProfileFragment updateProfileFragment = new EditProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        updateProfileFragment.setArguments(bundle);
        activity.replaceFragment(updateProfileFragment, activity.getString(R.string.edit_profile));
        activity.backAllowed = true;
    }

    @OnClick(R.id.card_home_address)
    void onClickHome() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        bundle.putInt("address", 0);
        Intent intent = new Intent(activity, MapsActivity.class);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, ADDRESS_CODE_HOME);
    }

    @OnClick(R.id.card_group_shifts)
    void onClickGroupAndShifts() {
        GroupAndShiftFragment groupAndShiftFragment = new GroupAndShiftFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        groupAndShiftFragment.setArguments(bundle);
        activity.replaceFragment(groupAndShiftFragment, activity.getString(R.string.group_and_shift));
        activity.backAllowed = true;
    }

    @OnClick(R.id.card_nodal_address)
    void onClickNodal() {
        if (user.getHomeStop() != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("user", user);
            bundle.putInt("address", 1);
            Intent intent = new Intent(activity, MapsActivity.class);
            intent.putExtras(bundle);
            activity.startActivityForResult(intent, ADDRESS_CODE_NODAL);
        } else {
            uiUtils.shortToast("Please set home stop first");
        }
    }

    private void setValues(User user) {
        this.user = user;
        if (user.getFirstName() != null && user.getLastName() != null) {
            if (activity != null)
                activity.setTitle(user.getFirstName() + " " + user.getLastName());
        } else {
            if (activity != null) activity.setTitle("Profile");
        }
        if (user.getHomeStop() != null && user.getAddress() != null) {
            Address address = user.getAddress();
            String addressFromObj = address.getArea() + " " + address.getLocality() + " " + address.getLandmark() + ", " + address.getPostalCode();
            txtHomeAddress.setText("");
            txtHomeAddress.setText(addressFromObj);
        }
        if (user.getCompanyName() != null) {
            txtCompanyName.setText(user.getCompanyName());
        }
        if (user.getEmployeeId() != null) {
            txtEmpId.setText(user.getEmployeeId());
        }

        if (user.getNodalStop() != null) {
            txtNodalAddress.setText(user.getNodalStop().getName());
        }

        if (user.getGroup() == null && user.getShift() == null) {
            mainLayoutGroup.setVisibility(View.GONE);
            txtNoGrpShift.setVisibility(View.VISIBLE);
        }
        if (user.getGroup() != null) {
            txtGroupName.setText(user.getGroup().getName());
            isAllowedToCLickOnGroup = true;
        }
        if (user.getShift() != null) {
            txtShiftName.setText(user.getShift().getName());
            isAllowedToCLickOnGroup = true;
        }

        boolean imageStatus = sharedPreferenceUtils.getImageStatus();
        if (imageStatus) {
            user.setProfilePicUri(sharedPreferenceUtils.getImageData());
            String encodedImage = user.getProfilePicUri();
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imgProfile.setImageBitmap(decodedByte);
        }

    }

    private void getProfileInfo() {
        uiUtils.showProgressDialog();
        UserStore.getInstance().getProfileInfo(sharedPreferenceUtils.getAccessToken(), new UserCallback() {
            @Override
            public void onSuccess(User user) {
                if (user != null) {
                    try {
                        uiUtils.dismissDialog();
                        setValues(user);
                        noDataLayout.setVisibility(View.GONE);
                        layoutMain.setVisibility(View.VISIBLE);
                        swipeRefreshLayout.setRefreshing(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }

            @Override
            public void onFailure(Error error) {
                swipeRefreshLayout.setRefreshing(false);
                uiUtils.dismissDialog();
                uiUtils.shortToast("Unable to fetch profile details!");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADDRESS_CODE_HOME || requestCode == ADDRESS_CODE_NODAL) {
            if (resultCode == RESULT_OK) {
                getProfileInfo();
            }
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        getProfileInfo();
    }
}
