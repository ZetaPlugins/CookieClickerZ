package com.zetaplugins.cookieclickerz.util;

import java.math.*;

/**
 * Utility class for pricing calculations.
 */
public final class PricingUtils {
    private static final MathContext MC = new MathContext(50, RoundingMode.HALF_UP);

    private PricingUtils() {}

    /**
     * Calculate the price of an item at a specific level.
     *
     * @param baseBI The base price as a BigInteger.
     * @param r The price multiplier (growth rate).
     * @param level The level of the item.
     * @return The price at the specified level as a BigInteger.
     */
    public static BigInteger priceAtLevel(BigInteger baseBI, double r, int level) {
        if (level < 0) level = 0;
        BigDecimal base = new BigDecimal(baseBI);
        if (r == 1.0d) {
            return base.toBigInteger();
        }
        BigDecimal price = base.multiply(pow(BigDecimal.valueOf(r), level), MC);
        if (price.signum() <= 0) return BigInteger.ZERO;
        return price.setScale(0, RoundingMode.HALF_UP).toBigInteger();
    }

    /**
     * Calculate the total cost for purchasing 'n' levels starting from 'currentLevel'.
     *
     * @param baseBI The base price as a BigInteger.
     * @param r The price multiplier (growth rate).
     * @param currentLevel The current level of the item.
     * @param n The number of levels to purchase.
     * @return The total cost for 'n' levels as a BigInteger.
     */
    public static BigInteger totalCostForN(BigInteger baseBI, double r, int currentLevel, int n) {
        if (n <= 0) return BigInteger.ZERO;
        BigDecimal base = new BigDecimal(baseBI);

        if (r == 1.0d) {
            BigDecimal first = base.multiply(pow(BigDecimal.ONE, currentLevel), MC);
            BigDecimal total = first.multiply(BigDecimal.valueOf(n), MC);
            return total.setScale(0, RoundingMode.HALF_UP).toBigInteger();
        }

        BigDecimal rBD = BigDecimal.valueOf(r);
        BigDecimal rPowL = pow(rBD, currentLevel);
        BigDecimal first = base.multiply(rPowL, MC);

        BigDecimal rn = pow(rBD, n);
        BigDecimal numerator = rn.subtract(BigDecimal.ONE, MC);
        BigDecimal denominator = rBD.subtract(BigDecimal.ONE, MC);

        BigDecimal sum = first.multiply(numerator, MC).divide(denominator, MC);

        if (sum.signum() <= 0) return BigInteger.ZERO;
        return sum.setScale(0, RoundingMode.HALF_UP).toBigInteger();
    }

    /**
     * Efficiently computes a^n using exponentiation by squaring.
     *
     * @param a The base as a BigDecimal.
     * @param n The exponent as an integer.
     * @return The result of a raised to the power of n as a BigDecimal.
     */
    public static BigDecimal pow(BigDecimal a, int n) {
        if (n <= 0) return BigDecimal.ONE;
        BigDecimal result = BigDecimal.ONE;
        BigDecimal base = a;
        int exp = n;
        while (exp > 0) {
            if ((exp & 1) == 1) {
                result = result.multiply(base, MC);
            }
            base = base.multiply(base, MC);
            exp >>= 1;
        }
        return result;
    }
}