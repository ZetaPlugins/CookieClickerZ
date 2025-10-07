package com.zetaplugins.cookieclickerz.listeners.inventory;

import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.zetaplugins.cookieclickerz.CookieClickerZ;
import com.zetaplugins.cookieclickerz.storage.PlayerData;
import com.zetaplugins.cookieclickerz.storage.Storage;
import com.zetaplugins.cookieclickerz.util.MessageUtils;
import com.zetaplugins.cookieclickerz.util.MessageUtils.Replaceable;
import com.zetaplugins.cookieclickerz.util.achievements.AchievementCategory;
import com.zetaplugins.cookieclickerz.util.achievements.AchievementType;
import com.zetaplugins.cookieclickerz.util.gui.GuiAssets;
import com.zetaplugins.cookieclickerz.util.gui.MainGUI;
import com.zetaplugins.cookieclickerz.util.gui.UpgradeGUI;

import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import static com.zetaplugins.cookieclickerz.util.PricingUtils.*;

public class UpgradeGuiClickListener implements Listener {
    private final CookieClickerZ plugin;

    private static final MathContext MC = new MathContext(50, RoundingMode.HALF_UP);

    public UpgradeGuiClickListener(CookieClickerZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!UpgradeGUI.isOpen(player)) return;

        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getItemMeta() == null) return;

        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();

        String ciType = pdc.get(new NamespacedKey(plugin, "citype"), PersistentDataType.STRING);
        if (ciType == null) return;

        if (ciType.equals("prev") || ciType.equals("next")) {
            GuiAssets.playClickSound(player);

            Integer pageObj = pdc.get(new NamespacedKey(plugin, "openpage"), PersistentDataType.INTEGER);
            if (pageObj == null) return;

            int targetPage = pageObj;
            if (targetPage < 0) return;

            UpgradeGUI.open(player, targetPage);
            return;
        }

        if (ciType.equals("back")) {
            GuiAssets.playClickSound(player);
            UpgradeGUI.close(player);
            MainGUI.open(player);
            return;
        }

        if (!ciType.equals("upgrade")) return;

        String id = pdc.get(new NamespacedKey(plugin, "id"), PersistentDataType.STRING);
        if (id == null) return;

        UpgradeGUI.Upgrade upgrade = new UpgradeGUI.Upgrade(id);

        if (upgrade.isRequirePermission() && !player.hasPermission("cookieclickerz.buyupgrade." + upgrade.getId())) {
            player.sendMessage(MessageUtils.getAndFormatMsg(
                    false,
                    "noUpgradePerms",
                    "&cYou do not have permission to buy this upgrade!"
            ));
            player.playSound(player.getLocation(), safeSound("errorSound", "ENTITY_VILLAGER_NO"), 1, 1);
            return;
        }

        Storage storage = plugin.getStorage();
        PlayerData playerData = storage.load(player.getUniqueId());

        int currentLevel = playerData.getUpgradeLevel("upgrade_" + upgrade.getId());

        BigInteger basePriceBI = upgrade.getBaseprice();
        double priceMultiplier = upgrade.getPriceMultiplier();

        int amountToBuy = 1;
        if (event.isRightClick()) amountToBuy = 10;

        BigInteger totalCost;

        if (event.isShiftClick()) {
            // Buy max: find largest n so totalCost(n) <= totalCookies
            BigInteger cookies = playerData.getTotalCookies();
            amountToBuy = computeMaxAffordable(basePriceBI, priceMultiplier, currentLevel, cookies);

            if (amountToBuy <= 0) {
                player.sendMessage(MessageUtils.getAndFormatMsg(false, "notEnoughCookies",
                        "&cYou don't have enough cookies!"));
                player.playSound(player.getLocation(), safeSound("errorSound", "ENTITY_VILLAGER_NO"), 1, 1);
                return;
            }

            totalCost = totalCostForN(basePriceBI, priceMultiplier, currentLevel, amountToBuy);
        } else {
            // Single or x10 purchase
            totalCost = totalCostForN(basePriceBI, priceMultiplier, currentLevel, amountToBuy);
            if (playerData.getTotalCookies().compareTo(totalCost) < 0) {
                player.sendMessage(MessageUtils.getAndFormatMsg(false, "notEnoughCookies",
                        "&cYou don't have enough cookies!"));
                player.playSound(player.getLocation(), safeSound("errorSound", "ENTITY_VILLAGER_NO"), 1, 1);
                return;
            }
        }

        playerData.setTotalCookies(playerData.getTotalCookies().subtract(totalCost));
        playerData.setCookiesPerClick(playerData.getCookiesPerClick()
                .add(upgrade.getCpc().multiply(BigInteger.valueOf(amountToBuy))));
        playerData.setOfflineCookies(playerData.getOfflineCookies()
                .add(upgrade.getOfflineCookies().multiply(BigInteger.valueOf(amountToBuy))));
        playerData.addUpgrade("upgrade_" + upgrade.getId(), currentLevel + amountToBuy);
        storage.save(playerData);

        player.sendMessage(MessageUtils.getAndFormatMsg(true, "upgradeBought",
                "&7You bought the upgrade %ac%%upgrade%&7!",
                new Replaceable("%upgrade%", upgrade.getName())));
        player.playSound(player.getLocation(), safeSound("upgradeSound", "ENTITY_PLAYER_LEVELUP"), 1, 1);

        for (AchievementType achievementType : AchievementType.getByCategory(AchievementCategory.UPGRADES)) {
            playerData.progressAchievement(achievementType, amountToBuy, plugin);
            storage.save(playerData);
        }

        UpgradeGUI.close(player);
        UpgradeGUI.open(player);
    }

    /**
     * Compute the maximum n such that totalCostForN(...) <= cookies.
     * Uses exponential search + binary search
     *
     * @param baseBI Base price
     * @param r Price multiplier
     * @param currentLevel Current level of the upgrade
     * @param cookies Number of cookies available
     * @return Maximum n affordable (0 if none)
     */
    private int computeMaxAffordable(BigInteger baseBI, double r, int currentLevel, BigInteger cookies) {
        if (cookies.signum() <= 0) return 0;

        BigInteger firstPrice = priceAtLevel(baseBI, r, currentLevel);
        if (cookies.compareTo(firstPrice) < 0) return 0;

        int low = 1;
        int high = 1;
        while (true) {
            BigInteger cost = totalCostForN(baseBI, r, currentLevel, high);
            if (cost.compareTo(cookies) <= 0) {
                if (high > 1_000_000_000) break;
                low = high;
                high = Math.min(high << 1, Integer.MAX_VALUE);
            } else {
                break;
            }
        }

        int left = low, right = high;
        while (left < right) {
            int mid = left + (right - left + 1) / 2;
            BigInteger cost = totalCostForN(baseBI, r, currentLevel, mid);
            if (cost.compareTo(cookies) <= 0) {
                left = mid;
            } else {
                right = mid - 1;
            }
        }
        return left;
    }

    /**
     * Safely get a Sound from config, with fallback to default and hardcoded value
     * @param configKey Config key
     * @param def Default value if config value is invalid
     * @return Sound
     */
    private Sound safeSound(String configKey, String def) {
        String name = plugin.getConfig().getString(configKey, def);
        try {
            return Sound.valueOf(name);
        } catch (IllegalArgumentException ex) {
            try {
                return Sound.valueOf(def);
            } catch (IllegalArgumentException ignored) {
                return Sound.ENTITY_PLAYER_LEVELUP;
            }
        }
    }
}