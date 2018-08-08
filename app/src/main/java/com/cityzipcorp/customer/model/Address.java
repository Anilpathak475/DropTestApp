package com.cityzipcorp.customer.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anilpathak on 21/11/17.
 */

public class Address implements Parcelable {

    public static final Parcelable.Creator<Address> CREATOR = new Parcelable.Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel source) {
            return new Address(source);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };
    private String id;
    private String society = "";
    private String locality = "";
    private String landmark = "";
    private String area = "";
    private String city;
    private String state;
    private String country;
    @SerializedName("street_address")
    private String streetAddress = "";
    @SerializedName("postal_code")
    private String postalCode = "";

    public Address() {
    }

    private Address(Parcel in) {
        this.id = in.readString();
        this.society = in.readString();
        this.locality = in.readString();
        this.landmark = in.readString();
        this.area = in.readString();
        this.city = in.readString();
        this.state = in.readString();
        this.country = in.readString();
        this.streetAddress = in.readString();
        this.postalCode = in.readString();
    }

    public String getSociety() {
        return society;
    }

    public void setSociety(String society) {
        this.society = society;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.society);
        dest.writeString(this.locality);
        dest.writeString(this.landmark);
        dest.writeString(this.area);
        dest.writeString(this.city);
        dest.writeString(this.state);
        dest.writeString(this.country);
        dest.writeString(this.streetAddress);
        dest.writeString(this.postalCode);
    }
}
