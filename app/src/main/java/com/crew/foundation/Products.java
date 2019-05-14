package com.crew.foundation;

public class Products {
    String product_name;
    String short_name;
    String description;
    String pic;
    String id_name;
    String category;
    int price;
    int stock;

    public Products(String product_name, String short_name, String description, String pic,String id_name, String category, int price, int stock) {
        this.product_name = product_name;
        this.short_name = short_name;
        this.description = description;
        this.pic = pic;
        this.price = price;
        this.stock = stock;
        this.id_name = id_name;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Products(){}

    public String getId_name() {
        return id_name;
    }

    public void setId_name(String id_name) {
        this.id_name = id_name;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
