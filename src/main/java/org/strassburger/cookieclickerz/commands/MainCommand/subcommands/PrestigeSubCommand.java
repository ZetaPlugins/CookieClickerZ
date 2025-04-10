package org.strassburger.cookieclickerz.commands.MainCommand.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.strassburger.cookieclickerz.CookieClickerZ;
import org.strassburger.cookieclickerz.commands.CommandUsageException;
import org.strassburger.cookieclickerz.commands.SubCommand;
import org.strassburger.cookieclickerz.util.MessageUtils;
import org.strassburger.cookieclickerz.util.achievements.AchievementCategory;
import org.strassburger.cookieclickerz.util.achievements.AchievementType;
import org.strassburger.cookieclickerz.storage.PlayerData;
import org.strassburger.cookieclickerz.storage.Storage;

public class PrestigeSubCommand implements SubCommand {
    private final CookieClickerZ plugin;

    public PrestigeSubCommand(CookieClickerZ plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandUsageException {
        Storage storage = plugin.getStorage();

        if (args.length < 2) {
            throw new CommandUsageException("/cc prestige [player] <get, set>");
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

        PlayerData targetPlayerData = storage.load(target.getUniqueId());
        if (targetPlayerData == null) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(
                    false,
                    "playerDataStorageNull",
                    "&cPlayerDataStorage is null!"
            ));
            return false;
        }

        if (args.length < 3) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(
                    true,
                    "getPrestige",
                    "&7%ac%%player%&7's prestige level is %ac%%prestige%",
                    new MessageUtils.Replaceable<>("%player%", target.getName()),
                    new MessageUtils.Replaceable<>("%prestige%", targetPlayerData.getPrestige())
            ));
            return true;
        }

        String optionThree = args[2]; // get, set

        if (optionThree.equals("get")) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(
                    true,
                    "getPrestige",
                    "&7%ac%%player%&7's prestige level is %ac%%prestige%",
                    new MessageUtils.Replaceable<>("%player%", target.getName()),
                    new MessageUtils.Replaceable<>("%prestige%", targetPlayerData.getPrestige())
            ));
            return true;
        }

        if (optionThree.equals("set")) {
            if (args.length < 4) {
                throw new CommandUsageException("/cc prestige [player] set <level>");
            }

            String levelString = args[3]; // level
            int level = Integer.parseInt(levelString);

            if (level < 0) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(
                        false,
                        "negativeAmount",
                        "&cAmount cannot be negative!"
                ));
                return false;
            }

            targetPlayerData.setPrestige(level);
            for (AchievementType achievementType : AchievementType.getByCategory(AchievementCategory.PRESTIGE)) {
                targetPlayerData.setAchievementProgress(achievementType, level);
            }
            storage.save(targetPlayerData);
            sender.sendMessage(MessageUtils.getAndFormatMsg(
                    true,
                    "setPrestige",
                    "&7Successfully set %ac%%player%&7's prestige level to %ac%%amount%",
                    new MessageUtils.Replaceable<>("%player%", target.getName()),
                    new MessageUtils.Replaceable<>("%amount%", level)
            ));
            return true;
        }

        return false;
    }

    @Override
    public String getUsage() {
        return "/cc prestige [player] <get, set>";
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("cookieclickerz.managecookies");
    }
}
