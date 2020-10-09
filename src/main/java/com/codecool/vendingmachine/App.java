package com.codecool.vendingmachine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class App {

    public static void main( String[] args ) throws FileNotFoundException {
        BankMachine bankMachine = new BankMachine();
        ProductMachine productMachine = new ProductMachine();
        Scanner scanner;
        if (args.length == 1) {
            String file = App.class.getClassLoader().getResource(args[0]).getFile();
            scanner = new Scanner(new File(file));
        } else {
            scanner = new Scanner(System.in);
        }
        IOMachine ioMachine = new IOMachine(scanner);
        VendingMachine vendingMachine = new VendingMachine(bankMachine, ioMachine, productMachine);
        vendingMachine.run();
    }

}
