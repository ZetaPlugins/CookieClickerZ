package org.strassburger.cookieclickerz.util.achievements;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.strassburger.cookieclickerz.CookieClickerZ;
import org.strassburger.cookieclickerz.util.MessageUtils;

import java.math.BigInteger;

public class AchievementManager {
    private final CookieClickerZ plugin;

    public AchievementManager(CookieClickerZ plugin) {
        this.plugin = plugin;
    }

    /**
     * Set the progress of an achievement
     * @param player The player to set the achievement progress for
     * @param achievement The achievement to set the progress for
     * @param progressAmount The amount to set the progress to
     * @return Whether the achievement was completed
     */
    public boolean setAchievementProgress(Player player, Achievement achievement, int progressAmount) {
        if (achievement.isCompleted()) return false;

        achievement.setProgress(progressAmount);

        if (achievement.isCompleted()) {
            sendAchievementMessage(player, achievement.getType());
            return true;
        }

        return false;
    }

    /**
     * Progress an achievement by a certain amount
     * @param player The player to progress the achievement for
     * @param achievement The achievement to progress
     * @param progressAmount The amount to progress the achievement by
     * @return Whether the achievement was completed
     */
    public boolean progressAchievement(Player player, Achievement achievement, int progressAmount) {
        if (achievement.isCompleted()) return false;

        achievement.setProgress(achievement.getProgress() + progressAmount);

        if (achievement.isCompleted()) {
            sendAchievementMessage(player, achievement.getType());
            return true;
        }

        return false;
    }

    /**
     * Progress a cookies achievement
     * @param player The player to progress the achievement for
     * @param achievement The achievement to progress
     * @param totalCookies The total cookies of the player
     * @return Whether the achievement was completed
     */
    public boolean progressCookiesAchievement(Player player, Achievement achievement, BigInteger totalCookies) {
        if (achievement.isCompleted()) return false;

        if (totalCookies.compareTo(achievement.getType().getBigIntegerGoal()) >= 0) {
            achievement.setProgress(1);
            sendAchievementMessage(player, achievement.getType());
            return true;
        }

        return false;
    }

    /**
     * Send an achievement message to a player
     * @param player The player to send the message to
     * @param achievementType The achievement type to send the message for
     */
    public void sendAchievementMessage(Player player, AchievementType achievementType) {
        String achievementName = plugin.getLanguageManager().getString("achievements." + achievementType.getSlug() + ".name");
        if (achievementName == null) achievementName = achievementType.getSlug();

        String achievementDescription = plugin.getLanguageManager().getString("achievements." + achievementType.getSlug() + ".description");
        if (achievementDescription == null) achievementDescription = "No description available";

        player.sendMessage(MessageUtils.getAndFormatMsg(
                true,
                "achievementUnlocked",
                "<#9932cc>&lAchievement! &r&7You unlocked the achievement <hover:show_text:'<#9932cc>%achievement%\n&7%description%'><#9932cc>%achievement%&7!</hover>",
                new MessageUtils.Replaceable<>("%achievement%", achievementName),
                new MessageUtils.Replaceable<>("%description%", achievementDescription)
        ));

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
    }

    protected CookieClickerZ getPlugin() {
        return plugin;
    }
}
