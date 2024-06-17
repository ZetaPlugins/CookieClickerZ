package org.strassburger.cookieclickerz.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class NumFormatter {
    private static final String[] suffixes = { "", "K", "M", "B", "T", "Q", "QQ", "S", "SS", "O", "N", "D" };
    private static final BigInteger[] multipliers;

    static {
        multipliers = new BigInteger[suffixes.length];
        multipliers[0] = BigInteger.ONE;
        for (int i = 1; i < multipliers.length; i++) {
            multipliers[i] = multipliers[i - 1].multiply(BigInteger.valueOf(1000));
        }
    }

    private NumFormatter() {}

    public static BigInteger stringToBigInteger(String str) {
        for (int i = suffixes.length - 1; i >= 1; i--) {
            if (str.endsWith(suffixes[i])) {
                String numberPart = str.substring(0, str.length() - suffixes[i].length());
                try {
                    BigDecimal number = new BigDecimal(numberPart);
                    return number.multiply(new BigDecimal(multipliers[i])).toBigInteger();
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }

        try {
            return new BigInteger(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String formatBigInt(BigInteger bigInt) {
        int index = 0;
        BigDecimal number = new BigDecimal(bigInt);
        while (number.compareTo(BigDecimal.valueOf(1000)) >= 0 && index < suffixes.length - 1) {
            number = number.divide(BigDecimal.valueOf(1000));
            index++;
        }
        return number.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() + suffixes[index];
    }
}
