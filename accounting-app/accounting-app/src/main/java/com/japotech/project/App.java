package com.japotech.project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;
import java.util.Vector;

import com.japotech.project.core.Account;
import com.japotech.project.core.Transaction;
import com.japotech.project.core.ViewHelper;


/**
 * Hello world!
 *
 */
public class App 
{
    final static private String DATA_FILE = "accounting-app\\src\\main\\resources\\accounting.dat";

    static int readInDataFile(Vector<Account> accounts, BufferedReader br) {
        
        String leadin;
        int transactionCount = 0;
        try {
            while (br.read() != -1) {
                leadin = br.readLine();
                if (leadin.isEmpty()) 
                    continue;
                else if (leadin.equals(Account.BEGIN_ACCOUNT)) {
                    Account acc = new Account();
                    Account.readAccountData(acc, br);
                    accounts.add(acc);
                }
                else if (leadin.equals(Transaction.BEGIN_TRANSACTION)) {
                    Transaction t = new Transaction();
                    Transaction.readTransactionData(t, br);
                    ++transactionCount;
                    for (Account acc : accounts) {
                        String accn = t.getAccountName();
                        if (accn.equals(acc.getName()))
                            acc.addTransaction(t);
                    }
                }
                else
                    System.err.println("Unknown header field found: " + leadin);

                leadin = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return transactionCount;
    }

    static void outputDataToFile(Vector<Account> accounts, BufferedWriter bw) throws IOException{
        for (Account acc : accounts) {
            Account.writeAccountData(acc, bw);
            for (Transaction t : acc.getTransactions()) {
                t.writeTransactionData(t, bw);
            }
        }
    }


    public static void main( String[] args ) throws Exception{
        Locale currLoc = new Locale.Builder().setLanguage("en").setRegion("IT").build();
        Locale.setDefault(currLoc);

        boolean quit = false;
        Vector<Account> accounts = new Vector<Account>();
        
        InputStream is = new FileInputStream(DATA_FILE);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            while (br.read() != -1) {
                int transactions = readInDataFile(accounts, br);
                System.out.println("Read in: " + transactions + " transactions\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        

        ViewHelper.printBanner();

        //Main loop of the program
        while (!quit) {
            String command = ViewHelper.prompt();

            if (command.isEmpty()) {
                continue;
            }
            else if (command.equals("quit")) {
                quit = true; 
                break;
            }
            else if (command.equals("ledger")) {
                for (Account acc : accounts) {
                    ViewHelper.printAccountLedger(acc);
                }
            }
            else if (command.equals("deposit")) {
                Transaction t = ViewHelper.readTransaction(false, accounts);
                ViewHelper.addToAccount(t, accounts);
                System.out.println("Deposit complete \n\n");
            }
            else if (command.equals("withdraw")) {
                Transaction t = ViewHelper.readTransaction(true, accounts);
                ViewHelper.addToAccount(t, accounts);
                System.out.println("Withdraw complete \n\n");
            }
            else if (command.equals("new")) {
                Account acc = ViewHelper.readAccount();
                accounts.add(acc);
                System.out.println("Account created \n\n");
            }
            else if (command.equals("delete")) {
                ViewHelper.deleteAccount(accounts);
                System.out.println("Account deleted \n\n");
            }
            else if (command.equals("help")) {
                ViewHelper.printHelp();
            }
            else
            System.err.println("Unknown command");
        }

        try {
            OutputStream os = new FileOutputStream(DATA_FILE);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
            outputDataToFile(accounts, bw);
        } catch (Exception e) {
            e.printStackTrace();
        }   
    }
}
