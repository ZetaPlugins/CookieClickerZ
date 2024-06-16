package org.strassburger.cookieclickerz.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.strassburger.cookieclickerz.CookieClickerZ;
import org.strassburger.cookieclickerz.util.MessageUtils;
import org.strassburger.cookieclickerz.util.Replaceable;
import org.strassburger.cookieclickerz.util.storage.PlayerDataStorage;

import java.util.List;

public class MainCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        FileConfiguration config = CookieClickerZ.getInstance().getConfig();
        PlayerDataStorage playerDataStorage = CookieClickerZ.getInstance().getPlayerDataStorage();

        if (args.length == 0) {
            String version = CookieClickerZ.getInstance().getDescription().getVersion();
            sender.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.versionMsg", "FALLBACK&7You are using version %version%", new Replaceable("%version%", version)));
            return false;
        }

        String optionOne = args[0];
        String optionTwo = null;
        String optionThree = null;

        if (optionOne.equals("reload")) {
            if (!sender.hasPermission("lifestealz.admin.reload")) {
                throwPermissionError(sender);
                return false;
            }

            CookieClickerZ.getInstance().reloadConfig();
            config = CookieClickerZ.getInstance().getConfig();
            CookieClickerZ.getInstance().getLanguageManager().reload();
            sender.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.reloadMsg", "&7Successfully reloaded the plugin!"));
            return true;
        }

        if (optionOne.equals("clicker")) {
            if (!sender.hasPermission("cookieclicker.manageclickers")) {
                throwPermissionError(sender);
                return false;
            }

            if (args.length < 2) {
                throwUsageError(sender, "/cc clicker <add, remove> [block]");
                return false;
            }

            optionTwo = args[1]; // add or remove

            if (optionTwo == null || (!optionTwo.equals("add") && !optionTwo.equals("remove"))) {
                throwUsageError(sender, "/cc clicker <add, remove> [block]");
                return false;
            }

            Location targetBlock = null;

            if (optionTwo.equals("add")) {
                if (!(sender instanceof Player)) return false;
                Player player = (Player) sender;

                targetBlock = getTargetBlockLocation(player, 5);

                sender.sendMessage("Added targetblock: " + CookieClickerZ.locationToString(targetBlock));
            }
        }

        return false;
    }

    private void throwUsageError(CommandSender sender, String usage) {
        Component msg = MessageUtils.getAndFormatMsg(false, "messages.usageError", "&cUsage: %usage%", new Replaceable("%usage%", usage));
        sender.sendMessage(msg);
    }

    private void throwPermissionError(CommandSender sender) {
        Component msg = MessageUtils.getAndFormatMsg(false, "messages.noPermissionError", "&cYou don't have permission to use this!");
        sender.sendMessage(msg);
    }

    private Location getTargetBlockLocation(Player player, int maxDistance) {
        BlockIterator blockIterator = new BlockIterator(player, maxDistance);
        Block lastBlock = blockIterator.next();
        while (blockIterator.hasNext()) {
            lastBlock = blockIterator.next();
            if (lastBlock.getType() != Material.AIR) {
                break;
            }
        }
        return lastBlock.getType() != Material.AIR ? lastBlock.getLocation() : null;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        return null;
    }
}
