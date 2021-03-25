package com.japotech.project.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public class Transaction {
    
    final static public String BEGIN_TRANSACTION = "BTRANS";

    private String accountName;
    private BigDecimal amount;
    private String memo;
    private ZonedDateTime transactionDateTime;

    //Constructors
    //"Default"
    public Transaction() {
        this.accountName = "";
        this.amount = BigDecimal.ZERO;
        this.memo = "";
        this.transactionDateTime = ZonedDateTime.now(ZoneId.of("Europe/Rome"));
    }

    //Overloaded
    public Transaction(String _accountName, BigDecimal _amount, String _memo) { 
        this.accountName = _accountName;
        this.amount = _amount;
        this.memo = _memo;
        this.transactionDateTime = ZonedDateTime.now(ZoneId.of("Europe/Rome"));
    }

    //Getters
    final public String getAccountName() {
        return accountName;
    }

    final public BigDecimal getAmount() {
        return amount;
    } 

    final public String getMemo() {
        return memo;
    }

    final public ZonedDateTime getTransactionDateTime() {
        return transactionDateTime;
    }

    //Local methods
    final static public void readTransactionData(Transaction t, BufferedReader br) {
        try {
            t.accountName = br.readLine();
            t.transactionDateTime = ZonedDateTime.parse(br.readLine(), DateTimeFormatter.ofPattern("E dd-MM-yyyy HH:mm:ss O"));
            t.amount = new BigDecimal(br.readLine()).setScale(2);
            t.memo = br.readLine().trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    final public void writeTransactionData(Transaction t, BufferedWriter bw) throws IOException{
        bw.write(BEGIN_TRANSACTION + " ");
        bw.write(t.accountName + " ");
        bw.write(t.transactionDateTime.format(DateTimeFormatter.ofPattern("E dd-MM-yyyy HH:mm:ss O")).toString() + " ");
        bw.write(String.valueOf(t.amount) + " ");
        bw.write(t.memo + "\n");

        bw.flush();
    }

}

