package com.cityzipcorp.customer.callbacks;

import com.cityzipcorp.customer.model.Group;

import java.util.List;

/**
 * Created by anilpathak on 02/01/18.
 */

public interface GroupCallBack {
    void onSuccess(List<Group> group);
    void onFailure(Error error);
}
