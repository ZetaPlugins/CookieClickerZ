package org.strassburger.cookieclickerz.listeners.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class AchievementsGuiClickListener implements Listener {
    @EventHandler
    public void onClickEvent(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
