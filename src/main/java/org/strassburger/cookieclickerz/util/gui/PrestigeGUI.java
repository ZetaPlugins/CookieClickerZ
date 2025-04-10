package org.strassburger.cookieclickerz.util.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.strassburger.cookieclickerz.CookieClickerZ;
import org.strassburger.cookieclickerz.util.items.CustomItem;
import org.strassburger.cookieclickerz.util.MessageUtils;
import org.strassburger.cookieclickerz.storage.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PrestigeGUI {
    private static final List<UUID> openInventories = new ArrayList<>();
    FileConfiguration config = CookieClickerZ.getInstance().getConfig();

    private PrestigeGUI() {}

    public static boolean isOpen(Player player) {
        return openInventories.contains(player.getUniqueId());
    }

    public static void open(Player player) {
        PlayerData playerData = CookieClickerZ.getInstance().getStorage().load(player.getUniqueId());

        Inventory inventory = Bukkit.createInventory(null, 4 * 9, MessageUtils.getAndFormatMsg(false, "inventories.prestige.title", "&8Prestige"));
        GuiAssets.addBorder(inventory);
        GuiAssets.addBackButton(inventory);
        addGlass(inventory);

        inventory.setItem(13,
                new CustomItem(Material.FEATHER)
                        .setName(MessageUtils.getAndFormatMsg(false, "inventories.prestige.prestige", "&6&lPrestige"))
                        .setLore(List.of(
                                MessageUtils.getAndFormatMsg(false, "inventories.prestige.description", "&7Prestige to massively increase your cookie production!"),
                                MessageUtils.formatMsg(" ")
                        ))
                        .getItemStack()
        );

        inventory.setItem(20, GuiAssets.getPretsigeGlassItem(1, playerData));
        inventory.setItem(21, GuiAssets.getPretsigeGlassItem(2, playerData));
        inventory.setItem(22, GuiAssets.getPretsigeGlassItem(3, playerData));
        inventory.setItem(23, GuiAssets.getPretsigeGlassItem(4, playerData));
        inventory.setItem(24, GuiAssets.getPretsigeGlassItem(5, playerData));

        player.openInventory(inventory);
        openInventories.add(player.getUniqueId());
    }

    public static void close(Player player) {
        if (isOpen(player)) openInventories.remove(player.getUniqueId());
    }

    private static void addGlass(Inventory inventory) {
        inventory.setItem(10, GuiAssets.getGlassItem());
        inventory.setItem(11, GuiAssets.getGlassItem());
        inventory.setItem(12, GuiAssets.getGlassItem());
        inventory.setItem(14, GuiAssets.getGlassItem());
        inventory.setItem(15, GuiAssets.getGlassItem());
        inventory.setItem(16, GuiAssets.getGlassItem());
        inventory.setItem(19, GuiAssets.getGlassItem());
        inventory.setItem(25, GuiAssets.getGlassItem());
    }
}
