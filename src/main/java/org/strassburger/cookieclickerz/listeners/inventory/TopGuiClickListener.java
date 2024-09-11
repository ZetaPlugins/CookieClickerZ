package org.strassburger.cookieclickerz.listeners.inventory;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.strassburger.cookieclickerz.CookieClickerZ;
import org.strassburger.cookieclickerz.util.gui.GuiAssets;
import org.strassburger.cookieclickerz.util.gui.MainGUI;
import org.strassburger.cookieclickerz.util.gui.TopGUI;
import org.strassburger.cookieclickerz.util.gui.UpgradeGUI;

public class TopGuiClickListener implements Listener {
    private final CookieClickerZ plugin;

    public TopGuiClickListener(CookieClickerZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!TopGUI.isOpen(player)) return;

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getItemMeta() == null) return;
        String ciType = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "citype"), PersistentDataType.STRING);
        if (ciType == null) return;

        if (ciType.equals("back")) {
            event.setCancelled(true);
            GuiAssets.playClickSound(player);
            TopGUI.close(player);
            MainGUI.open(player);
            return;
        }

        event.setCancelled(true);
    }
}