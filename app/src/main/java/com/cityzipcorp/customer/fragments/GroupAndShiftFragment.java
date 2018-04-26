package com.cityzipcorp.customer.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.cityzipcorp.customer.R;
import com.cityzipcorp.customer.activities.HomeActivity;
import com.cityzipcorp.customer.base.BaseFragment;
import com.cityzipcorp.customer.callbacks.GroupCallBack;
import com.cityzipcorp.customer.callbacks.UserCallback;
import com.cityzipcorp.customer.model.Group;
import com.cityzipcorp.customer.model.Shift;
import com.cityzipcorp.customer.model.User;
import com.cityzipcorp.customer.store.UserStore;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by anilpathak on 02/01/18.
 */

public class GroupAndShiftFragment extends BaseFragment {

    @BindView(R.id.spn_group)
    Spinner spnGroup;

    @BindView(R.id.spn_shift)
    Spinner spnShift;

    @BindView(R.id.btn_update)
    Button btnUpdate;

    List<Group> groupList = new ArrayList<>();
    private int selectedIndexOfGroup = -1;
    private int selectedIndexOfShift = -1;
    private User userFromProfile;

    private boolean isiInit = true;
    private String profileStatus = "";

    public static GroupAndShiftFragment getInstance() {
        return new GroupAndShiftFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) getActivity().setTitle(getString(R.string.group_and_shift));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_and_shift, container, false);
        ButterKnife.bind(this, view);

        initListeners();
        getGroupsAndShifts();

        return view;
    }

    private void initListeners() {
        spnGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedIndexOfGroup = i;
                setAdapter(spnShift, groupList.get(i).getStringShifts());
                if (isiInit && profileStatus.equalsIgnoreCase("")) {
                    setAdapter(spnShift, getShiftFromGroupList(groupList.get(i)));
                    List<Shift> shiftList = groupList.get(i).getShifts();
                    for (int j = 0; j < shiftList.size(); j++) {
                        Shift shiftFromList = shiftList.get(j);
                        if (userFromProfile != null && userFromProfile.getShift() != null)
                            if (shiftFromList.getId().equalsIgnoreCase(userFromProfile.getShift().getId())) {
                                spnShift.setSelection(j);

                            }
                    }
                    isiInit = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spnShift.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedIndexOfShift = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getBundleExtra() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey(PROFILE_STATUS) && bundle.containsKey("user")) {
                if ((getActivity()) != null) ((HomeActivity) getActivity()).
                        btnBack.setVisibility(View.GONE);
                profileStatus = bundle.getString(PROFILE_STATUS);
                userFromProfile = bundle.getParcelable("user");
                setAdapter(spnGroup, getGroupsFromGroupList(groupList));
            } else if (bundle.containsKey("user")) {
                userFromProfile = bundle.getParcelable("user");
                if (userFromProfile != null) {
                    setValues(userFromProfile);
                }
            }
        }

    }

    private void setValues(User user) {
        if (user.getGroup() != null && user.getShift() != null) {
            isiInit = true;
            Group group = user.getGroup();
            setAdapter(spnGroup, getGroupsFromGroupList(groupList));
            for (int i = 0; i < groupList.size(); i++) {
                Group groupFromList = groupList.get(i);
                if (groupFromList.getId().equalsIgnoreCase(group.getId())) {
                    spnGroup.setSelection(i);
                }
            }
        }

    }

    @OnClick(R.id.btn_update)
    void onUpdate() {
        updateGroupAndShift();
    }

    private void setAdapter(Spinner spinner, List<String> objectList) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(activity, R.layout.spinner_item, objectList);
        spinner.setAdapter(arrayAdapter);
    }

    private void getGroupsAndShifts() {
        uiUtils.showProgressDialog();
        UserStore.getInstance().getShiftByGroup(sharedPreferenceUtils.getAccessToken(), new GroupCallBack() {
            @Override
            public void onSuccess(List<Group> groups) {
                uiUtils.dismissDialog();
                if (groups.isEmpty()) {
                    activity.onBackPressed();
                    uiUtils.shortToast("No groups and shift");
                } else {
                    groupList = groups;
                    getBundleExtra();
                }
            }

            @Override
            public void onFailure(Error error) {
                uiUtils.dismissDialog();
                uiUtils.shortToast("No groups and shift");
            }
        });
    }


    private void updateGroupAndShift() {
        /* if (validate()) {*/
        uiUtils.showProgressDialog();
        User user = new User();
        user.setId(userFromProfile.getId());
        Group group = groupList.get(selectedIndexOfGroup);
        Shift shift = group.getShifts().get(selectedIndexOfShift);
        user.setGroupId(group.getId());
        user.setShiftId(shift.getId());
        UserStore.getInstance().updateProfileInfo(sharedPreferenceUtils.getAccessToken(), user, new UserCallback() {
            @Override
            public void onSuccess(User user) {
                uiUtils.dismissDialog();
                if (!profileStatus.equalsIgnoreCase("")) {
                    ((HomeActivity) getActivity()).setUpBottomNavigationView(2);
                } else {
                    activity.onBackPressed();
                }
            }

            @Override
            public void onFailure(Error error) {
                uiUtils.dismissDialog();
                uiUtils.shortToast("Unable to update details");
            }
        });
        //}
    }

    /*private boolean validate() {
        if (selectedIndexOfGroup == 0) {
            uiUtils.shortToast("Please Select group");
            return false;
        }
        return true;
    }*/

    private List<String> getGroupsFromGroupList(List<Group> groups) {
        List<String> groupList = new ArrayList<>();
        for (Group group : groups) {
            groupList.add(group.getName());
        }
        return groupList;
    }


    private List<String> getShiftFromGroupList(Group groups) {
        List<String> shiftList = new ArrayList<>();
        for (Shift shift : groups.getShifts()) {
            shiftList.add(shift.getName());
        }
        return shiftList;
    }
}
