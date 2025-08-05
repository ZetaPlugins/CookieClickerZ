package com.zetaplugins.cookieclickerz.listeners.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import com.zetaplugins.cookieclickerz.util.gui.AchievementGUI;

public class AchievementsGuiClickListener implements Listener {
    @EventHandler
    public void onClickEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!AchievementGUI.isOpen(player)) return;

        event.setCancelled(true);
    }
}
