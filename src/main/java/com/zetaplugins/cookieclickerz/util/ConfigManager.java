package com.zetaplugins.cookieclickerz.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.zetaplugins.cookieclickerz.CookieClickerZ;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private final CookieClickerZ plugin;

    public ConfigManager(CookieClickerZ plugin) {
        this.plugin = plugin;
    }

    public FileConfiguration getClickerConfig() {
        return getCustomConfig("clicker");
    }

    public FileConfiguration getUpgradesConfig() {
        return getCustomConfig("upgrades");
    }

    public FileConfiguration getPrestigeConfig() {
        return getCustomConfig("prestige");
    }

    public FileConfiguration getAchievementCategoryConfig() {
        return getCustomConfig("achievementCategories");
    }

    public FileConfiguration getAchievementConfig() {
        return getCustomConfig("achievements");
    }

    public FileConfiguration getCustomConfig(String fileName) {
        File configFile = new File(plugin.getDataFolder(),  fileName.contains(".yml") ? fileName : fileName + ".yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource(fileName + ".yml", false);
        }

        return YamlConfiguration.loadConfiguration(configFile);
    }

    public void saveCustomConfig(String fileName, FileConfiguration config) {
        String name = fileName.contains(".yml") ? fileName : fileName + ".yml";
        File configFile = new File(plugin.getDataFolder(), name);
        try {
            config.save(configFile);
        } catch (Exception e) {
            plugin.getLogger().severe("Could not save " + name + ": " + e.getMessage());
        }
    }

    /**
     * Gets all configs as a map of file name to file content used for debug command
     * @return Map of file name to file content
     */
    public Map<String, String> getConfigsMap() {
        Map<String, String> configs = new HashMap<>();
        configs.put("config.yml", plugin.getConfig().saveToString());
        configs.put("clicker.yml", getClickerConfig().saveToString());
        configs.put("upgrades.yml", getUpgradesConfig().saveToString());
        configs.put("prestige.yml", getPrestigeConfig().saveToString());
        configs.put("achievementCategories.yml", getAchievementCategoryConfig().saveToString());
        configs.put("achievements.yml", getAchievementConfig().saveToString());
        return configs;
    }

}
