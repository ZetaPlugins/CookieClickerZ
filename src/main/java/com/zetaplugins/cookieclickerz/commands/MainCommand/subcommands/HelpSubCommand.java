package com.zetaplugins.cookieclickerz.commands.MainCommand.subcommands;

import org.bukkit.command.CommandSender;
import com.zetaplugins.cookieclickerz.commands.SubCommand;
import com.zetaplugins.cookieclickerz.util.MessageUtils;

public class HelpSubCommand implements SubCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        sender.sendMessage(MessageUtils.getAndFormatMsg(
                false,
                "helpMsg",
                "\n%ac%<b><grey>></grey> CookieClickerZ</b>\n\n<gray>You can create a clicker block with <click:SUGGEST_COMMAND:/cc clicker add ><u>/cc clicker add</u></click>.\nGet cookies by left clicking on the block and open the menu with a right click.\n\n%ac%<u><click:open_url:'https://docs.zetaplugins.com/cookieclickerz'>Documentation</click></u>  %ac%<u><click:open_url:'https://strassburger.org/discord'>Support Discord</click></u>\n"
        ));
        return true;
    }

    @Override
    public String getUsage() {
        return "/cc help";
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return true;
    }
}
