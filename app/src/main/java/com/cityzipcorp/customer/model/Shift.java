package com.cityzipcorp.customer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by anilpathak on 02/01/18.
 */

public class Shift implements Parcelable {
    public static final Parcelable.Creator<Shift> CREATOR = new Parcelable.Creator<Shift>() {
        @Override
        public Shift createFromParcel(Parcel source) {
            return new Shift(source);
        }

        @Override
        public Shift[] newArray(int size) {
            return new Shift[size];
        }
    };
    private String id;
    private String name;

    public Shift() {
    }

    protected Shift(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
    }
}
