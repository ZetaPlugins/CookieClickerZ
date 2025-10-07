package com.zetaplugins.cookieclickerz.commands.MainCommand.subcommands;

import com.zetaplugins.cookieclickerz.commands.CommandUsageException;
import com.zetaplugins.cookieclickerz.commands.SubCommand;
import com.zetaplugins.cookieclickerz.util.MessageUtils;
import com.zetaplugins.cookieclickerz.util.gui.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenSubCommand implements SubCommand {

    public OpenSubCommand() {}

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandUsageException {
        if (args.length < 2) {
            throw new CommandUsageException("/cc open <main, upgrades, achievements, prestige, top>");
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(
                    MessageUtils.getAndFormatMsg(
                            false,
                            "noConsoleError",
                            "&cYou need to be a player to execute this command!"
                    )
            );
            return false;
        }

        Player player = (Player) sender;

        openMenu(player, args[1]);

        return true;
    }

    private void openMenu(Player player, String menu) {
        switch (menu.toLowerCase()) {
            case "main":
                MainGUI.open(player);
                break;
            case "upgrades":
                UpgradeGUI.open(player);
                break;
            case "achievements":
                AchievementGUI.open(player);
                break;
            case "prestige":
                PrestigeGUI.open(player);
                break;
            case "top":
                TopGUI.open(player);
                break;
            default:
                player.sendMessage(
                        MessageUtils.getAndFormatMsg(
                                false,
                                "invalidMenu",
                                "&cInvalid menu! Available menus: main, upgrades, achievements, prestige, top"
                        )
                );
                break;
        }
    }

    @Override
    public String getUsage() {
        return "/cc open <main, upgrades, achievements, prestige, top>";
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("cookieclickerz.openmenu");
    }
}
