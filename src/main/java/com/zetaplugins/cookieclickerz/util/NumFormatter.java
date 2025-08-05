package com.zetaplugins.cookieclickerz.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class NumFormatter {
    private static final String[] suffixes = { "", "K", "M", "B", "T", "Q", "QQ", "S", "SS", "O", "N", "D" };
    private static final BigInteger[] multipliers;

    /**
     * 1 -> 1
     * 1000 -> 1K
     * 1000000 -> 1M
     * 1000000000 -> 1B
     * 1000000000000 -> 1T
     * 1000000000000000 -> 1Q
     * 1000000000000000000 -> 1QQ
     * 1000000000000000000000 -> 1S
     * 1000000000000000000000000 -> 1SS
     * 1000000000000000000000000000 -> 1O
     * 1000000000000000000000000000000 -> 1N
     * 1000000000000000000000000000000000 -> 1D
     */

    static {
        multipliers = new BigInteger[suffixes.length];
        multipliers[0] = BigInteger.ONE;
        for (int i = 1; i < multipliers.length; i++) {
            multipliers[i] = multipliers[i - 1].multiply(BigInteger.valueOf(1000));
        }
    }

    private NumFormatter() {}

    private static BigDecimal convertStringToBigDecimal(String str) {
        for (int i = suffixes.length - 1; i >= 1; i--) {
            if (str.endsWith(suffixes[i])) {
                String numberPart = str.substring(0, str.length() - suffixes[i].length());
                try {
                    return new BigDecimal(numberPart).multiply(new BigDecimal(multipliers[i]));
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        try {
            return new BigDecimal(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static BigInteger stringToBigInteger(String str) {
        BigDecimal bigDecimal = convertStringToBigDecimal(str);
        return bigDecimal != null ? bigDecimal.toBigInteger() : null;
    }

    public static BigDecimal stringToBigDecimal(String str) {
        return convertStringToBigDecimal(str);
    }

    private static String formatNumber(BigDecimal number) {
        number = number.setScale(2, RoundingMode.DOWN);

        int index = 0;
        while (number.compareTo(BigDecimal.valueOf(1000)) >= 0 && index < suffixes.length - 1) {
            number = number.divide(BigDecimal.valueOf(1000), 2, RoundingMode.DOWN);
            index++;
        }
        return number.stripTrailingZeros().toPlainString() + suffixes[index];
    }

    public static String formatBigInt(BigInteger bigInt) {
        return formatNumber(new BigDecimal(bigInt));
    }

    public static String formatBigDecimal(BigDecimal bigDecimal) {
        return formatNumber(bigDecimal);
    }
}