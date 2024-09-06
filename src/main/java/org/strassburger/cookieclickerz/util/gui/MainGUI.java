package org.strassburger.cookieclickerz.util.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.strassburger.cookieclickerz.CookieClickerZ;
import org.strassburger.cookieclickerz.util.*;
import org.strassburger.cookieclickerz.util.storage.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainGUI {
    private static final List<UUID> openInventories = new ArrayList<>();

    private MainGUI() {}

    public static boolean isOpen(Player player) {
        return openInventories.contains(player.getUniqueId());
    }

    public static void open(Player player) {
        final CookieClickerZ plugin = CookieClickerZ.getInstance();
        PlayerData playerData = plugin.getStorage().load(player.getUniqueId());

        Inventory inventory = Bukkit.createInventory(null, 5 * 9, MessageUtils.getAndFormatMsg(false, "inventories.main.title", "&8CookieClickerZ"));
        GuiAssets.addBorder(inventory, 5 * 9);

        inventory.setItem(11, new CustomItem(Material.GOLD_INGOT)
                .setName(MessageUtils.getAndFormatMsg(false, "inventories.main.shopTitle", "&e&lShop"))
                .setLore(List.of(
                        MessageUtils.getAndFormatMsg(false, "inventories.main.shopDescription", "&7Buy upgrades to increase your cookie production!"),
                        MessageUtils.formatMsg(" ")
                ))
                .getItemStack());
        inventory.setItem(15, new CustomItem(Material.EXPERIENCE_BOTTLE)
                .setName(MessageUtils.getAndFormatMsg(false, "inventories.main.boosterTitle", "<#9932cc>&lBoosters"))
                .setLore(List.of(
                        MessageUtils.getAndFormatMsg(false, "inventories.main.boosterDescription", "&7Use boosters to increase your cookie production!"),
                        MessageUtils.formatMsg(" ")
                ))
                .getItemStack());
        inventory.setItem(22, new CustomItem(Material.COOKIE)
                .setName(MessageUtils.getAndFormatMsg(false, "inventories.main.clickerTitle", "%ac%&lYour Cookies"))
                .setLore(MessageUtils.getAndFormatMsgList(
                        "inventories.main.clickerDescription",
                        new MessageUtils.Replaceable<>("%cookies%", NumFormatter.formatBigInt(playerData.getTotalCookies())),
                        new MessageUtils.Replaceable<>("%cpc%", NumFormatter.formatBigInt(playerData.getCookiesPerClick())),
                        new MessageUtils.Replaceable<>("%offlinecookies%", NumFormatter.formatBigInt(playerData.getOfflineCookies())),
                        new MessageUtils.Replaceable<>("%multiplier%", new PrestigeData(plugin, playerData.getPrestige()).getMultiplier())
                ))
                .getItemStack());
        inventory.setItem(29, new CustomItem(Material.FEATHER)
                .setName(MessageUtils.getAndFormatMsg(false, "inventories.main.prestigeTitle", "&6&lPrestige"))
                .setLore(List.of(
                        MessageUtils.getAndFormatMsg(false, "inventories.main.prestigeDescription", "&7Prestige massively increase your cookie production!"),
                        MessageUtils.formatMsg(" ")
                ))
                .getItemStack());
        inventory.setItem(33, new CustomItem(Material.BARRIER)
                .setName("&c&o&lComing soon")
                .getItemStack());

        player.openInventory(inventory);
        openInventories.add(player.getUniqueId());
    }

    public static void close(Player player) {
        if (isOpen(player)) openInventories.remove(player.getUniqueId());
    }
}
