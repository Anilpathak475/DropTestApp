package com.cityzipcorp.customerapp.activities;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cityzipcorp.customerapp.utils.CalenderUtil;
import com.cityzipcorp.customerapp.R;
import com.cityzipcorp.customerapp.model.Schedule;
import com.cityzipcorp.customerapp.model.TimeUpdate;
import com.cityzipcorp.customerapp.utils.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditEventActivity extends AppCompatActivity {

    @BindView(R.id.layout_start_time)
    LinearLayout layoutStartTime;

    @BindView(R.id.layout_end_time)
    LinearLayout layoutEndTime;

    @BindView(R.id.txt_start_time)
    TextView txtStartTime;

    @BindView(R.id.txt_end_time)
    TextView txtEndTime;

    @BindView(R.id.txt_date)
    TextView txtDate;

    @BindView(R.id.txt_day)
    TextView txtDay;

    @BindView(R.id.btn_save)
    Button btnSave;

    @BindView(R.id.btn_cancel)
    Button btnCancel;

    @BindView(R.id.spn_cancel_reason)
    Spinner spnCancelReason;

    @BindView(R.id.layout_cancel)
    LinearLayout layoutCancel;


    private boolean isStartTime;

    private int startHour;
    private int startMinute;

    private int endHour;
    private int endMinute;
    private List<String> reasons = new ArrayList<>();
    private int position = 0;
    private Schedule schedule;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(Constants.EDIT_INTENT_EXTRA_DATA) && bundle.containsKey(Constants.EDIT_INTENT_EXTRA_POSITION)) {
                position = bundle.getInt(Constants.EDIT_INTENT_EXTRA_POSITION);
                schedule = (Schedule) bundle.get(Constants.EDIT_INTENT_EXTRA_DATA);
                try {
                    setValues();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }


    @OnClick(R.id.layout_start_time)
    void onStartTimeClick() {
        showDialog(1);
        isStartTime = true;
        btnSave.setVisibility(View.VISIBLE);
    }


    @OnClick(R.id.layout_end_time)
    void onEndTimeClick() {

        // TODO Auto-generated method stub
        showDialog(2);
        isStartTime = false;
    }

    @OnClick(R.id.btn_cancel)
    void onCancle() {
        layoutCancel.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.GONE);
        btnSave.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_save)
    void onSave() {

        Intent intent = new Intent();

        setResult(0, intent);
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        // set time picker as current time
        if (id == 1) {
            return new TimePickerDialog(this, timePickerListener, startHour, startMinute,
                    false);
        } else {
            return new TimePickerDialog(this, timePickerListener, endHour, endMinute,
                    false);

        }
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {


        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            // TODO Auto-generated method stub
            updateTime(hourOfDay, minutes);

        }

    };

    // Used to convert 24hr format to 12hr format with AM/PM values
    private void updateTime(int hours, int mins) {

        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";


        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        // Append in a StringBuilder
        setTime(hours, Integer.parseInt(minutes), timeSet);
    }

    private void setTime(int hours, int minutes, String timeSet) {
        String time = hours + ":" + minutes + " " + timeSet;
        if (isStartTime) {
            startHour = hours;
            startMinute = minutes;
            txtStartTime.setText(time);
        } else {
            if (txtEndTime.getText().toString().contains("PM")) {
                Toast.makeText(this, "Date has been changed", Toast.LENGTH_SHORT).show();
            }
            endHour = hours;
            endMinute = minutes;
            txtEndTime.setText(time);
        }
    }

    public void setValues() {
        Date date = schedule.getDate();
        txtDate.setText(CalenderUtil.getMonthAndDate(date));
        TimeUpdate inTime = schedule.getInTimeUpdate();
        Date inTimeDate = inTime.getTimestamp();
        startHour = inTimeDate.getHours();
        startMinute = inTimeDate.getMinutes();
        txtStartTime.setText(CalenderUtil.getTime(inTimeDate));
        txtDay.setText(CalenderUtil.getDay(inTimeDate));

        TimeUpdate outTime = schedule.getOutTimeUpdate();
        Date outTimeDate = outTime.getTimestamp();
        endHour = outTimeDate.getHours();
        endMinute = outTimeDate.getMinutes();
        txtEndTime.setText(CalenderUtil.getTime(outTimeDate));
    }
}



