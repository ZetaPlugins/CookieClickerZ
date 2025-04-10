package org.strassburger.cookieclickerz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.strassburger.cookieclickerz.util.gui.*;

public class InventoryCloseListener implements Listener {
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        MainGUI.close(player);
        UpgradeGUI.close(player);
        PrestigeGUI.close(player);
        TopGUI.close(player);
        AchievementGUI.close(player);
    }
}
