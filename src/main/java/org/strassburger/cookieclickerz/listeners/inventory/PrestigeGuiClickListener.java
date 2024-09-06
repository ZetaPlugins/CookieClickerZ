package org.strassburger.cookieclickerz.listeners.inventory;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.strassburger.cookieclickerz.CookieClickerZ;
import org.strassburger.cookieclickerz.util.MessageUtils;
import org.strassburger.cookieclickerz.util.PrestigeData;
import org.strassburger.cookieclickerz.util.gui.PrestigeGUI;
import org.strassburger.cookieclickerz.util.storage.PlayerData;

import java.math.BigInteger;

public class PrestigeGuiClickListener implements Listener {
    private final CookieClickerZ plugin;

    public PrestigeGuiClickListener(CookieClickerZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClickEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!PrestigeGUI.isOpen(player)) return;

        event.setCancelled(true);

        if (event.getSlot() < 20 || event.getSlot() > 24) return;

        PrestigeData prestigeData = new PrestigeData(plugin, event.getSlot() - 19);

        PlayerData playerData = plugin.getStorage().load(player.getUniqueId());

        // Check if the player already has a higher or equal prestige level.
        // If the player's current prestige is greater than or equal to the prestigeData level, they can't buy it.
        if (playerData.getPrestige() >= prestigeData.getLevel()) return;

        // Check if the player is trying to buy a prestige level that is more than one level above their current level.
        // If the prestigeData level is greater than the player's current prestige + 1, they can't buy it.
        if (prestigeData.getLevel() > playerData.getPrestige() + 1) return;

        BigInteger cost = prestigeData.getCost();

        if (playerData.getTotalCookies().compareTo(cost) < 0) {
            player.playSound(player.getLocation(), Sound.valueOf(plugin.getConfig().getString("errorSound", "ENTITY_VILLAGER_NO")), 1, 1);
            player.sendMessage(MessageUtils.getAndFormatMsg(false, "notEnoughCookies", "&cYou don't have enough cookies!"));
            return;
        }

        playerData.setPrestige(prestigeData.getLevel());
        playerData.setTotalCookies(playerData.getTotalCookies().subtract(cost));
        plugin.getStorage().save(playerData);

        player.playSound(player.getLocation(), Sound.valueOf(plugin.getConfig().getString("prestigeSound", "ENTITY_PLAYER_LEVELUP")), 1, 1);
        player.sendMessage(MessageUtils.getAndFormatMsg(
                true,
                "prestigeSuccess",
                "&7You prestiged to Prestige %ac%%prestige%&7! You now have a %ac%%multiplier%x cookie multiplier!",
                new MessageUtils.Replaceable<>("%prestige%", prestigeData.getLevel()),
                new MessageUtils.Replaceable<>("%multiplier%", prestigeData.getMultiplier())
        ));

        PrestigeGUI.close(player);
        PrestigeGUI.open(player);
    }
}
