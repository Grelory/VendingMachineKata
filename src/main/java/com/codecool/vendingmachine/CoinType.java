package com.codecool.vendingmachine;

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
}
