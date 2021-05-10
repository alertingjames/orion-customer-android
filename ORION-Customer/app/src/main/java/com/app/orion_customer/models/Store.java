package com.app.orion_customer.models;

import com.app.orion_customer.commons.Commons;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Store implements Comparable<Store>{
    int id = 1;
    int userId = 1;
    String name = "";
    String phoneNumber = "";
    String address = "";
    String logoUrl = "";
    String _status = "";
    String _registered_time = "";
    float ratings = 0.0f;
    int reviews = 0;
    LatLng latLng = null;

    ArrayList<String> categoryList = new ArrayList<>();
    ArrayList<String> subcategoryList = new ArrayList<>();
    ArrayList<String> genderList = new ArrayList<>();

    public Store(){

    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setCategoryList(ArrayList<String> categoryList) {
        this.categoryList.clear();
        this.categoryList.addAll(categoryList);
    }

    public void setSubcategoryList(ArrayList<String> subcategoryList) {
        this.subcategoryList.clear();
        this.subcategoryList.addAll(subcategoryList);
    }

    public void setGenderList(ArrayList<String> genderList) {
        this.genderList.clear();
        this.genderList.addAll(genderList);
    }

    public ArrayList<String> getCategoryList() {
        return categoryList;
    }

    public ArrayList<String> getSubcategoryList() {
        return subcategoryList;
    }

    public ArrayList<String> getGenderList() {
        return genderList;
    }

    public void setRatings(float ratings) {
        this.ratings = ratings;
    }

    public void setReviews(int reviews) {
        this.reviews = reviews;
    }

    public float getRatings() {
        return ratings;
    }

    public int getReviews() {
        return reviews;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public void set_status(String _status) {
        this._status = _status;
    }

    public void set_registered_time(String _registered_time) {
        this._registered_time = _registered_time;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String get_status() {
        return _status;
    }

    public String get_registered_time() {
        return _registered_time;
    }

    @Override
    public int compareTo(Store other) {
        if(Commons.nameSort == 1)return this.name.compareToIgnoreCase(other.name);
        else if(Commons.nameSort == 2)return other.name.compareToIgnoreCase(this.name);
        else return 0;
    }
}





























