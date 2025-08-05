package com.zetaplugins.cookieclickerz.listeners.inventory;

import com.zetaplugins.cookieclickerz.util.gui.*;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import com.zetaplugins.cookieclickerz.CookieClickerZ;
import com.zetaplugins.cookieclickerz.util.MessageUtils;
import com.zetaplugins.cookieclickerz.util.gui.*;

public class MainGuiClickListener implements Listener {
    private final CookieClickerZ plugin;

    public MainGuiClickListener(CookieClickerZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClickEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!MainGUI.isOpen(player)) return;

        switch (event.getSlot()) {
            case 11: {// Upgrades
                if (!player.hasPermission("cookieclickerz.upgrades")) {
                    throwPermissionError(player);
                    return;
                }
                GuiAssets.playClickSound(player);
                MainGUI.close(player);
                UpgradeGUI.open(player);
                break;
            }
            case 15: {// Achievements
                if (!player.hasPermission("cookieclickerz.viewachievements")) {
                    throwPermissionError(player);
                    return;
                }
                GuiAssets.playClickSound(player);
                MainGUI.close(player);
                AchievementGUI.open(player);
                break;
            }
            case 29: {// Prestige
                if (!player.hasPermission("cookieclickerz.prestige")) {
                    throwPermissionError(player);
                    return;
                }
                GuiAssets.playClickSound(player);
                MainGUI.close(player);
                PrestigeGUI.open(player);
                break;
            }
            case 33: {// Top
                if (!player.hasPermission("cookieclickerz.top")) {
                    throwPermissionError(player);
                    return;
                }
                GuiAssets.playClickSound(player);
                MainGUI.close(player);
                TopGUI.open(player);
                break;
            }
        }
        event.setCancelled(true);
    }

    private void throwPermissionError(Player player) {
        Component msg = MessageUtils.getAndFormatMsg(false, "noPermissionError", "&cYou don't have permission to use this!");
        player.sendMessage(msg);
    }
}
