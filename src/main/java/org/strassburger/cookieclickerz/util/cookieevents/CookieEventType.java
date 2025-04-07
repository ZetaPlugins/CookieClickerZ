package org.strassburger.cookieclickerz.util.cookieevents;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.strassburger.cookieclickerz.CookieClickerZ;
import org.strassburger.cookieclickerz.util.MessageUtils;
import org.strassburger.cookieclickerz.util.NumFormatter;
import org.strassburger.cookieclickerz.util.achievements.AchievementType;
import org.strassburger.cookieclickerz.util.storage.PlayerData;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

public enum CookieEventType {
    // cps x7 for 77 seconds
    COOKIE_FRENZY(0.005, 77, true) {
        @Override
        public void run(CookieClickerZ plugin, UUID uuid) {
            PlayerData playerData = plugin.getStorage().load(uuid);
            if (playerData == null) return;
            playerData.progressAchievement(AchievementType.SUGAR_RUSH, 1, plugin);
            playerData.progressAchievement(AchievementType.EVENT_HORIZON, 1, plugin);
            plugin.getStorage().save(playerData);
        }
    },

    // get 10% of your bank
    LUCKY(0.007, 0, true) {
        @Override
        public void run(CookieClickerZ plugin, UUID uuid) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player == null) return;
            PlayerData playerData = plugin.getStorage().load(uuid);
            if (playerData == null) return;

            BigDecimal totalCookies = new BigDecimal(playerData.getTotalCookies());
            BigDecimal luckyAmount = totalCookies.multiply(new BigDecimal("0.1"));
            playerData.setTotalCookies(playerData.getTotalCookies().add(luckyAmount.toBigInteger()));

            playerData.progressAchievement(AchievementType.EVENT_HORIZON, 1, plugin);

            if (luckyAmount.compareTo(new BigDecimal("1000000")) > 0) {
                playerData.progressAchievement(AchievementType.MONEY_MAGNET, 1, plugin);
            }

            plugin.getStorage().save(playerData);

            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

            player.sendMessage(MessageUtils.getAndFormatMsg(
                    true,
                    "eventMessages.instant.LUCKY",
                    "%ac%&lLucky! &r&7You got %ac%%amount% &7cookies!",
                    new MessageUtils.Replaceable<>("%amount%", NumFormatter.formatBigInt(luckyAmount.toBigInteger()))
            ));
        }
    },

    // clicks x777 for 7 seconds
    CLICK_FRENZY(0.002, 7, true) {
        @Override
        public void run(CookieClickerZ plugin, UUID uuid) {
            PlayerData playerData = plugin.getStorage().load(uuid);
            if (playerData == null) return;
            playerData.progressAchievement(AchievementType.EVENT_HORIZON, 1, plugin);
            plugin.getStorage().save(playerData);
        }
    },

    // lose 5% of your bank
    RUIN(0.002, 0, false) {
        @Override
        public void run(CookieClickerZ plugin, UUID uuid) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player == null) return;
            PlayerData playerData = plugin.getStorage().load(uuid);
            if (playerData == null) return;

            BigDecimal totalCookies = new BigDecimal(playerData.getTotalCookies());
            BigDecimal ruinAmount = totalCookies.multiply(new BigDecimal("0.05"));
            playerData.setTotalCookies(playerData.getTotalCookies().subtract(ruinAmount.toBigInteger()));

            playerData.progressAchievement(AchievementType.EVENT_HORIZON, 1, plugin);
            playerData.progressAchievement(AchievementType.NOT_MY_COOKIES, 1, plugin);

            if (ruinAmount.compareTo(new BigDecimal("1000000")) > 0) {
                playerData.progressAchievement(AchievementType.OOF, 1, plugin);
            }

            plugin.getStorage().save(playerData);

            player.playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1, 1);

            player.sendMessage(MessageUtils.getAndFormatMsg(
                    true,
                    "eventMessages.instant.RUIN",
                    "&c&lRuin! &r&7You lost %ac%%amount% &7cookies!",
                    new MessageUtils.Replaceable<>("%amount%", NumFormatter.formatBigInt(ruinAmount.toBigInteger()))
            ));
        }
    },

    // cps x0.5 for 66 seconds
    CURSED_FINGER(0.002, 66, false) {
        @Override
        public void run(CookieClickerZ plugin, UUID uuid) {
            PlayerData playerData = plugin.getStorage().load(uuid);
            if (playerData == null) return;
            playerData.progressAchievement(AchievementType.EVENT_HORIZON, 1, plugin);
            playerData.progressAchievement(AchievementType.NOT_MY_COOKIES, 1, plugin);
            plugin.getStorage().save(playerData);
        }
    };

    private final String id = name();
    private final double appearanceRate;
    private final long duration;
    private final boolean isPositive;

    private static final Random random = new Random();

    /**
     * @param appearanceRate appearance rate of the event
     * @param duration duration of the event in seconds
     */
    CookieEventType(double appearanceRate, long duration, boolean isPositive) {
        this.appearanceRate = appearanceRate;
        this.duration = duration * 20;// convert seconds to ticks
        this.isPositive = isPositive;
    }

    /**
     * @return the id of the event
     */
    public String getId() {
        return id;
    }

    /**
     * @return true if the event is positive, false otherwise
     */
    public boolean isPositive() {
        return isPositive;
    }

    /**
     * @return appearance rate of the event
     */
    public double getAppearanceRate() {
        return CookieClickerZ.getInstance().getConfig().getDouble("events.rates." + name(), appearanceRate);
    }

    /**
     * @return duration of the event in ticks (0 if the event is instant)
     */
    public long getDuration() {
        return duration;
    }

    /**
     * @return true if the event is instant, false otherwise
     */
    public boolean isInstant() {
        return duration == 0;
    }

    public abstract void run(CookieClickerZ plugin, UUID uuid);

    /**
     * Get a random CookieEventType based on appearance rates or null if no event should happen.
     * @return a random CookieEventType or null
     */
    @Nullable
    public static CookieEventType getRandom() {
        // Calculate the total appearance rate sum
        double totalRate = 0;
        for (CookieEventType eventType : CookieEventType.values()) {
            totalRate += eventType.getAppearanceRate();
        }

        double noEventRate = 1 - totalRate;
        if (noEventRate < 0) throw new IllegalStateException("The sum of appearance rates is greater than 1");
        totalRate += noEventRate;

        // Generate a random number between 0 and totalRate
        double randomValue = random.nextDouble() * totalRate;

        // Select the event based on the random value
        double cumulativeRate = 0.0;
        for (CookieEventType eventType : CookieEventType.values()) {
            cumulativeRate += eventType.getAppearanceRate();
            if (randomValue <= cumulativeRate) {
                return eventType;
            }
        }

        return null;
    }
}
