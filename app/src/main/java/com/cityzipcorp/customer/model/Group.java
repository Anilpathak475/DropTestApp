package com.cityzipcorp.customer.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anilpathak on 02/01/18.
 */

public class Group implements Parcelable {

    public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel source) {
            return new Group(source);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };
    private String id;
    private String name;
    private List<Shift> shifts;

    public Group() {
    }

    protected Group(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.shifts = new ArrayList<Shift>();
        in.readList(this.shifts, Shift.class.getClassLoader());
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

    public List<Shift> getShifts() {
        return shifts;
    }

    public void setShifts(List<Shift> shifts) {
        this.shifts = shifts;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeList(this.shifts);
    }

    public List<String> getStringShifts() {
        List<String> shiftList = new ArrayList<>();
        for (Shift shift : shifts) {
            shiftList.add(shift.getName());
        }
        return shiftList;
    }
}
