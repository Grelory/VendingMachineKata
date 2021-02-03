# VendingMachineKata

An application for operating a vending machine.

Assignment source: [vending-machine-kata](https://github.com/guyroyse/vending-machine-kata#make-change)

## Features: 

> (User must provide the following commands to operate the machine)

**User menu:**

  - accept coins
  ```
  #NICKEL
  #DIME
  #QUARTER
  ```
  - dispense products
  ```
  @COLA
  @CHIPS
  @CANDY
  ```
  - return money
  ```
  %RETURN
  ```
  
**Admin menu:**
  - access admin panel
  ```
  #1234
  ```
  - print available commands
  ```
  %menu
  ```
  - create coins report
  ```
  1
  ```
  - create products report
  ```
  2
  ```
  - quit admin panel
  ```
  3
  ```
  - disable the machine
  ```
  4
  ```
  - add coins
  ```
  #+[coin_name]
  [amount]
  ```
  - remove coins
  ```
  #+[coin_name]
  [amount]
  ```
  - add products 
  ```
  @+[product_name]
  [amount]
  ```
  - remove products
  ```
  @-[product_name]
  [amount]
  ```

## Messages:
```
INSERTED COIN 

RETURNED COINS

NO PRODUCTS AVAILABLE
COIN DISCARDED

NO PRODUCTS AVAILABLE

PRODUCT NOT AVAILABLE

EXACT CHANGE ONLY

TRANSACTION DISCARDED
EXACT CHANGE ONLY

THANK YOU
PURCHASED PRODUCT

UNKNOWN COMMAND 
```

## Launching:
Compilation:
```
mvn package
```
Start-up:
```
java -jar target/VendingMachine.jar
```
