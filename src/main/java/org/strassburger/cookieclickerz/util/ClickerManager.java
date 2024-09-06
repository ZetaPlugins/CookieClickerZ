package org.strassburger.cookieclickerz.util;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.strassburger.cookieclickerz.CookieClickerZ;

import java.util.ArrayList;
import java.util.List;

public class ClickerManager {
    private ClickerManager() {}

    /**
     * Adds a clicker to the clicker.yml
     *
     * @param clicker The location of the clicker
     * @param name The name of the clicker
     */
    public static void addClicker(String clicker, String name) {
        ConfigManager configManager = CookieClickerZ.getInstance().getConfigManager();
        FileConfiguration clickerConfig = configManager.getCustomConfig("clicker");
        if (clickerConfig == null) return;
        clickerConfig.set(name + ".name", name);
        clickerConfig.set(name + ".location", clicker);
        configManager.saveCustomConfig("clicker", clickerConfig);
    }

    /**
     * Adds a clicker to the clicker.yml
     *
     * @param clicker The location of the clicker
     * @param name The name of the clicker
     */
    public static void addClicker(Location clicker, String name) {
        addClicker(CookieClickerZ.locationToString(clicker), name);
    }

    /**
     * Checks if a clicker exists
     *
     * @param clicker The name of the clicker
     */
    public static boolean isClicker(String clicker) {
        ConfigManager configManager = CookieClickerZ.getInstance().getConfigManager();
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
    public static boolean isClicker(Location location) {
        ConfigManager configManager = CookieClickerZ.getInstance().getConfigManager();
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
    public static void removeClicker(String name) {
        ConfigManager configManager = CookieClickerZ.getInstance().getConfigManager();
        FileConfiguration clickerConfig = configManager.getCustomConfig("clicker");
        if (clickerConfig == null) return;
        clickerConfig.set("clicker." + name, null);
        configManager.saveCustomConfig("clicker", clickerConfig);
    }

    /**
     * Gets a list of all clickers
     * @return A list of all clickers
     */
    public static List<String> getClickers() {
        ConfigManager configManager = CookieClickerZ.getInstance().getConfigManager();
        FileConfiguration clickerConfig = configManager.getCustomConfig("clicker");
        if (clickerConfig == null) return null;
        return new ArrayList<>(clickerConfig.getKeys(false));
    }

    /**
     * Gets the location of a clicker
     *
     * @param name The name of the clicker
     * @return The location of the clicker
     */
    public static String getClickerLocationAsString(String name) {
        ConfigManager configManager = CookieClickerZ.getInstance().getConfigManager();
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
    public static Location getClickerLocation(String name) {
        String locationString = getClickerLocationAsString(name);
        if (locationString == null) return null;
        return CookieClickerZ.stringToLocation(locationString);
    }
}
