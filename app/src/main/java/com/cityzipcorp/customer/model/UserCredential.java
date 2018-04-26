package com.cityzipcorp.customer.model;

/**
 * Created by anilpathak on 14/11/17.
 */

public class UserCredential {

    private String email;
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
