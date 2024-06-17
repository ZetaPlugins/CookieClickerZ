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
import org.strassburger.cookieclickerz.util.ClickerManager;
import org.strassburger.cookieclickerz.util.ConfigManager;
import org.strassburger.cookieclickerz.util.MessageUtils;
import org.strassburger.cookieclickerz.util.Replaceable;
import org.strassburger.cookieclickerz.util.storage.PlayerDataStorage;

import java.util.ArrayList;
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
                throwUsageError(sender, "/cc clicker <add, remove>");
                return false;
            }

            optionTwo = args[1]; // add or remove

            if (optionTwo == null || (!optionTwo.equals("add") && !optionTwo.equals("remove")) && !optionTwo.equals("list")) {
                throwUsageError(sender, "/cc clicker <add, remove>");
                return false;
            }

            if (optionTwo.equals("add")) {
                if (!(sender instanceof Player)) return false;
                Player player = (Player) sender;

                if (args.length < 3) {
                    throwUsageError(sender, "/cc clicker add <name>");
                    return false;
                }

                optionThree = args[2]; // clicker name

                Location targetBlock = getTargetBlockLocation(player, 5);

                if (targetBlock == null) {
                    sender.sendMessage(MessageUtils.getAndFormatMsg(false, "notLookingAtBlock", "&cYou need to be looking at a block!"));
                    return false;
                }

                if (ClickerManager.isClicker(targetBlock)) {
                    sender.sendMessage(MessageUtils.getAndFormatMsg(false, "alreadyClicker", "&cThis block is already a clicker!"));
                    return false;
                }

                if (ClickerManager.isClicker(optionThree)) {
                    sender.sendMessage(MessageUtils.getAndFormatMsg(false, "alreadyClickerName", "&cThis clicker name is already in use!"));
                    return false;
                }

                ClickerManager.addClicker(targetBlock, optionThree);

                sender.sendMessage(
                        MessageUtils.getAndFormatMsg(
                                true,
                                "clickerAddConfirm",
                                "&7Successfully added a clicker with the name %ac%%name% &7at the location %ac%%location%",
                                new Replaceable("%name%", optionThree),
                                new Replaceable("%location%", CookieClickerZ.locationToString(targetBlock))
                        )
                );
                return false;
            }

            if (optionTwo.equals("remove")) {
                if (args.length < 3) {
                    throwUsageError(sender, "/cc clicker remove <block>");
                    return false;
                }

                optionThree = args[2]; // clicker name

                if (!ClickerManager.isClicker(optionThree)) {
                    sender.sendMessage(MessageUtils.getAndFormatMsg(false, "notClicker", "&cThis block is not a clicker!"));
                    return false;
                }

                ClickerManager.removeClicker(optionThree);

                sender.sendMessage(
                        MessageUtils.getAndFormatMsg(
                                true,
                                "clickerRemoveConfirm",
                                "&7Successfully removed the clicker with the name %ac%%name%",
                                new Replaceable("%name%", optionThree)
                        )
                );
                return false;
            }

            if (optionTwo.equals("list")) {
                List<String> clickers = ClickerManager.getClickers();

                if (clickers == null || clickers.isEmpty()) {
                    sender.sendMessage(MessageUtils.getAndFormatMsg(false, "noClickers", "&cThere are no clickers!"));
                    return false;
                }

                sender.sendMessage(
                        MessageUtils.getAndFormatMsg(
                                true,
                                "clickerList",
                                "&7Clickers: %ac%%clickers%",
                                new Replaceable("%clickers%", formatList(clickers))
                        )
                );
                return false;
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

    private static String formatList(List<String> items) {
        StringBuilder formattedString = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            formattedString.append(items.get(i));
            if (i < items.size() - 1) {
                formattedString.append("&7, ").append(MessageUtils.getAccentColor());
            }
        }
        return formattedString.toString();
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            ArrayList<String> returnlist = new ArrayList<>();
            returnlist.add("help");
            returnlist.add("reload");
            if (sender.hasPermission("cookieclicker.manageclickers")) returnlist.add("clicker");
            return returnlist;
        }

        if (args.length == 2) {
            if (args[0].equals("clicker")) return List.of("add", "remove", "list");
        }

        if (args.length == 3) {
            if (args[0].equals("clicker") && args[1].equals("add")) return List.of("name");
            if (args[0].equals("clicker") && args[1].equals("remove")) return ClickerManager.getClickers();
        }

        return List.of();
    }
}
