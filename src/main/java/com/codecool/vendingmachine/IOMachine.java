package com.codecool.vendingmachine;

import java.util.Scanner;

public class IOMachine {

    private Scanner scanner;

    public IOMachine(Scanner scanner) {
        this.scanner = scanner;
    }

    public void displayAdminMenu() {
        System.out.println("Admin menu: \n" +
                "1. Display coins report\n" +
                "2. Display products report\n" +
                "3. Quit admin panel\n" +
                "4. Turn vending machine off\n" +
                "#+QUARTER >>> Add quarters\n" +
                "#+DIME >>> Add dimes\n" +
                "#+NICKEL >>> Add nickels\n" +
                "#-QUARTER >>> Retrieve quarters\n" +
                "#-DIME >>> Retrieve dimes\n" +
                "#-NICKEL >>> Retrieve nickels\n" +
                "@+COLA >>> Add cola\n" +
                "@+CHIPS >>> Add chips\n" +
                "@+CANDY >>> Add candy\n" +
                "@-COLA >>> Retrieve cola\n" +
                "@-CHIPS >>> Retrieve chips\n" +
                "@-CANDY >>> Retrieve candy");
    }

    public void displayCustomerMenu() {
        System.out.println("@[product_name]  >>> buy product (cola, chips, candy)\n" +
                "#[coins] >>> insert coin (nickel, dime, quarter)\n" +
                "%return >>> retrieve inserted coins");
    }

    public void displayInsertedCoin(String coinName) {
        System.out.println("Coin inserted: " + coinName);
    }

    public void displayReturnedCoin(String coinName) {
        System.out.println("Coin returned: " + coinName);
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }

    public String getNextLineString() {
        if (scanner.hasNextLine()) return scanner.nextLine();
        return "";
    }

    public void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public int getNumberOfItems(String item) {
        // todo test
        displayMessage("Enter number of " + item);
        int numberOfItems = -1;
        while (numberOfItems < 0) {
            try {
                int number = Integer.parseInt(getNextLineString());
                if (number >= 0) numberOfItems = number;
            } catch (NumberFormatException ignored) {}
        }
        return numberOfItems;
    }
}
