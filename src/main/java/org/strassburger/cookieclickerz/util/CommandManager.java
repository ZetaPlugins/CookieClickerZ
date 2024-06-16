package org.strassburger.cookieclickerz.util;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.strassburger.cookieclickerz.CookieClickerZ;
import org.strassburger.cookieclickerz.commands.MainCommand;

public class CommandManager {
    private static final CookieClickerZ plugin = CookieClickerZ.getInstance();

    private CommandManager() {}

    /**
     * Registers all commands
     */
    public static void registerCommands() {
        registerCommand("cookieclicker", new MainCommand(), new MainCommand());
    }

    /**
     * Registers a command
     *
     * @param name The name of the command
     * @param executor The executor of the command
     * @param tabCompleter The tab completer of the command
     */
    private static void registerCommand(String name, CommandExecutor executor, TabCompleter tabCompleter) {
        PluginCommand command = plugin.getCommand(name);

        if (command != null) {
            command.setExecutor(executor);
            command.setTabCompleter(tabCompleter);
            command.permissionMessage(MessageUtils.getAndFormatMsg(false, "messages.noPermsError", "<red>You do not have permission to execute this command!"));
        }
    }
}
