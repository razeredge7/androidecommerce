package com.crew.foundation;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Transaction implements Parcelable {
    String transaction_Id;
    int total_cost;
    String courier;
    String payment;
    int total_item;
    String status;
    ArrayList<ShoppingCart> cart;
    String date;

    public Transaction(String transaction_Id, int total_cost, String courier, String payment, int total_item, String status,ArrayList<ShoppingCart> cart,String date) {
        this.transaction_Id = transaction_Id;
        this.total_cost = total_cost;
        this.courier = courier;
        this.payment = payment;
        this.total_item = total_item;
        this.status = status;
        this.cart = cart;
        this.date = date;
    }

    public Transaction() {
    }

    protected Transaction(Parcel in) {
        transaction_Id = in.readString();
        total_cost = in.readInt();
        courier = in.readString();
        payment = in.readString();
        total_item = in.readInt();
        status = in.readString();
        cart = in.createTypedArrayList(ShoppingCart.CREATOR);
        date = in.readString();
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(transaction_Id);
        parcel.writeInt(total_cost);
        parcel.writeString(courier);
        parcel.writeString(payment);
        parcel.writeInt(total_item);
        parcel.writeString(status);
        parcel.writeTypedList(cart);
        parcel.writeString(date);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<ShoppingCart> getCart() {
        return cart;
    }

    public void setCart(ArrayList<ShoppingCart> cart) {
        this.cart = cart;
    }

    public String getTransaction_Id() {
        return transaction_Id;
    }

    public void setTransaction_Id(String transaction_Id) {
        this.transaction_Id = transaction_Id;
    }

    public int getTotal_cost() {
        return total_cost;
    }

    public void setTotal_cost(int total_cost) {
        this.total_cost = total_cost;
    }

    public String getCourier() {
        return courier;
    }

    public void setCourier(String courier) {
        this.courier = courier;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public int getTotal_item() {
        return total_item;
    }

    public void setTotal_item(int total_item) {
        this.total_item = total_item;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
