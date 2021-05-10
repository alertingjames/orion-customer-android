package com.app.orion_customer.models;

import com.app.orion_customer.R;
import com.app.orion_customer.commons.Commons;

public class Category implements Comparable<Category>{
    int id = 0;
    String title = "";
    int resource = R.drawable.logo;

    public Category(){

    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getResource() {
        return resource;
    }

    @Override
    public int compareTo(Category other) {
        return this.title.compareToIgnoreCase(other.title);
    }
}
