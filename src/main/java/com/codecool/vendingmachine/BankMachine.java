package com.codecool.vendingmachine;

import java.util.*;

public class BankMachine {

    private Map<CoinType, Integer> deposit;

    public BankMachine() {
        deposit = new HashMap();
        deposit.put(CoinType.NICKEL, 0);
        deposit.put(CoinType.DIMES, 0);
        deposit.put(CoinType.QUARTER, 0);
    }

    public void insertCoin(CoinType coin) {
        deposit.put(coin, deposit.get(coin) + 1);
    }

    public List<CoinType> createChange(List<CoinType> purchasePrice, List<CoinType> insertedCoins) {
        int inserted = calculateSumOfCoins(insertedCoins);
        int price = calculateSumOfCoins(purchasePrice);
        if (price > inserted) throw new IllegalArgumentException();
        int change = inserted - price;
        if (change == 0) return Collections.emptyList();
        List<CoinType> changeCoinsList = createChangeOrReturnEmptyList(change);
        return changeCoinsList;
    }

    public void retrieveCoins(List<CoinType> coinsToRemove) {
        int numberOfQuarters = 0;
        int numberOfDimens = 0;
        int numberOfNickels = 0;
        for (CoinType coinType : coinsToRemove) {
            switch (coinType) {
                case QUARTER:
                    numberOfQuarters++;
                    break;
                case DIMES:
                    numberOfDimens++;
                    break;
                case NICKEL:
                    numberOfNickels++;
                    break;
            }
        }
        removeChangeCoinsFromDeposit(numberOfQuarters, numberOfDimens, numberOfNickels);
    }

    public boolean isNotAbleToMakeAnyChange() {
        return noNickelsAndDimensInDeposit();
    }

    public boolean isAbleToReturnTheChange(List<CoinType> purchasePrice, List<CoinType> insertedCoins) {
        int price = calculateSumOfCoins(purchasePrice);
        int inserted = calculateSumOfCoins(insertedCoins);
        if (price > inserted) throw new IllegalArgumentException();
        int change = inserted - price;
        return createChangeOrReturnEmptyList(change).size() > 0;
    }

    public String makeReport() {
        return "Remaining coins:\n" +
                "\tQuarters: " + deposit.get(CoinType.QUARTER) + "\n" +
                "\tDimens: " + deposit.get(CoinType.DIMES) + "\n" +
                "\tNickels: " + deposit.get(CoinType.NICKEL);
    }

    private List<CoinType> createChangeOrReturnEmptyList(int changeValue) {
        List<CoinType> change = new ArrayList<>();
        int currentValue = 0;
        int amountOfQuarters = deposit.get(CoinType.QUARTER);
        int amountOfDimens = deposit.get(CoinType.DIMES);
        int amountOfNickels = deposit.get(CoinType.NICKEL);
        while (currentValue < changeValue) {
            int deficit = changeValue - currentValue;
            if (deficit >= CoinType.QUARTER.getValue() && amountOfQuarters > 0) {
                amountOfQuarters--;
                change.add(CoinType.QUARTER);
                currentValue += CoinType.QUARTER.getValue();
            } else if (deficit >= CoinType.DIMES.getValue() && amountOfDimens > 0) {
                amountOfDimens--;
                change.add(CoinType.DIMES);
                currentValue += CoinType.DIMES.getValue();
            } else if (deficit >= CoinType.NICKEL.getValue() && amountOfNickels > 0) {
                amountOfNickels--;
                change.add(CoinType.NICKEL);
                currentValue += CoinType.NICKEL.getValue();
            } else {
                break;
            }
        }
        if (calculateSumOfCoins(change) == changeValue) return change;
        else return Collections.emptyList();
    }

    private void removeChangeCoinsFromDeposit(int numberOfQuarters, int numberOfDimens, int numberOfNickels) {
        if (coinsToRemoveAreNotAvailableInDeposit(numberOfQuarters, numberOfDimens, numberOfNickels)) {
            return;
        }
        deposit.put(CoinType.QUARTER, deposit.get(CoinType.QUARTER) - numberOfQuarters);
        deposit.put(CoinType.DIMES, deposit.get(CoinType.DIMES) - numberOfDimens);
        deposit.put(CoinType.NICKEL, deposit.get(CoinType.NICKEL) - numberOfNickels);
    }

    private boolean coinsToRemoveAreNotAvailableInDeposit(int quarters, int dimens, int nickels) {
        return (quarters > deposit.get(CoinType.QUARTER) ||
                dimens > deposit.get(CoinType.DIMES) ||
                nickels > deposit.get(CoinType.NICKEL));
    }

    private boolean noNickelsAndDimensInDeposit() {
        return deposit.get(CoinType.NICKEL) <= 0 &&
                deposit.get(CoinType.DIMES) <= 0;
    }

    public int calculateSumOfCoins(List<CoinType> coins) {
        return coins.stream().map(CoinType::getValue).reduce(0, Integer::sum);
    }
}
