package com.zetaplugins.cookieclickerz.listeners.inventory;

import com.zetaplugins.cookieclickerz.util.gui.GuiAssets;
import com.zetaplugins.cookieclickerz.util.gui.MainGUI;
import com.zetaplugins.cookieclickerz.util.gui.PrestigeGUI;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import com.zetaplugins.cookieclickerz.CookieClickerZ;
import com.zetaplugins.cookieclickerz.util.MessageUtils;
import com.zetaplugins.cookieclickerz.util.PrestigeData;
import com.zetaplugins.cookieclickerz.util.achievements.AchievementCategory;
import com.zetaplugins.cookieclickerz.util.achievements.AchievementType;
import com.zetaplugins.cookieclickerz.util.gui.*;
import com.zetaplugins.cookieclickerz.storage.PlayerData;

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

        ItemStack item = event.getCurrentItem();
        String ciType = (item != null && item.getItemMeta() != null) ? item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "citype"), PersistentDataType.STRING) : null;

        if (ciType != null && ciType.equals("back")) {
            event.setCancelled(true);
            GuiAssets.playClickSound(player);
            PrestigeGUI.close(player);
            MainGUI.open(player);
            return;
        }

        if (event.getSlot() < 8 || event.getSlot() > 27) return;

        PrestigeData prestigeData = new PrestigeData(plugin, event.getSlot() - 8);

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
        playerData.setTotalCookies(BigInteger.ZERO);
        playerData.setCookiesPerClick(BigInteger.ONE);
        playerData.setOfflineCookies(BigInteger.ZERO);
        playerData.resetUpgrades();
        plugin.getStorage().save(playerData);

        player.playSound(player.getLocation(), Sound.valueOf(plugin.getConfig().getString("prestigeSound", "ENTITY_PLAYER_LEVELUP")), 1, 1);
        player.sendMessage(MessageUtils.getAndFormatMsg(
                true,
                "prestigeSuccess",
                "&7You prestiged to Prestige %ac%%prestige%&7! You now have a %ac%%multiplier%x cookie multiplier!",
                new MessageUtils.Replaceable<>("%prestige%", prestigeData.getLevel()),
                new MessageUtils.Replaceable<>("%multiplier%", prestigeData.getMultiplier())
        ));

        for (AchievementType achievementType : AchievementType.getByCategory(AchievementCategory.PRESTIGE)) {
            playerData.setAchievementProgress(achievementType, prestigeData.getLevel(), plugin);
        }
        plugin.getStorage().save(playerData);

        for (String command : prestigeData.getCommands()) {
            plugin.getServer().dispatchCommand(
                    plugin.getServer().getConsoleSender(),
                    command.replace("%player%", player.getName())
            );
        }

        PrestigeGUI.close(player);
        PrestigeGUI.open(player);
    }
}
