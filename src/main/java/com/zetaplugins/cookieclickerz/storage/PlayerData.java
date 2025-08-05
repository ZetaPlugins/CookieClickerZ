package com.zetaplugins.cookieclickerz.storage;

import org.bukkit.entity.Player;
import com.zetaplugins.cookieclickerz.CookieClickerZ;
import com.zetaplugins.cookieclickerz.util.NumFormatter;
import com.zetaplugins.cookieclickerz.util.achievements.Achievement;
import com.zetaplugins.cookieclickerz.util.achievements.AchievementType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.*;

public final class PlayerData {
    private final static BigDecimal TOTAL_COOKIES_WEIGHT = new BigDecimal("0.4");
    private final static BigDecimal COOKIES_PER_CLICK_WEIGHT = new BigDecimal("0.3");
    private final static BigDecimal PRESTIGE_WEIGHT = new BigDecimal("0.5");

    private final String name;
    private final String uuid;
    private BigInteger totalCookies = BigInteger.ZERO;
    private int totalClicks = 0;
    private long lastLogoutTime = System.currentTimeMillis();
    private Map<String, Integer> upgrades = new HashMap<>();
    private List<Achievement> achievements = new ArrayList<>();
    private BigInteger cookiesPerClick = BigInteger.ONE;
    private BigInteger offlineCookies = BigInteger.ZERO;
    private int prestige = 0;
    private boolean removedUpgrades = false;

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
        removedUpgrades = true;
    }

    public boolean hasRemovedUpgrades() {
        return removedUpgrades;
    }

    public void setHasRemovedUpgrades(boolean removedUpgrades) {
        this.removedUpgrades = removedUpgrades;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    /**
     * Get an achievement by its slug
     * @param achievementSlug The slug of the achievement to get
     * @return The achievement if found, otherwise an empty optional
     */
    public Optional<Achievement> getAchievement(String achievementSlug) {
        return achievements.stream()
                .filter(achievement -> achievement.getType().getSlug().equals(achievementSlug))
                .findFirst();
    }

    /**
     * Get an achievement by its type
     * @param achievementType The achievement type to get
     * @return The achievement if found, otherwise an empty optional
     */
    public Optional<Achievement> getAchievement(AchievementType achievementType) {
        return achievements.stream()
                .filter(achievement -> achievement.getType().equals(achievementType))
                .findFirst();
    }

    /**
     * Set the progress of an achievement
     * @param achievementSlug The slug of the achievement to set the progress for
     * @param progress The progress to set
     * @throws IllegalArgumentException If the achievement slug is invalid
     */
    public void setAchievementProgress(String achievementSlug, int progress) throws IllegalArgumentException {
        setAchievementProgress(
                AchievementType.getBySlug(achievementSlug).orElseThrow(
                        () -> new IllegalArgumentException("Invalid achievement slug: " + achievementSlug)
                ),
                progress
        );
    }

    /**
     * Set the progress of an achievement
     * @param achievementType The achievement type to set the progress for
     * @param progress The progress to set
     */
    public void setAchievementProgress(AchievementType achievementType, int progress) {
        getAchievement(achievementType).ifPresentOrElse(
                achievement -> achievement.setProgress(progress),
                () -> achievements.add(new Achievement(achievementType, progress))
        );
    }

    public void setAchievementProgress(AchievementType achievementType, int progressAmount, CookieClickerZ plugin) throws IllegalArgumentException {
        getAchievement(achievementType).ifPresentOrElse(
                achievement -> {
                    Player player = plugin.getServer().getPlayer(UUID.fromString(uuid));
                    plugin.getAchievementManager().setAchievementProgress(player, achievement, progressAmount);
                },
                () -> {
                    Achievement achievement = new Achievement(achievementType, 0);
                    achievements.add(achievement);
                    plugin.getAchievementManager().setAchievementProgress(
                            plugin.getServer().getPlayer(UUID.fromString(uuid)),
                            achievement,
                            progressAmount
                    );
                }
        );
    }

    /**
     * Progress an achievement by a certain amount
     * @param achievementType The achievement type to progress
     * @param progressAmount The amount to progress the achievement by
     * @param plugin The plugin instance
     */
    public void progressAchievement(AchievementType achievementType, int progressAmount, CookieClickerZ plugin) {
        getAchievement(achievementType).ifPresentOrElse(
                achievement -> {
                    Player player = plugin.getServer().getPlayer(UUID.fromString(uuid));
                    plugin.getAchievementManager().progressAchievement(player, achievement, progressAmount);
                },
                () -> {
                    Achievement achievement = new Achievement(achievementType, 0);
                    achievements.add(achievement);
                    plugin.getAchievementManager().progressAchievement(
                            plugin.getServer().getPlayer(UUID.fromString(uuid)),
                            achievement,
                            progressAmount
                    );
                }
        );
    }

    public void progressCookiesAchievement(AchievementType achievementType, BigInteger totalCookies, CookieClickerZ plugin) {
        getAchievement(achievementType).ifPresentOrElse(
                achievement -> {
                    Player player = plugin.getServer().getPlayer(UUID.fromString(uuid));
                    plugin.getAchievementManager().progressCookiesAchievement(player, achievement, totalCookies);
                },
                () -> {
                    Achievement achievement = new Achievement(achievementType, 0);
                    achievements.add(achievement);
                    plugin.getAchievementManager().progressCookiesAchievement(
                            plugin.getServer().getPlayer(UUID.fromString(uuid)),
                            achievement,
                            totalCookies
                    );
                }
        );
    }

    public BigInteger getCookiesPerClick() {
        return cookiesPerClick;
    }

    public void setCookiesPerClick(BigInteger cookiesPerClick) throws IllegalArgumentException {
        if (cookiesPerClick.compareTo(BigInteger.ZERO) < 0) {
            throw new IllegalArgumentException("cookiesPerClick cannot be negative");
        }
        this.cookiesPerClick = cookiesPerClick;
    }

    public BigInteger getOfflineCookies() {
        return offlineCookies;
    }

    public void setOfflineCookies(BigInteger offlineCookies) throws IllegalArgumentException {
        if (offlineCookies.compareTo(BigInteger.ZERO) < 0) {
            throw new IllegalArgumentException("offlineCookies cannot be negative");
        }
        this.offlineCookies = offlineCookies;
    }

    public int getPrestige() {
        return prestige;
    }

    public void setPrestige(int prestige) {
        this.prestige = prestige;
    }

    /**
     * Get the player's score
     * @return The player's score
     */
    public BigDecimal getScore() {
        return calculatePlayerScore(totalCookies, cookiesPerClick, prestige);
    }

    /**
     * Calculate the player's score based on the total cookies, cookies per click, and prestige level
     * @param totalCookies The total amount of cookies the player has
     * @param cookiesPerClick The amount of cookies the player gets per click
     * @param prestige The player's prestige level
     * @return The player's score
     */
    private BigDecimal calculatePlayerScore(BigInteger totalCookies, BigInteger cookiesPerClick, int prestige) {
        BigDecimal logTotalCookies = bigIntegerLog(totalCookies);
        BigDecimal cookiesPerClickValue = new BigDecimal(cookiesPerClick);

        // Formula: w1 * log10(totalCookies) + w2 * cookiesPerClick + w3 * prestige
        return (TOTAL_COOKIES_WEIGHT.multiply(logTotalCookies))
                .add(COOKIES_PER_CLICK_WEIGHT.multiply(cookiesPerClickValue))
                .add(PRESTIGE_WEIGHT.multiply(new BigDecimal(prestige)));
    }

    /**
     * Calculate the log10 of a BigInteger
     * @param value The value to calculate the log10 of
     * @return The log10 of the value
     */
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

    /**
     * Get the player's score formatted as a string
     * @return The player's score formatted as a string
     */
    public String getFormattedScore() {
        return NumFormatter.formatBigDecimal(getScore());
    }
}
