package org.strassburger.cookieclickerz.util;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.strassburger.cookieclickerz.CookieClickerZ;

import java.util.ArrayList;
import java.util.List;

public class ClickerManager {
    private final CookieClickerZ plugin;

    public ClickerManager(CookieClickerZ plugin) {
        this.plugin = plugin;
    }

    /**
     * Adds a clicker to the clicker.yml
     *
     * @param clicker The location of the clicker
     * @param name The name of the clicker
     */
    public void addClicker(String clicker, String name) {
        ConfigManager configManager = plugin.getConfigManager();
        FileConfiguration clickerConfig = configManager.getCustomConfig("clicker");
        if (clickerConfig == null) return;
        clickerConfig.set(name + ".name", name);
        clickerConfig.set(name + ".location", clicker);
        configManager.saveCustomConfig("clicker", clickerConfig);
        if (plugin.getHologramManager() != null) plugin.getHologramManager().spawnHologram(new Clicker(name, CookieClickerZ.stringToLocation(clicker)));
    }

    /**
     * Adds a clicker to the clicker.yml
     *
     * @param clicker The location of the clicker
     * @param name The name of the clicker
     */
    public void addClicker(Location clicker, String name) {
        addClicker(CookieClickerZ.locationToString(clicker), name);
    }

    /**
     * Checks if a clicker exists
     *
     * @param clicker The name of the clicker
     */
    public boolean isClicker(String clicker) {
        ConfigManager configManager = plugin.getConfigManager();
        FileConfiguration clickerConfig = configManager.getCustomConfig("clicker");
        if (clickerConfig == null) return false;
        List<String> clickerNames = new ArrayList<>(clickerConfig.getKeys(false));
        return clickerNames.contains(clicker);
    }

    /**
     * Checks if a clicker exists
     *
     * @param location The location of the clicker
     */
    public boolean isClicker(Location location) {
        ConfigManager configManager = plugin.getConfigManager();
        FileConfiguration clickerConfig = configManager.getCustomConfig("clicker");
        if (clickerConfig == null) return false;
        String locationString = CookieClickerZ.locationToString(location);
        for (String key : clickerConfig.getKeys(false)) {
            String storedLocation = clickerConfig.getString(key + ".location");
            if (locationString.equals(storedLocation)) return true;
        }
        return false;
    }

    /**
     * Removes a clicker from the clicker.yml
     *
     * @param name The name of the clicker
     */
    public void removeClicker(String name) {
        ConfigManager configManager = plugin.getConfigManager();
        FileConfiguration clickerConfig = configManager.getCustomConfig("clicker");
        if (clickerConfig == null) return;
        clickerConfig.set(name, null);
        configManager.saveCustomConfig("clicker", clickerConfig);
        if (plugin.getHologramManager() != null) plugin.getHologramManager().removeHologram(name);
    }

    /**
     * Gets a clicker
     * @param name The name of the clicker
     * @return The clicker
     */
    public Clicker getClicker(String name) {
        return new Clicker(name, getClickerLocation(name));
    }

    /**
     * Gets a list of all clickers
     * @return A list of all clickers
     */
    public List<Clicker> getClickers() {
        List<Clicker> clickers = new ArrayList<>();
        for (String name : getClickerKeys()) {
            clickers.add(getClicker(name));
        }
        return clickers;
    }

    /**
     * Gets a list of all clicker names
     * @return A list of all clicker names
     */
    @NotNull
    public List<String> getClickerKeys() {
        ConfigManager configManager = plugin.getConfigManager();
        FileConfiguration clickerConfig = configManager.getCustomConfig("clicker");
        if (clickerConfig == null) return new ArrayList<>();
        return new ArrayList<>(clickerConfig.getKeys(false));
    }

    /**
     * Gets the location of a clicker
     *
     * @param name The name of the clicker
     * @return The location of the clicker
     */
    public String getClickerLocationAsString(String name) {
        ConfigManager configManager = plugin.getConfigManager();
        FileConfiguration clickerConfig = configManager.getCustomConfig("clicker");
        if (clickerConfig == null) return null;
        return clickerConfig.getString(name + ".location");
    }

    /**
     * Gets the location of a clicker
     *
     * @param name The name of the clicker
     * @return The location of the clicker
     */
    public Location getClickerLocation(String name) {
        String locationString = getClickerLocationAsString(name);
        if (locationString == null) return null;
        return CookieClickerZ.stringToLocation(locationString);
    }

    public static class Clicker {
        private final String name;
        private final Location location;

        public Clicker(String name, Location location) {
            this.name = name;
            this.location = location;
        }

        public String getName() {
            return name;
        }

        public Location getLocation() {
            return location;
        }
    }
}