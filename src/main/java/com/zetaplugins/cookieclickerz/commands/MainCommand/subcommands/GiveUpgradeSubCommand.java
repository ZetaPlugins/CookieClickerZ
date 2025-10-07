package com.zetaplugins.cookieclickerz.commands.MainCommand.subcommands;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import com.zetaplugins.cookieclickerz.CookieClickerZ;
import com.zetaplugins.cookieclickerz.commands.CommandUsageException;
import com.zetaplugins.cookieclickerz.commands.SubCommand;
import com.zetaplugins.cookieclickerz.util.MessageUtils;
import com.zetaplugins.cookieclickerz.util.NumFormatter;
import com.zetaplugins.cookieclickerz.storage.PlayerData;
import com.zetaplugins.cookieclickerz.storage.Storage;

import java.math.BigInteger;

public class GiveUpgradeSubCommand implements SubCommand {
    private final CookieClickerZ plugin;

    public GiveUpgradeSubCommand(CookieClickerZ plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandUsageException {
        Storage storage = plugin.getStorage();

        if (args.length < 4) {
            throw new CommandUsageException("/cc giveupgrade [player] [upgrade] [amount]");
        }

        String playerName = args[1];
        String upgradeId = args[2];
        String amountStr = args[3];

        Integer amount = null;
        try {
            amount = Integer.parseInt(amountStr);
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(
                    false,
                    "positiveAmount",
                    "&cInvalid amount! Must be a positive integer."
            ));
            return false;
        }

        if (amount <= 0) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(
                    false,
                    "positiveAmount",
                    "&cAmount must be a positive integer!"
            ));
            return false;
        }

        Player target = plugin.getServer().getPlayer(playerName);
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
                    "&cPlayer data not found! Player needs to rejoin!"
            ));
            return false;
        }

        FileConfiguration upgradesConfig = plugin.getConfigManager().getCustomConfig("upgrades");
        if (upgradesConfig == null || !upgradesConfig.contains(upgradeId)) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(
                    false,
                    "upgradeNotFound",
                    "&cUpgrade '%upgrade%' not found!",
                    new MessageUtils.Replaceable<>("%upgrade%", upgradeId)
            ));
            return false;
        }

        String upgradeName = upgradesConfig.getString(upgradeId + ".name", upgradeId);
        BigInteger upgradeCpc = new BigInteger(upgradesConfig.getString(upgradeId + ".cpc", "0"));
        BigInteger upgradeOfflineCookies = new BigInteger(upgradesConfig.getString(upgradeId + ".offlineCookies", "0"));

        BigInteger totalCpcToAdd = upgradeCpc.multiply(BigInteger.valueOf(amount));
        BigInteger totalOfflineCookiesToAdd = upgradeOfflineCookies.multiply(BigInteger.valueOf(amount));

        int currentLevel = targetPlayerData.getUpgradeLevel("upgrade_" + upgradeId);
        int newLevel = currentLevel + amount;

        targetPlayerData.setCookiesPerClick(targetPlayerData.getCookiesPerClick().add(totalCpcToAdd));
        targetPlayerData.setOfflineCookies(targetPlayerData.getOfflineCookies().add(totalOfflineCookiesToAdd));
        targetPlayerData.addUpgrade("upgrade_" + upgradeId, newLevel);

        storage.save(targetPlayerData);

        sender.sendMessage(MessageUtils.getAndFormatMsg(
                true,
                "giveUpgradeSuccess",
                "&7Successfully gave %ac%%amount% &7levels of %ac%%upgrade% &7to %ac%%player%&7! (%ac%+%cpc% &7CPC, %ac%+%offline% &7offline cookies/sec)",
                new MessageUtils.Replaceable<>("%amount%", String.valueOf(amount)),
                new MessageUtils.Replaceable<>("%upgrade%", upgradeName),
                new MessageUtils.Replaceable<>("%player%", target.getName()),
                new MessageUtils.Replaceable<>("%cpc%", NumFormatter.formatBigInt(totalCpcToAdd)),
                new MessageUtils.Replaceable<>("%offline%", NumFormatter.formatBigInt(totalOfflineCookiesToAdd))
        ));

        if (sender instanceof Player sendingPlayer) sendingPlayer.playSound(sendingPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

        if (!(sender instanceof Player) || !((Player) sender).getUniqueId().equals(target.getUniqueId())) {
            target.sendMessage(MessageUtils.getAndFormatMsg(
                    true,
                    "upgradeReceived",
                    "&7You received %ac%%amount% &7levels of %ac%%upgrade%&7! (%ac%+%cpc% &7CPC, %ac%+%offline% &7offline cookies/sec)",
                    new MessageUtils.Replaceable<>("%amount%", String.valueOf(amount)),
                    new MessageUtils.Replaceable<>("%upgrade%", upgradeName),
                    new MessageUtils.Replaceable<>("%cpc%", NumFormatter.formatBigInt(totalCpcToAdd)),
                    new MessageUtils.Replaceable<>("%offline%", NumFormatter.formatBigInt(totalOfflineCookiesToAdd))
            ));

            target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        }

        return true;
    }

    @Override
    public String getUsage() {
        return "/cc giveupgrade [player] [upgrade] [amount]";
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("cookieclickerz.admin.giveupgrade");
    }
}