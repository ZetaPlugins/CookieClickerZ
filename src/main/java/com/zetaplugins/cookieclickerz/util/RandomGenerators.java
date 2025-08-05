package com.zetaplugins.cookieclickerz.util;

import java.math.BigInteger;
import java.util.Random;

public class RandomGenerators {
    private RandomGenerators() {}

    /**
     * Generates a random word with the given length
     * @param wordLength The length of the word
     * @return The random word
     */
    public static String generateRandomWord(int wordLength) {
        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder word = new StringBuilder(wordLength);

        // Create random word
        for (int i = 0; i < wordLength; i++) {
            int index = random.nextInt(alphabet.length());
            word.append(alphabet.charAt(index));
        }

        return word.toString();
    }

    /**
     * Generates a random number between min and max (inclusive)
     * @param min The minimum number (inclusive)
     * @param max The maximum number (inclusive)
     * @return The random number
     */
    public static int generateRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    /**
     * Generates a random long between min and max (inclusive)
     * @param min The minimum number (inclusive)
     * @param max The maximum number (inclusive)
     * @return The random long
     */
    public static long generateRandomLong(long min, long max) {
        Random random = new Random();
        return random.nextLong() % (max - min + 1) + min;
    }

    /**
     * Generates a random BigInteger between min and max (inclusive)
     * @param min The minimum number (inclusive)
     * @param max The maximum number (inclusive)
     * @return The random BigInteger
     */
    public static BigInteger generateRandomBigInteger(BigInteger min, BigInteger max) {
        Random random = new Random();
        BigInteger range = max.subtract(min);
        int length = range.bitLength();
        BigInteger result = new BigInteger(length, random);
        if (result.compareTo(min) < 0) {
            result = result.add(min);
        }
        if (result.compareTo(range) >= 0) {
            result = result.mod(range).add(min);
        }
        return result;
    }
}
