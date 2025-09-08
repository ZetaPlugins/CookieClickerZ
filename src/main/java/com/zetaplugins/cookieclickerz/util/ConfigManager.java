package com.zetaplugins.cookieclickerz.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.zetaplugins.cookieclickerz.CookieClickerZ;

import java.io.File;

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

    public FileConfiguration getAchievementsConfig() {
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
}
