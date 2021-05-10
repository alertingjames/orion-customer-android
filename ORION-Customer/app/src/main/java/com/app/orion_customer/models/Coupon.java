package com.app.orion_customer.models;

public class Coupon {
    int id = 0;
    int userId = 0;
    int discount = 0;
    long expireTime = 0;
    String option = "";
    String status = "";

    public Coupon(){

    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getOption() {
        return option;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getDiscount() {
        return discount;
    }

    public long getExpireTime() {
        return expireTime;
    }
}
