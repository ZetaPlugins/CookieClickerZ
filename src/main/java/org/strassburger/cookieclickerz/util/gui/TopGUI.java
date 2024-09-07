package org.strassburger.cookieclickerz.util.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.strassburger.cookieclickerz.CookieClickerZ;
import org.strassburger.cookieclickerz.util.CustomItem;
import org.strassburger.cookieclickerz.util.MessageUtils;
import org.strassburger.cookieclickerz.util.NumFormatter;
import org.strassburger.cookieclickerz.util.PrestigeData;
import org.strassburger.cookieclickerz.util.storage.PlayerData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TopGUI {
    private static final List<UUID> openInventories = new ArrayList<>();
    FileConfiguration config = CookieClickerZ.getInstance().getConfig();

    private TopGUI() {}

    public static boolean isOpen(Player player) {
        return openInventories.contains(player.getUniqueId());
    }

    public static void open(Player player) {
        final CookieClickerZ plugin = CookieClickerZ.getInstance();
        PlayerData playerData = plugin.getStorage().load(player.getUniqueId());

        Inventory inventory = Bukkit.createInventory(null, 6 * 9, MessageUtils.getAndFormatMsg(false, "inventories.top.title", "&8Top Players"));
        GuiAssets.addBorder(inventory, 6 * 9);

        List<PlayerData> topPlayers = plugin.getStorage().getAllPlayers().parallelStream()
                        .sorted(Comparator.comparing(PlayerData::getScore).reversed())
                        .limit(10)
                        .collect(Collectors.toList());

        inventory.setItem(13, getTopPlayerItem(plugin, getPlayerOnPosition(topPlayers, 1), 1, "<#FFD700><b>"));
        inventory.setItem(20, getTopPlayerItem(plugin, getPlayerOnPosition(topPlayers, 2), 2, "<#C0C0C0><b>"));
        inventory.setItem(24, getTopPlayerItem(plugin, getPlayerOnPosition(topPlayers, 3), 3, "<#CD7F32><b>"));
        inventory.setItem(37, getTopPlayerItem(plugin, getPlayerOnPosition(topPlayers, 4), 4, "&8"));
        inventory.setItem(38, getTopPlayerItem(plugin, getPlayerOnPosition(topPlayers, 5), 5, "&8"));
        inventory.setItem(39, getTopPlayerItem(plugin, getPlayerOnPosition(topPlayers, 6), 6, "&8"));
        inventory.setItem(40, getTopPlayerItem(plugin, getPlayerOnPosition(topPlayers, 7), 7, "&8"));
        inventory.setItem(41, getTopPlayerItem(plugin, getPlayerOnPosition(topPlayers, 8), 8, "&8"));
        inventory.setItem(42, getTopPlayerItem(plugin, getPlayerOnPosition(topPlayers, 9), 9, "&8"));
        inventory.setItem(43, getTopPlayerItem(plugin, getPlayerOnPosition(topPlayers, 10), 10, "&8"));

        player.openInventory(inventory);
        openInventories.add(player.getUniqueId());
    }

    public static void close(Player player) {
        if (isOpen(player)) openInventories.remove(player.getUniqueId());
    }

    private static ItemStack getTopPlayerItem(CookieClickerZ plugin, @Nullable PlayerData playerData, int place, String placeColor) {
        if (playerData == null) return new CustomItem(Material.SKELETON_SKULL)
                .setName(MessageUtils.getAndFormatMsg(false, "inventories.top.player.title", "%placeColor%#%place% &7%player%",
                        new MessageUtils.Replaceable<>("%placeColor%", placeColor),
                        new MessageUtils.Replaceable<>("%place%", String.valueOf(place)),
                        new MessageUtils.Replaceable<>("%player%", "&8N/A")
                ))
                .getItemStack();

        return new CustomItem(CustomItem.getHead(playerData.getUuid()))
                .setName(MessageUtils.getAndFormatMsg(false, "inventories.top.player.title", "%placeColor%#%place% &7%player%",
                        new MessageUtils.Replaceable<>("%placeColor%", placeColor),
                        new MessageUtils.Replaceable<>("%place%", String.valueOf(place)),
                        new MessageUtils.Replaceable<>("%player%", playerData.getName())
                ))
                .setLore(MessageUtils.getAndFormatMsgList("inventories.top.player.description",
                        new MessageUtils.Replaceable<>("%cookies%", NumFormatter.formatBigInt(playerData.getTotalCookies())),
                        new MessageUtils.Replaceable<>("%cpc%", NumFormatter.formatBigInt(playerData.getCookiesPerClick())),
                        new MessageUtils.Replaceable<>("%offlinecookies%", NumFormatter.formatBigInt(playerData.getOfflineCookies())),
                        new MessageUtils.Replaceable<>("%multiplier%", new PrestigeData(plugin, playerData.getPrestige()).getMultiplier()),
                        new MessageUtils.Replaceable<>("%score%", playerData.getFormattedScore()
                        )
                ))
                .getItemStack();
    }

    private static @Nullable PlayerData getPlayerOnPosition(List<PlayerData> topPlayers, int position) {
        return topPlayers.size() < position ? null : topPlayers.get(position - 1);
    }
}
