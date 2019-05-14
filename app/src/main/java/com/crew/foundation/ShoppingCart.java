package com.crew.foundation;

import android.os.Parcel;
import android.os.Parcelable;

public class ShoppingCart implements Parcelable {

    String title;
    String category;
    int price;
    String pic_path;
    String id_name;


    public ShoppingCart(String title, String category, int price, String pic_path,String id_name) {
        this.title = title;
        this.category = category;
        this.price = price;
        this.pic_path = pic_path;
        this.id_name = id_name;
    }

    public ShoppingCart(Parcel in) {
        title = in.readString();
        category = in.readString();
        price = in.readInt();
        pic_path = in.readString();
        id_name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(category);
        dest.writeInt(price);
        dest.writeString(pic_path);
        dest.writeString(id_name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ShoppingCart> CREATOR = new Creator<ShoppingCart>() {
        @Override
        public ShoppingCart createFromParcel(Parcel in) {
            return new ShoppingCart(in);
        }

        @Override
        public ShoppingCart[] newArray(int size) {
            return new ShoppingCart[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getId_name() {
        return id_name;
    }

    public void setId_name(String id_name) {
        this.id_name = id_name;
    }

    public ShoppingCart() {
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getPic_path() {
        return pic_path;
    }

    public void setPic_path(String pic_path) {
        this.pic_path = pic_path;
    }
}
