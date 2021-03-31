package com.japotech.project.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ChoiceFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Locale;
import java.util.Scanner;
import java.util.Vector;

import com.google.common.io.LineReader;
import org.apache.commons.lang3.StringUtils;


public class ViewHelper {
    
    private static Scanner scMain = new Scanner(System.in);
    
    final public static void printBanner() throws Exception{
    File banner = new File("src/main/resources/banner.txt");
    Scanner scBanner = new Scanner(banner);

    while (scBanner.hasNextLine()) {
        System.out.println(StringUtils.center(scBanner.nextLine(), 80));
    }

    scBanner.close();

    System.out.print("\n\n\n\n\n");
    }

    final public static void printIntro(UserInfo usi) throws Exception {
        System.out.println(
            "Welcome " + usi.getNickname() + ", this is Money Mng\n" + 
            "It will help you tho manage your expenses\n" + 
            "You can add you accounts and make deposits and withdrawals\n" + 
            "If you want to see all the available functions input the help command\n" );
    }

    final public static String prompt() throws IOException {

        String c = null;
        
        System.out.print("input command> ");
        
        if (scMain != null) {
            c = scMain.nextLine().trim();
        }

        return c;
    }

    final public static Transaction readTransaction(boolean withdraw, Vector<Account> accounts) throws IOException {
        String account = "";
        BigDecimal amount = BigDecimal.ZERO;
        String memo = "";

        LineReader lr = new LineReader(new InputStreamReader(System.in));
        System.out.print("Account name: ");
        account = lr.readLine();
        for (Account acc : accounts) {
            if (account.equals(acc.getName())) {
                System.out.print("Amount: ");
                amount = new BigDecimal(lr.readLine()).setScale(2);
                System.out.print("Memo: ");
                memo = lr.readLine();
            }
            else {
                System.err.println("There is no account with this name");
            }
        }

        if (withdraw && amount.compareTo(BigDecimal.ZERO) > 0) {
            amount = amount.negate();
        }

        Transaction t = new Transaction(account, amount, memo);

        return t;
    }

    final public static Account readAccount() throws IOException{
        String accountName = "";
        System.out.print("New account name: ");
    
        accountName = scMain.nextLine();

        Account a = new Account(accountName);
        return a;

        //     Account a = new Account(accountName);
        // try (BufferedReader br = 
        //                 new BufferedReader(
        //                     new InputStreamReader(System.in))) {
        //     System.out.print("New account name: ");
        //     String accountName = br.readLine();
        //     Account a = new Account(accountName);

        //     return a;
        // } 
    }

    final public static void deleteAccount(Vector<Account> accounts) throws IOException {
        String accountName;
        LineReader lr = new LineReader(new InputStreamReader(System.in));
        System.out.print("Accounts available: ");
        double[] filelimits = {1,2};
        String[] filepart = {"{1} transaction", "{2} transactions"};
        ChoiceFormat fileform = new ChoiceFormat(filelimits, filepart);
        Format[] testFormats = {fileform, NumberFormat.getInstance()};
        MessageFormat pattform = new MessageFormat("{0}");
        pattform.setFormats(testFormats);
        Object[] testArgs = {null, null};
        for (Account acc : accounts){
            testArgs[0] = acc.getTransactions().size();
            testArgs[1] = testArgs[0];
            System.out.println(acc.getName() + ": " + pattform.format(testArgs));
        }

        System.out.print("Account name: ");
        accountName = lr.readLine(); 

        // Delete the account from the vector
        Iterator<Account> accIt = accounts.iterator();
        while (accIt.hasNext() ){
            if (accIt.next().getName().equals(accountName)) {
                accIt.remove();
            }
        }
    }

    final public static void printHelp() {
        String line = StringUtils.repeat('-', 80);
        System.out.println(StringUtils.center("Commands", 80));
        System.out.println(line);
        System.out.println(StringUtils.leftPad("help", 20) + StringUtils.leftPad("Print Help Message", 50));
        System.out.println(StringUtils.leftPad("new", 20) + StringUtils.leftPad("Create a new account", 50));
        System.out.println(StringUtils.leftPad("deposit", 20) + StringUtils.leftPad("Create a new deposit", 50));
        System.out.println(StringUtils.leftPad("withdraw", 20) + StringUtils.leftPad("Create a new withdrawal", 50));
        System.out.println(StringUtils.leftPad("ledger", 20) + StringUtils.leftPad("Print out account ledger", 50));
        System.out.println(StringUtils.leftPad("delete", 20) + StringUtils.leftPad("Delete an account and all transactions", 50));
        System.out.println(StringUtils.leftPad("quit", 20) + StringUtils.leftPad("Quit the program and write the data file", 50));
        System.out.print("\n\n\n");
    }

    final public static void addToAccount(Transaction t, Vector<Account> accounts) {
       for (Account acc : accounts) {
           String accn = t.getAccountName();
           if (accn.equals(acc.getName()))
            acc.addTransaction(t);
       }
    }

    final public static void printAccountLedger(Account acc) {
        String line = StringUtils.repeat('-', 80);
        StringBuffer sb = new StringBuffer();
        sb.append("Ledger for " + acc.getName());
        System.out.print("\n\n");
        System.out.println(StringUtils.center(sb.toString(), 80));
        System.out.println(line);
        for (Transaction t : acc.getTransactions()) {
            ZonedDateTime time = t.getTransactionDateTime();
            DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("E dd-MM-yyyy HH:mm:ss O");
            System.out.println(StringUtils.leftPad(dtFormat.format(time), 20));
            System.out.println(StringUtils.rightPad(t.getMemo(), 40));
            NumberFormat currFormat = NumberFormat.getCurrencyInstance(Locale.ITALY);
            System.out.println(StringUtils.rightPad(currFormat.format(t.getAmount()), 40));
            System.out.print('\n');
        }

        sb.delete(0, sb.length());
        NumberFormat currFormat = NumberFormat.getCurrencyInstance(Locale.ITALY);
        sb.append("Total: " + currFormat.format(acc.getBalance()));
        String dashes = StringUtils.repeat('-', sb.toString().length());
        System.out.println(StringUtils.leftPad(dashes, 80));
        System.out.println(StringUtils.leftPad(sb.toString(), 80));

    }
}
