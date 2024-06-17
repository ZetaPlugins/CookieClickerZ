package org.strassburger.cookieclickerz.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;

public class ConfigManager {
    private final JavaPlugin plugin;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Creates a custom config file
     *
     * @param configName The name of the config file (with extension)
     */
    public void createCustomConfig(String configName) {
        File configDir = new File(plugin.getDataFolder(), "config");

        if (!configDir.exists()) configDir.mkdir();

        File configFile = new File(configDir, configName);
        if (!configFile.exists()) {
            try (InputStream in = plugin.getResource("config/" + configName)) {
                if (in == null) {
                    plugin.getLogger().log(Level.SEVERE, "Template not found in resources: " + configName);
                    return;
                }
                Files.copy(in, configFile.toPath());
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create config file: " + configName, e);
            }
        }
    }

    /**
     * Gets a custom config file
     *
     * @param configName The name of the config file (with extension)
     * @return The config file
     */
    public FileConfiguration getCustomConfig(String configName) {
        File configDir = new File(plugin.getDataFolder(), "config");
        File configFile = new File(configDir, configName);
        if (!configFile.exists()) {
            plugin.getLogger().log(Level.SEVERE, "Config file not found: " + configName);
            return null;
        }
        return YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Saves a custom config file
     *
     * @param configName The name of the config file (with extension)
     * @param config The config file
     */
    public void saveCustomConfig(String configName, FileConfiguration config) {
        File configDir = new File(plugin.getDataFolder(), "config");
        File configFile = new File(configDir, configName);
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config file: " + configName, e);
        }
    }

    /**
     * Reloads a custom config file
     *
     * @param configName The name of the config file (with extension)
     */
    public void reloadCustomConfig(String configName) {
        File configDir = new File(plugin.getDataFolder(), "config");
        File configFile = new File(configDir, configName);
        if (!configFile.exists()) {
            plugin.getLogger().log(Level.SEVERE, "Config file not found: " + configName);
            return;
        }
        YamlConfiguration.loadConfiguration(configFile);
    }
}
