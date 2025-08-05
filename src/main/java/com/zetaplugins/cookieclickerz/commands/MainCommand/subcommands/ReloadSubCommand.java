package com.zetaplugins.cookieclickerz.commands.MainCommand.subcommands;

import org.bukkit.command.CommandSender;
import com.zetaplugins.cookieclickerz.CookieClickerZ;
import com.zetaplugins.cookieclickerz.commands.SubCommand;
import com.zetaplugins.cookieclickerz.util.MessageUtils;

public class ReloadSubCommand implements SubCommand {
    private final CookieClickerZ plugin;

    public ReloadSubCommand(CookieClickerZ plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        plugin.reloadConfig();
        plugin.getLanguageManager().reload();
        sender.sendMessage(MessageUtils.getAndFormatMsg(
                true,
                "reloadMsg",
                "&7Successfully reloaded the plugin!"
        ));
        return true;
    }

    @Override
    public String getUsage() {
        return "/cc reload";
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("cookieclickerz.admin.reload");
    }
}
