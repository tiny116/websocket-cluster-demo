package com.dy.websocket.websocketcluster.chatgpt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SimpleRainbowTable {
    private static final int PASSWORD_MIN_LENGTH = 1;
    private static final int PASSWORD_MAX_LENGTH = 3;
    private static final int CHAIN_LENGTH = 5000;
    private static final int TABLE_SIZE = 5000;
    private static final String CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static void main(String[] args) {
        Map<String, String> rainbowTable = generateRainbowTable();
        String targetMD5 = "202cb962ac59075b964b07152d234b70"; // The MD5 hash for "123"
        String foundPassword = lookupPasswordInRainbowTable(rainbowTable, targetMD5);

        if (foundPassword != null) {
            System.out.println("Found Password: " + foundPassword);
        } else {
            System.out.println("Password not found in the rainbow table.");
        }
    }

    private static Map<String, String> generateRainbowTable() {
        Map<String, String> rainbowTable = new HashMap<>();

        for (int i = 0; i < TABLE_SIZE; i++) {
            String plaintextPassword = generateRandomPassword();
            String hashValue = generateChain(plaintextPassword);
            rainbowTable.put(hashValue, plaintextPassword);
        }

        return rainbowTable;
    }

    private static String lookupPasswordInRainbowTable(Map<String, String> rainbowTable, String targetMD5) {
        String currentHash = targetMD5;

        System.out.println("Looking up target MD5: " + targetMD5);
        for (int i = 0; i < CHAIN_LENGTH; i++) {
            System.out.println("Step " + (i + 1) + ": Current hash value: " + currentHash);

            String password = rainbowTable.get(currentHash);
            if (password != null) {
                return password;
            }
            currentHash = reduceFunction(currentHash, i);
        }

        return null;
    }

    private static String generateChain(String plaintextPassword) {
        String currentHash = calculateMD5Hash(plaintextPassword);

        for (int i = 0; i < CHAIN_LENGTH; i++) {
            plaintextPassword = reduceFunction(plaintextPassword, i);
            currentHash = calculateMD5Hash(plaintextPassword);
        }

        return currentHash;
    }

    private static String calculateMD5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String reduceFunction(String password, int index) {
        int reducedValue = Integer.parseInt(convertToBase10(password), 10) + index;
        return convertToBase36(reducedValue);
    }

    private static String generateRandomPassword() {
        Random random = new Random();
        int passwordLength = random.nextInt(PASSWORD_MAX_LENGTH - PASSWORD_MIN_LENGTH + 1) + PASSWORD_MIN_LENGTH;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < passwordLength; i++) {
            char randomChar = CHARACTERS.charAt(random.nextInt(CHARACTERS.length()));
            sb.append(randomChar);
        }

        return sb.toString();
    }

    private static String convertToBase10(String number) {
        int result = 0;
        for (char c : number.toCharArray()) {
            result = result * 36 + CHARACTERS.indexOf(c);
        }
        return Integer.toString(result);
    }


    private static String convertToBase36(int number) {
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            sb.append(CHARACTERS.charAt(number % 36));
            number /= 36;
        }
        return sb.reverse().toString();
    }
}