package com.cityzipcorp.customerapp.adapter;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cityzipcorp.customerapp.R;
import com.cityzipcorp.customerapp.callbacks.ScheduleAdapterChildCallback;
import com.cityzipcorp.customerapp.model.Schedule;
import com.cityzipcorp.customerapp.model.TimeUpdate;
import com.cityzipcorp.customerapp.utils.CalenderUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by anilpathak on 30/10/17.
 */

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.DayEventViewHolder> {

    private List<Schedule> dayEventList = new ArrayList<>();
    private Activity context;
    private ScheduleAdapterChildCallback scheduleAdapterChildCallback;
    public ScheduleAdapter(Activity context, List<Schedule> dayEventList, ScheduleAdapterChildCallback scheduleAdapterChildCallback) {
        this.dayEventList = dayEventList;
        this.context = context;
        this.scheduleAdapterChildCallback = scheduleAdapterChildCallback;
    }

    @Override
    public DayEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_day_event, parent, false);

        return new DayEventViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(DayEventViewHolder holder, final int position) {
        final Schedule schedule = dayEventList.get(position);
        setValues(holder, schedule);
        holder.inTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scheduleAdapterChildCallback.inTimeClick(position);
            }
        });
        holder.outTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scheduleAdapterChildCallback.outTimeClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dayEventList.size();
    }

    public class DayEventViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_date)
        TextView date;
        @BindView(R.id.txt_start_time)
        TextView startTime;
        @BindView(R.id.txt_end_time)
        TextView endTime;
        @BindView(R.id.layout_in_time)
        LinearLayout inTimeLayout;
        @BindView(R.id.layout_out_time)
        LinearLayout outTimeLayout;
        @BindView(R.id.layout_data)
        LinearLayout layoutData;
        @BindView(R.id.layout_no_data)
        RelativeLayout layoutNoData;

        private DayEventViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private void setValues(DayEventViewHolder holder, Schedule schedule) {
        try {
            Date eventDate = schedule.getDate();
            String startDate = CalenderUtil.getDay(eventDate) + ", " + CalenderUtil.getMonthAndDate(eventDate);
            holder.date.setText(startDate);
            holder.layoutData.setVisibility(View.VISIBLE);
            holder.layoutNoData.setVisibility(View.GONE);

            setInTimeValue(holder, schedule);
            setOutTimeValues(holder, schedule);
        } catch (Exception e) {
            e.printStackTrace();
            holder.layoutData.setVisibility(View.GONE);
            holder.layoutNoData.setVisibility(View.VISIBLE);
        }
    }

    private void setInTimeValue(DayEventViewHolder holder, Schedule schedule) throws NullPointerException {
        TimeUpdate inTime = schedule.getInTimeUpdate();
        holder.startTime.setText(CalenderUtil.getTime(inTime.getTimestamp()));
        if (inTime.isCancelled()) {
            holder.inTimeLayout.setBackgroundColor(getColor(R.color.cancelled_bg));
        } else if (inTime.getId() != null) {
            holder.inTimeLayout.setBackgroundColor(getColor(R.color.edited_date));
        } else {
            holder.inTimeLayout.setBackgroundColor(getColor(R.color.white));
        }
    }

    private void setOutTimeValues(DayEventViewHolder holder, Schedule schedule) throws NullPointerException {
        TimeUpdate outTime = schedule.getOutTimeUpdate();
        holder.endTime.setText(CalenderUtil.getTime(outTime.getTimestamp()));
        if (outTime.isCancelled()) {
            holder.outTimeLayout.setBackgroundColor(getColor(R.color.cancelled_bg));
        } else if (outTime.getId() != null) {
            holder.outTimeLayout.setBackgroundColor(getColor(R.color.edited_date));
        } else {
            holder.outTimeLayout.setBackgroundColor(getColor(R.color.white));
        }
    }

    private int getColor(int colorId) {
        return context.getResources().getColor(colorId);
    }
}
