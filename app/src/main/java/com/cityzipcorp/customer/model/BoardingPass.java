package com.cityzipcorp.customer.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class BoardingPass implements Parcelable {

    public static final Parcelable.Creator<BoardingPass> CREATOR = new Parcelable.Creator<BoardingPass>() {
        @Override
        public BoardingPass createFromParcel(Parcel source) {
            return new BoardingPass(source);
        }

        @Override
        public BoardingPass[] newArray(int size) {
            return new BoardingPass[size];
        }
    };
    @SerializedName("vehicle_color")
    private String vehicleColor = "";
    @SerializedName("trip_name")
    private String tripName = "";
    @SerializedName("stop_timestamp")
    private Date stopTimestamp;
    @SerializedName("stop_location")
    private GeoJsonPoint stopLocation;
    @SerializedName("vehicle_model")
    private String vehicleModel = "";
    @SerializedName("stop_address")
    private String stopAddress = "";
    @SerializedName("vehicle_type")
    private String vehicleType = "";
    @SerializedName("driver_name")
    private String driverName = "";
    @SerializedName("driver_phone_number")
    private String driverPhoneNumber = "";
    @SerializedName("trip_type")
    private String tripType = "";
    @SerializedName("duty_id")
    private String dutyId = "";
    @SerializedName("vehicle_number")
    private String vehicleNumber = "";
    @SerializedName("stop_id")
    private String stopId = "";
    @SerializedName("id")
    private String id = "";
    private String otp = "";
    @SerializedName("stop_type")
    private String stopType = "";
    private boolean attended;
    @SerializedName("attended_at")
    private Date attendedAt;
    @SerializedName("attended_location")
    private GeoJsonPoint geoJsonPoint;

    public BoardingPass() {
    }

    protected BoardingPass(Parcel in) {
        this.vehicleColor = in.readString();
        this.tripName = in.readString();
        long tmpStopTimestamp = in.readLong();
        this.stopTimestamp = tmpStopTimestamp == -1 ? null : new Date(tmpStopTimestamp);
        this.stopLocation = in.readParcelable(GeoJsonPoint.class.getClassLoader());
        this.vehicleModel = in.readString();
        this.stopAddress = in.readString();
        this.vehicleType = in.readString();
        this.driverName = in.readString();
        this.driverPhoneNumber = in.readString();
        this.tripType = in.readString();
        this.dutyId = in.readString();
        this.vehicleNumber = in.readString();
        this.stopId = in.readString();
        this.id = in.readString();
        this.otp = in.readString();
        this.stopType = in.readString();
        this.attended = in.readByte() != 0;
        long tmpAttendedAt = in.readLong();
        this.attendedAt = tmpAttendedAt == -1 ? null : new Date(tmpAttendedAt);
        this.geoJsonPoint = in.readParcelable(GeoJsonPoint.class.getClassLoader());
    }

    public boolean isAttended() {
        return attended;
    }

    public void setAttended(boolean attended) {
        this.attended = attended;
    }

    public Date getAttendedAt() {
        return attendedAt;
    }

    public void setAttendedAt(Date attendedAt) {
        this.attendedAt = attendedAt;
    }

    public GeoJsonPoint getGeoJsonPoint() {
        return geoJsonPoint;
    }

    public void setGeoJsonPoint(GeoJsonPoint geoJsonPoint) {
        this.geoJsonPoint = geoJsonPoint;
    }

    public String getVehicleColor() {
        return vehicleColor;
    }

    public void setVehicleColor(String vehicleColor) {
        this.vehicleColor = vehicleColor;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public Date getStopTimestamp() {
        return stopTimestamp;
    }

    public void setStopTimestamp(Date stopTimestamp) {
        this.stopTimestamp = stopTimestamp;
    }

    public GeoJsonPoint getStopLocation() {
        return stopLocation;
    }

    public void setStopLocation(GeoJsonPoint geoJsonPoint) {
        this.stopLocation = geoJsonPoint;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getStopAddress() {
        return stopAddress;
    }

    public void setStopAddress(String stopAddress) {
        this.stopAddress = stopAddress;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhoneNumber() {
        return driverPhoneNumber;
    }

    public void setDriverPhoneNumber(String driverPhoneNumber) {
        this.driverPhoneNumber = driverPhoneNumber;
    }

    public String getTripType() {
        return tripType;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    public String getDutyId() {
        return dutyId;
    }

    public void setDutyId(String dutyId) {
        this.dutyId = dutyId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStopType() {
        return stopType;
    }

    public void setStopType(String stopType) {
        this.stopType = stopType;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.vehicleColor);
        dest.writeString(this.tripName);
        dest.writeLong(this.stopTimestamp != null ? this.stopTimestamp.getTime() : -1);
        dest.writeParcelable(this.stopLocation, flags);
        dest.writeString(this.vehicleModel);
        dest.writeString(this.stopAddress);
        dest.writeString(this.vehicleType);
        dest.writeString(this.driverName);
        dest.writeString(this.driverPhoneNumber);
        dest.writeString(this.tripType);
        dest.writeString(this.dutyId);
        dest.writeString(this.vehicleNumber);
        dest.writeString(this.stopId);
        dest.writeString(this.id);
        dest.writeString(this.otp);
        dest.writeString(this.stopType);
        dest.writeByte(this.attended ? (byte) 1 : (byte) 0);
        dest.writeLong(this.attendedAt != null ? this.attendedAt.getTime() : -1);
        dest.writeParcelable(this.geoJsonPoint, flags);
    }
}
