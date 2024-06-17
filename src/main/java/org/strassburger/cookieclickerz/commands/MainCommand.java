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
import org.strassburger.cookieclickerz.util.MessageUtils;
import org.strassburger.cookieclickerz.util.NumFormatter;
import org.strassburger.cookieclickerz.util.Replaceable;
import org.strassburger.cookieclickerz.util.storage.PlayerData;
import org.strassburger.cookieclickerz.util.storage.PlayerDataStorage;

import java.math.BigInteger;
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
            if (!sender.hasPermission("cookieclickerz.admin.reload")) {
                throwPermissionError(sender);
                return false;
            }

            CookieClickerZ.getInstance().reloadConfig();
            config = CookieClickerZ.getInstance().getConfig();
            CookieClickerZ.getInstance().getLanguageManager().reload();
            CookieClickerZ.getInstance().getConfigManager().reloadCustomConfig("clicker.yml");
            sender.sendMessage(MessageUtils.getAndFormatMsg(true, "reloadMsg", "&7Successfully reloaded the plugin!"));
            return true;
        }

        if (optionOne.equals("cookies")) {
            if (!sender.hasPermission("cookieclickerz.managecookies")) {
                throwPermissionError(sender);
                return false;
            }

            if (args.length < 4) {
                throwUsageError(sender, "/cc cookies [player] <add, remove, set> [amount]");
                return false;
            }

            optionTwo = args[1]; // player
            optionThree = args[2]; // add, remove, set
            String amount = args[3]; // amount

            BigInteger amountNum = NumFormatter.stringToBigInteger(amount);

            if (amountNum == null) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(false, "invalidAmount", "&cInvalid amount!"));
                return false;
            }

            if (amountNum.compareTo(BigInteger.ZERO) < 0) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(false, "negativeAmount", "&cAmount cannot be negative!"));
                return false;
            }

            Player target = CookieClickerZ.getInstance().getServer().getPlayer(optionTwo);

            if (target == null) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(false, "playerNotFound", "&cPlayer not found!"));
                return false;
            }

            PlayerData targetPlayerData = playerDataStorage.load(target.getUniqueId());
            if (targetPlayerData == null) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(false, "playerDataStorageNull", "&cPlayerDataStorage is null!"));
                return false;
            }

            if (optionThree.equals("add")) {
                targetPlayerData.setTotalCookies(targetPlayerData.getTotalCookies().add(amountNum));
                playerDataStorage.save(targetPlayerData);
                sender.sendMessage(MessageUtils.getAndFormatMsg(true, "addCookies", "&7Successfully added %ac%%amount% &7cookies to %ac%%player%", new Replaceable("%amount%", NumFormatter.formatBigInt(amountNum)), new Replaceable("%player%", target.getName())));
                return false;
            }

            if (optionThree.equals("remove")) {
                targetPlayerData.setTotalCookies(targetPlayerData.getTotalCookies().subtract(amountNum));
                playerDataStorage.save(targetPlayerData);
                sender.sendMessage(MessageUtils.getAndFormatMsg(true, "removeCookies", "&7Successfully removed %ac%%amount% &7cookies from %ac%%player%", new Replaceable("%amount%", NumFormatter.formatBigInt(amountNum)), new Replaceable("%player%", target.getName())));
                return false;
            }

            if (optionThree.equals("set")) {
                targetPlayerData.setTotalCookies(amountNum);
                playerDataStorage.save(targetPlayerData);
                sender.sendMessage(MessageUtils.getAndFormatMsg(true, "setCookies", "&7Successfully set %ac%%player%&7's cookies to %ac%%amount%", new Replaceable("%amount%", NumFormatter.formatBigInt(amountNum)), new Replaceable("%player%", target.getName())));
                return false;
            }
        }

        if (optionOne.equals("cpc")) {
            if (!sender.hasPermission("cookieclickerz.managecookies")) {
                throwPermissionError(sender);
                return false;
            }

            if (args.length < 4) {
                throwUsageError(sender, "/cc cpc [player] <add, remove, set> [amount]");
                return false;
            }

            optionTwo = args[1]; // player
            optionThree = args[2]; // add, remove, set
            String amount = args[3]; // amount

            BigInteger amountNum = NumFormatter.stringToBigInteger(amount);

            if (amountNum == null) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(false, "invalidAmount", "&cInvalid amount!"));
                return false;
            }

            if (amountNum.compareTo(BigInteger.ZERO) < 0) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(false, "negativeAmount", "&cAmount cannot be negative!"));
                return false;
            }

            Player target = CookieClickerZ.getInstance().getServer().getPlayer(optionTwo);

            if (target == null) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(false, "playerNotFound", "&cPlayer not found!"));
                return false;
            }

            PlayerData targetPlayerData = playerDataStorage.load(target.getUniqueId());
            if (targetPlayerData == null) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(false, "playerDataStorageNull", "&cPlayerDataStorage is null!"));
                return false;
            }

            if (optionThree.equals("add")) {
                targetPlayerData.setCookiesPerClick(targetPlayerData.getCookiesPerClick().add(amountNum));
                playerDataStorage.save(targetPlayerData);
                sender.sendMessage(MessageUtils.getAndFormatMsg(true, "addCPC", "&7Successfully added %ac%%amount% &7cookies per click to %ac%%player%", new Replaceable("%amount%", NumFormatter.formatBigInt(amountNum)), new Replaceable("%player%", target.getName())));
                return false;
            }

            if (optionThree.equals("remove")) {
                targetPlayerData.setCookiesPerClick(targetPlayerData.getCookiesPerClick().subtract(amountNum));
                playerDataStorage.save(targetPlayerData);
                sender.sendMessage(MessageUtils.getAndFormatMsg(true, "removeCPC", "&7Successfully removed %ac%%amount% &7cookies per click from %ac%%player%", new Replaceable("%amount%", NumFormatter.formatBigInt(amountNum)), new Replaceable("%player%", target.getName())));
                return false;
            }

            if (optionThree.equals("set")) {
                targetPlayerData.setCookiesPerClick(amountNum);
                playerDataStorage.save(targetPlayerData);
                sender.sendMessage(MessageUtils.getAndFormatMsg(true, "setCPC", "&7Successfully set %ac%%player%&7's cookies per click to %ac%%amount%", new Replaceable("%amount%", NumFormatter.formatBigInt(amountNum)), new Replaceable("%player%", target.getName())));
                return false;
            }
        }

        if (optionOne.equals("clicker")) {
            if (!sender.hasPermission("cookieclickerz.manageclickers")) {
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
        Component msg = MessageUtils.getAndFormatMsg(false, "usageError", "&cUsage: %usage%", new Replaceable("%usage%", usage));
        sender.sendMessage(msg);
    }

    private void throwPermissionError(CommandSender sender) {
        Component msg = MessageUtils.getAndFormatMsg(false, "noPermissionError", "&cYou don't have permission to use this!");
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
            if (sender.hasPermission("cookieclickerz.manageclickers")) returnlist.add("clicker");
            if (sender.hasPermission("cookieclickerz.managecookies")) returnlist.add("cookies");
            if (sender.hasPermission("cookieclickerz.managecookies")) returnlist.add("cpc");
            return returnlist;
        }

        if (args.length == 2) {
            if (args[0].equals("clicker")) return List.of("add", "remove", "list");
            if (args[0].equals("cookies") || args[0].equals("cpc")) return null;
        }

        if (args.length == 3) {
            if (args[0].equals("clicker") && args[1].equals("add")) return List.of("name");
            if (args[0].equals("clicker") && args[1].equals("remove")) return ClickerManager.getClickers();
            if (args[0].equals("cookies") || args[0].equals("cpc")) return List.of("add", "remove", "set");
        }

        if (args.length == 4) {
            if (args[0].equals("cookies") || args[0].equals("cpc")) return List.of("100", "1K", "1M", "1B", "1T", "1Q", "1QQ", "1S", "1SS", "1O", "1N", "1D");
        }

        return List.of();
    }
}
