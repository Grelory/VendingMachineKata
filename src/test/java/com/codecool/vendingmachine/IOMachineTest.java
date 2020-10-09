package com.codecool.vendingmachine;

import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class IOMachineTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private IOMachine ioMachine;

    @BeforeEach
    public void setUpStreams() throws FileNotFoundException {
        String scannerTestFile = getClass().getClassLoader().getResource("getNextLineTestFile.txt").getFile();
        ioMachine = new IOMachine(new Scanner(new File(scannerTestFile)));
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void should_PrintCustomerMenu_When_DisplayCustomerMenu() {
        String pattern = "@[product_name]  >>> buy product (cola, chips, candy)\n" +
                "#[coins] >>> insert coin (nickel, dime, quarter)\n" +
                "%return >>> retrieve inserted coins\n";
        ioMachine.displayCustomerMenu();
        assertEquals(pattern, outContent.toString());
    }

    @Test
    public void should_PrintAdminMenu_When_DisplayCustomerMenu() {
        String pattern = "Admin menu: \n" +
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
                "@-CANDY >>> Retrieve candy\n";
        ioMachine.displayAdminMenu();
        assertEquals(pattern, outContent.toString());
    }

    @Test
    public void should_PrintOutput_When_DisplayMessage() {
        String message = "hello you!";
        ioMachine.displayMessage(message);
        assertEquals(message + "\n", outContent.toString());
    }

    @Test
    public void should_PrintInsertedCoin_When_DisplayInsertedCoin() {
        String coin = "NICKEL";
        ioMachine.displayInsertedCoin(coin);
        assertEquals("Coin inserted: " + coin + "\n", outContent.toString());
    }

    @Test
    public void should_PrintRemovedCoin_When_DisplayReturnedCoin() {
        String coin = "NICKEL";
        ioMachine.displayReturnedCoin(coin);
        assertEquals("Coin returned: " + coin + "\n", outContent.toString());
    }


    @Test
    public void should_ReturnProvidedString_When_GetNextLineString() {
        String nextLineString = ioMachine.getNextLineString();
        assertNotNull(nextLineString);
        assertEquals("NextLine", nextLineString);
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

}