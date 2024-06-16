package org.strassburger.cookieclickerz.util;

import org.bukkit.event.Listener;
import org.strassburger.cookieclickerz.CookieClickerZ;

public class EventManager {
    private static final CookieClickerZ plugin = CookieClickerZ.getInstance();

    private EventManager() {}

    /**
     * Registers all listeners
     */
    public static void registerListeners() {
//        registerListener(new PlayerJoinListener());
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
