package com.codecool.vendingmachine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BankMachineTest {

    private BankMachine bankMachine;
    private int amountOfNickels = 10;
    private int amountOfDimens = 5;
    private int amountOfQuarters = 8;

    @BeforeEach
    public void doBeforeEach() {
        bankMachine = new BankMachine();
        List<CoinType> initialCoinsList = getInitialCoinsList();
        initialCoinsList.forEach(bankMachine::insertCoin);
    }

    @Test
    public void should_SaveCoinsIntoMap_When_InsertCoins() throws NoSuchFieldException, IllegalAccessException {
        Field depositField = bankMachine.getClass().getDeclaredField("deposit");
        depositField.setAccessible(true);
        Map<CoinType, Integer> deposit = (Map<CoinType, Integer>) depositField.get(bankMachine);
        assertAll(() -> {
            assertEquals(amountOfNickels, deposit.get(CoinType.NICKEL));
            assertEquals(amountOfDimens, deposit.get(CoinType.DIMES));
            assertEquals(amountOfQuarters, deposit.get(CoinType.QUARTER));
        });
    }

    @Test
    public void should_ReturnProperChangeDueToGivenPrice_When_CreateChangeMethodIsInvoked() {
        List<CoinType> price = createOneDollarCoinsList();
        List<CoinType> insertedCoins = createOneDollarCoinsList();

        insertedCoins.add(CoinType.NICKEL);
        List<CoinType> expectedCoin = List.of(CoinType.NICKEL);
        assertEquals(expectedCoin, bankMachine.createChange(price, insertedCoins));
        insertedCoins.add(CoinType.NICKEL);
        expectedCoin = List.of(CoinType.DIMES);
        assertEquals(expectedCoin, bankMachine.createChange(price, insertedCoins));
        insertedCoins.add(CoinType.NICKEL);
        insertedCoins.add(CoinType.DIMES);
        expectedCoin = List.of(CoinType.QUARTER);
        assertEquals(expectedCoin, bankMachine.createChange(price, insertedCoins));
    }

    @Test
    public void should_EraseCoinsFromMap_When_RetrieveCoins() throws IllegalAccessException, NoSuchFieldException {
        List<CoinType> coinsToRemove = new ArrayList<>();
        int numberOfCoinsToRemove = 3;
        for (int i = 0; i < numberOfCoinsToRemove; i++) {
            coinsToRemove.add(CoinType.QUARTER);
            coinsToRemove.add(CoinType.DIMES);
            coinsToRemove.add(CoinType.NICKEL);
        }
        bankMachine.retrieveCoins(coinsToRemove);
        Class<BankMachine> bankMachineClass = (Class<BankMachine>) bankMachine.getClass();
        Field depositField = bankMachineClass.getDeclaredField("deposit");
        depositField.setAccessible(true);
        Map<CoinType, Integer> deposit = (Map<CoinType, Integer>) depositField.get(bankMachine);
        assertAll(() -> {
            assertEquals(amountOfQuarters - numberOfCoinsToRemove,
                    deposit.get(CoinType.QUARTER));
            assertEquals(amountOfDimens - numberOfCoinsToRemove,
                    deposit.get(CoinType.DIMES));
            assertEquals(amountOfNickels - numberOfCoinsToRemove,
                    deposit.get(CoinType.NICKEL));
        });
    }

    @Test
    public void should_IsNotAbleToMakeAnyChangeReturnTrue_When_DepositIsEmpty()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<CoinType> initialCoinsList = getInitialCoinsList();
        bankMachine.retrieveCoins(initialCoinsList);
        Class<BankMachine> bankMachineClass = (Class<BankMachine>) bankMachine.getClass();
        Method isNotAbleToMakeAnyChange = bankMachineClass.getDeclaredMethod("isNotAbleToMakeAnyChange");
        assertTrue((Boolean) isNotAbleToMakeAnyChange.invoke(bankMachine));
    }

    @Test
    public void should_ReturnTrue_When_IsAbleToMakeChange() {
        List<CoinType> oneDollar = createOneDollarCoinsList();
        List<CoinType> oneDollarAndRemainder = createOneDollarCoinsList();
        assertAll(() -> {
            for (int i = 0; i < 4; i++) {
                oneDollarAndRemainder.add(CoinType.NICKEL);
                assertTrue(bankMachine.isAbleToReturnTheChange(oneDollar, oneDollarAndRemainder));
            }
        });
    }

    @Test
    public void should_ReturnChangeInList_When_CreateChangeOrEmptyListMethodIsInvoked()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        int value = 40;
        List<CoinType> expectedChange = new ArrayList<>();
        expectedChange.add(CoinType.QUARTER);
        expectedChange.add(CoinType.DIMES);
        expectedChange.add(CoinType.NICKEL);
        Method method = bankMachine.getClass().getDeclaredMethod("createChangeOrReturnEmptyList", int.class);
        method.setAccessible(true);
        assertEquals(expectedChange, method.invoke(bankMachine, value));
    }

    @Test
    public void should_ReturnEmptyList_When_CreateChangeIsNotPossible() throws NoSuchFieldException,
            IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class<BankMachine> bankMachineClass = (Class<BankMachine>) bankMachine.getClass();
        Field depositField = bankMachineClass.getDeclaredField("deposit");
        depositField.setAccessible(true);
        Map<CoinType, Integer> deposit = (Map<CoinType, Integer>) depositField.get(bankMachine);
        int value = 5;
        value += deposit.keySet().stream().mapToInt(coinType -> deposit.get(coinType) * coinType.getValue()).sum();
        System.out.println(value);
        Method method = bankMachineClass.getDeclaredMethod("createChangeOrReturnEmptyList", int.class);
        method.setAccessible(true);
        List<CoinType> emptyList = Collections.emptyList();
        assertEquals(emptyList, (List<CoinType>) method.invoke(bankMachine, value));
    }

    @Test
    public void should_ReturnAppropriateValue_When_CalculatesSumOfCoinsValues()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<CoinType> oneDollar = createOneDollarCoinsList();
        Method method = bankMachine.getClass().getDeclaredMethod("getSumOfCoins", List.class);
        method.setAccessible(true);
        assertEquals(100, method.invoke(bankMachine, oneDollar));
    }

    @Test
    public void should_ReturnAppropriateNumberOfCoins_When_MakeReport() {

        String pattern = "Remaining coins:\n" +
                "\tQuarters: " + amountOfQuarters + "\n" +
                "\tDimens: " + amountOfDimens + "\n" +
                "\tNickels: " + amountOfNickels;

        assertEquals(pattern, bankMachine.makeReport());

    }

    private List<CoinType> createOneDollarCoinsList() {
        List<CoinType> oneDollar = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            oneDollar.add(CoinType.QUARTER);
        }
        return oneDollar;
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


}