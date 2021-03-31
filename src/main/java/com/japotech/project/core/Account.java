package com.japotech.project.core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Scanner;
import java.util.Vector;


public class Account {
    
    public final static String BEGIN_ACCOUNT = "BACCNT";

    private String name;
    private Vector<Transaction> transactions;

    //Constructors
    //"Default"
    public Account() {
        this.name = "";
        this.transactions = new Vector<Transaction>();
    }

    //Override
    public Account(String _name) {
        this.name = _name;
        this.transactions = new Vector<Transaction>();
    }

    //Getters
    final public String getName() {
        return name;
    }

    final public Vector<Transaction> getTransactions() {
        return transactions;
    }

    final public BigDecimal getBalance() {

        BigDecimal sum = BigDecimal.ZERO;
        sum = transactions.stream().reduce(
            sum, (a, b) -> a.add(b.getAmount()), BigDecimal::add);
        
        return sum;
    }

    // Local Methods
    final public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    final static public void readAccountData(Account acc, String content) {
        acc.name = content.trim();
    }

    
    final static public void readAccountData(Account acc, Scanner sc) throws IOException {
        sc.useDelimiter("\\s");
        acc.name = sc.nextLine();
    }

    final static public void writeAccountData(Account acc, BufferedWriter bw) throws IOException {

        bw.write(BEGIN_ACCOUNT + " ");
        bw.write(acc.name +  "\n");

        bw.flush();
    }

}
