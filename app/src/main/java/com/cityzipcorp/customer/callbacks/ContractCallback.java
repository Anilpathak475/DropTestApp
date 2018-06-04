package com.cityzipcorp.customer.callbacks;

import com.cityzipcorp.customer.model.Contract;

import java.util.List;

public interface ContractCallback {
    void onSuccess(List<Contract> areas);

    void onFailure(Error error);
}
