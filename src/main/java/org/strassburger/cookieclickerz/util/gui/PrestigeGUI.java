package org.strassburger.cookieclickerz.util.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.strassburger.cookieclickerz.CookieClickerZ;
import org.strassburger.cookieclickerz.util.items.CustomItem;
import org.strassburger.cookieclickerz.util.MessageUtils;
import org.strassburger.cookieclickerz.storage.PlayerData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PrestigeGUI {
    private static final List<UUID> openInventories = new ArrayList<>();
    FileConfiguration config = CookieClickerZ.getInstance().getConfig();

    private PrestigeGUI() {}

    public static boolean isOpen(Player player) {
        return openInventories.contains(player.getUniqueId());
    }

    public static void open(Player player) {
        PlayerData playerData = CookieClickerZ.getInstance().getStorage().load(player.getUniqueId());

        if (playerData == null) {
            CookieClickerZ.getInstance().getLogger().warning("Player data not found for " + player.getName());
            return;
        }

        final int firstSlot = 9;
        final int rowSize = 9;
        final int maxRows = 3;
        final int maxSlots = rowSize * maxRows;

        FileConfiguration config = CookieClickerZ.getInstance().getConfigManager().getPrestigeConfig();
        ConfigurationSection levelsSection = config.getConfigurationSection("levels");

        if (levelsSection == null) {
            CookieClickerZ.getInstance().getLogger().warning("Prestige levels configuration section is missing!");
            return;
        }

        // Ensure levels are sorted numerically
        List<Integer> levelKeys = levelsSection.getKeys(false).stream()
                .map(Integer::parseInt)
                .sorted()
                .collect(Collectors.toList());

        final int rowsNeeded = Math.min(maxRows, (int) Math.ceil(levelKeys.size() / 9.0));

        // {rowsNeeded} for the levels + 2 for the header and footer * 9 for the slots in each row
        Inventory inventory = Bukkit.createInventory(null, (rowsNeeded + 2) * rowSize, MessageUtils.getAndFormatMsg(false, "inventories.prestige.title", "&8Prestige"));
        GuiAssets.addBorder(inventory);
        GuiAssets.addBackButton(inventory);
        addGlass(inventory);

        inventory.setItem(4,
                new CustomItem(Material.FEATHER)
                        .setName(MessageUtils.getAndFormatMsg(false, "inventories.prestige.prestige", "&6&lPrestige"))
                        .setLore(List.of(
                                MessageUtils.getAndFormatMsg(false, "inventories.prestige.description", "&7Prestige to massively increase your cookie production!"),
                                MessageUtils.formatMsg(" ")
                        ))
                        .getItemStack()
        );

        int index = 0;
        for (int level : levelKeys) {
            int slot = firstSlot + index;
            if (slot >= firstSlot + maxSlots) break;

            try {
                inventory.setItem(slot, GuiAssets.getPretsigeGlassItem(level, playerData));
            } catch (NumberFormatException e) {
                CookieClickerZ.getInstance().getLogger().warning("Invalid prestige level key: " + level);
            }

            index++;
        }

        // fill empty slots with glass
        for (int i = firstSlot + index; i < firstSlot + (rowsNeeded * rowSize); i++) {
            inventory.setItem(i, GuiAssets.getGlassItem());
        }

        player.openInventory(inventory);
        openInventories.add(player.getUniqueId());
    }

    public static void close(Player player) {
        if (isOpen(player)) openInventories.remove(player.getUniqueId());
    }

    private static void addGlass(Inventory inventory) {
        inventory.setItem(3, GuiAssets.getGlassItem());
        inventory.setItem(5, GuiAssets.getGlassItem());
    }
}
