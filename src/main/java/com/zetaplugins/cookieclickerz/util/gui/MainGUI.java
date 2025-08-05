package com.zetaplugins.cookieclickerz.util.gui;

import com.zetaplugins.cookieclickerz.util.MessageUtils;
import com.zetaplugins.cookieclickerz.util.NumFormatter;
import com.zetaplugins.cookieclickerz.util.PrestigeData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import com.zetaplugins.cookieclickerz.CookieClickerZ;
import com.zetaplugins.cookieclickerz.util.*;
import com.zetaplugins.cookieclickerz.util.items.CustomItem;
import com.zetaplugins.cookieclickerz.storage.PlayerData;

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

        Material mainItem = Material.valueOf(plugin.getConfig().getString("mainItem", "COOKIE"));

        Inventory inventory = Bukkit.createInventory(null, 5 * 9, MessageUtils.getAndFormatMsg(false, "inventories.main.title", "&8CookieClickerZ"));
        GuiAssets.addBorder(inventory);

        inventory.setItem(11, new CustomItem(Material.GOLD_INGOT)
                .setName(MessageUtils.getAndFormatMsg(false, "inventories.main.shopTitle", "&e&lShop"))
                .setLore(MessageUtils.getAndFormatMsgList("inventories.main.shopDescription"))
                .getItemStack());
        inventory.setItem(15, new CustomItem(Material.EXPERIENCE_BOTTLE)
                .setName(MessageUtils.getAndFormatMsg(false, "inventories.main.achievementsTitle", "<#9932cc>&lBoosters"))
                .setLore(MessageUtils.getAndFormatMsgList("inventories.main.achievementsDescription"))
                .getItemStack());
        inventory.setItem(22, new CustomItem(mainItem)
                .setName(MessageUtils.getAndFormatMsg(false, "inventories.main.clickerTitle", "%ac%&lYour Cookies"))
                .setLore(MessageUtils.getAndFormatMsgList(
                        "inventories.main.clickerDescription",
                        new MessageUtils.Replaceable<>("%cookies%", NumFormatter.formatBigInt(playerData.getTotalCookies())),
                        new MessageUtils.Replaceable<>("%cpc%", NumFormatter.formatBigInt(playerData.getCookiesPerClick())),
                        new MessageUtils.Replaceable<>("%offlinecookies%", NumFormatter.formatBigInt(playerData.getOfflineCookies())),
                        new MessageUtils.Replaceable<>("%multiplier%", new PrestigeData(plugin, playerData.getPrestige()).getMultiplier()),
                        new MessageUtils.Replaceable<>("%score%", playerData.getFormattedScore())
                ))
                .getItemStack());
        inventory.setItem(29, new CustomItem(Material.FEATHER)
                .setName(MessageUtils.getAndFormatMsg(false, "inventories.main.prestigeTitle", "&6&lPrestige"))
                .setLore(MessageUtils.getAndFormatMsgList("inventories.main.prestigeDescription"))
                .getItemStack());
        inventory.setItem(33, new CustomItem(CustomItem.getHead(player))
                .setName(MessageUtils.getAndFormatMsg(false, "inventories.main.topTitle", "<#FF5733>&lTop Players"))
                .setLore(MessageUtils.getAndFormatMsgList("inventories.main.topDescription"))
                .getItemStack());

        player.openInventory(inventory);
        openInventories.add(player.getUniqueId());
    }

    public static void close(Player player) {
        if (isOpen(player)) openInventories.remove(player.getUniqueId());
    }
}
