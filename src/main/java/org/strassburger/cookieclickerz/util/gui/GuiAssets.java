package org.strassburger.cookieclickerz.util.gui;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.strassburger.cookieclickerz.CookieClickerZ;
import org.strassburger.cookieclickerz.util.*;
import org.strassburger.cookieclickerz.util.items.CustomItem;
import org.strassburger.cookieclickerz.util.storage.PlayerData;

import java.util.List;
import java.util.Objects;

public class GuiAssets {
    private GuiAssets() {}

    public static void addBorder(Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (i < 9 || i >= inventory.getSize() - 9 || i % 9 == 0 || i % 9 == 8) {
                inventory.setItem(i, getGlassItem());
            }
        }
    }

    public static ItemStack getGlassItem() {
        return new CustomItem(Material.GRAY_STAINED_GLASS_PANE).setName("&r ").getItemStack();
    }

    public static void fillInventoryWithGlass(Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, getGlassItem());
            }
        }
    }

    public static void addPagination(Inventory inventory, int page, boolean prevArr, boolean nextArr) {
        LanguageManager lm = CookieClickerZ.getInstance().getLanguageManager();
        ItemStack prev = new CustomItem(Material.ARROW)
                .setName(lm.getString("inventories.navigation.previous"))
                .setCustomDataContainer("citype", PersistentDataType.STRING, "prev")
                .setCustomDataContainer("openpage", PersistentDataType.INTEGER, page - 1)
                .getItemStack();
        ItemStack next = new CustomItem(Material.ARROW)
                .setName(lm.getString("inventories.navigation.next"))
                .setCustomDataContainer("citype", PersistentDataType.STRING, "next")
                .setCustomDataContainer("openpage", PersistentDataType.INTEGER, page + 1)
                .getItemStack();

        if (prevArr) inventory.setItem(inventory.getSize() - 7, prev);
        if (nextArr) inventory.setItem(inventory.getSize() - 3, next);
    }

    public static void playClickSound(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1, 1);
    }

    public static void addBackButton(Inventory inventory) {
        inventory.setItem(inventory.getSize() - 5,
                new CustomItem(Material.BARRIER)
                        .setName(CookieClickerZ.getInstance().getLanguageManager().getString("inventories.navigation.back"))
                        .setCustomDataContainer("citype", PersistentDataType.STRING, "back")
                        .getItemStack()
        );
    }

    public static boolean isBackButton(ItemStack itemStack) {
        return itemStack.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(CookieClickerZ.getInstance(), "citype"), PersistentDataType.STRING)
                && Objects.equals(itemStack.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(CookieClickerZ.getInstance(), "citype"), PersistentDataType.STRING), "back");
    }

    public static ItemStack createUpgradeItem(UpgradeGUI.Upgrade upgrade) {
        String affordable = upgrade.isAffordable() ? "&a" : "&c";
        String levelColor = upgrade.getLevel() <= 0 ? "&8" : "&e";
        return new CustomItem(Material.valueOf(upgrade.getItem()))
                .setName(upgrade.getName())
                .setLore(List.of(
                        MessageUtils.formatMsg("&7Cookies per Click: %ac%" + NumFormatter.formatBigInt(upgrade.getCpc())),
                        MessageUtils.formatMsg("&7Offline Cookies: %ac%" + NumFormatter.formatBigInt(upgrade.getOfflineCookies())),
                        MessageUtils.formatMsg("&r "),
                        MessageUtils.formatMsg("&7Price: " + affordable + NumFormatter.formatBigInt(upgrade.getUpgradePrice())),
                        MessageUtils.formatMsg(" "),
                        MessageUtils.formatMsg("&7Level: " + levelColor + upgrade.getLevel())
                ))
                .addFlag(ItemFlag.HIDE_ATTRIBUTES)
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .addFlag(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
                .addFlag(ItemFlag.HIDE_UNBREAKABLE)
                .addFlag(ItemFlag.HIDE_ARMOR_TRIM)
                .setCustomDataContainer("citype", PersistentDataType.STRING, "upgrade")
                .setCustomDataContainer("id", PersistentDataType.STRING, upgrade.getId())
                .getItemStack();
    }

    public static ItemStack getPretsigeGlassItem(int prestigeLevel , PlayerData playerData) {
        PrestigeData prestigeData = new PrestigeData(CookieClickerZ.getInstance(), prestigeLevel);

        if (playerData.getPrestige() + 1 == prestigeLevel) {
            return new CustomItem(Material.YELLOW_STAINED_GLASS_PANE)
                    .setName(prestigeData.getName())
                    .setLore(MessageUtils.getAndFormatMsgList(
                            "inventories.prestige.upgradeDescription.available",
                            new MessageUtils.Replaceable<>("%multiplier%", prestigeData.getMultiplier()),
                            new MessageUtils.Replaceable<>("%price%", NumFormatter.formatBigInt(prestigeData.getCost()))
                    ))
                    .getItemStack();
        } else if (playerData.getPrestige() >= prestigeLevel) {
            return new CustomItem(Material.LIME_STAINED_GLASS_PANE)
                    .setName(prestigeData.getName())
                    .setLore(MessageUtils.getAndFormatMsgList(
                            "inventories.prestige.upgradeDescription.bought",
                            new MessageUtils.Replaceable<>("%multiplier%", prestigeData.getMultiplier()),
                            new MessageUtils.Replaceable<>("%price%", NumFormatter.formatBigInt(prestigeData.getCost()))
                    ))
                    .getItemStack();
        } else {
            return new CustomItem(Material.RED_STAINED_GLASS_PANE)
                    .setName(prestigeData.getName())
                    .setLore(MessageUtils.getAndFormatMsgList(
                            "inventories.prestige.upgradeDescription.unavailable",
                            new MessageUtils.Replaceable<>("%multiplier%", prestigeData.getMultiplier()),
                            new MessageUtils.Replaceable<>("%price%", NumFormatter.formatBigInt(prestigeData.getCost()))
                    ))
                    .getItemStack();
        }
    }
}
