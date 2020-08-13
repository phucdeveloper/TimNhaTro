package com.doan.timnhatro.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

public class Utilities implements Parcelable {
    private String nameUtilities;
    private int iconUtilities;
    private boolean isChecked;

    public Utilities(String nameUtilities, int iconUtilities, boolean isChecked) {
        this.nameUtilities = nameUtilities;
        this.iconUtilities = iconUtilities;
        this.isChecked = isChecked;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected Utilities(Parcel in) {
        nameUtilities = in.readString();
        iconUtilities = in.readInt();
        isChecked = in.readBoolean();
    }

    public static final Creator<Utilities> CREATOR = new Creator<Utilities>() {
        @Override
        public Utilities createFromParcel(Parcel in) {
            return new Utilities(in);
        }

        @Override
        public Utilities[] newArray(int size) {
            return new Utilities[size];
        }
    };

    public String getNameUtilities() {
        return nameUtilities;
    }

    public int getIconUtilities() {
        return iconUtilities;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(nameUtilities);
        parcel.writeInt(iconUtilities);
        parcel.writeBoolean(isChecked);
    }
}
