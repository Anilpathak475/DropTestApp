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
import android.widget.Toast;

import com.cityzipcorp.customer.R;
import com.cityzipcorp.customer.base.BaseActivity;
import com.cityzipcorp.customer.callbacks.ReasonCallback;
import com.cityzipcorp.customer.callbacks.ShiftCallback;
import com.cityzipcorp.customer.model.Reason;
import com.cityzipcorp.customer.model.Schedule;
import com.cityzipcorp.customer.model.ShiftTiming;
import com.cityzipcorp.customer.model.TimeUpdate;
import com.cityzipcorp.customer.store.ScheduleStore;
import com.cityzipcorp.customer.utils.CalenderUtil;
import com.cityzipcorp.customer.utils.Constants;
import com.cityzipcorp.customer.utils.NetworkUtils;
import com.cityzipcorp.customer.utils.SharedPreferenceManager;
import com.cityzipcorp.customer.utils.UiUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    private boolean isStartTime;

    private List<String> inTimes = new ArrayList<>();
    private List<String> outTimes = new ArrayList<>();

    private List<String> reasonList = new ArrayList<>();
    private Schedule schedule;
    private TimeUpdate inTimeUpdate;
    private TimeUpdate outTimeUpdate;
    private UiUtils uiUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        ButterKnife.bind(this);
        uiUtils = new UiUtils(this);
        sharedPreferenceManager = new SharedPreferenceManager(this);
        initTabLayout();
        getShiftTimings();
        getReason();
    }

    private void initTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText("In Time"));
        tabLayout.addTab(tabLayout.newTab().setText("Out Time"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setOnTabSelectedListener(this);
    }

    private void getShiftTimings() {
        ScheduleStore.getInstance().getShiftTimings(sharedPreferenceManager.getAccessToken(), new ShiftCallback() {
            @Override
            public void onSuccess(ShiftTiming shiftTiming) {
                inTimes.addAll(shiftTiming.getInTimes());
                outTimes.addAll(shiftTiming.getOutTimes());
                setAdapterToSpinner(CalenderUtil.convertShiftTimeTo12HrsFormat(shiftTiming.getInTimes()), spnInTime);
                setAdapterToSpinner(CalenderUtil.convertShiftTimeTo12HrsFormat(shiftTiming.getOutTimes()), spnOutTime);
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
            ScheduleStore.getInstance().getReason(sharedPreferenceManager.getAccessToken(), new ReasonCallback() {
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
                    uiUtils.shortToast("Something went wrong!");
                }
            });
        } else {

        }
    }

    private void setUiValues() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(Constants.EDIT_INTENT_EXTRA_DATA)) {
                schedule = (Schedule) bundle.get(Constants.EDIT_INTENT_EXTRA_DATA);
                String timeToDisplay = bundle.getString(Constants.EDIT_INTENT_EXTRA_TIME);
                if (timeToDisplay.equalsIgnoreCase("InTime")) {
                    tabLayout.getTabAt(0).select();
                    layoutInTime.setVisibility(View.VISIBLE);
                } else if (timeToDisplay.equalsIgnoreCase("OutTime")) {
                    tabLayout.getTabAt(1).select();
                    layoutOutTime.setVisibility(View.VISIBLE);
                } else if (timeToDisplay.equalsIgnoreCase("NoTrip")) {
                    // Sets default
                    setDefaultValues();
                    tabLayout.getTabAt(0).select();
                }
                try {
                    inTimeUpdate = schedule.getInTimeUpdate();
                    setInTimeValues();
                } catch (Exception e) {
                    chkCancelInTime.setVisibility(View.GONE);
                    e.printStackTrace();


                }
                try {
                    outTimeUpdate = schedule.getOutTimeUpdate();
                    setOutTimeValues();
                } catch (Exception e) {
                    e.printStackTrace();
                    chkCancelOutTime.setVisibility(View.GONE);
                }
            } else if (bundle.containsKey("dateMap")) {
                HashMap<String, Schedule> bulkList = (HashMap<String, Schedule>) bundle.getSerializable("dateMap");
                Set<String> dateSet = bulkList.keySet();
                String dateToDisplay = "";
                for (String key : dateSet) {
                    Date date = CalenderUtil.getDateFromString(key);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    dateToDisplay = dateToDisplay + calendar.get(Calendar.DATE) + " " + CalenderUtil.getMonth(date) + ", ";
                       /* if(index == 0 ) {
                            Date date = CalenderUtil.getDateFromString(key);
                            dateToDisplay = date.getDate() +" "+CalenderUtil.getMonth(date);
                        }
                        if(index == dateSet.size()-1) {
                            Date date = CalenderUtil.getDateFromString(key);
                            dateToDisplay = dateToDisplay + " - "+date.getDate() +" "+CalenderUtil.getMonth(date);
                        }*/
                }
                txtDate.setText(dateToDisplay);
                tabLayout.getTabAt(0).select();
                layoutInTime.setVisibility(View.VISIBLE);
                txtDay.setText("Selected Dates");
            } else {
                inTimeUpdate = new TimeUpdate();
                outTimeUpdate = new TimeUpdate();
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

    private void updateInTime(Schedule schedule) throws ParseException {
        if (validateInTimeValues()) {
            //TODO :Make common method for values
            TimeUpdate inTimeUpDate = new TimeUpdate();
            inTimeUpDate.setTimestamp(getTimeFromString(inTimes.get(spnInTime.getSelectedItemPosition()), schedule.getDate()));
            inTimeUpDate.setCancelled(chkCancelInTime.isChecked());
            inTimeUpDate.setReason(spnCancelReasonInTime.getSelectedItem().toString());
            schedule.setInTimeUpdate(inTimeUpDate);
            schedule.setOutTimeUpdate(null);
            updateSchedule(schedule);
        }
    }

    private void updateOutTime(Schedule schedule) throws ParseException {
        if (validateOutTimeValues()) {
            TimeUpdate outTimeUpdate = new TimeUpdate();
            outTimeUpdate.setTimestamp(getTimeFromString(outTimes.get(spnOutTime.getSelectedItemPosition()), schedule.getDate()));
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
        calendar.setTime(date);
        calendar.set(Calendar.DATE, dateFromSchedule.getDate());
        calendar.set(Calendar.MONTH, dateFromSchedule.getMonth());
        return calendar.getTime();
    }

    private void updateSchedule(Schedule schedule) {
        ScheduleStore.getInstance().updateSchedule(this, schedule, sharedPreferenceManager.getAccessToken());
    }

    private void setInTimeValues() {
        txtDate.setText(CalenderUtil.getMonthAndDate(schedule.getDate()));
        txtDay.setText(CalenderUtil.getDay(schedule.getDate()));
        Date inTimeDate = inTimeUpdate.getTimestamp();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(inTimeDate);
        if (inTimes.indexOf(CalenderUtil.getTime(inTimeDate)) <= 0) {
            spnInTime.setSelection(inTimes.indexOf(CalenderUtil.getTime(inTimeDate)));
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
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(outTimeDate);
        if (inTimes.indexOf(CalenderUtil.getTime(outTimeDate)) <= 0) {
            spnInTime.setSelection(inTimes.indexOf(CalenderUtil.getTime(outTimeDate)));
        }
        if (outTimeUpdate.isCancelled()) {
            chkCancelOutTime.setChecked(true);
        }
        String reason = outTimeUpdate.getReason();
        if (!TextUtils.isEmpty(reason)) {
            spnCancelReasonOutTime.setSelection(reasonList.indexOf(reason));
        }
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
            Toast.makeText(this, "Please Select Time", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (spnInTime.getSelectedItem().toString().equalsIgnoreCase(CalenderUtil.getTime(schedule.getInTimeUpdate().getTimestamp()))) {

        }
        if (spnCancelReasonInTime.getSelectedItem().toString().
                equalsIgnoreCase("Select Reason")) {
            Toast.makeText(this, "Please Select Reason", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateOutTimeValues() {
        if (spnOutTime.getSelectedItem().toString().equalsIgnoreCase("Select")) {
            Toast.makeText(this, "Please Select Time", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (spnCancelReasonOutTime.getSelectedItem().toString()
                .equalsIgnoreCase("Select Reason")) {
            Toast.makeText(this, "Please Select Reason", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        this.setResult(RESULT_CANCELED, new Intent().putExtra("status", "backPressed"));
        finish();
    }
}



