package com.dy.websocket.websocketcluster;

import java.util.Arrays;

public class PasswordCracker {
    private static final String CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int MAX_LENGTH = 4;

    public static void main(String[] args) {
        char[] password = new char[MAX_LENGTH];
        crackPassword(password, 0);
    }

    private static void crackPassword(char[] password, int position) {
        if (position == password.length) {
            System.out.println("Found password: " + Arrays.toString(password));
            System.exit(1);
            return;
        }

        for (char c : CHARACTERS.toCharArray()) {
            password[position] = c;
            crackPassword(password, position + 1);
        }
    }
}