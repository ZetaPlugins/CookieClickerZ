package org.strassburger.cookieclickerz.commands.MainCommand.subcommands;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.strassburger.cookieclickerz.CookieClickerZ;
import org.strassburger.cookieclickerz.commands.CommandUsageException;
import org.strassburger.cookieclickerz.commands.SubCommand;
import org.strassburger.cookieclickerz.util.MessageUtils;
import org.strassburger.cookieclickerz.util.NumFormatter;
import org.strassburger.cookieclickerz.util.RandomGenerators;
import org.strassburger.cookieclickerz.storage.PlayerData;

import java.math.BigInteger;
import java.util.Objects;

public class DevSubCommand implements SubCommand {
    private final CookieClickerZ plugin;

    public DevSubCommand(CookieClickerZ plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandUsageException {
        if (args.length < 2) {
            throw new CommandUsageException("/cc dev <test | addMockData>");
        }

        String optionTwo = args[1]; // command

        if (optionTwo == null) {
            throw new CommandUsageException("/cc dev <test | addMockData>");
        }

        if (optionTwo.equals("test")) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(
                    true,
                    "devTest",
                    "&7Test successful!"
            ));
            return true;
        }

        if (optionTwo.equals("addMockData")) {
            if (args.length < 3) {
                throw new CommandUsageException("/cc dev addMockData <amount>");
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

    @Override
    public String getUsage() {
        return "/cc dev <test | addMockData>";
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("cookieclickerz.dev");
    }
}
