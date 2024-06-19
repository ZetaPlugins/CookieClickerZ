package org.strassburger.cookieclickerz.util.gui;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.strassburger.cookieclickerz.CookieClickerZ;
import org.strassburger.cookieclickerz.util.MessageUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UpgradeGUI {
    private static List<UUID> openInventories = new ArrayList<>();
    private static Inventory inventory = null;
    private static FileConfiguration config = CookieClickerZ.getInstance().getConfig();
    private static final int ITEMS_PER_PAGE = 28;

    static class Upgrade {
        private String name;
        private BigInteger baseprice;
        private double priceMultiplier;
        private String item;
        private BigInteger cpc;
        private BigInteger offlineCookies;

        // Getters and setters

        public Upgrade(String name, BigInteger baseprice, double priceMultiplier, String item, BigInteger cpc, BigInteger offlineCookies) {
            this.name = name;
            this.baseprice = baseprice;
            this.priceMultiplier = priceMultiplier;
            this.item = item;
            this.cpc = cpc;
            this.offlineCookies = offlineCookies;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public BigInteger getBaseprice() {
            return baseprice;
        }

        public void setBaseprice(BigInteger baseprice) {
            this.baseprice = baseprice;
        }

        public double getPriceMultiplier() {
            return priceMultiplier;
        }

        public void setPriceMultiplier(double priceMultiplier) {
            this.priceMultiplier = priceMultiplier;
        }

        public String getItem() {
            return item;
        }

        public void setItem(String item) {
            this.item = item;
        }

        public BigInteger getCpc() {
            return cpc;
        }

        public void setCpc(BigInteger cpc) {
            this.cpc = cpc;
        }

        public BigInteger getOfflineCookies() {
            return offlineCookies;
        }

        public void setOfflineCookies(BigInteger offlineCookies) {
            this.offlineCookies = offlineCookies;
        }
    }

    private UpgradeGUI() {}

    public static boolean isOpen(Player player) {
        return openInventories.contains(player.getUniqueId());
    }

    public static void open(Player player) {
        inventory = Bukkit.createInventory(null, 6 * 9, MessageUtils.getAndFormatMsg(false, "inventories.main.title", "&8CookieClickerZ"));
        GuiAssets.addBorder(inventory, 6 * 9);

        List<Upgrade> upgrades = new ArrayList<>();
        ConfigurationSection upgradesSection = config.getConfigurationSection("upgrades");
        if (upgradesSection == null) return;
        for (String key : upgradesSection.getKeys(false)) {
            String name = config.getString("upgrades." + key + ".name");
            BigInteger baseprice = new BigInteger(config.getString("upgrades." + key + ".baseprice", "0"));
            double priceMultiplier = config.getDouble("upgrades." + key + ".priceMultiplier");
            String item = config.getString("upgrades." + key + ".item");
            BigInteger cpc = new BigInteger(config.getString("upgrades." + key + ".cpc", "0"));
            BigInteger offlineCookies = new BigInteger(config.getString("upgrades." + key + ".offlineCookies", "0"));
            upgrades.add(new Upgrade(name, baseprice, priceMultiplier, item, cpc, offlineCookies));
        }
        List<Upgrade> itemsForPage = getItemsForPage(upgrades, 1);
        for (Upgrade upgrade : itemsForPage) {
            inventory.addItem(GuiAssets.createUpgradeItem(upgrade));
        }
        player.openInventory(inventory);
        openInventories.add(player.getUniqueId());
    }

    public static void close(Player player) {
        if (isOpen(player)) openInventories.remove(player.getUniqueId());
    }

    private static List<Upgrade> getItemsForPage(List<Upgrade> upgrades, int page) {
        int totalItems = upgrades.size();
        int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);

        if (page < 1 || page > totalPages) {
            throw new IllegalArgumentException("Invalid page number: " + page);
        }

        int startIndex = (page - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, totalItems);

        return new ArrayList<>(upgrades.subList(startIndex, endIndex));
    }
}
