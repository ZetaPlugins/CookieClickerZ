package org.strassburger.cookieclickerz.util;

import org.bukkit.event.Listener;
import org.strassburger.cookieclickerz.CookieClickerZ;
import org.strassburger.cookieclickerz.listeners.*;
import org.strassburger.cookieclickerz.listeners.inventory.*;

public class EventManager {
    private final CookieClickerZ plugin;

    public EventManager(CookieClickerZ plugin) {
        this.plugin = plugin;
    }

    /**
     * Registers all listeners
     */
    public void registerListeners() {
        registerListener(new PlayerInteractionListener(plugin));
        registerListener(new PlayerJoinListener(plugin));
        registerListener(new PlayerQuitListener(plugin));
        registerListener(new BlockBreakListener(plugin));
        registerListener(new InventoryCloseListener());
        registerListener(new MainGuiClickListener(plugin));
        registerListener(new UpgradeGuiClickListener(plugin));
        registerListener(new PrestigeGuiClickListener(plugin));
        registerListener(new TopGuiClickListener(plugin));
    }

    /**
     * Registers a listener
     *
     * @param listener The listener to register
     */
    private void registerListener(Listener listener) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }
}
