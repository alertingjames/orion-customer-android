package com.app.orion_customer.comparators;

import com.app.orion_customer.commons.Commons;
import com.app.orion_customer.models.Product;

import java.util.Comparator;

public class ProductNameComparator implements Comparator<Product> {

    @Override
    public int compare(Product product1, Product product2) {
        if(Commons.nameSort == 1)return product1.getName().compareToIgnoreCase(product2.getName());
        else if(Commons.nameSort == 2)return product2.getName().compareToIgnoreCase(product1.getName());
        else return 0;
    }
}