package com.cityzipcorp.customer.callbacks;

import com.cityzipcorp.customer.model.Area;

import java.util.List;

public interface AreaCallback {
    void onSuccess(List<Area> areas);

    void onFailure(Error error);
}
