package com.app.orion_customer.models;

public class CartItem {
    int id = 1;
    int user_id = 1;
    int vendor_id = 1;
    int store_id = 1;
    int brand_id = 1;
    String store_name = "";
    int product_id = 1;
    String product_name = "";
    String category = "";
    String subcategory = "";
    String gender = "";
    String gender_key = "";
    double price = 0.0d;
    double new_price = 0.0d;
    String unit = "SGD";
    int quantity = 0;
    String date_time = "";
    String picture_url = "";
    double delivery_price = 0.0d;
    int delivery_days = 0;
    String status = "";

    public CartItem(){

    }

    public void setVendor_id(int vendor_id) {
        this.vendor_id = vendor_id;
    }

    public int getVendor_id() {
        return vendor_id;
    }

    public void setNew_price(double new_price) {
        this.new_price = new_price;
    }

    public double getNew_price() {
        return new_price;
    }

    public void setDelivery_price(double delivery_price) {
        this.delivery_price = delivery_price;
    }

    public void setDelivery_days(int delivery_days) {
        this.delivery_days = delivery_days;
    }

    public double getDelivery_price() {
        return delivery_price;
    }

    public int getDelivery_days() {
        return delivery_days;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public void setBrand_id(int brand_id) {
        this.brand_id = brand_id;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setGender_key(String gender_key) {
        this.gender_key = gender_key;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getStore_id() {
        return store_id;
    }

    public int getBrand_id() {
        return brand_id;
    }

    public String getStore_name() {
        return store_name;
    }

    public int getProduct_id() {
        return product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getCategory() {
        return category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public String getGender() {
        return gender;
    }

    public String getGender_key() {
        return gender_key;
    }

    public double getPrice() {
        return price;
    }

    public String getUnit() {
        return unit;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDate_time() {
        return date_time;
    }

    public String getPicture_url() {
        return picture_url;
    }

    public String getStatus() {
        return status;
    }
}
