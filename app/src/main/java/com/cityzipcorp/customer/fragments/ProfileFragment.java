package com.cityzipcorp.customer.fragments;

import android.app.Dialog;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.cityzipcorp.customer.R;
import com.cityzipcorp.customer.activities.MapsActivity;
import com.cityzipcorp.customer.base.BaseFragment;
import com.cityzipcorp.customer.callbacks.DialogCallback;
import com.cityzipcorp.customer.callbacks.OptOutDialogCallback;
import com.cityzipcorp.customer.callbacks.UserCallback;
import com.cityzipcorp.customer.model.Address;
import com.cityzipcorp.customer.model.User;
import com.cityzipcorp.customer.store.UserStore;
import com.cityzipcorp.customer.utils.NetworkUtils;
import com.cityzipcorp.customer.utils.SharedPreferenceManagerConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

/**
 * Created by anilpathak on 02/11/17.
 */

public class ProfileFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.img_edit_profile)
    ImageView imgEditProfile;

    @BindView(R.id.card_home_address)
    CardView cardAddress;

    @BindView(R.id.layout_week_off)
    CardView cardWeeklyOff;

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

    @BindView(R.id.txt_no_week_off)
    TextView txtNoWeekOff;

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

    @BindView(R.id.opt_in_switch)
    Switch optInSwitch;

    @BindView(R.id.weekdays)
    LinearLayout layoutWeekdays;

    @BindView(R.id.opt_in_description)
    TextView optInDesc;

    @BindView(R.id.t_btn_mon)
    ToggleButton toggleButtonMon;

    @BindView(R.id.t_btn_tue)
    ToggleButton toggleButtonTue;

    @BindView(R.id.t_btn_wed)
    ToggleButton toggleButtonWed;

    @BindView(R.id.t_btn_thu)
    ToggleButton toggleButtonThu;

    @BindView(R.id.t_btn_fri)
    ToggleButton toggleButtonFri;
    @BindView(R.id.t_btn_sat)
    ToggleButton toggleButtonSat;
    @BindView(R.id.t_btn_sun)
    ToggleButton toggleButtonSun;
    boolean isAllowedToCLickOnGroup = false;
    int ADDRESS_CODE_HOME = 101;
    int ADDRESS_CODE_NODAL = 102;
    private User user;
    private boolean onGoingProfileRequest = false;
    private List<String> days;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
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

    void setOnpInCheckListener() {
        optInSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (NetworkUtils.isNetworkAvailable(activity)) {
                    optInChanged(b);
                } else {
                    optInSwitch.setChecked(!b);
                    uiUtils.noInternetDialog();
                }
            }
        });
    }

    private void optInChanged(final boolean checked) {
        if (!checked) {
            uiUtils.optOutConformationDialog("Are you sure you want to opt out!", new OptOutDialogCallback() {
                @Override
                public void onYes() {
                    user.setOptedIn(false);
                    updateOptInSelection();
                }

                public void onNo() {
                    optInSwitch.setChecked(true);
                }
            });
        } else {
            user.setOptedIn(true);
            updateOptInSelection();
        }
    }

    private void updateOptInSelection() {
        uiUtils.showProgressDialog();
        UserStore.getInstance(sharedPreferenceUtils.getValue(SharedPreferenceManagerConstant.BASE_URL), activity.macId).
                updateProfileInfo(sharedPreferenceUtils.getValue(SharedPreferenceManagerConstant.ACCESS_TOKEN), user, new UserCallback() {
                    @Override
                    public void onSuccess(User user) {
                        uiUtils.dismissDialog();
                        setOptInDescription(user.getOptedIn());
                        activity.isOptIn = user.getOptedIn();
                    }

                    @Override
                    public void onFailure(Error error) {
                        uiUtils.dismissDialog();
                        optInSwitch.setChecked(!user.getOptedIn());
                        activity.isOptIn = !user.getOptedIn();
                        uiUtils.shortToast(error.getLocalizedMessage());
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

    @OnClick(R.id.layout_week_off)
    void onClickWeeklyOff() {
        days = new ArrayList<>();
        final Dialog dialog = new Dialog(activity);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        assert window != null;
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
        dialog.setContentView(R.layout.layout_weekly_off);
        ToggleButton tBtnMon = dialog.findViewById(R.id.t_btn_mon);
        ToggleButton tBtnTue = dialog.findViewById(R.id.t_btn_tue);
        ToggleButton tBtnWed = dialog.findViewById(R.id.t_btn_wed);
        ToggleButton tBtnThu = dialog.findViewById(R.id.t_btn_thu);
        ToggleButton tBtnFri = dialog.findViewById(R.id.t_btn_fri);
        ToggleButton tBtnSat = dialog.findViewById(R.id.t_btn_sat);
        ToggleButton tBtnSun = dialog.findViewById(R.id.t_btn_sun);
        Button btnSubmit = dialog.findViewById(R.id.btn_submit);
        String[] weeklyOff = user.getWeeekdaysOff();
        if (weeklyOff != null && user.getWeeekdaysOff().length > 0) {
            for (String weekOff : weeklyOff) {
                days.add(weekOff);
                ToggleButton toggleButton = dialog.findViewById(getDayId(weekOff));
                toggleButton.setChecked(true);
            }
        }
        tBtnMon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String day = "Monday";
                if (b) {
                    days.add(day);
                } else {
                    if (days.contains(day))
                        days.remove(day);
                }
            }
        });
        tBtnTue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String day = "Tuesday";
                if (b) {
                    days.add(day);
                } else {
                    if (days.contains(day))
                        days.remove(day);
                }
            }
        });
        tBtnWed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                String day = "Wednesday";
                if (b) {
                    days.add(day);
                } else {
                    if (days.contains(day))
                        days.remove(day);
                }
            }
        });
        tBtnThu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String day = "Thursday";
                if (b) {
                    days.add(day);
                } else {
                    if (days.contains(day))
                        days.remove(day);
                }
            }
        });

        tBtnFri.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String day = "Friday";
                if (b) {
                    days.add(day);
                } else {
                    if (days.contains(day))
                        days.remove(day);
                }
            }
        });
        tBtnSat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String day = "Saturday";
                if (b) {
                    days.add(day);
                } else {
                    if (days.contains(day))
                        days.remove(day);
                }
            }
        });
        tBtnSun.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String day = "Sunday";
                if (b) {
                    days.add(day);
                } else {
                    if (days.contains(day))
                        days.remove(day);
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (days.size() > 0) {
                    String[] selectedDays = days.toArray(new String[days.size()]);
                    user.setWeeekdaysOff(selectedDays);
                    uiUtils.showProgressDialog();

                    UserStore.getInstance(sharedPreferenceUtils.getValue(SharedPreferenceManagerConstant.BASE_URL), activity.macId).
                            updateProfileInfo(sharedPreferenceUtils.getValue(SharedPreferenceManagerConstant.ACCESS_TOKEN), user, new UserCallback() {
                                @Override
                                public void onSuccess(User user) {
                                    dialog.dismiss();
                                    getProfileInfo();
                                }

                                @Override
                                public void onFailure(Error error) {
                                    uiUtils.dismissDialog();
                                    uiUtils.shortToast(error.getLocalizedMessage());
                                }
                            });
                } else {
                    uiUtils.shortToast("Please select 1 day for week off");
                }
            }
        });
        dialog.show();
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
            String addressFromObj = address.getSociety() + ", "
                    + address.getLocality() + ", "
                    + address.getStreetAddress() + ", "
                    + address.getLandmark() + ", "
                    + address.getArea() + ", "
                    + address.getCity() + ", "
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
            optInSwitch.setChecked(user.getOptedIn());
            activity.isOptIn = user.getOptedIn();
            setOptInDescription(user.getOptedIn());
            setOnpInCheckListener();
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
        weeklyOff(user);
        if (!sharedPreferenceUtils.getValue(SharedPreferenceManagerConstant.IMAGE_DATA).equalsIgnoreCase("")) {
            user.setProfilePicUri(sharedPreferenceUtils.getValue(SharedPreferenceManagerConstant.IMAGE_DATA));
            String encodedImage = user.getProfilePicUri();
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imgProfile.setImageBitmap(decodedByte);
        }
        if (user.getHomeStop() == null) {
            uiUtils.conformationDialog("Please set home stop", new DialogCallback() {
                @Override
                public void onYes() {
                    onClickHome();
                }
            });
        } else if (user.getNodalStop() == null) {
            uiUtils.conformationDialog("Please set nodal stop", new DialogCallback() {
                @Override
                public void onYes() {
                    onClickNodal();
                }
            });
        }

    }

    private void weeklyOff(User user) {
        toggleButtonMon.setChecked(false);
        toggleButtonTue.setChecked(false);
        toggleButtonWed.setChecked(false);
        toggleButtonThu.setChecked(false);
        toggleButtonFri.setChecked(false);
        toggleButtonSat.setChecked(false);
        toggleButtonSun.setChecked(false);
        String[] weeklyOff = user.getWeeekdaysOff();
        if (weeklyOff != null && user.getWeeekdaysOff().length > 0) {
            layoutWeekdays.setVisibility(View.VISIBLE);
            txtNoWeekOff.setVisibility(View.GONE);
            for (String weekOff : weeklyOff) {
                setWeeklyOffs(weekOff);
            }
        } else {
            layoutWeekdays.setVisibility(View.GONE);
            txtNoWeekOff.setVisibility(View.VISIBLE);
        }
    }

    private void setOptInDescription(boolean optedIn) {
        if (optedIn) {
            optInDesc.setText(R.string.opt_in_opted_in_description);
        } else {
            optInDesc.setText(R.string.opt_in_opted_out_description);
        }
    }

    private void getProfileInfo() {
        if (!onGoingProfileRequest)
            if (NetworkUtils.isNetworkAvailable(activity)) {
                onGoingProfileRequest = true;
                UserStore.getInstance(sharedPreferenceUtils.getValue(SharedPreferenceManagerConstant.BASE_URL), activity.macId).
                        getProfileInfo(sharedPreferenceUtils.getValue(SharedPreferenceManagerConstant.ACCESS_TOKEN),
                                new UserCallback() {
                                    @Override
                                    public void onSuccess(User user) {
                                        if (user != null) {
                                            onGoingProfileRequest = false;
                                            try {
                                                uiUtils.dismissDialog();
                                                setValues(user);
                                                noDataLayout.setVisibility(View.GONE);
                                                layoutMain.setVisibility(View.VISIBLE);
                                                swipeRefreshLayout.setRefreshing(false);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                uiUtils.dismissDialog();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Error error) {
                                        onGoingProfileRequest = false;
                                        swipeRefreshLayout.setRefreshing(false);
                                        uiUtils.dismissDialog();
                                        uiUtils.shortToast(error.getLocalizedMessage());
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

    private int getDayId(String day) {
        int id = 0;
        switch (day) {
            case "Monday":
                id = R.id.t_btn_mon;
                break;
            case "Tuesday":
                id = R.id.t_btn_tue;
                break;
            case "Wednesday":
                id = R.id.t_btn_wed;
                break;
            case "Thursday":
                id = R.id.t_btn_thu;
                break;
            case "Friday":
                id = R.id.t_btn_fri;
                break;
            case "Saturday":
                id = R.id.t_btn_sat;
                break;
            case "Sunday":
                id = R.id.t_btn_sun;
                break;
        }
        return id;
    }

    private void setWeeklyOffs(String weekOff) {
        if (weekOff.equalsIgnoreCase("Monday")) {
            toggleButtonMon.setChecked(true);
        }
        if (weekOff.equalsIgnoreCase("Tuesday")) {
            toggleButtonTue.setChecked(true);
        }
        if (weekOff.equalsIgnoreCase("Wednesday")) {
            toggleButtonWed.setChecked(true);
        }
        if (weekOff.equalsIgnoreCase("Thursday")) {
            toggleButtonThu.setChecked(true);
        }
        if (weekOff.equalsIgnoreCase("Friday")) {
            toggleButtonFri.setChecked(true);
        }
        if (weekOff.equalsIgnoreCase("Saturday")) {
            toggleButtonSat.setChecked(true);
        }
        if (weekOff.equalsIgnoreCase("Sunday")) {
            toggleButtonSun.setChecked(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
