package com.codecool.vendingmachine;

import java.util.*;

public class ProductMachine {

    Map<ProductType, Integer> products = new HashMap<>();

    public ProductMachine() {
        products = new HashMap();
        products.put(ProductType.COLA, 0);
        products.put(ProductType.CHIPS, 0);
        products.put(ProductType.CANDY, 0);
    }

    public void insertProduct(ProductType product) {
        products.put(product, products.get(product) + 1);
    }

    public void retrieveProduct(ProductType product) {
        int available = products.get(product);
        if (available <= 0) return;
        products.put(product, available - 1);
    }

    public boolean isProductAvailable(ProductType product) {
        return products.get(product) > 0;
    }

    public boolean isAllProductsSoldOut() {
        return products.values().stream().filter(i -> i > 0).findAny().isEmpty();
    }

    public String makeReport() {
        return "Remaining products:\n" +
                "\tCola: " + products.get(ProductType.COLA) + "\n" +
                "\tChips: " + products.get(ProductType.CHIPS) + "\n" +
                "\tCandy: " + products.get(ProductType.CANDY);
    }

    public List<CoinType> getPriceOfProductInCoins(ProductType product) {
        List<CoinType> price = new ArrayList<>();
        int currentValue = 0;
        while (currentValue < product.getValue()) {
            int deficit = product.getValue() - currentValue;
            if (deficit >= CoinType.QUARTER.getValue()) {
                price.add(CoinType.QUARTER);
                currentValue += CoinType.QUARTER.getValue();
            } else if (deficit >= CoinType.DIMES.getValue()) {
                price.add(CoinType.DIMES);
                currentValue += CoinType.DIMES.getValue();
            } else if (deficit >= CoinType.NICKEL.getValue()) {
                price.add(CoinType.NICKEL);
                currentValue += CoinType.NICKEL.getValue();
            } else {
                break;
            }
        }
        return price;
    }

}
