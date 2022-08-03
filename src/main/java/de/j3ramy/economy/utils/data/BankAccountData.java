package de.j3ramy.economy.utils.data;

import java.time.LocalDate;
import java.util.Random;

public class BankAccountData {
    public static final int ACCOUNT_NUMBER_LENGTH = 9;
    public static final int PIN_LENGTH = 4;
    public static final int VALIDITY_IN_MONTHS = 6;

    private String owner = "";
    private String accountNumber = "";
    private String validity = "";
    private String pin = "";
    private int balance = 0;
    private boolean isLocked;
    private String dateOfBirth = "";

    public BankAccountData(){}

    public BankAccountData(String owner){
        this.owner = owner;
        this.accountNumber = this.generateAccountNumber();
        this.validity = this.generateValidityDate();
        this.pin = this.generatePin();
    }

    public BankAccountData(String owner, String accountNumber, String validity, String pin){
        this.owner = owner;
        this.accountNumber = accountNumber;
        this.validity = validity;
        this.pin = pin;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getOwner() {
        return this.owner;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public String getValidity() {
        return this.validity;
    }

    public String getPin() {
        return this.pin;
    }

    public boolean isSet(){
        return !this.owner.isEmpty() && !this.accountNumber.isEmpty() && !this.validity.isEmpty();
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public int getBalance() {
        return this.balance;
    }

    public String getDateOfBirth() {
        return this.dateOfBirth;
    }

    public boolean isLocked() {
        return this.isLocked;
    }

    private String generateAccountNumber(){
        StringBuilder accNumber = new StringBuilder();
        Random random = new Random();

        for(int i = 0; i < ACCOUNT_NUMBER_LENGTH; i++){
            accNumber.append(random.nextInt(10));
        }

        return accNumber.toString();
    }

    private String generateValidityDate(){
        LocalDate currentDate = LocalDate.now();
        LocalDate finalDate = currentDate.plusMonths(VALIDITY_IN_MONTHS);

        return finalDate.getMonth().getValue() + "/" + finalDate.getYear();
    }

    private String generatePin(){
        StringBuilder pin = new StringBuilder();
        Random random = new Random();

        for(int i = 0; i < PIN_LENGTH; i++){
            pin.append(random.nextInt(10));
        }

        return pin.toString();
    }
}
