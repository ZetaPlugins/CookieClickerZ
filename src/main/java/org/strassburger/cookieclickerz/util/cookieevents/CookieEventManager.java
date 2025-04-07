package org.strassburger.cookieclickerz.util.cookieevents;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.strassburger.cookieclickerz.CookieClickerZ;
import org.strassburger.cookieclickerz.util.MessageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CookieEventManager {
    private final CookieClickerZ plugin;
    private final List<CookieEvent> activeEvents = new ArrayList<>();

    public CookieEventManager(CookieClickerZ plugin) {
        this.plugin = plugin;
    }

    /**
     * Start an event for a player
     * @param player The player to start the event for
     * @param eventType The event type to start
     */
    public void startEvent(@NotNull Player player, @NotNull CookieEventType eventType) {
        CookieEvent event = new CookieEvent(eventType, player.getUniqueId());
        if (eventType.isInstant()) {
            event.runEvent(plugin);
        } else {
            event.runEvent(plugin);
            activeEvents.add(event);
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1, 1);
            player.sendMessage(MessageUtils.getAndFormatMsg(
                    true,
                    "eventMessages.start." + eventType.name(),
                    "%ac%&l" + eventType.name() + " &r&7event started!"
            ));
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                activeEvents.remove(event);
                player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1, 1);
                player.sendMessage(MessageUtils.getAndFormatMsg(
                        true,
                        "eventMessages.end." + eventType.name(),
                        "%ac%&l" + eventType.name() + " &r&7event ended!"
                ));
            }, eventType.getDuration());
        }
    }

    /**
     * Start a random event for a player
     * @param player The player to start the event for
     */
    public void startRandomEvent(@NotNull Player player) {
        CookieEventType eventType = CookieEventType.getRandom();
        if (eventType == null) return;
        startEvent(player, eventType);
    }

    /**
     * Get all active events for a player
     * @param player The player to get the events for
     * @return A list of all active events for the player
     */
    public List<CookieEvent> getEvents(@NotNull Player player) {
        return activeEvents.stream()
                .filter(event -> event.getUuid().equals(player.getUniqueId()))
                .collect(Collectors.toList());
    }

    /**
     * Check if a player has a specific event
     * @param player The player to check for the event
     * @param eventType The event type to check for
     * @return True if the player has the event, false otherwise
     */
    public boolean hasEvent(@NotNull Player player, @NotNull CookieEventType eventType) {
        return getEvents(player).stream().anyMatch(event -> event.getType() == eventType);
    }
}
