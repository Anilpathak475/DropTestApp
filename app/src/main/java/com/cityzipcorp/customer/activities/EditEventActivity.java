package com.cityzipcorp.customer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cityzipcorp.customer.R;
import com.cityzipcorp.customer.base.BaseActivity;
import com.cityzipcorp.customer.callbacks.ReasonCallback;
import com.cityzipcorp.customer.callbacks.ShiftCallback;
import com.cityzipcorp.customer.model.Reason;
import com.cityzipcorp.customer.model.ShiftTiming;
import com.cityzipcorp.customer.model.TimeUpdate;
import com.cityzipcorp.customer.model.UpdateSchedule;
import com.cityzipcorp.customer.store.ScheduleStore;
import com.cityzipcorp.customer.utils.CalenderUtil;
import com.cityzipcorp.customer.utils.Constants;
import com.cityzipcorp.customer.utils.NetworkUtils;
import com.cityzipcorp.customer.utils.SharedPreferenceManager;
import com.cityzipcorp.customer.utils.SharedPreferenceManagerConstant;
import com.cityzipcorp.customer.utils.UiUtils;
import com.cityzipcorp.customer.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class EditEventActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {

    @BindView(R.id.layout_in_time)
    LinearLayout layoutInTime;

    @BindView(R.id.toolbar)
    android.support.v7.widget.Toolbar toolbar;

   /* @BindView(R.id.btn_update)
    Button btnUpdate;*/

    @BindView(R.id.layout_main)
    LinearLayout layoutMain;

    @BindView(R.id.layout_out_time)
    LinearLayout layoutOutTime;

    @BindView(R.id.spn_in_time)
    Spinner spnInTime;

    @BindView(R.id.spn_out_time)
    Spinner spnOutTime;

    @BindView(R.id.txt_date)
    TextView txtDate;

    @BindView(R.id.txt_day)
    TextView txtDay;

    @BindView(R.id.btn_save)
    Button btnSave;

    @BindView(R.id.chk_cancel_in_time)
    CheckBox chkCancelInTime;

    @BindView(R.id.chk_cancel_out_time)
    CheckBox chkCancelOutTime;

    @BindView(R.id.spn_cancel_reason_in_time)
    Spinner spnCancelReasonInTime;

    @BindView(R.id.spn_cancel_reason_out_time)
    Spinner spnCancelReasonOutTime;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    private List<String> inTimes = new ArrayList<>();
    private List<String> outTimes = new ArrayList<>();

    private List<String> reasonList = new ArrayList<>();
    private UpdateSchedule schedule;
    private TimeUpdate inTimeUpdate;
    private TimeUpdate outTimeUpdate;
    private UiUtils uiUtils;
    private String macId;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        unbinder = ButterKnife.bind(this);
        uiUtils = new UiUtils(this);
        sharedPreferenceManager = new SharedPreferenceManager(this);
        macId = Utils.getInstance().getMacId(this);
        initTabLayout();
        getShiftTimings();
    }

    private void initTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText("In Time"));
        tabLayout.addTab(tabLayout.newTab().setText("Out Time"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setOnTabSelectedListener(this);
    }

    private void getShiftTimings() {
        ScheduleStore.getInstance(sharedPreferenceManager.getValue(SharedPreferenceManagerConstant.BASE_URL), macId).
                getShiftTimings(sharedPreferenceManager.getValue(SharedPreferenceManagerConstant.ACCESS_TOKEN), new ShiftCallback() {
                    @Override
                    public void onSuccess(ShiftTiming shiftTiming) {
                        inTimes.addAll(shiftTiming.getInTimes());
                        outTimes.addAll(shiftTiming.getOutTimes());
                        setAdapterToSpinner(inTimes, spnInTime);
                        setAdapterToSpinner(outTimes, spnOutTime);
                        getReason();
                    }

                    @Override
                    public void onFailure(Error error) {
                        uiUtils.shortToast(error.getLocalizedMessage());
                    }
                });
    }

    private void getReason() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            uiUtils.showProgressDialog();
            ScheduleStore.getInstance(sharedPreferenceManager.getValue(SharedPreferenceManagerConstant.BASE_URL), macId).
                    getReason(sharedPreferenceManager.getValue(SharedPreferenceManagerConstant.ACCESS_TOKEN), new ReasonCallback() {
                        @Override
                        public void onSuccess(List<Reason> reasons) {
                            reasonList.add("Select Reason");
                            for (Reason reason : reasons) {
                                reasonList.add(reason.getReason());
                            }
                            // TODO: Extract this to method
                            setAdapterToSpinner(reasonList, spnCancelReasonInTime);
                            setAdapterToSpinner(reasonList, spnCancelReasonOutTime);
                            setUiValues();
                            layoutMain.setVisibility(View.VISIBLE);
                            uiUtils.dismissDialog();
                        }

                        @Override
                        public void onFailure(Error error) {
                            uiUtils.dismissDialog();
                            uiUtils.shortToast(error.getMessage());
                        }
                    });
        } else {
            uiUtils.noInternetDialog();
        }
    }

    private void setUiValues() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(Constants.EDIT_INTENT_EXTRA_DATA)) {
                schedule = (UpdateSchedule) bundle.get(Constants.EDIT_INTENT_EXTRA_DATA);
                String timeToDisplay = bundle.getString(Constants.EDIT_INTENT_EXTRA_TIME);
                setDateAndDay();
                if (timeToDisplay != null && timeToDisplay.equalsIgnoreCase("InTime")) {
                    tabLayout.getTabAt(0).select();
                    layoutInTime.setVisibility(View.VISIBLE);
                } else if (timeToDisplay != null && timeToDisplay.equalsIgnoreCase("OutTime")) {
                    tabLayout.getTabAt(1).select();
                    layoutOutTime.setVisibility(View.VISIBLE);
                } else if (timeToDisplay != null && timeToDisplay.equalsIgnoreCase("NoTrip")) {
                    // Sets default
                    setDefaultValues();
                    tabLayout.getTabAt(0).select();
                }

                try {
                    inTimeUpdate = schedule.getInTimeUpdate();
                    if (inTimeUpdate != null)
                        setInTimeValues();
                } catch (Exception e) {
                    chkCancelInTime.setVisibility(View.GONE);
                    e.printStackTrace();


                }
                try {
                    outTimeUpdate = schedule.getOutTimeUpdate();
                    if (outTimeUpdate != null)
                        setOutTimeValues();
                } catch (Exception e) {
                    e.printStackTrace();
                    chkCancelOutTime.setVisibility(View.GONE);
                }
            }
        }
    }

    private void setDefaultValues() {
        layoutInTime.setVisibility(View.VISIBLE);
        chkCancelInTime.setVisibility(View.GONE);
        chkCancelOutTime.setVisibility(View.GONE);
    }


    private void setAdapterToSpinner(final List<String> reasonList, Spinner spinner) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, reasonList);
        spinner.setAdapter(arrayAdapter);

    }

    @OnClick(R.id.btn_save)
    void onSave() {
        try {
            if (tabLayout.getSelectedTabPosition() == 0) {
                updateInTime(schedule);
            } else {
                updateOutTime(schedule);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateInTime(UpdateSchedule schedule) throws ParseException {
        if (validateInTimeValues()) {
            TimeUpdate inTimeUpDate = new TimeUpdate();
            inTimeUpDate.setTimestamp(getTimeFromString(spnInTime.getSelectedItem().toString(), schedule.getDate()));
            inTimeUpDate.setCancelled(chkCancelInTime.isChecked());
            inTimeUpDate.setReason(spnCancelReasonInTime.getSelectedItem().toString());
            schedule.setInTimeUpdate(inTimeUpDate);
            schedule.setOutTimeUpdate(null);
            updateSchedule(schedule);
        }
    }

    private void updateOutTime(UpdateSchedule schedule) throws ParseException {
        if (validateOutTimeValues()) {
            TimeUpdate outTimeUpdate = new TimeUpdate();
            outTimeUpdate.setTimestamp(getTimeFromString(spnOutTime.getSelectedItem().toString(), schedule.getDate()));
            outTimeUpdate.setCancelled(chkCancelOutTime.isChecked());
            outTimeUpdate.setReason(spnCancelReasonOutTime.getSelectedItem().toString());
            schedule.setOutTimeUpdate(outTimeUpdate);
            schedule.setInTimeUpdate(null);
            updateSchedule(schedule);
        }
    }

    private Date getTimeFromString(String time, Date dateFromSchedule) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        Date date = sdf.parse(time);
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(dateFromSchedule);
        calendar.set(Calendar.HOUR, date.getHours());
        calendar.set(Calendar.MINUTE, date.getMinutes());
        return calendar.getTime();
    }

    private void updateSchedule(UpdateSchedule schedule) {
        ScheduleStore.getInstance(sharedPreferenceManager.getValue(SharedPreferenceManagerConstant.BASE_URL), macId).
                updateSchedule(this, schedule, sharedPreferenceManager.getValue(SharedPreferenceManagerConstant.ACCESS_TOKEN));
    }

    private void setInTimeValues() {
        Date inTimeDate = inTimeUpdate.getTimestamp();
        String time = CalenderUtil.get24hrsTime(inTimeDate);
        int indexOfList = inTimes.indexOf(time);
        if (indexOfList > -1) {
            spnInTime.setSelection(indexOfList);
        }
        if (inTimeUpdate.isCancelled()) {
            chkCancelInTime.setChecked(true);
        }
        String reason = inTimeUpdate.getReason();
        if (!TextUtils.isEmpty(reason)) {
            spnCancelReasonInTime.setSelection(reasonList.indexOf(reason));
        }
    }

    private void setOutTimeValues() {
        Date outTimeDate = outTimeUpdate.getTimestamp();
        String time = CalenderUtil.get24hrsTime(outTimeDate);
        int indexOfList = outTimes.indexOf(time);
        if (indexOfList > -1) {
            spnOutTime.setSelection(indexOfList);
        }
        if (outTimeUpdate.isCancelled()) {
            chkCancelOutTime.setChecked(true);
        }
        String reason = outTimeUpdate.getReason();
        if (!TextUtils.isEmpty(reason)) {
            spnCancelReasonOutTime.setSelection(reasonList.indexOf(reason));
        }
    }

    private void setDateAndDay() {
        txtDate.setText(CalenderUtil.getMonthAndDate(schedule.getDate()));
        txtDay.setText(CalenderUtil.getDay(schedule.getDate()));
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (tabLayout.getSelectedTabPosition() == 0) {
            layoutInTime.setVisibility(View.VISIBLE);
            layoutOutTime.setVisibility(View.GONE);
        } else if (tabLayout.getSelectedTabPosition() == 1) {
            layoutInTime.setVisibility(View.GONE);
            layoutOutTime.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private boolean validateInTimeValues() {
        if (spnInTime.getSelectedItem().toString().equalsIgnoreCase("Select")) {
            uiUtils.shortToast("Please Select Time");
            return false;
        }
        if (schedule.getInTimeUpdate() != null) {
            if (spnInTime.getSelectedItem().toString().equalsIgnoreCase(CalenderUtil.get24hrsTime(schedule.getInTimeUpdate().getTimestamp()))) {
                if (schedule.getInTimeUpdate().isCancelled()) {
                    if (chkCancelInTime.isChecked()) {
                        uiUtils.shortToast("Please select different time to update event");
                        return false;
                    }
                } else if (!chkCancelInTime.isChecked()) {
                    uiUtils.shortToast("Please select different time to update event");
                    return false;
                }
            }
        }
        if (spnCancelReasonInTime.getSelectedItem().toString().
                equalsIgnoreCase("Select Reason")) {
            uiUtils.shortToast("Please Select Reason");
            return false;
        }
        return true;
    }

    private boolean validateOutTimeValues() {
        if (spnOutTime.getSelectedItem().toString().equalsIgnoreCase("Select")) {
            uiUtils.shortToast("Please Select Time");
            return false;
        }
        if (schedule.getOutTimeUpdate() != null) {
            if (spnOutTime.getSelectedItem().toString().equalsIgnoreCase(CalenderUtil.get24hrsTime(schedule.getOutTimeUpdate().getTimestamp()))) {
                if (!chkCancelOutTime.isChecked()) {
                    uiUtils.shortToast("Please select different time to update event");
                    return false;
                }
            }
        }
        if (spnCancelReasonOutTime.getSelectedItem().toString()
                .equalsIgnoreCase("Select Reason")) {
            uiUtils.shortToast("Please Select Reason");
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        this.setResult(RESULT_CANCELED, new Intent().putExtra("status", "backPressed"));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}



