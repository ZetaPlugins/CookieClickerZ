package org.strassburger.cookieclickerz.util;

import org.bukkit.event.Listener;
import org.strassburger.cookieclickerz.CookieClickerZ;
import org.strassburger.cookieclickerz.listeners.*;

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
        registerListener(new InventoryCloseListener());
        registerListener(new InventoryClickListener());
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
