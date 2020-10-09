package com.codecool.vendingmachine;

import java.util.Optional;

public enum ProductType {

    COLA(100),
    CHIPS(50),
    CANDY(65);

    private final int value;

    ProductType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Optional<ProductType> get(String name) {
        switch (name.toUpperCase()) {
            case "COLA":
                return Optional.of(COLA);
            case "CHIPS":
                return Optional.of(CHIPS);
            case "CANDY":
                return Optional.of(CANDY);
            default:
                return Optional.empty();
        }
    }
}
