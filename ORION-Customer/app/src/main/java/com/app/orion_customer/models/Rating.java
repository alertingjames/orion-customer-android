package com.app.orion_customer.models;

public class Rating {
    int idx = 0;
    int storeId = 0;
    int productId = 0;
    int userId = 0;
    String userName = "";
    String userPictureUrl = "";
    float rating = 0.0f;
    String date = "";
    String subject = "";
    String description = "";

    public Rating(){

    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getProductId() {
        return productId;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPictureUrl(String userPictureUrl) {
        this.userPictureUrl = userPictureUrl;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIdx() {
        return idx;
    }

    public int getStoreId() {
        return storeId;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPictureUrl() {
        return userPictureUrl;
    }

    public float getRating() {
        return rating;
    }

    public String getDate() {
        return date;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }
}
