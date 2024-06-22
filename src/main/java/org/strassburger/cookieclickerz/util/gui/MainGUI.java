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
    private static List<UUID> openInventories = new ArrayList<>();
    private static Inventory inventory = null;
    FileConfiguration config = CookieClickerZ.getInstance().getConfig();

    private MainGUI() {}

    public static boolean isOpen(Player player) {
        return openInventories.contains(player.getUniqueId());
    }

    public static void open(Player player) {
        PlayerData playerData = CookieClickerZ.getInstance().getPlayerDataStorage().load(player.getUniqueId());

        inventory = Bukkit.createInventory(null, 5 * 9, MessageUtils.getAndFormatMsg(false, "inventories.main.title", "&8CookieClickerZ"));
        GuiAssets.addBorder(inventory, 5 * 9);

        inventory.setItem(11, new CustomItem(Material.GOLD_INGOT)
                .setName(MessageUtils.getAndFormatMsg(false, "inventories.main.shopTitle", "&e&lShop"))
                .setLore(List.of(
//                        MessageUtils.formatMsg(" "),
                        MessageUtils.getAndFormatMsg(false, "inventories.main.shopDescription", "&7Buy upgrades to increase your cookie production!"),
                        MessageUtils.formatMsg(" ")
                ))
                .getItemStack());
        inventory.setItem(15, new CustomItem(Material.EXPERIENCE_BOTTLE)
                .setName(MessageUtils.getAndFormatMsg(false, "inventories.main.boosterTitle", "<#9932cc>&lBoosters"))
                .setLore(List.of(
//                        MessageUtils.formatMsg(" "),
                        MessageUtils.getAndFormatMsg(false, "inventories.main.boosterDescription", "&7Use boosters to increase your cookie production!"),
                        MessageUtils.formatMsg(" ")
                ))
                .getItemStack());
        inventory.setItem(22, new CustomItem(Material.COOKIE)
                .setName(MessageUtils.getAndFormatMsg(false, "inventories.main.clickerTitle", "%ac%&lYour Cookies"))
                .setLore(List.of(
                        MessageUtils.formatMsg(" "),
                        MessageUtils.getAndFormatMsg(false, "inventories.main.clickerDescription1", "&8>> %ac%%cookies% &7%cookieName%", new Replaceable("%cookies%", NumFormatter.formatBigInt(playerData.getTotalCookies()))),
                        MessageUtils.formatMsg(" "),
                        MessageUtils.getAndFormatMsg(false, "inventories.main.clickerDescription2",  "&8>> %ac%+%cpc% &7%cookieName% per click",  new Replaceable("%cpc%", NumFormatter.formatBigInt(playerData.getCookiesPerClick()))),
                        MessageUtils.getAndFormatMsg(false, "inventories.main.clickerDescription3",  "&8>> %ac%+%offlinecookies% &7%cookieName% offline",  new Replaceable("%offlinecookies%", NumFormatter.formatBigInt(playerData.getOfflineCookies()))),
                        MessageUtils.formatMsg(" ")
                ))
                .getItemStack());
        inventory.setItem(29, new CustomItem(Material.FEATHER)
                .setName(MessageUtils.getAndFormatMsg(false, "inventories.main.prestigeTitle", "&6&lPrestige"))
                .setLore(List.of(
//                        MessageUtils.formatMsg(" "),
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
