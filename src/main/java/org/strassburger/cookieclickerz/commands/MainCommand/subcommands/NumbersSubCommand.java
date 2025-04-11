package org.strassburger.cookieclickerz.commands.MainCommand.subcommands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.strassburger.cookieclickerz.commands.SubCommand;
import org.strassburger.cookieclickerz.util.MessageUtils;

import java.util.List;

public class NumbersSubCommand implements SubCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        List<Component> msgs = MessageUtils.getAndFormatMsgList("numCheatSheet");
        for (Component msg : msgs) {
            sender.sendMessage(msg);
        }
        return true;
    }

    @Override
    public String getUsage() {
        return "/cc numbers";
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("cookieclickerz.numcheatsheet");
    }
}
