package org.strassburger.cookieclickerz.commands.MainCommand.subcommands;

import org.bukkit.command.CommandSender;
import org.strassburger.cookieclickerz.CookieClickerZ;
import org.strassburger.cookieclickerz.commands.SubCommand;
import org.strassburger.cookieclickerz.util.MessageUtils;

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
