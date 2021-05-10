package com.app.orion_customer.comparators;

import com.app.orion_customer.models.Product;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ProductChainedComparator implements Comparator<Product> {

    private List<Comparator<Product>> listComparators;

    @SafeVarargs
    public ProductChainedComparator(Comparator<Product>... comparators) {
        this.listComparators = Arrays.asList(comparators);
    }

    @Override
    public int compare(Product product1, Product product2) {
        for (Comparator<Product> comparator : listComparators) {
            int result = comparator.compare(product1, product2);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }
}
