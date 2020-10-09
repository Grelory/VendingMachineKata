package com.codecool.vendingmachine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProductMachineTest {

    private final BankMachine bankMachine = new BankMachine();
    private ProductMachine productMachine;
    private int amountOfCola = 10;
    private int amountOfChips = 5;
    private int amountOfCandy = 8;

    @BeforeEach
    public void doBeforeEach() {
        productMachine = new ProductMachine();
        List<ProductType> initialCoinsList = getInitialProductsList();
        initialCoinsList.forEach(productMachine::insertProduct);
    }

    @Test
    public void should_SaveProductsIntoMap_When_InsertProducts() throws NoSuchFieldException, IllegalAccessException {
        Field productsField = productMachine.getClass().getDeclaredField("products");
        productsField.setAccessible(true);
        Map<ProductType, Integer> products = (Map<ProductType, Integer>) productsField.get(productMachine);
        assertAll(() -> {
            assertEquals(amountOfCola, products.get(ProductType.COLA));
            assertEquals(amountOfChips, products.get(ProductType.CHIPS));
            assertEquals(amountOfCandy, products.get(ProductType.CANDY));
        });
    }

    @Test
    public void should_EraseProductFromMap_When_RetrieveProduct() throws IllegalAccessException, NoSuchFieldException {
        List<ProductType> productsToRemove = new ArrayList<>();
        int numberOfProductsToRemove = 3;
        for (int i = 0; i < numberOfProductsToRemove; i++) {
            productsToRemove.add(ProductType.COLA);
            productsToRemove.add(ProductType.CHIPS);
            productsToRemove.add(ProductType.CANDY);
        }
        productsToRemove.forEach(pr -> productMachine.retrieveProduct(pr));
        Class<ProductMachine> bankMachineClass = (Class<ProductMachine>) productMachine.getClass();
        Field productField = bankMachineClass.getDeclaredField("products");
        productField.setAccessible(true);
        Map<ProductType, Integer> product = (Map<ProductType, Integer>) productField.get(productMachine);
        assertAll(() -> {
            assertEquals(amountOfCola - numberOfProductsToRemove,
                    product.get(ProductType.COLA));
            assertEquals(amountOfChips - numberOfProductsToRemove,
                    product.get(ProductType.CHIPS));
            assertEquals(amountOfCandy - numberOfProductsToRemove,
                    product.get(ProductType.CANDY));
        });
    }

    @Test
    public void should_IsProductAvailableReturnTrue_When_IsInProducts() {
        assertTrue(productMachine.isProductAvailable(ProductType.COLA));
    }

    @Test
    public void should_IsProductAvailableReturnFalse_When_IsNotInProducts() {
        getInitialProductsList().stream().filter(p -> p == ProductType.COLA).forEach(productMachine::retrieveProduct);
        assertFalse(productMachine.isProductAvailable(ProductType.COLA));
    }

    @Test
    public void should_IsAllProductsSoldOutReturnFalse_When_OneProductIsInProducts() {
        getInitialProductsList().forEach(productMachine::retrieveProduct);
        productMachine.insertProduct(ProductType.COLA);
        assertFalse(productMachine.isAllProductsSoldOut());
    }

    @Test
    public void should_IsAllProductsSoldOutReturnTrue_When_ProductsIsEmpty() {
        getInitialProductsList().forEach(productMachine::retrieveProduct);
        assertTrue(productMachine.isAllProductsSoldOut());
    }

    @Test
    public void should_ReturnAppropriateNumberOfCoins_When_MakeReport() {

        String pattern = "Remaining products:\n" +
                "\tCola: " + amountOfCola + "\n" +
                "\tChips: " + amountOfChips + "\n" +
                "\tCandy: " + amountOfCandy;

        assertEquals(pattern, productMachine.makeReport());
    }

    @Test
    public void should_ReturnProperCoinList_When_CalculatesPriceInCoinsForGivenProduce() {
        List<CoinType> priceOfProductInCoins = productMachine.getPriceOfProductInCoins(ProductType.COLA);
        assertEquals(ProductType.COLA.getValue(), bankMachine.calculateSumOfCoins(priceOfProductInCoins));
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
}