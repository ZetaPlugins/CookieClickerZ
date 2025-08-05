package com.zetaplugins.cookieclickerz.commands.MainCommand.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.zetaplugins.cookieclickerz.CookieClickerZ;
import com.zetaplugins.cookieclickerz.commands.CommandUsageException;
import com.zetaplugins.cookieclickerz.commands.SubCommand;
import com.zetaplugins.cookieclickerz.util.MessageUtils;
import com.zetaplugins.cookieclickerz.util.NumFormatter;
import com.zetaplugins.cookieclickerz.storage.PlayerData;
import com.zetaplugins.cookieclickerz.storage.Storage;

import java.math.BigInteger;

public class CookiesSubCommand implements SubCommand {
    private final CookieClickerZ plugin;

    public CookiesSubCommand(CookieClickerZ plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandUsageException {
        Storage storage = plugin.getStorage();

        if (args.length < 4) {
            throw new CommandUsageException("/cc cookies [player] <add, remove, set> [amount]");
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

    @Override
    public String getUsage() {
        return "/cc cookies";
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("cookieclickerz.admin.managecookies");
    }
}
