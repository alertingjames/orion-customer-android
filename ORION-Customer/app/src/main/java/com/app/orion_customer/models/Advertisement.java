package com.app.orion_customer.models;

import com.app.orion_customer.R;

public class Advertisement {
    int id = 1;
    int store_id = 1;
    String store_name = "";
    String picture_url = "";
    int picture_res = R.drawable.logo;

    public Advertisement(){

    }

    public void setPicture_res(int picture_res) {
        this.picture_res = picture_res;
    }

    public int getPicture_res() {
        return picture_res;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }

    public int getId() {
        return id;
    }

    public int getStore_id() {
        return store_id;
    }

    public String getStore_name() {
        return store_name;
    }

    public String getPicture_url() {
        return picture_url;
    }
}
