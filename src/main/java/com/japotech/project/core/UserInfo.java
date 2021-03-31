package com.japotech.project.core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import com.google.common.io.LineReader;

public class UserInfo {
    
    public final static String INFO_NICKNM = "NICKNM";

    private String nickname = "";

    public UserInfo() {
        this.nickname = "";
    }
    public UserInfo(String _nickname) {
        this.nickname = _nickname;
    }
    
    final public void setNickname() throws IOException{
        LineReader lr = new LineReader(new InputStreamReader(System.in));
        System.out.print("I see you are a new user :) \n" + 
            "Please, enter a nickname of your choice: ");
        nickname = lr.readLine();
        System.out.print("\n");
    }

    final public String getNickname() {
        return nickname;
    }

    public final static void readUserInfoData(UserInfo usi, String content) {
            usi.nickname = content.trim();
    }

    public final static void readUserInfoData(UserInfo usi, Scanner sc) {
        usi.nickname = sc.nextLine().trim();
    }   

    public final static void writeUserInfoData(UserInfo usi, BufferedWriter bw) throws IOException {
        bw.write(INFO_NICKNM + " ");
        bw.write(usi.nickname + "\n");

        bw.flush();
        
    }

    

}
