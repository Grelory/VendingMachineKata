package com.codecool.vendingmachine;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


class VendingMachineTest {


    private BankMachine bankMachine;
    private int amountOfNickels = 10;
    private int amountOfDimens = 5;
    private int amountOfQuarters = 8;
    private ProductMachine productMachine;
    private int amountOfCola = 10;
    private int amountOfChips = 5;
    private int amountOfCandy = 8;
    private IOMachine ioMachine;
    private VendingMachine vendingMachine;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void doBeforeEach() {
        bankMachine = new BankMachine();
        productMachine = new ProductMachine();
        ioMachine = new IOMachine(new Scanner(System.in));
        productMachine = new ProductMachine();
        vendingMachine = new VendingMachine(bankMachine, ioMachine, productMachine);
        System.setOut(new PrintStream(outContent));
    }

    // todo insertUserCoin
    @Test
    public void should_PrintNoProductAvailable_When_InsertCoinAndProductMachineIsEmpty()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = vendingMachine.getClass().getDeclaredMethod("insertUserCoin", CoinType.class);
        method.setAccessible(true);
        method.invoke(vendingMachine, CoinType.QUARTER);
        String pattern = "NO PRODUCTS AVAILABLE\nCOIN DISCARDED\n";
        assertEquals(pattern, outContent.toString());
    }

    @Test
    public void should_PrintExactChangeOnly_When_InsertCoinAndBankMachineIsEmpty()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        fillProductMachineWithProducts();
        Method method = vendingMachine.getClass().getDeclaredMethod("insertUserCoin", CoinType.class);
        method.setAccessible(true);
        method.invoke(vendingMachine, CoinType.QUARTER);
        String pattern = "EXACT CHANGE ONLY\n";
        assertTrue(outContent.toString().startsWith(pattern));
    }

    @Test
    public void should_PrintCoinsInsertedMessage_When_InsertCoin()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        fillBankMachineWithCoins();
        fillProductMachineWithProducts();
        Method method = vendingMachine.getClass().getDeclaredMethod("insertUserCoin", CoinType.class);
        method.setAccessible(true);
        method.invoke(vendingMachine, CoinType.QUARTER);
        String pattern = "INSERTED COIN " + CoinType.QUARTER.toString() + "\n";
        assertEquals(pattern, outContent.toString());
    }

    @Test
    public void should_AddCoinToBankMachine_WhenCoinIsInserted()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        fillProductMachineWithProducts();
        Method method = vendingMachine.getClass().getDeclaredMethod("insertUserCoin", CoinType.class);
        method.setAccessible(true);
        method.invoke(vendingMachine, CoinType.QUARTER);
        Field depositField = bankMachine.getClass().getDeclaredField("deposit");
        depositField.setAccessible(true);
        Map<CoinType, Integer> deposit = (Map<CoinType, Integer>) depositField.get(bankMachine);
        assertEquals(1, deposit.get(CoinType.QUARTER));
    }

    @Test
    public void should_AddCoinToUserCoinsList_WhenCoinIsInserted()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        fillProductMachineWithProducts();
        Class<VendingMachine> vendingMachineClass = (Class<VendingMachine>) vendingMachine.getClass();
        Method method = vendingMachineClass.getDeclaredMethod("insertUserCoin", CoinType.class);
        method.setAccessible(true);
        method.invoke(vendingMachine, CoinType.QUARTER);
        Field insertedCoinsField = vendingMachineClass.getDeclaredField("insertedCoins");
        insertedCoinsField.setAccessible(true);
        List<CoinType> insertedCoins = (List<CoinType>) insertedCoinsField.get(vendingMachine);
        assertEquals(1, insertedCoins.size());
    }

    //todo return user coins

    @Test
    public void should_PrintMessageNonCoinReturned_When_NoneCoinsInserted()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = vendingMachine.getClass().getDeclaredMethod("returnUserCoins");
        method.setAccessible(true);
        method.invoke(vendingMachine);
        String pattern = "RETURNED COINS NONE\n";
        assertEquals(pattern, outContent.toString());
    }

    @Test
    public void should_PrintReturnedCoins_When_InsertedCoins()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        fillProductMachineWithProducts();
        Class<VendingMachine> vendingMachineClass = (Class<VendingMachine>) vendingMachine.getClass();
        Method insertUserCoin = vendingMachineClass.getDeclaredMethod("insertUserCoin", CoinType.class);
        insertUserCoin.setAccessible(true);
        insertUserCoin.invoke(vendingMachine, CoinType.QUARTER);
        insertUserCoin.invoke(vendingMachine, CoinType.DIMES);
        insertUserCoin.invoke(vendingMachine, CoinType.NICKEL);
        outContent.reset();
        Method returnUserCoins = vendingMachineClass.getDeclaredMethod("returnUserCoins");
        returnUserCoins.setAccessible(true);
        returnUserCoins.invoke(vendingMachine);
        String pattern = "RETURNED COINS QUARTER DIMES NICKEL\n";
        assertEquals(pattern, outContent.toString());
    }

    @Test
    public void should_RetrieveCoinsFromDeposit_When_ReturnCoins() throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        fillProductMachineWithProducts();
        Class<VendingMachine> vendingMachineClass = (Class<VendingMachine>) vendingMachine.getClass();
        Method insertUserCoin = vendingMachineClass.getDeclaredMethod("insertUserCoin", CoinType.class);
        insertUserCoin.setAccessible(true);
        insertUserCoin.invoke(vendingMachine, CoinType.QUARTER);
        outContent.reset();
        Field depositField = bankMachine.getClass().getDeclaredField("deposit");
        depositField.setAccessible(true);
        Map<CoinType, Integer> deposit = (Map<CoinType, Integer>) depositField.get(bankMachine);
        int numberOfQuartersBeforeReturn = deposit.get(CoinType.QUARTER);
        Method returnUserCoins = vendingMachineClass.getDeclaredMethod("returnUserCoins");
        returnUserCoins.setAccessible(true);
        returnUserCoins.invoke(vendingMachine);
        assertEquals(numberOfQuartersBeforeReturn - 1, deposit.get(CoinType.QUARTER));
    }

    @Test
    public void should_ClearUserCoinsList_When_ReturnCoins() throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        fillProductMachineWithProducts();
        Class<VendingMachine> vendingMachineClass = (Class<VendingMachine>) vendingMachine.getClass();
        Method insertUserCoin = vendingMachineClass.getDeclaredMethod("insertUserCoin", CoinType.class);
        insertUserCoin.setAccessible(true);
        insertUserCoin.invoke(vendingMachine, CoinType.QUARTER);
        outContent.reset();
        Method returnUserCoins = vendingMachineClass.getDeclaredMethod("returnUserCoins");
        returnUserCoins.setAccessible(true);
        returnUserCoins.invoke(vendingMachine);
        Field insertedCoinsField = vendingMachineClass.getDeclaredField("insertedCoins");
        insertedCoinsField.setAccessible(true);
        List<CoinType> insertedCoins = (List<CoinType>) insertedCoinsField.get(vendingMachine);
        assertTrue(insertedCoins.isEmpty());
    }

    // todo make a trade if possible
    @Test
    public void should_PrintNoProductsAvailable_When_MakeTradeAndProductMachineIsEmpty()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<VendingMachine> vendingMachineClass = (Class<VendingMachine>) vendingMachine.getClass();
        Method method = vendingMachineClass.getDeclaredMethod("makeTradeIfPossible", ProductType.class);
        method.setAccessible(true);
        method.invoke(vendingMachine, ProductType.COLA);
        String pattern = "NO PRODUCTS AVAILABLE\n";
        assertEquals(pattern, outContent.toString());
    }

    @Test
    public void should_PrintProductNotAvailable_When_MakeTradeAndGivenProductIsNotAvailable()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        productMachine.insertProduct(ProductType.CHIPS);
        Class<VendingMachine> vendingMachineClass = (Class<VendingMachine>) vendingMachine.getClass();
        Method method = vendingMachineClass.getDeclaredMethod("makeTradeIfPossible", ProductType.class);
        method.setAccessible(true);
        method.invoke(vendingMachine, ProductType.COLA);
        String pattern = "PRODUCT NOT AVAILABLE\n";
        assertEquals(pattern, outContent.toString());
    }

    // todo divide this method into two because the output change due to deposit in bankMachine
    @Test
    public void should_MakeChangePrintExactExchangeOnly_When_NotEnoughInsertedCoinsAndBankMachineIsEmpty()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        fillProductMachineWithProducts();
        Class<VendingMachine> vendingMachineClass = (Class<VendingMachine>) vendingMachine.getClass();
        Method makeTrade = vendingMachineClass.getDeclaredMethod("makeTradeIfPossible", ProductType.class);
        makeTrade.setAccessible(true);
        makeTrade.invoke(vendingMachine, ProductType.COLA);
        String pattern = "EXACT CHANGE ONLY\n";
        assertTrue(outContent.toString().startsWith(pattern));
    }

    @Test
    public void should_MakeChangePrintInsertCoin_When_NotEnoughInsertedCoins()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        fillProductMachineWithProducts();
        fillBankMachineWithCoins();
        Class<VendingMachine> vendingMachineClass = (Class<VendingMachine>) vendingMachine.getClass();
        Method method = vendingMachineClass.getDeclaredMethod("makeTradeIfPossible", ProductType.class);
        method.setAccessible(true);
        method.invoke(vendingMachine, ProductType.COLA);
        String pattern = "INSERT COIN\n";
        assertTrue(outContent.toString().startsWith(pattern));
    }

    @Test
    public void should_MakeChangePrintMoneyInfo_When_NotEnoughInsertedCoins()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        fillProductMachineWithProducts();
        fillBankMachineWithCoins();
        Class<VendingMachine> vendingMachineClass = (Class<VendingMachine>) vendingMachine.getClass();
        Method method = vendingMachineClass.getDeclaredMethod("makeTradeIfPossible", ProductType.class);
        method.setAccessible(true);
        method.invoke(vendingMachine, ProductType.COLA);
        String pattern = "INSERT COIN\nPRODUCT PRICE " + ProductType.COLA.getValue() +
                "\nINSERTED COINS 0\n";
        assertEquals(pattern, outContent.toString());
    }

    @Test
    public void should_MakeChangeAndPrintProperMessage_When_ProductIsAvailableAndPriceEqualsInsertedCoins()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        fillProductMachineWithProducts();
        fillBankMachineWithCoins();
        Method insertUserCoin = vendingMachine.getClass().getDeclaredMethod("insertUserCoin", CoinType.class);
        insertUserCoin.setAccessible(true);
        for (CoinType coin : productMachine.getPriceOfProductInCoins(ProductType.COLA)) {
            insertUserCoin.invoke(vendingMachine, coin);
        }
        outContent.reset();
        Class<VendingMachine> vendingMachineClass = (Class<VendingMachine>) vendingMachine.getClass();
        Method method = vendingMachineClass.getDeclaredMethod("makeTradeIfPossible", ProductType.class);
        method.setAccessible(true);
        method.invoke(vendingMachine, ProductType.COLA);
        String pattern = "THANK YOU\nPURCHASED PRODUCT " + ProductType.COLA.toString() +
                "\nRETURNED COINS NONE\n";
        assertEquals(pattern, outContent.toString());
    }

    @Test
    public void should_MakeChangeAndReturnMoney_When_InsertedCoinsIsGreaterThanPriceCoins()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        fillProductMachineWithProducts();
        fillBankMachineWithCoins();
        Method insertUserCoin = vendingMachine.getClass().getDeclaredMethod("insertUserCoin", CoinType.class);
        insertUserCoin.setAccessible(true);
        for (CoinType coin : productMachine.getPriceOfProductInCoins(ProductType.COLA)) {
            insertUserCoin.invoke(vendingMachine, coin);
            insertUserCoin.invoke(vendingMachine, coin);
        }
        outContent.reset();
        Class<VendingMachine> vendingMachineClass = (Class<VendingMachine>) vendingMachine.getClass();
        Method method = vendingMachineClass.getDeclaredMethod("makeTradeIfPossible", ProductType.class);
        method.setAccessible(true);
        method.invoke(vendingMachine, ProductType.COLA);
        String pattern = "THANK YOU\nPURCHASED PRODUCT " + ProductType.COLA.toString() +
                "\nRETURNED COINS QUARTER QUARTER QUARTER QUARTER\n"; //todo
        assertEquals(pattern, outContent.toString());
    }

    @Test
    public void should_RemoveProductFromProductMachine_When_TransactionIsMade() throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        fillProductMachineWithProducts();
        fillBankMachineWithCoins();
        Method insertUserCoin = vendingMachine.getClass().getDeclaredMethod("insertUserCoin", CoinType.class);
        insertUserCoin.setAccessible(true);
        for (CoinType coin : productMachine.getPriceOfProductInCoins(ProductType.COLA)) {
            insertUserCoin.invoke(vendingMachine, coin);
        }
        outContent.reset();
        Class<VendingMachine> vendingMachineClass = (Class<VendingMachine>) vendingMachine.getClass();
        Method method = vendingMachineClass.getDeclaredMethod("makeTradeIfPossible", ProductType.class);
        method.setAccessible(true);
        method.invoke(vendingMachine, ProductType.COLA);

        Field productsField = productMachine.getClass().getDeclaredField("products");
        productsField.setAccessible(true);
        Map<ProductMachine, Integer> products = (Map<ProductMachine, Integer>) productsField.get(productMachine);
        assertEquals(amountOfCola - 1, products.get(ProductType.COLA));
    }

    @Test
    public void should_PrintExactChangeOnly_When_UnableToMakeChange() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        fillProductMachineWithProducts();
        Method insertUserCoin = vendingMachine.getClass().getDeclaredMethod("insertUserCoin", CoinType.class);
        insertUserCoin.setAccessible(true);
        for (int i = 0; i < 3; i++) {
            insertUserCoin.invoke(vendingMachine, CoinType.QUARTER);
        }
        outContent.reset();
        Class<VendingMachine> vendingMachineClass = (Class<VendingMachine>) vendingMachine.getClass();
        Method method = vendingMachineClass.getDeclaredMethod("makeTradeIfPossible", ProductType.class);
        method.setAccessible(true);
        method.invoke(vendingMachine, ProductType.CANDY);
        String pattern = "TRANSACTION DISCARDED\nEXACT CHANGE ONLY\n";
        assertEquals(pattern, outContent.toString());
    }

    @Test
    public void should_InvokeProperAdminMethods_When_RunCommandsFromAdminFileTest()
            throws FileNotFoundException {
        String runTestFile = getClass().getClassLoader().getResource("adminTestFile.txt").getFile();
        ioMachine = new IOMachine(new Scanner(new File(runTestFile)));
        ProductMachine mockProductMachine = mock(ProductMachine.class);
        BankMachine mockBankMachine = mock(BankMachine.class);
        vendingMachine = new VendingMachine(mockBankMachine, ioMachine, mockProductMachine);
        vendingMachine.run();
        verify(mockBankMachine, times(3)).insertCoin(any(CoinType.class));
        verify(mockBankMachine, times(3)).retrieveCoins(any(List.class));
        verify(mockProductMachine, times(3)).insertProduct(any(ProductType.class));
        verify(mockProductMachine, times(3)).retrieveProduct(any(ProductType.class));
        verify(mockBankMachine).makeReport();
        verify(mockProductMachine).makeReport();

    }

    @Test
    public void should_InvokeProperCustomerMethods_When_RunCommandsFromCustomerFileTest()
            throws FileNotFoundException {
        String runTestFile = getClass().getClassLoader().getResource("customerTestFile.txt").getFile();
        ioMachine = new IOMachine(new Scanner(new File(runTestFile)));
        ProductMachine mockProductMachine = mock(ProductMachine.class);
        when(mockProductMachine.isProductAvailable(ProductType.COLA)).thenReturn(true);
        BankMachine mockBankMachine = mock(BankMachine.class);
        when(mockBankMachine.calculateSumOfCoins(any(List.class))).thenReturn(100);
        vendingMachine = new VendingMachine(mockBankMachine, ioMachine, mockProductMachine);
        vendingMachine.run();
        verify(mockBankMachine, times(22)).insertCoin(CoinType.NICKEL);
        verify(mockBankMachine, atLeastOnce()).retrieveCoins(any(List.class));
        verify(mockProductMachine).insertProduct(any(ProductType.class));
        verify(mockProductMachine).retrieveProduct(any(ProductType.class));
    }

    private void fillBankMachineWithCoins() {
        List<CoinType> initialCoinsList = getInitialCoinsList();
        initialCoinsList.forEach(bankMachine::insertCoin);
    }

    private void fillProductMachineWithProducts() {
        List<ProductType> initialProductsList = getInitialProductsList();
        initialProductsList.forEach(productMachine::insertProduct);
    }

    private  List<ProductType> getInitialProductsList() {
        List<ProductType> initialProducts = new ArrayList<>();
        for (int i = 0; i < amountOfCola; i++) {
            initialProducts.add(ProductType.COLA);
            if (i < amountOfChips) initialProducts.add(ProductType.CHIPS);
            if (i < amountOfCandy) initialProducts.add(ProductType.CANDY);
        }
        return initialProducts;
    }

    private  List<CoinType> getInitialCoinsList() {
        List<CoinType> initialCoins = new ArrayList<>();
        for (int i = 0; i < amountOfNickels; i++) {
            initialCoins.add(CoinType.NICKEL);
            if (i < amountOfDimens) initialCoins.add(CoinType.DIMES);
            if (i < amountOfQuarters) initialCoins.add(CoinType.QUARTER);
        }
        return initialCoins;
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

}