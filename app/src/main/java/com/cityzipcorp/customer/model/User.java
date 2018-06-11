package com.cityzipcorp.customer.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anilpathak on 14/11/17.
 */

public class User implements Parcelable {
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    private String id;
    private String email;
    private String gender;
    private String password;
    @SerializedName("alternate_email")
    private String alternateEmail;
    private String token;
    @SerializedName("valid_upto")
    private String validUpto;
    @SerializedName("company_name")
    private String companyName;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("phone_number")
    private String phoneNumber;
    @SerializedName("home_stop")
    private GeoLocateAddress homeStop;
    @SerializedName("employee_id")
    private String employeeId;
    @SerializedName("nodal_stop")
    private NodalStopBody nodalStop;
    private Group group;
    private Shift shift;
    private String profilePicUri;
    @SerializedName("group_id")
    private String groupId;
    @SerializedName("shift_id")
    private String shiftId;
    @SerializedName("opt_in")
    private Boolean optedIn;
    @SerializedName("weekly_off_days")
    private String[] weeekdaysOff;

    public User() {
    }

    protected User(Parcel in) {
        this.id = in.readString();
        this.email = in.readString();
        this.token = in.readString();
        this.validUpto = in.readString();
        this.companyName = in.readString();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.phoneNumber = in.readString();
        this.homeStop = in.readParcelable(GeoLocateAddress.class.getClassLoader());
        this.nodalStop = in.readParcelable(NodalStopBody.class.getClassLoader());
        this.employeeId = in.readString();
        this.alternateEmail = in.readString();
        this.password = in.readString();
        this.shift = in.readParcelable(Shift.class.getClassLoader());
        this.group = in.readParcelable(Group.class.getClassLoader());
        this.optedIn = in.readByte() != 0; //optedIn == true if byte != 0
        in.readStringArray(weeekdaysOff);
    }

    public User(NodalStopBody nodalStopBody) {
        this.nodalStop = nodalStopBody;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getToken() {
        return token;
    }

    public Address getAddress() {
        return homeStop.getAddress();
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getAlternateEmail() {
        return alternateEmail;
    }

    public void setAlternateEmail(String alternateEmail) {
        this.alternateEmail = alternateEmail;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public GeoLocateAddress getHomeStop() {
        return homeStop;
    }

    public void setHomeStop(GeoLocateAddress homeStop) {
        this.homeStop = homeStop;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.email);
        dest.writeString(this.token);
        dest.writeString(this.validUpto);
        dest.writeString(this.companyName);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.phoneNumber);
        dest.writeParcelable(this.homeStop, flags);
        dest.writeParcelable(this.nodalStop, flags);
        dest.writeString(this.employeeId);
        dest.writeString(this.alternateEmail);
        dest.writeString(this.password);
        dest.writeParcelable(this.shift, flags);
        dest.writeParcelable(this.group, flags);
        dest.writeByte((byte) (this.optedIn ? 1 : 0)); //if optedIn == true, byte == 1
        dest.writeStringArray(this.weeekdaysOff);
    }

    public NodalStopBody getNodalStop() {
        return nodalStop;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setShiftId(String shiftId) {
        this.shiftId = shiftId;
    }

    public String getProfilePicUri() {
        return profilePicUri;
    }

    public void setProfilePicUri(String profilePicUri) {
        this.profilePicUri = profilePicUri;
    }

    public Boolean getOptedIn() {
        return optedIn;
    }

    public void setOptedIn(Boolean optedIn) {
        this.optedIn = optedIn;
    }

    public String[] getWeeekdaysOff() {
        return weeekdaysOff;
    }

    public void setWeeekdaysOff(String[] weeekdaysOff) {
        this.weeekdaysOff = weeekdaysOff;
    }
}
