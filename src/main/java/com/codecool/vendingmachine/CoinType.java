package com.codecool.vendingmachine;

import java.util.Optional;

public enum CoinType {

    QUARTER(25),
    DIMES(10),
    NICKEL(5);

    private final int value;

    CoinType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Optional<CoinType> get(String name) {
        switch (name.toUpperCase()) {
            case "QUARTER":
                return Optional.of(QUARTER);
            case "DIMES":
                return Optional.of(DIMES);
            case "NICKEL":
                return Optional.of(NICKEL);
            default:
                return Optional.empty();
        }
    }
}
