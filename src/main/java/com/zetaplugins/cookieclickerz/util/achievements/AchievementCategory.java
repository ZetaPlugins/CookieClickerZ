package com.zetaplugins.cookieclickerz.util.achievements;

import com.zetaplugins.cookieclickerz.CookieClickerZ;
import org.bukkit.configuration.file.FileConfiguration;

public enum AchievementCategory {
    CLICKS("clicks"),
    COOKIES("cookies"),
    UPGRADES("upgrades"),
    EVENTS("events"),
    PRESTIGE("prestige"),
    MISC("misc"),
    ;

    private final String slug;

    AchievementCategory(String slug) {
        this.slug = slug;
    }

    public String getSlug() {
        return slug;
    }

    public String getHeadBase64(CookieClickerZ plugin) {
        FileConfiguration achievementsConfig = plugin.getConfigManager().getAchievementCategoryConfig();
        return achievementsConfig.getString(slug + ".head", "none");
    }

    @Override
    public String toString() {
        return slug;
    }

    public static AchievementCategory fromSlug(String slug) {
        for (AchievementCategory category : values()) {
            if (category.slug.equals(slug)) {
                return category;
            }
        }
        return null;
    }
}