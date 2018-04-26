package com.cityzipcorp.customer.model;

import com.google.gson.annotations.SerializedName;

public class SetNewPassword {

    @SerializedName("reset_id")
    private String resetId;
    @SerializedName("password")
    private String newPassword;

    private String confirmPassword;
    private String email;

    public void setResetId(String resetId) {
        this.resetId = resetId;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
