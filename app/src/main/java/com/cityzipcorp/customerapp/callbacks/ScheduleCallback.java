package com.cityzipcorp.customerapp.callbacks;

import com.cityzipcorp.customerapp.model.Schedule;

import java.util.List;

/**
 * Created by anilpathak on 06/11/17.
 */

public interface ScheduleCallback {
    void onSuccess(List<Schedule> schedules);
    void onFailure(Error error);
}
