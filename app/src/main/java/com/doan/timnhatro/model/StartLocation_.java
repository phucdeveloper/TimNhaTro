package com.doan.timnhatro.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StartLocation_ implements Parcelable {

    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lng")
    @Expose
    private Double lng;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.lat);
        dest.writeValue(this.lng);
    }

    public StartLocation_() {
    }

    protected StartLocation_(Parcel in) {
        this.lat = (Double) in.readValue(Double.class.getClassLoader());
        this.lng = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Creator<StartLocation_> CREATOR = new Creator<StartLocation_>() {
        @Override
        public StartLocation_ createFromParcel(Parcel source) {
            return new StartLocation_(source);
        }

        @Override
        public StartLocation_[] newArray(int size) {
            return new StartLocation_[size];
        }
    };
}
