package com.zetaplugins.cookieclickerz.commands.MainCommand.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.zetaplugins.cookieclickerz.CookieClickerZ;
import com.zetaplugins.cookieclickerz.commands.CommandUsageException;
import com.zetaplugins.cookieclickerz.commands.SubCommand;
import com.zetaplugins.cookieclickerz.util.MessageUtils;
import com.zetaplugins.cookieclickerz.util.NumFormatter;
import com.zetaplugins.cookieclickerz.util.achievements.Achievement;
import com.zetaplugins.cookieclickerz.util.achievements.AchievementCategory;
import com.zetaplugins.cookieclickerz.util.achievements.AchievementType;
import com.zetaplugins.cookieclickerz.storage.PlayerData;

public class AchievementsSubCommand implements SubCommand {
    private final CookieClickerZ plugin;

    public AchievementsSubCommand(CookieClickerZ plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandUsageException {
        if (args.length < 3) {
            throw new CommandUsageException("/cc achievements [player] <get | set>");
        }

        String optionTwo = args[1]; // player

        Player target = plugin.getServer().getPlayer(optionTwo);

        if (target == null) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(
                    false,
                    "playerNotFound",
                    "&cPlayer not found!"
            ));
            return false;
        }

        PlayerData targetPlayerData = plugin.getStorage().load(target.getUniqueId());

        if (targetPlayerData == null) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(
                    false,
                    "playerDataStorageNull",
                    "&cPlayerDataStorage is null!"
            ));
            return false;
        }

        if (args[2].equals("get")) {
            if (args.length < 4) {
                throw new CommandUsageException("/cc achievements [player] get <achievement>");
            }

            String optionFour = args[3]; // achievement

            String achievementProgress = "0";

            AchievementType achievementType = AchievementType.getBySlug(optionFour).orElse(null);

            if (achievementType == null) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(
                        false,
                        "achievementNotFound",
                        "&cAchievement not found!"
                ));
                return false;
            }

            Achievement achievement = targetPlayerData.getAchievement(achievementType).orElse(null);

            if (achievement != null) {
                achievementProgress = String.valueOf(achievement.getProgress());

                if (achievement.getType().getCategory() == AchievementCategory.COOKIES) {
                    achievementProgress =
                            targetPlayerData.getTotalCookies().compareTo(achievement.getType().getBigIntegerGoal()) >= 0
                                    ? NumFormatter.formatBigInt(achievement.getType().getBigIntegerGoal())
                                    : NumFormatter.formatBigInt(targetPlayerData.getTotalCookies());
                }
            }

            sender.sendMessage(MessageUtils.getAndFormatMsg(true,
                    "getAchievement",
                    "&7Achievement %ac%%achievement% &7for %ac%%player%&7: %ac%%progress%&8/%ac%%goal%",
                    new MessageUtils.Replaceable<>(
                            "%achievement%",
                            plugin.getLanguageManager().getString("achievements." + achievementType.getSlug() + ".name")
                    ),
                    new MessageUtils.Replaceable<>("%player%", target.getName()),
                    new MessageUtils.Replaceable<>("%progress%", achievementProgress),
                    new MessageUtils.Replaceable<>("%goal%", NumFormatter.formatBigInt(achievementType.getBigIntegerGoal()))
            ));

            return true;
        }

        if (args[2].equals("set")) {
            if (args.length < 5) {
                throw new CommandUsageException("/cc achievements [player] set <achievement> <progress>");
            }

            String optionFour = args[3]; // achievement
            String optionFive = args[4]; // progress

            int progress = Integer.parseInt(optionFive);

            AchievementType achievementType = AchievementType.getBySlug(optionFour).orElse(null);

            if (achievementType == null) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(
                        false,
                        "achievementNotFound",
                        "&cAchievement not found!"
                ));
                return false;
            }

            targetPlayerData.setAchievementProgress(achievementType, progress);

            plugin.getStorage().save(targetPlayerData);

            sender.sendMessage(MessageUtils.getAndFormatMsg(
                    true,
                    "setAchievement",
                    "&7Successfully set %ac%%player%&7's achievement %ac%%achievement% &7progress to %ac%%progress%",
                    new MessageUtils.Replaceable<>("%player%", target.getName()),
                    new MessageUtils.Replaceable<>(
                            "%achievement%",
                            plugin.getLanguageManager().getString("achievements." + achievementType.getSlug() + ".name")
                    ),
                    new MessageUtils.Replaceable<>("%progress%", progress)
            ));

            return true;
        }

        return false;
    }

    @Override
    public String getUsage() {
        return "/cc achievements [player] <get | set>";
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("cookieclickerz.admin.manageachievements");
    }
}
