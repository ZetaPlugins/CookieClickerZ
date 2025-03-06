package org.strassburger.cookieclickerz.commands.MainCommand.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.strassburger.cookieclickerz.CookieClickerZ;
import org.strassburger.cookieclickerz.commands.CommandUsageException;
import org.strassburger.cookieclickerz.commands.SubCommand;
import org.strassburger.cookieclickerz.util.MessageUtils;
import org.strassburger.cookieclickerz.util.cookieevents.CookieEvent;
import org.strassburger.cookieclickerz.util.cookieevents.CookieEventManager;
import org.strassburger.cookieclickerz.util.cookieevents.CookieEventType;

import java.util.List;
import java.util.stream.Collectors;

public class EventSubCommand implements SubCommand {
    private final CookieClickerZ plugin;

    public EventSubCommand(CookieClickerZ plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandUsageException {
        if (args.length < 3) {
            throw new CommandUsageException("/cc events [player] <start, get>");
        }

        String optionTwo = args[1]; // player
        String optionThree = args[2]; // start, stop

        Player target = plugin.getServer().getPlayer(optionTwo);

        if (target == null) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(
                    false,
                    "playerNotFound",
                    "&cPlayer not found!"
            ));
            return false;
        }

        CookieEventManager eventManager = plugin.getCookieEventManager();

        if (optionThree.equals("get")) {
            List<CookieEvent> events = eventManager.getEvents(target);

            if (events == null || events.isEmpty()) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(
                        false,
                        "noEvent",
                        "&cThere is no event for %ac%%player%",
                        new MessageUtils.Replaceable<>("%player%", target.getName())
                ));
                return false;
            }

            sender.sendMessage(MessageUtils.getAndFormatMsg(
                    true,
                    "getEvent",
                    "&7Events for %ac%%player%&7: %ac%%events%",
                    new MessageUtils.Replaceable<>("%player%", target.getName()),
                    new MessageUtils.Replaceable<>(
                            "%events%",
                            events.stream()
                                    .map(event -> event.getType().name())
                                    .collect(Collectors.joining(", "))
                    )
            ));

            return true;
        }

        if (optionThree.equals("start")) {
            if (args.length < 4) {
                throw new CommandUsageException("/cc events [player] start <event>");
            }

            String optionFour = args[3]; // event

            if (optionFour.equals("random")) {
                eventManager.startRandomEvent(target);
                sender.sendMessage(MessageUtils.getAndFormatMsg(
                        false,
                        "randomEvent",
                        "&7Random event started for %ac%%player%",
                        new MessageUtils.Replaceable<>("%player%", target.getName())
                ));
                return true;
            }

            CookieEventType eventType = CookieEventType.valueOf(optionFour);

            if (eventType == null) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(
                        false,
                        "invalidEvent",
                        "&cInvalid event!"
                ));
                return false;
            }

            eventManager.startEvent(target, eventType);
            sender.sendMessage(MessageUtils.getAndFormatMsg(
                    true,
                    "startEvent",
                    "&7Event %ac%%event% &7started for %ac%%player%",
                    new MessageUtils.Replaceable<>("%event%", eventType.name()),
                    new MessageUtils.Replaceable<>("%player%", target.getName())
            ));
            return true;
        }

        return false;
    }

    @Override
    public String getUsage() {
        return "/cc events [player] <start, get>";
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("cookieclickerz.manageevents");
    }
}
