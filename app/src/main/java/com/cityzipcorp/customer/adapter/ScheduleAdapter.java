package com.cityzipcorp.customer.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cityzipcorp.customer.R;
import com.cityzipcorp.customer.callbacks.ScheduleAdapterChildCallback;
import com.cityzipcorp.customer.model.Schedule;
import com.cityzipcorp.customer.model.TimeUpdate;
import com.cityzipcorp.customer.utils.CalenderUtil;
import com.cityzipcorp.customer.utils.UiUtils;

import java.util.ArrayList;
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
    private UiUtils uiUtils;
    public ScheduleAdapter(Activity context, List<Schedule> dayEventList, ScheduleAdapterChildCallback scheduleAdapterChildCallback) {
        this.dayEventList = dayEventList;
        this.context = context;
        this.scheduleAdapterChildCallback = scheduleAdapterChildCallback;
        uiUtils = new UiUtils(context);
    }

    @Override
    public DayEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_day_event, parent, false);

        return new DayEventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DayEventViewHolder holder, final int position) {

        Schedule schedule = dayEventList.get(position);
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
        holder.layoutNoData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scheduleAdapterChildCallback.onNoTripClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dayEventList.size();
    }

    public class DayEventViewHolder extends RecyclerView.ViewHolder {
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
        // DateTime eventDate = schedule.getDate();
        if (schedule.getInTimeUpdate() == null && schedule.getOutTimeUpdate() == null) {
            holder.layoutData.setVisibility(View.GONE);
            holder.layoutNoData.setVisibility(View.VISIBLE);

        } else {
            holder.layoutData.setVisibility(View.VISIBLE);
            holder.layoutNoData.setVisibility(View.GONE);
            if (schedule.getInTimeUpdate() != null) {
                setInTimeValue(holder, schedule);
            } else  {
                holder.startTime.setText("--:--");
            }
            if (schedule.getOutTimeUpdate() != null) {
                setOutTimeValues(holder, schedule);
            } else {
                holder.endTime.setText("--:--");
            }
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
