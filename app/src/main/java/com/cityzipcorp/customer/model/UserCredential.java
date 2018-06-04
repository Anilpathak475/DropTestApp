package com.cityzipcorp.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anilpathak on 14/11/17.
 */

public class UserCredential {

    private String email;
    private String password;

    @SerializedName("g_recaptcha_token")
    private String captchaResuktToken;

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

    public String getCaptchaResuktToken() {
        return captchaResuktToken;
    }

    public void setCaptchaResuktToken(String captchaResuktToken) {
        this.captchaResuktToken = captchaResuktToken;
    }
}
