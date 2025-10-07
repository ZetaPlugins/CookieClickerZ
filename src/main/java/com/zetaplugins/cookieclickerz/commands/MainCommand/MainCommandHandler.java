package com.zetaplugins.cookieclickerz.commands.MainCommand;

import com.zetaplugins.cookieclickerz.commands.MainCommand.subcommands.*;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import com.zetaplugins.cookieclickerz.CookieClickerZ;
import com.zetaplugins.cookieclickerz.commands.CommandUsageException;
import com.zetaplugins.cookieclickerz.commands.MainCommand.subcommands.*;
import com.zetaplugins.cookieclickerz.commands.SubCommand;
import com.zetaplugins.cookieclickerz.util.MessageUtils;

import java.util.HashMap;
import java.util.Map;

public class MainCommandHandler implements CommandExecutor {
    private final CookieClickerZ plugin;
    private final Map<String, SubCommand> commands = new HashMap<>();

    public MainCommandHandler(CookieClickerZ plugin) {
        this.plugin = plugin;

        commands.put("help", new HelpSubCommand());
        commands.put("reload", new ReloadSubCommand(plugin));
        commands.put("cookies", new CookiesSubCommand(plugin));
        commands.put("prestige", new PrestigeSubCommand(plugin));
        commands.put("clicker", new ClickerSubCommand(plugin));
        commands.put("dev", new DevSubCommand(plugin));
        commands.put("events", new EventSubCommand(plugin));
        commands.put("achievements", new AchievementsSubCommand(plugin));
        commands.put("numbers", new NumbersSubCommand());
        commands.put("open", new OpenSubCommand());
        commands.put("giveupgrade", new GiveUpgradeSubCommand(plugin));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            sendVersionMessage(sender);
            return true;
        }

        SubCommand subCommand = commands.get(args[0]);

        if (subCommand == null) {
            sendVersionMessage(sender);
            return true;
        }

        if (!subCommand.hasPermission(sender)) {
            throwPermissionError(sender);
            return false;
        }

        try {
            return subCommand.execute(sender, args);
        } catch (CommandUsageException e) {
            throwUsageError(sender, e.getUsage());
            return false;
        }
    }

    private void sendVersionMessage(CommandSender sender) {
        String version = plugin.getDescription().getVersion();
        sender.sendMessage(MessageUtils.getAndFormatMsg(
                true,
                "messages.versionMsg",
                "FALLBACK&7You are using version %version%",
                new MessageUtils.Replaceable<>("%version%", version)
        ));
    }

    private void throwUsageError(@NotNull CommandSender sender, String usage) {
        Component msg = MessageUtils.getAndFormatMsg(
                false,
                "usageError",
                "&cUsage: %usage%",
                new MessageUtils.Replaceable<>("%usage%", usage)
        );
        sender.sendMessage(msg);
    }

    private void throwPermissionError(@NotNull CommandSender sender) {
        Component msg = MessageUtils.getAndFormatMsg(
                false,
                "noPermissionError",
                "&cYou don't have permission to use this!"
        );
        sender.sendMessage(msg);
    }
}
