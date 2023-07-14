package com.Pie4u.animalcare;

import java.security.SecureRandom;
import java.util.Random;

public class AnimalRescueUtil {

    private static final int AUTO_ID_LENGTH = 20;
    private static final String AUTO_ID_ALPHABET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random rand = new SecureRandom();

    public static String generateAutoId() {
        StringBuilder builder = new StringBuilder();
        int maxRandom = AUTO_ID_ALPHABET.length();
        for (int i = 0; i < AUTO_ID_LENGTH; i++) {
            builder.append(AUTO_ID_ALPHABET.charAt(rand.nextInt(maxRandom)));
        }
        return builder.toString();
    }
}
