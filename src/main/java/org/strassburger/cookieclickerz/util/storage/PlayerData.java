package org.strassburger.cookieclickerz.util.storage;

import org.strassburger.cookieclickerz.util.NumFormatter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {
    private final static BigDecimal TOTAL_COOKIES_WEIGHT = new BigDecimal("0.4");
    private final static BigDecimal COOKIES_PER_CLICK_WEIGHT = new BigDecimal("0.3");
    private final static BigDecimal PRESTIGE_WEIGHT = new BigDecimal("0.5");

    private final String name;
    private final String uuid;
    private BigInteger totalCookies = BigInteger.ZERO;
    private int totalClicks = 0;
    private long lastLogoutTime = System.currentTimeMillis();
    private Map<String, Integer> upgrades = new HashMap<>();
    private BigInteger cookiesPerClick = BigInteger.ONE;
    private BigInteger offlineCookies = BigInteger.ZERO;
    private int prestige = 0;

    public PlayerData(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid.toString();
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return UUID.fromString(uuid);
    }

    public BigInteger getTotalCookies() {
        return totalCookies;
    }

    public void setTotalCookies(BigInteger totalCookies) throws IllegalArgumentException {
        if (totalCookies.compareTo(BigInteger.ZERO) < 0)
            throw new IllegalArgumentException("totalCookies cannot be negative");
        this.totalCookies = totalCookies;
    }

    public int getTotalClicks() {
        return totalClicks;
    }

    public void setTotalClicks(int totalClicks) {
        this.totalClicks = totalClicks;
    }

    public long getLastLogoutTime() {
        return lastLogoutTime;
    }

    public void setLastLogoutTime(long lastLogoutTime) {
        this.lastLogoutTime = lastLogoutTime;
    }

    public Map<String, Integer> getUpgrades() {
        return upgrades;
    }

    public void setUpgrades(Map<String, Integer> upgrades) {
        this.upgrades = upgrades;
    }

    public void addUpgrade(String upgradeName, int level) {
        upgrades.put(upgradeName, level);
    }

    public int getUpgradeLevel(String upgradeName) {
        return upgrades.getOrDefault(upgradeName, 0);
    }

    public void resetUpgrades() {
        upgrades.clear();
    }

    public BigInteger getCookiesPerClick() {
        return cookiesPerClick;
    }

    public void setCookiesPerClick(BigInteger cookiesPerClick) {
        if (cookiesPerClick.compareTo(BigInteger.ZERO) < 0) throw new IllegalArgumentException("cookiesPerClick cannot be negative");
        this.cookiesPerClick = cookiesPerClick;
    }

    public BigInteger getOfflineCookies() {
        return offlineCookies;
    }

    public void setOfflineCookies(BigInteger offlineCookies) {
        if (offlineCookies.compareTo(BigInteger.ZERO) < 0) throw new IllegalArgumentException("offlineCookies cannot be negative");
        this.offlineCookies = offlineCookies;
    }

    public int getPrestige() {
        return prestige;
    }

    public void setPrestige(int prestige) {
        this.prestige = prestige;
    }

    public BigDecimal getScore() {
        return calculatePlayerScore(totalCookies, cookiesPerClick, prestige);
    }

    public static BigDecimal calculatePlayerScore(BigInteger totalCookies, BigInteger cookiesPerClick, int prestige) {
        BigDecimal logTotalCookies = bigIntegerLog(totalCookies);
        BigDecimal cookiesPerClickValue = new BigDecimal(cookiesPerClick);

        // Formula: w1 * log10(totalCookies) + w2 * cookiesPerClick + w3 * prestige
        return (TOTAL_COOKIES_WEIGHT.multiply(logTotalCookies))
                .add(COOKIES_PER_CLICK_WEIGHT.multiply(cookiesPerClickValue))
                .add(PRESTIGE_WEIGHT.multiply(new BigDecimal(prestige)));
    }

    private static BigDecimal bigIntegerLog(BigInteger value) {
        if (value.compareTo(BigInteger.ONE) <= 0) {
            return BigDecimal.ZERO;
        }
        int digits = value.toString().length() - 1;
        BigDecimal bigDecimalValue = new BigDecimal(value);

        BigDecimal firstDigitValue = bigDecimalValue.movePointLeft(digits);
        BigDecimal fractionalLog = new BigDecimal(Math.log10(firstDigitValue.doubleValue()), MathContext.DECIMAL64);

        return new BigDecimal(digits).add(fractionalLog);
    }

    public String getFormattedScore() {
        return NumFormatter.formatBigDecimal(getScore());
    }
}
