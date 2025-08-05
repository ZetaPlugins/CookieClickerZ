package com.zetaplugins.cookieclickerz.commands.MainCommand.subcommands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.zetaplugins.cookieclickerz.CookieClickerZ;
import com.zetaplugins.cookieclickerz.commands.CommandUsageException;
import com.zetaplugins.cookieclickerz.commands.SubCommand;
import com.zetaplugins.cookieclickerz.util.ClickerManager;
import com.zetaplugins.cookieclickerz.util.MessageUtils;

import java.util.List;

public class ClickerSubCommand implements SubCommand {
    private final CookieClickerZ plugin;

    public ClickerSubCommand(CookieClickerZ plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandUsageException {
        if (args.length < 2) {
            throw new CommandUsageException("/cc clicker <add, list, remove>");
        }

        String optionTwo = args[1]; // add or remove

        if (optionTwo == null || (!optionTwo.equals("add") && !optionTwo.equals("remove")) && !optionTwo.equals("list")) {
            throw new CommandUsageException("/cc clicker <add, list, remove>");
        }

        if (optionTwo.equals("add")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(
                        false,
                        "noConsoleError",
                        "&cYou need to be a player to execute this command!"
                ));
                return false;
            }

            Player player = (Player) sender;

            if (args.length < 3) {
                throw new CommandUsageException("/cc clicker add <name>");
            }

            String optionThree = args[2]; // clicker name

            Location targetBlock = getTargetBlockLocation(player, 5);

            if (targetBlock == null) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(
                        false,
                        "notLookingAtBlock",
                        "&cYou need to be looking at a block!"
                ));
                return false;
            }

            if (plugin.getClickerManager().isClicker(targetBlock)) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(
                        false,
                        "alreadyClicker",
                        "&cThis block is already a clicker!"
                ));
                return false;
            }

            if (plugin.getClickerManager().isClicker(optionThree)) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(
                        false,
                        "alreadyClickerName",
                        "&cThis clicker name is already in use!"
                ));
                return false;
            }

            plugin.getClickerManager().addClicker(targetBlock, optionThree);

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
                throw new CommandUsageException("/cc clicker remove <block>");
            }

            String optionThree = args[2]; // clicker name

            if (!plugin.getClickerManager().isClicker(optionThree)) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(
                        false,
                        "notClicker",
                        "&cThis block is not a clicker!"
                ));
                return false;
            }

            plugin.getClickerManager().removeClicker(optionThree);

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
            List<ClickerManager.Clicker> clickers = plugin.getClickerManager().getClickers();

            if (clickers.isEmpty()) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(
                        false,
                        "noClickers",
                        "&cThere are no clickers!"
                ));
                return false;
            }

            sender.sendMessage(
                    MessageUtils.getAndFormatMsg(
                            true,
                            "clickerList",
                            "&7Clickers: %ac%%clickers%",
                            new MessageUtils.Replaceable<>("%clickers%", formatList(clickers, sender.getName()))
                    )
            );
            return true;
        }

        return false;
    }

    @Override
    public String getUsage() {
        return "/cc clicker <add, list, remove>";
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("cookieclickerz.admin.manageclickers");
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

    private @NotNull String formatList(@NotNull List<ClickerManager.Clicker> items, String playerName) {
        String teleportToClicker = CookieClickerZ.getInstance().getConfig().getString("teleportToClicker", "Teleport to clicker %name%");
        return items.stream()
                .map(item -> {
                    String hoverText = teleportToClicker.replace("%name%", item.getName());

                    double x = item.getLocation().getX();
                    double y = item.getLocation().getY() + 1;
                    double z = item.getLocation().getZ();

                    String clickCommand = "<click:RUN_COMMAND:/tp " + playerName + " " + x + " " + y + " " + z + ">";
                    return MessageUtils.getAccentColor() +
                            "<u><hover:show_text:" + hoverText + ">" + clickCommand + item.getName() + "</click></hover></u>";

                })
                .reduce((s1, s2) -> s1 + "&7, " + s2)
                .orElse("");
    }
}
