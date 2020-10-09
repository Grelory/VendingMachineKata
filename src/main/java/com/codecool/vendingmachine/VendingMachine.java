package com.codecool.vendingmachine;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VendingMachine {

    private final String adminPassword = "#1234";
    private BankMachine bankMachine;
    private boolean vendingMachineIsRunning = true;
    private List<CoinType> insertedCoins = new ArrayList();
    private IOMachine ioMachine;
    private ProductMachine productMachine;

    public VendingMachine(BankMachine bankMachine, IOMachine ioMachine, ProductMachine productMachine) {
        this.bankMachine = bankMachine;
        this.ioMachine = ioMachine;
        this.productMachine = productMachine;
    }

    private void insertUserCoin(CoinType coin) {
        if (productMachine.isAllProductsSoldOut()) {
            ioMachine.displayMessage("NO PRODUCTS AVAILABLE\nCOIN DISCARDED");
            return;
        }
        if (bankMachine.isNotAbleToMakeAnyChange()) {
            ioMachine.displayMessage("EXACT CHANGE ONLY");
        }
        bankMachine.insertCoin(coin);
        insertedCoins.add(coin);
        ioMachine.displayMessage("INSERTED COIN " + coin.toString());
    }

    private void returnUserCoins() {
        if (insertedCoins.isEmpty()) {
            ioMachine.displayMessage("RETURNED COINS NONE");
            return;
        }
        StringBuilder messageBuilder = new StringBuilder("RETURNED COINS");
        for (CoinType coin : insertedCoins) {
            messageBuilder.append(" ").append(coin.toString());
        }
        ioMachine.displayMessage(messageBuilder.toString());
        bankMachine.retrieveCoins(insertedCoins);
        insertedCoins.clear();
    }

    private void makeTradeIfPossible(ProductType product) {
        if (productMachine.isAllProductsSoldOut()) {
            ioMachine.displayMessage("NO PRODUCTS AVAILABLE");
            return;
        } else if (!productMachine.isProductAvailable(product)) {
            ioMachine.displayMessage("PRODUCT NOT AVAILABLE");
            return;
        }
        int insertedCoinsValue = bankMachine.calculateSumOfCoins(insertedCoins);
        int productValue = product.getValue();
        if (productValue > insertedCoinsValue) {
            if (bankMachine.isNotAbleToMakeAnyChange()) ioMachine.displayMessage("EXACT CHANGE ONLY");
            else ioMachine.displayMessage("INSERT COIN");
            ioMachine.displayMessage("PRODUCT PRICE " + productValue +
                    "\nINSERTED COINS " + insertedCoinsValue);
            return;
        } else if (ableToReturnChange(product, insertedCoinsValue, productValue)) {
            ioMachine.displayMessage("THANK YOU\nPURCHASED PRODUCT " + product.toString());
            insertedCoins = bankMachine.createChange(productMachine.getPriceOfProductInCoins(product), insertedCoins);
            productMachine.retrieveProduct(product);
            returnUserCoins();
        } else {
            ioMachine.displayMessage("TRANSACTION DISCARDED\nEXACT CHANGE ONLY");
        }
    }

    private boolean ableToReturnChange(ProductType product, int insertedCoinsValue, int productValue) {
        return (productValue == insertedCoinsValue ||
                bankMachine.isAbleToReturnTheChange(productMachine.getPriceOfProductInCoins(product), insertedCoins));
    }

    private void runAdminPanel() {
        boolean adminPanelIsRunning = true;
        while (adminPanelIsRunning) {
            String command = ioMachine.getNextLineString();
            switch (command) {
                case "1":
                    ioMachine.displayMessage(bankMachine.makeReport());
                    continue;
                case "2":
                    ioMachine.displayMessage(productMachine.makeReport());
                    continue;
                case "3":
                    adminPanelIsRunning = false;
                    continue;
                case "4":
                    adminPanelIsRunning = false;
                    vendingMachineIsRunning = false;
                    continue;
                case "%menu":
                    ioMachine.displayAdminMenu();
                    continue;
            }
            String item = "";
            int numberOfItemToChange = 0;
            if (command.startsWith("#") || command.startsWith("@")){
                if (command.length() > 2) item = command.substring(2);
                numberOfItemToChange = ioMachine.getNumberOfItems(item);
            } else {
                continue;
            }
            if (command.startsWith("#")) {
                Optional<CoinType> optionalCoinType = CoinType.get(item);
                if (optionalCoinType.isEmpty()) continue;
                if (command.charAt(1) == '+') {
                    addCoinsToBankMachine(optionalCoinType.get(), numberOfItemToChange);
                }
                else if (command.charAt(1) == '-') {
                    removeCoinsFromBankMachine(optionalCoinType.get(), numberOfItemToChange);
                }
            } else if (command.startsWith("@")) {
                Optional<ProductType> optionalProductType = ProductType.get(item);
                if (optionalProductType.isEmpty()) continue;
                if (command.charAt(1) == '+') {
                    addProductsToProductsMachine(optionalProductType.get(), numberOfItemToChange);
                }
                else if (command.charAt(1) == '-') {
                    removeProductsFromProductsMachine(optionalProductType.get(), numberOfItemToChange);
                }
            } else {
                ioMachine.displayMessage("UNKNOWN COMMAND " + command.toUpperCase());
            }

        }
    }

    private void addCoinsToBankMachine(CoinType coinType, int numberOfItemToChange) {
        for (int i = 0; i < numberOfItemToChange; i++) {
            bankMachine.insertCoin(coinType);
        }
    }

    private void removeCoinsFromBankMachine(CoinType coinType, int numberOfItemToChange) {
        List<CoinType> coinsToRetrieve = new ArrayList<>();
        for (int i = 0; i < numberOfItemToChange; i++) {
            coinsToRetrieve.add(coinType);
        }
        bankMachine.retrieveCoins(coinsToRetrieve);
    }

    private void addProductsToProductsMachine(ProductType productType, int numberOfItemToChange) {
        for (int i = 0; i < numberOfItemToChange; i++) {
            productMachine.insertProduct(productType);
        }
    }

    private void removeProductsFromProductsMachine(ProductType productType, int numberOfItemToChange) {
        for (int i = 0; i < numberOfItemToChange; i++) {
            productMachine.retrieveProduct(productType);
        }
    }

    public void run() {
        while (vendingMachineIsRunning) {
            String userInput = ioMachine.getNextLineString().toUpperCase();

            if (userInput.equals(adminPassword)) {
                runAdminPanel();
                continue;
            }

            if (userInput.toUpperCase().equals("%RETURN")) {
                returnUserCoins();
                continue;
            }
            String item = "";
            if (userInput.length() > 1) item = userInput.substring(1);
            else continue;
            if (userInput.startsWith("@")) {
                Optional<ProductType> optionalProduct = ProductType.get(item);
                if (optionalProduct.isPresent()) {
                    makeTradeIfPossible(optionalProduct.get());
                    continue;
                }
            } else if (userInput.startsWith("#")) {
                Optional<CoinType> optionalCoin = CoinType.get(item);
                if (optionalCoin.isPresent()) {
                    insertUserCoin(optionalCoin.get());
                    continue;
                }
            }
            ioMachine.displayMessage("UNKNOWN COMMAND " + userInput.toUpperCase());
            ioMachine.displayCustomerMenu();

        }
    }



}
