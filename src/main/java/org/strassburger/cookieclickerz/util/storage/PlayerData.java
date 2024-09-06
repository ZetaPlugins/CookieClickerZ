package org.strassburger.cookieclickerz.util.storage;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {
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

    public String getUuid() {
        return uuid;
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
}
