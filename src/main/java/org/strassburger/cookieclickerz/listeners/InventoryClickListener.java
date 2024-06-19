package org.strassburger.cookieclickerz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.strassburger.cookieclickerz.util.gui.GuiAssets;
import org.strassburger.cookieclickerz.util.gui.MainGUI;
import org.strassburger.cookieclickerz.util.gui.UpgradeGUI;

public class InventoryClickListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (MainGUI.isOpen(player)) {
            switch (event.getSlot()) {
                case 11: {
                    GuiAssets.playClickSound(player);
                    player.closeInventory();
                    UpgradeGUI.open(player);
                    break;
                }
            }
            event.setCancelled(true);
        }

        if (UpgradeGUI.isOpen(player)) {
            event.setCancelled(true);
        }
    }
}
