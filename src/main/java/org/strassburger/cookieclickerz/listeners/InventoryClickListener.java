package org.strassburger.cookieclickerz.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.strassburger.cookieclickerz.CookieClickerZ;
import org.strassburger.cookieclickerz.util.MessageUtils;
import org.strassburger.cookieclickerz.util.Replaceable;
import org.strassburger.cookieclickerz.util.gui.GuiAssets;
import org.strassburger.cookieclickerz.util.gui.MainGUI;
import org.strassburger.cookieclickerz.util.gui.UpgradeGUI;
import org.strassburger.cookieclickerz.util.storage.PlayerData;
import org.strassburger.cookieclickerz.util.storage.PlayerDataStorage;

import java.math.BigInteger;

public class InventoryClickListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (MainGUI.isOpen(player)) {
            switch (event.getSlot()) {
                case 11: {
                    GuiAssets.playClickSound(player);
                    MainGUI.close(player);
                    UpgradeGUI.open(player);
                    break;
                }
            }
            event.setCancelled(true);
        }

        if (UpgradeGUI.isOpen(player)) {
            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();
            if (item == null || item.getItemMeta() == null) return;
            String ciType = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(CookieClickerZ.getInstance(), "citype"), PersistentDataType.STRING);
            if (ciType == null) return;

            if (ciType.equals("prev") || ciType.equals("next")) {
                GuiAssets.playClickSound(player);
                int targetPage = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(CookieClickerZ.getInstance(), "openpage"), PersistentDataType.INTEGER);
                if (targetPage < 0) return;
                UpgradeGUI.open(player, targetPage);
                return;
            }

            if (!ciType.equals("upgrade")) return;

            String id = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(CookieClickerZ.getInstance(), "id"), PersistentDataType.STRING);
            if (id == null) return;

            UpgradeGUI.Upgrade upgrade = new UpgradeGUI.Upgrade(id);

            PlayerDataStorage playerDataStorage = CookieClickerZ.getInstance().getPlayerDataStorage();
            PlayerData playerData = playerDataStorage.load(player.getUniqueId());
            int upgradelevel = playerData.getUpgradeLevel("upgrade_" + upgrade.getId());
            BigInteger upgradePrice = upgrade.getBaseprice().multiply(BigInteger.valueOf((long) Math.pow(upgrade.getPriceMultiplier(), upgradelevel)));
            if (playerData.getTotalCookies().compareTo(upgradePrice) < 0) {
                player.sendMessage(MessageUtils.getAndFormatMsg(false, "notEnoughCookies", "&cYou don't have enough cookies!"));
                player.playSound(player.getLocation(), Sound.valueOf(CookieClickerZ.getInstance().getConfig().getString("errorSound", "ENTITY_VILLAGER_NO")), 1, 1);
                return;
            }

            playerData.setTotalCookies(playerData.getTotalCookies().subtract(upgradePrice));
            playerData.setCookiesPerClick(playerData.getCookiesPerClick().add(upgrade.getCpc()));
            playerData.setOfflineCookies(playerData.getOfflineCookies().add(upgrade.getOfflineCookies()));
            playerData.addUpgrade("upgrade_" + upgrade.getId(), upgradelevel + 1);
            playerDataStorage.save(playerData);

            player.sendMessage(MessageUtils.getAndFormatMsg(true, "upgradeBought", "&7You bought the upgrade %ac%%upgrade%&7!", new Replaceable("%upgrade%", upgrade.getName())));
            player.playSound(player.getLocation(), Sound.valueOf(CookieClickerZ.getInstance().getConfig().getString("upgradeSound", "ENTITY_PLAYER_LEVELUP")), 1, 1);

            UpgradeGUI.close(player);
            UpgradeGUI.open(player);
        }
    }
}
