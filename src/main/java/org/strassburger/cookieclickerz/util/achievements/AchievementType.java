package org.strassburger.cookieclickerz.util.achievements;

import org.strassburger.cookieclickerz.util.NumFormatter;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum AchievementType {
    CLICKER_ROOKIE("clicks_clicker_rookie",
            AchievementCategory.CLICKS,
            false,
            1),
    FINGER_WORKOUT("clicks_finger_workout",
            AchievementCategory.CLICKS,
            false,
            100),
    CLICK_CHAMPION("clicks_click_champion",
            AchievementCategory.CLICKS,
            false,
            1000),
    UNSTOPPABLE_CLICKER("clicks_unstoppable_clicker",
            AchievementCategory.CLICKS,
            false,
            10_000),
    COOKIE_MACHINE("clicks_cookie_machine",
            AchievementCategory.CLICKS,
            false,
            1_000_000),

    FIRST_BATCH("cookies_first_batch",
            AchievementCategory.COOKIES,
            false,
            NumFormatter.stringToBigInteger("1K")),
    COOKIE_CONNOISSEUR("cookies_cookie_connoisseur",
            AchievementCategory.COOKIES,
            false,
            NumFormatter.stringToBigInteger("100K")),
    COOKIE_COLLECTOR("cookies_cookie_collector",
            AchievementCategory.COOKIES,
            false,
            NumFormatter.stringToBigInteger("1M")),
    COOKIE_HOARDER("cookies_cookie_hoarder",
            AchievementCategory.COOKIES,
            false,
            NumFormatter.stringToBigInteger("1B")),
    COOKIE_OVERLORD("cookies_cookie_overlord",
            AchievementCategory.COOKIES,
            false,
            NumFormatter.stringToBigInteger("1T")),

    SMART_SHOPPER("upgrades_smart_shopper",
            AchievementCategory.UPGRADES,
            false,
            1),
    UPGRADE_MASTER("upgrades_upgrade_master",
            AchievementCategory.UPGRADES,
            false,
            50),
    SAVVY_SPENDER("upgrades_savvy_spender",
            AchievementCategory.UPGRADES,
            false,
            150),
    UPGRADE_ENTHUSIAST("upgrades_upgrade_enthusiast",
            AchievementCategory.UPGRADES,
            false,
            500),
    GOTTA_UPGRADE_THEM_ALL("upgrades_gotta_upgrade_them_all",
            AchievementCategory.UPGRADES,
            false,
            1000),
    ;

    private final String slug;
    private final AchievementCategory category;
    private final boolean hidden;
    private final Number goal;

    AchievementType(String slug, AchievementCategory category, boolean hidden, int goal) {
        this.slug = slug;
        this.category = category;
        this.hidden = hidden;
        this.goal = goal;
    }

    AchievementType(String slug, AchievementCategory category, boolean hidden, BigInteger goal) {
        this.slug = slug;
        this.category = category;
        this.hidden = hidden;
        this.goal = goal;
    }

    public String getSlug() {
        return slug;
    }

    public AchievementCategory getCategory() {
        return category;
    }

    public boolean isHidden() {
        return hidden;
    }

    public Number getGoal() {
        return goal;
    }

    /**
     * Get the goal as a BigInteger
     * @return the goal as a BigInteger
     */
    public BigInteger getBigIntegerGoal() {
        return goal instanceof BigInteger ? (BigInteger) goal : BigInteger.valueOf((Integer) goal);
    }

    /**
     * Get the goal as an integer
     * @return the goal as an integer
     * @throws UnsupportedOperationException if the goal is a BigInteger
     */
    public int getIntGoal() throws UnsupportedOperationException {
        if (goal instanceof Integer) {
            return (Integer) goal;
        }
        throw new UnsupportedOperationException("Goal is BigInteger, use getBigIntegerGoal() instead.");
    }

    /**
     * Get the achievement type by its slug
     * @param slug the slug of the achievement type
     * @return the achievement type if found, otherwise an empty optional
     */
    public static Optional<AchievementType> getBySlug(String slug) {
        for (AchievementType type : values()) {
            if (type.getSlug().equals(slug)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }

    /**
     * Get all achievements of a specific category
     * @param category the category to filter by
     * @return a list of achievements of the specified category
     */
    public static List<AchievementType> getByCategory(AchievementCategory category) {
        return Stream.of(values())
                .filter(achievementType -> achievementType.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    public static List<AchievementType> getAll() {
        return Stream.of(values())
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return slug;
    }
}
