package org.strassburger.cookieclickerz.util;

import org.bukkit.event.Listener;
import org.strassburger.cookieclickerz.CookieClickerZ;
import org.strassburger.cookieclickerz.listeners.BlockBreakListener;
import org.strassburger.cookieclickerz.listeners.PlayerInteractionListener;
import org.strassburger.cookieclickerz.listeners.PlayerJoinListener;

public class EventManager {
    private static final CookieClickerZ plugin = CookieClickerZ.getInstance();

    private EventManager() {}

    /**
     * Registers all listeners
     */
    public static void registerListeners() {
        registerListener(new PlayerInteractionListener());
        registerListener(new PlayerJoinListener());
        registerListener(new BlockBreakListener());
    }

    /**
     * Registers a listener
     *
     * @param listener The listener to register
     */
    private static void registerListener(Listener listener) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }
}
