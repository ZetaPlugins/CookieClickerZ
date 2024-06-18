package org.strassburger.cookieclickerz.util.gui;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.strassburger.cookieclickerz.util.CustomItem;

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
}
