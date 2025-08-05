package com.zetaplugins.cookieclickerz.util.achievements;

public enum AchievementCategory {
    CLICKS("clicks", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTMyYTU4MzhmZDljZDRjOTc3ZjE1MDcxZDY5OTdmZjVjN2Y5NTYwNzRhMmRhNTcxYTE5Y2NlZmIwM2M1NyJ9fX0="),
    COOKIES("cookies", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjA1ZWY4ODE5ZmI2YTkyMTQ4MjQ5ZmY0OGMzOTE0ZTkyYzI3M2U3ODc0NTIzZmY3OTMwYjlmNWNjOTNjZWFhZSJ9fX0"),
    UPGRADES("upgrades", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTVmZDY3ZDU2ZmZjNTNmYjM2MGExNzg3OWQ5YjUzMzhkNzMzMmQ4ZjEyOTQ5MWE1ZTE3ZThkNmU4YWVhNmMzYSJ9fX0="),
    EVENTS("events", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjJiYTllYWQ5N2M4ZmI0NGIxNTZhZGU5Y2IyMTRlYTkxMjQzNGEyY2M0N2M0ZGVjNTBmMjEwMjFjNzVkZDJkNyJ9fX0="),
    PRESTIGE("prestige", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTkwY2JkNzJlNDFhOWJkNDExYmU5MjliNzNmZDI2OTIwNjM2OGIyODEwZDZjNjgxOTkxOGNiOGViNjYyMjRmNCJ9fX0="),
    MISC("misc", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjAzNzEyMDYyZTgyYTA3OTdmZDdiMGMxMDU3NmVkOTI0ODBmZGM5NGJkMmUwMGMyMTk2MWIzNGJlNjBlODU4ZSJ9fX0="),
    ;

    private final String slug;
    private final String headBase64;

    AchievementCategory(String slug, String headBase64) {
        this.slug = slug;
        this.headBase64 = headBase64;
    }

    public String getSlug() {
        return slug;
    }

    public String getHeadBase64() {
        return headBase64;
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