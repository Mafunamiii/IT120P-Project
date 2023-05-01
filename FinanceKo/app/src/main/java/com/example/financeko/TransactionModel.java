package com.example.financeko;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class TransactionModel implements Parcelable {
    String id;

    String userID; //foreignkey
    String amount;
    String date;
    String category;

    String frequency;

    public TransactionModel(String id, String userID, String date, String category, String frequency, String amount) {
        this.id = id;
        this.userID = userID;
        this.date = date;
        this.category = category;
        this.frequency = frequency;
        this.amount = amount;
    }

    protected TransactionModel(Parcel in) {
        id = in.readString();
        userID = in.readString();
        date = in.readString();
        category = in.readString();
        frequency = in.readString();
        amount = in.readString();
    }

    public static final Creator<TransactionModel> CREATOR = new Creator<TransactionModel>() {
        @Override
        public TransactionModel createFromParcel(Parcel in) {
            return new TransactionModel(in);
        }

        @Override
        public TransactionModel[] newArray(int size) {
            return new TransactionModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel out, int i) {
        out.writeString(id);
        out.writeString(userID);
        out.writeString(date);
        out.writeString(category);
        out.writeString(frequency);
        out.writeString(amount);
    }

    public String getId() {
        return id;
    }

    public String getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getCategory(){ return category;}

    public String getUserID() { return userID; }

    public String getFrequency() { return frequency; }
}
