package org.strassburger.cookieclickerz.util.gui;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.strassburger.cookieclickerz.CookieClickerZ;
import org.strassburger.cookieclickerz.util.MessageUtils;
import org.strassburger.cookieclickerz.storage.PlayerData;
import org.strassburger.cookieclickerz.storage.Storage;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UpgradeGUI {
    private static final List<UUID> openInventories = new ArrayList<>();
    private static final FileConfiguration config = CookieClickerZ.getInstance().getConfigManager().getUpgradesConfig();
    private static final int ITEMS_PER_PAGE = 28;

    public static class Upgrade {
        private final String id;
        private String name;
        private BigInteger baseprice;
        private double priceMultiplier;
        private String item;
        private BigInteger cpc;
        private BigInteger offlineCookies;
        private BigInteger upgradePrice;
        private boolean affordable;
        private int level;

        // Getters and setters

        public Upgrade(String id, String name, BigInteger baseprice, double priceMultiplier, String item, BigInteger cpc, BigInteger offlineCookies) {
            this.id = id;
            this.name = name;
            this.baseprice = baseprice;
            this.priceMultiplier = priceMultiplier;
            this.item = item;
            this.cpc = cpc;
            this.offlineCookies = offlineCookies;
        }

        public Upgrade(String id) {
            this.id = id;
            this.name = config.getString(id + ".name");
            this.baseprice = new BigInteger(config.getString( id + ".baseprice", "0"));
            this.priceMultiplier = config.getDouble( id + ".priceMultiplier");
            this.item = config.getString(id + ".item");
            this.cpc = new BigInteger(config.getString(id + ".cpc", "0"));
            this.offlineCookies = new BigInteger(config.getString(id + ".offlineCookies", "0"));
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public boolean isAffordable() {
            return affordable;
        }

        public void setAffordable(boolean affordable) {
            this.affordable = affordable;
        }

        public BigInteger getUpgradePrice() {
            return upgradePrice;
        }

        public void setUpgradePrice(BigInteger upgradePrice) {
            this.upgradePrice = upgradePrice;
        }

        public String getId() {
            return id;
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
        open(player, 1);
    }

    public static void open(Player player, int page) {
        Inventory inventory = Bukkit.createInventory(null, 6 * 9, MessageUtils.getAndFormatMsg(false, "inventories.upgrades.title", "&8Upgrades"));
        GuiAssets.addBorder(inventory);
        GuiAssets.addBackButton(inventory);

        Storage storage = CookieClickerZ.getInstance().getStorage();
        PlayerData playerData = storage.load(player.getUniqueId());

        List<Upgrade> upgrades = new ArrayList<>();
        for (String key : config.getKeys(false)) {
            Upgrade upgrade = new Upgrade(key);
            int upgradelevel = playerData.getUpgradeLevel("upgrade_" + upgrade.getId());
            BigInteger upgradePrice = upgrade.getBaseprice().multiply(BigInteger.valueOf((long) Math.pow(upgrade.getPriceMultiplier(), upgradelevel)));
            upgrade.setUpgradePrice(upgradePrice);
            upgrade.setAffordable(playerData.getTotalCookies().compareTo(upgradePrice) >= 0);
            upgrade.setLevel(upgradelevel);
            upgrades.add(upgrade);
        }

        List<Upgrade> itemsForPage = getItemsForPage(upgrades, page);
        for (Upgrade upgrade : itemsForPage) {
            inventory.addItem(GuiAssets.createUpgradeItem(upgrade));
        }

        GuiAssets.addPagination(inventory, page, page > 1, page < (int) Math.ceil((double) upgrades.size() / ITEMS_PER_PAGE));

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
