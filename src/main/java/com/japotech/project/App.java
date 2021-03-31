package com.japotech.project;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;
import java.util.Scanner;
import java.util.Vector;

import com.japotech.project.core.Account;
import com.japotech.project.core.Transaction;
import com.japotech.project.core.UserInfo;
import com.japotech.project.core.ViewHelper;


public class App 
{
    final static private String DATA_FILE = "src/main/resources/accounting.dat";

    static int readInDataFile(UserInfo usi, Vector<Account> accounts, Scanner sc) {
        //char[] tempCb = new char[UserInfo.INFO_NICKNM.length()];
        //String leadin[] = {};
        String leadin = "";
        int transactionCount = 0;
        try {
            //sc.useDelimiter("\\s");
            while (sc.hasNext("[A-Z]*")) {
                leadin = sc.next();
                //System.out.println(br.readLine());
                //leadin = String.valueOf(tempCb);
                // leadin = br.readLine().split("\\s", 2);
                // String lineHeader = leadin[0];
                // String lineContent = leadin[1];
                if (leadin.isEmpty()) 
                    break;
                else if (leadin.equals(UserInfo.INFO_NICKNM)) {
                    UserInfo.readUserInfoData(usi, sc);
                }
                else if (leadin.equals(Account.BEGIN_ACCOUNT)) {
                    Account acc = new Account();
                    Account.readAccountData(acc, sc);
                    accounts.add(acc);
                }
                else if (leadin.equals(Transaction.BEGIN_TRANSACTION)) {
                    Transaction t = new Transaction();
                    Transaction.readTransactionData(t, sc);
                    ++transactionCount;
                    for (Account acc : accounts) {
                        String accn = t.getAccountName();
                        if (accn.equals(acc.getName()))
                            acc.addTransaction(t);
                    }
                }
                else
                    System.err.println("Unknown header field found: " + leadin);

                //Arrays.fill(leadin, "");
                leadin = "";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //sc.close();
        return transactionCount;
    }

    static void outputDataToFile(UserInfo usi, Vector<Account> accounts, BufferedWriter bw) throws IOException{
        UserInfo.writeUserInfoData(usi, bw);
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

        UserInfo usi = new UserInfo();
        boolean quit = false;
        Vector<Account> accounts = new Vector<Account>();
        
        InputStream is = new FileInputStream(DATA_FILE);
        // try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
        //     while (br.ready()) {
                 
        //         int transactions = readInDataFile(usi, accounts, br);
        //         System.out.println("Read in: " + transactions + " transactions\n");
        //     }
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
        try (Scanner sc = new Scanner(new InputStreamReader(is))) {
            while (sc.hasNext()) {
                 
                int transactions = readInDataFile(usi, accounts, sc);
                System.out.println("Read in: " + transactions + " transactions\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ViewHelper.printBanner();

        //Show the intro requesting to input 
        //the nickname only the first time you launch the app
        if (usi.getNickname().isEmpty() ) {
            usi.setNickname();
            ViewHelper.printIntro(usi);
        }

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
            outputDataToFile(usi, accounts, bw);
        } catch (Exception e) {
            e.printStackTrace();
        }   
    }
}
