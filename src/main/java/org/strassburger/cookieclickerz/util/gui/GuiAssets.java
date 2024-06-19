package org.strassburger.cookieclickerz.util.gui;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.strassburger.cookieclickerz.util.CustomItem;
import org.strassburger.cookieclickerz.util.MessageUtils;
import org.strassburger.cookieclickerz.util.NumFormatter;

import java.util.List;

public class GuiAssets {
    private GuiAssets() {}

    public static void addBorder(Inventory inventory, int inventorySize) {
        ItemStack glass = new CustomItem(Material.GRAY_STAINED_GLASS_PANE).setName("&r ").getItemStack();

        for (int i = 0; i < inventorySize; i++) {
            if (i < 9 || i >= inventorySize - 9 || i % 9 == 0 || i % 9 == 8) {
                inventory.setItem(i, glass);
            }
        }
    }

    public static void playClickSound(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1, 1);
    }

    public static ItemStack createUpgradeItem(UpgradeGUI.Upgrade upgrade) {
        return new CustomItem(Material.valueOf(upgrade.getItem()))
                .setName(upgrade.getName())
                .setLore(List.of(
                        MessageUtils.formatMsg("&7Cookies per Click: %ac%" + NumFormatter.formatBigInt(upgrade.getCpc())),
                        MessageUtils.formatMsg("&7Offline Cookies: %ac%" + NumFormatter.formatBigInt(upgrade.getOfflineCookies())),
                        MessageUtils.formatMsg("&r "),
                        MessageUtils.formatMsg("&7Base Price: &e" + NumFormatter.formatBigInt(upgrade.getBaseprice()))
                ))
                .addFlag(ItemFlag.HIDE_ATTRIBUTES)
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .getItemStack();
    }
}
