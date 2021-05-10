package com.app.orion_customer.comparators;

import com.app.orion_customer.commons.Commons;
import com.app.orion_customer.models.Product;

import java.util.Comparator;

public class ProductPriceComparator implements Comparator<Product> {
    @Override
    public int compare(Product product1, Product product2) {
        if(Commons.priceSort == 1) return (int) (product1.getPrice() - product2.getPrice());
        else if(Commons.priceSort == 2) return (int) (product2.getPrice() - product1.getPrice());
        else return 0;
    }
}
