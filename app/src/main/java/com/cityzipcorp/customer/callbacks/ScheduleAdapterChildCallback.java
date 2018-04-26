package com.cityzipcorp.customer.callbacks;

/**
 * Created by anilpathak on 09/11/17.
 */

public interface ScheduleAdapterChildCallback {
    void inTimeClick(int position);
    void outTimeClick(int position);
    void onNoTripClick(int position);
}
