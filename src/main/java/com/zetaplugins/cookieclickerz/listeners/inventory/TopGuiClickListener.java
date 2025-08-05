package com.zetaplugins.cookieclickerz.listeners.inventory;

import com.zetaplugins.cookieclickerz.util.gui.GuiAssets;
import com.zetaplugins.cookieclickerz.util.gui.MainGUI;
import com.zetaplugins.cookieclickerz.util.gui.PrestigeGUI;
import com.zetaplugins.cookieclickerz.util.gui.TopGUI;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import com.zetaplugins.cookieclickerz.CookieClickerZ;
import com.zetaplugins.cookieclickerz.util.gui.*;

public class TopGuiClickListener implements Listener {
    private final CookieClickerZ plugin;

    public TopGuiClickListener(CookieClickerZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!TopGUI.isOpen(player)) return;

        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getItemMeta() == null) return;
        String ciType = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "citype"), PersistentDataType.STRING);
        if (ciType == null) return;

        if (ciType.equals("back")) {
            event.setCancelled(true);
            GuiAssets.playClickSound(player);
            PrestigeGUI.close(player);
            MainGUI.open(player);
        }
    }
}