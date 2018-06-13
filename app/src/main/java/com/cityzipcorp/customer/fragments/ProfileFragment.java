package com.cityzipcorp.customer.fragments;

import android.content.Intent;
import android.content.res.Configuration;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cityzipcorp.customer.R;
import com.cityzipcorp.customer.activities.MapsActivity;
import com.cityzipcorp.customer.base.BaseFragment;
import com.cityzipcorp.customer.callbacks.DialogCallback;
import com.cityzipcorp.customer.callbacks.UserCallback;
import com.cityzipcorp.customer.model.Address;
import com.cityzipcorp.customer.model.User;
import com.cityzipcorp.customer.store.UserStore;
import com.cityzipcorp.customer.utils.NetworkUtils;
import com.cityzipcorp.customer.utils.SharedPreferenceManagerConstant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
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

    @BindView(R.id.card_change_password)
    CardView cardChangePassword;

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

    @BindView(R.id.opt_in_checkbox)
    CheckBox optInCheckBox;

    @BindView(R.id.opt_in_description)
    TextView optInDesc;

    @BindView((R.id.weekdays_off_layout))
    RelativeLayout weeklyDaysOffLayout;

    boolean isAllowedToCLickOnGroup = false;
    int ADDRESS_CODE_HOME = 101;
    int ADDRESS_CODE_NODAL = 102;
    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        init();
        activity.setUpProfileView();
        return view;
    }

    private void init() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        onRefresh();
                                    }
                                }
        );
    }

    @OnClick(R.id.img_edit_profile)
    void editProfileClick() {
        if (!NetworkUtils.isNetworkAvailable(activity)) {
            uiUtils.noInternetDialog();
            return;
        }
        EditProfileFragment updateProfileFragment = new EditProfileFragment();
        Bundle bundle = getUserBundle();
        updateProfileFragment.setArguments(bundle);
        activity.replaceFragment(updateProfileFragment, activity.getString(R.string.edit_profile));
        activity.backAllowed = true;
    }

    @NonNull
    private Bundle getUserBundle() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        return bundle;
    }

    @OnCheckedChanged(R.id.opt_in_checkbox)
    void onOptedInCheckedBoxClicked(CompoundButton button, boolean checked) {

            uiUtils.showProgressDialog();

            if (NetworkUtils.isNetworkAvailable(activity)) {
                updateOptInSelection(checked);
            } else {
                button.setChecked(!checked);
                uiUtils.noInternetDialog();
                uiUtils.dismissDialog();
            }
    }

    private void updateOptInSelection(final boolean checked) {
        user.setOptedIn(checked);
        UserStore.getInstance(sharedPreferenceUtils.getValue(SharedPreferenceManagerConstant.BASE_URL)).
                updateProfileInfo(sharedPreferenceUtils.getValue(SharedPreferenceManagerConstant.ACCESS_TOKEN), user, new UserCallback() {
                    @Override
                    public void onSuccess(User user) {
                        uiUtils.dismissDialog();
                        setOptInDescription(user.getOptedIn());
                    }

                    @Override
                    public void onFailure(Error error) {
                        uiUtils.dismissDialog();
                        optInCheckBox.setChecked(!checked);
                        uiUtils.shortToast("Unable to update profile!");
                    }
                });
    }

    @OnClick(R.id.card_home_address)
    void onClickHome() {
        if (!NetworkUtils.isNetworkAvailable(activity)) {
            uiUtils.noInternetDialog();
            return;
        }
        if (user != null) {
            Bundle bundle = getUserBundle();
            bundle.putInt("address", 0);
            Intent intent = new Intent(activity, MapsActivity.class);
            intent.putExtras(bundle);
            activity.startActivityForResult(intent, ADDRESS_CODE_HOME);
        }
    }

    @OnClick(R.id.card_group_shifts)
    void onClickGroupAndShifts() {
        if (!NetworkUtils.isNetworkAvailable(activity)) {
            uiUtils.noInternetDialog();
            return;
        }
        GroupAndShiftFragment groupAndShiftFragment = new GroupAndShiftFragment();
        Bundle bundle = getUserBundle();
        groupAndShiftFragment.setArguments(bundle);
        activity.replaceFragment(groupAndShiftFragment, activity.getString(R.string.group_and_shift));
        activity.backAllowed = true;
    }

    @OnClick(R.id.card_nodal_address)
    void onClickNodal() {
        if (!NetworkUtils.isNetworkAvailable(activity)) {
            uiUtils.noInternetDialog();
            return;
        }
        if (user != null) {
            if (user.getHomeStop() != null) {
                Bundle bundle = getUserBundle();
                bundle.putInt("address", 1);
                Intent intent = new Intent(activity, MapsActivity.class);
                intent.putExtras(bundle);
                activity.startActivityForResult(intent, ADDRESS_CODE_NODAL);
            } else {
                uiUtils.shortToast("Please set home stop first");
            }
        }
    }

    private void setValues(User user) {
        this.user = user;
        if (user.getFirstName() != null && user.getLastName() != null) {
            if (activity != null)
                activity.setTitle(user.getFirstName() + " " + user.getLastName());
        }
        if (user.getHomeStop() != null && user.getAddress() != null) {
            Address address = user.getAddress();
            String addressFromObj = address.getLocality() + ", "
                    + address.getLandmark() + ", "
                    + address.getArea() + ", "
                    + address.getPostalCode();
            txtHomeAddress.setText(addressFromObj);
        }
        if (user.getCompanyName() != null) {
            txtCompanyName.setText(user.getCompanyName());
        }
        if (user.getEmployeeId() != null) {
            txtEmpId.setText(user.getEmployeeId());
        }
        if (user.getOptedIn() != null) {
            optInCheckBox.setChecked(user.getOptedIn());
            setOptInDescription(user.getOptedIn());
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

        if (!sharedPreferenceUtils.getValue(SharedPreferenceManagerConstant.IMAGE_DATA).equalsIgnoreCase("")) {
            user.setProfilePicUri(sharedPreferenceUtils.getValue(SharedPreferenceManagerConstant.IMAGE_DATA));
            String encodedImage = user.getProfilePicUri();
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imgProfile.setImageBitmap(decodedByte);
        }
        if (user.getHomeStop() == null) {
            uiUtils.getAlertDialogWithMessage("Please set home stop", new DialogCallback() {
                @Override
                public void onYes() {
                    onClickHome();
                }
            });
        } else if (user.getNodalStop() == null) {
            uiUtils.getAlertDialogWithMessage("Please set nodal stop", new DialogCallback() {
                @Override
                public void onYes() {
                    onClickNodal();
                }
            });
        }

    }

    private void setOptInDescription(boolean optedIn) {
        if (optedIn) {
            optInDesc.setText(R.string.opt_in_opted_in_description);
            weeklyDaysOffLayout.setVisibility(View.VISIBLE);
        } else {
            optInDesc.setText(R.string.opt_in_opted_out_description);
            weeklyDaysOffLayout.setVisibility(View.GONE);
        }
    }

    private void getProfileInfo() {
        if (NetworkUtils.isNetworkAvailable(activity)) {
            UserStore.getInstance(sharedPreferenceUtils.getValue(SharedPreferenceManagerConstant.BASE_URL)).
                    getProfileInfo(sharedPreferenceUtils.getValue(SharedPreferenceManagerConstant.ACCESS_TOKEN),
                            new UserCallback() {
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
                                            uiUtils.dismissDialog();
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
        } else {
            uiUtils.noInternetDialog();
            uiUtils.dismissDialog();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @OnClick(R.id.card_change_password)
    void onClickPasswordChange() {
        if (!NetworkUtils.isNetworkAvailable(activity)) {
            uiUtils.noInternetDialog();
            return;
        }
        ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
        activity.replaceFragment(changePasswordFragment, activity.getString(R.string.change_password));
        activity.backAllowed = true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADDRESS_CODE_HOME || requestCode == ADDRESS_CODE_NODAL) {
            if (resultCode == RESULT_OK) {
                uiUtils.showProgressDialog();
                getProfileInfo();
            }
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        getProfileInfo();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getProfileInfo();
    }
}
