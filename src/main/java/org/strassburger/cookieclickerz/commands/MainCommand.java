package org.strassburger.cookieclickerz.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.strassburger.cookieclickerz.CookieClickerZ;
import org.strassburger.cookieclickerz.util.ClickerManager;
import org.strassburger.cookieclickerz.util.MessageUtils;
import org.strassburger.cookieclickerz.util.NumFormatter;
import org.strassburger.cookieclickerz.util.RandomGenerators;
import org.strassburger.cookieclickerz.util.storage.PlayerData;
import org.strassburger.cookieclickerz.util.storage.Storage;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class MainCommand implements CommandExecutor, TabCompleter {
    private final CookieClickerZ plugin;

    public MainCommand(CookieClickerZ plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (args.length == 0) return handleVersionCommand(sender);

        switch (args[0]) {
            case "reload":
                return handleReload(sender);
            case "cookies":
                return handleCookies(sender, args);
            case "prestige":
                return handlePrestige(sender, args);
            case "clicker":
                return handleClicker(sender, args);
            case "top":
                List<PlayerData> topPlayers = plugin.getStorage().getAllPlayers().stream().sorted(Comparator.comparing(PlayerData::getTotalCookies).reversed()).collect(Collectors.toList());
                sender.sendMessage(MessageUtils.getAndFormatMsg(false, "topPlayers", "&7Top 10 players:"));
                for (int i = 0; i < 10 && i < topPlayers.size(); i++) {
                    PlayerData playerData = topPlayers.get(i);
                    sender.sendMessage(MessageUtils.getAndFormatMsg(false, "topPlayer", "&7%ac%%position%. %ac%%player% - %ac%%cookies%", new MessageUtils.Replaceable<>("%position%", i + 1), new MessageUtils.Replaceable<>("%player%", playerData.getName()), new MessageUtils.Replaceable<>("%cookies%", NumFormatter.formatBigInt(playerData.getTotalCookies()))));
                }
                return true;
            case "dev":
                return handleDev(sender, args);
            default:
                return false;
        }
    }

    private boolean handleVersionCommand(@NotNull CommandSender sender) {
        String version = plugin.getDescription().getVersion();
        sender.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.versionMsg", "FALLBACK&7You are using version %version%", new MessageUtils.Replaceable<>("%version%", version)));
        return true;
    }

    private boolean handleReload(@NotNull CommandSender sender) {
        if (!sender.hasPermission("cookieclickerz.admin.reload")) {
            throwPermissionError(sender);
            return false;
        }

        plugin.reloadConfig();
        plugin.getLanguageManager().reload();
        sender.sendMessage(MessageUtils.getAndFormatMsg(true, "reloadMsg", "&7Successfully reloaded the plugin!"));
        return true;
    }

    private boolean handleCookies(@NotNull CommandSender sender, String[] args) {
        Storage storage = plugin.getStorage();

        if (!sender.hasPermission("cookieclickerz.managecookies")) {
            throwPermissionError(sender);
            return false;
        }

        if (args.length < 4) {
            throwUsageError(sender, "/cc cookies [player] <add, remove, set> [amount]");
            return false;
        }

        String optionTwo = args[1]; // player
        String optionThree = args[2]; // add, remove, set
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

        Player target = plugin.getServer().getPlayer(optionTwo);

        if (target == null) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "playerNotFound", "&cPlayer not found!"));
            return false;
        }

        PlayerData targetPlayerData = storage.load(target.getUniqueId());
        if (targetPlayerData == null) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "playerDataStorageNull", "&cPlayerDataStorage is null!"));
            return false;
        }

        switch (optionThree) {
            case "add":
                targetPlayerData.setTotalCookies(targetPlayerData.getTotalCookies().add(amountNum));
                storage.save(targetPlayerData);
                sender.sendMessage(MessageUtils.getAndFormatMsg(true, "addCookies", "&7Successfully added %ac%%amount% &7cookies to %ac%%player%", new MessageUtils.Replaceable<>("%amount%", NumFormatter.formatBigInt(amountNum)), new MessageUtils.Replaceable<>("%player%", target.getName())));
                return true;
            case "remove":
                targetPlayerData.setTotalCookies(targetPlayerData.getTotalCookies().subtract(amountNum));
                storage.save(targetPlayerData);
                sender.sendMessage(MessageUtils.getAndFormatMsg(true, "removeCookies", "&7Successfully removed %ac%%amount% &7cookies from %ac%%player%", new MessageUtils.Replaceable<>("%amount%", NumFormatter.formatBigInt(amountNum)), new MessageUtils.Replaceable<>("%player%", target.getName())));
                return true;
            case "set":
                targetPlayerData.setTotalCookies(amountNum);
                storage.save(targetPlayerData);
                sender.sendMessage(MessageUtils.getAndFormatMsg(true, "setCookies", "&7Successfully set %ac%%player%&7's cookies to %ac%%amount%", new MessageUtils.Replaceable<>("%amount%", NumFormatter.formatBigInt(amountNum)), new MessageUtils.Replaceable<>("%player%", target.getName())));
                return true;
        }

        return false;
    }

    private boolean handlePrestige(@NotNull CommandSender sender, String[] args) {
        Storage storage = plugin.getStorage();

        if (!sender.hasPermission("cookieclickerz.managecookies")) {
            throwPermissionError(sender);
            return false;
        }

        if (args.length < 2) {
            throwUsageError(sender, "/cc prestige [player] <get, set>");
            return false;
        }

        String optionTwo = args[1]; // player

        Player target = plugin.getServer().getPlayer(optionTwo);

        if (target == null) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "playerNotFound", "&cPlayer not found!"));
            return false;
        }

        PlayerData targetPlayerData = storage.load(target.getUniqueId());
        if (targetPlayerData == null) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "playerDataStorageNull", "&cPlayerDataStorage is null!"));
            return false;
        }

        if (args.length < 3) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(true, "getPrestige", "&7%ac%%player%&7's prestige level is %ac%%prestige%", new MessageUtils.Replaceable<>("%player%", target.getName()), new MessageUtils.Replaceable<>("%prestige%", targetPlayerData.getPrestige())));
            return true;
        }

        String optionThree = args[2]; // get, set

        if (optionThree.equals("get")) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(true, "getPrestige", "&7%ac%%player%&7's prestige level is %ac%%prestige%", new MessageUtils.Replaceable<>("%player%", target.getName()), new MessageUtils.Replaceable<>("%prestige%", targetPlayerData.getPrestige())));
            return true;
        }

        if (optionThree.equals("set")) {
            if (args.length < 4) {
                throwUsageError(sender, "/cc prestige [player] set <level>");
                return false;
            }

            String levelString = args[3]; // level
            int level = Integer.parseInt(levelString);

            if (level < 0) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(false, "negativeAmount", "&cAmount cannot be negative!"));
                return false;
            }

            targetPlayerData.setPrestige(level);
            storage.save(targetPlayerData);
            sender.sendMessage(MessageUtils.getAndFormatMsg(true, "setPrestige", "&7Successfully set %ac%%player%&7's prestige level to %ac%%amount%", new MessageUtils.Replaceable<>("%player%", target.getName()), new MessageUtils.Replaceable<>("%amount%", level)));
            return true;
        }

        return false;
    }

    private boolean handleClicker(@NotNull CommandSender sender, String[] args) {
        if (!sender.hasPermission("cookieclickerz.manageclickers")) {
            throwPermissionError(sender);
            return false;
        }

        if (args.length < 2) {
            throwUsageError(sender, "/cc clicker <add, remove>");
            return false;
        }

        String optionTwo = args[1]; // add or remove

        if (optionTwo == null || (!optionTwo.equals("add") && !optionTwo.equals("remove")) && !optionTwo.equals("list")) {
            throwUsageError(sender, "/cc clicker <add, list, remove>");
            return false;
        }

        if (optionTwo.equals("add")) {
            if (!(sender instanceof Player)) return false;
            Player player = (Player) sender;

            if (args.length < 3) {
                throwUsageError(sender, "/cc clicker add <name>");
                return false;
            }

            String optionThree = args[2]; // clicker name

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
                            new MessageUtils.Replaceable<>("%name%", optionThree),
                            new MessageUtils.Replaceable<>("%location%", CookieClickerZ.locationToString(targetBlock))
                    )
            );
            return true;
        }

        if (optionTwo.equals("remove")) {
            if (args.length < 3) {
                throwUsageError(sender, "/cc clicker remove <block>");
                return false;
            }

            String optionThree = args[2]; // clicker name

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
                            new MessageUtils.Replaceable<>("%name%", optionThree)
                    )
            );
            return true;
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
                            new MessageUtils.Replaceable<>("%clickers%", formatList(clickers))
                    )
            );
            return true;
        }

        return false;
    }

    private boolean handleDev(@NotNull CommandSender sender, String[] args) {
        if (!sender.hasPermission("cookieclickerz.dev")) {
            throwPermissionError(sender);
            return false;
        }

        if (args.length < 2) {
            throwUsageError(sender, "/cc dev <test | addMockData>");
            return false;
        }

        String optionTwo = args[1]; // command

        if (optionTwo == null) {
            throwUsageError(sender, "/cc dev <test | addMockData>");
            return false;
        }

        if (optionTwo.equals("test")) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(true, "devTest", "&7Test successful!"));
            return true;
        }

        if (optionTwo.equals("addMockData")) {
            if (args.length < 3) {
                throwUsageError(sender, "/cc dev addMockData <amount>");
                return false;
            }

            String amountString = args[2]; // amount
            int amount = Integer.parseInt(amountString);

            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    for (int i = 0; i < amount; i++) {
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            player.sendActionBar(MessageUtils.formatMsg("&7Adding mock player &e" + (i + 1) + "/" + amount + "&7..."));
                        }

                        String name = RandomGenerators.generateRandomWord(8);
                        BigInteger totalCookies = RandomGenerators.generateRandomBigInteger(BigInteger.ZERO, Objects.requireNonNull(NumFormatter.stringToBigInteger("1Q")));
                        int prestige = RandomGenerators.generateRandomNumber(0, 5);

                        PlayerData playerData = new PlayerData(name, plugin.getServer().getOfflinePlayer(name).getUniqueId());
                        playerData.setTotalCookies(totalCookies);
                        playerData.setPrestige(prestige);
                        playerData.setLastLogoutTime(System.currentTimeMillis() - RandomGenerators.generateRandomLong(0, 1000000));
                        playerData.setTotalClicks(RandomGenerators.generateRandomNumber(0, 1000));
                        playerData.setCookiesPerClick(RandomGenerators.generateRandomBigInteger(BigInteger.ONE, Objects.requireNonNull(NumFormatter.stringToBigInteger("100K"))));
                        playerData.setOfflineCookies(RandomGenerators.generateRandomBigInteger(BigInteger.ZERO, Objects.requireNonNull(NumFormatter.stringToBigInteger("100K"))));

                        plugin.getStorage().save(playerData);
                    }

                    if (sender instanceof Player) {
                        ((Player) sender).playSound(((Player) sender).getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    }
                    sender.sendMessage(MessageUtils.formatMsg("&8[&a<b>!<!b>&8] &7Successfully added &e" + amount + " &7mock players!"));
                }
            };

            runnable.runTaskAsynchronously(plugin);

            return true;
        }

        return false;
    }

    private void throwUsageError(@NotNull CommandSender sender, String usage) {
        Component msg = MessageUtils.getAndFormatMsg(false, "usageError", "&cUsage: %usage%", new MessageUtils.Replaceable<>("%usage%", usage));
        sender.sendMessage(msg);
    }

    private void throwPermissionError(@NotNull CommandSender sender) {
        Component msg = MessageUtils.getAndFormatMsg(false, "noPermissionError", "&cYou don't have permission to use this!");
        sender.sendMessage(msg);
    }

    private @Nullable Location getTargetBlockLocation(Player player, int maxDistance) {
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

    private static @NotNull String formatList(@NotNull List<String> items) {
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
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String @NotNull [] args) {
        if (args.length == 1) {
            ArrayList<String> returnlist = new ArrayList<>();
            returnlist.add("help");
            if (sender.hasPermission("cookieclickerz.reload")) returnlist.add("reload");
            if (sender.hasPermission("cookieclickerz.manageclickers")) returnlist.add("clicker");
            if (sender.hasPermission("cookieclickerz.managecookies")) returnlist.add("cookies");
            if (sender.hasPermission("cookieclickerz.managecookies")) returnlist.add("prestige");
            return returnlist;
        }

        if (args.length == 2) {
            if (args[0].equals("clicker")) return List.of("add", "remove", "list");
            if (args[0].equals("cookies") || args[0].equals("prestige")) return null;
            if (args[0].equals("dev")) return List.of("test", "addMockData");
        }

        if (args.length == 3) {
            if (args[0].equals("clicker") && args[1].equals("add")) return List.of("name");
            if (args[0].equals("clicker") && args[1].equals("remove")) return ClickerManager.getClickers();
            if (args[0].equals("cookies")) return List.of("add", "remove", "set");
            if (args[0].equals("prestige")) return List.of("get", "set");
            if (args[0].equals("dev") && args[1].equals("addMockData")) return List.of("1", "2", "3", "4", "5");
        }

        if (args.length == 4) {
            if (args[0].equals("cookies")) return List.of("100", "1K", "1M", "1B", "1T", "1Q", "1QQ", "1S", "1SS", "1O", "1N", "1D");
            if (args[0].equals("prestige") && args[2].equals("set")) return List.of("0", "1", "2", "3", "4", "5");
        }

        return List.of();
    }
}
