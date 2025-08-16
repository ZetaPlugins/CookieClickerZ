package com.zetaplugins.cookieclickerz.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import com.zetaplugins.cookieclickerz.CookieClickerZ;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class LanguageManager {
    private final JavaPlugin plugin;
    public static final List<String> defaultLangs = List.of("en-US", "de-DE", "ru-RU", "cs-CZ");

    private HashMap<String, String> translationMap;
    private FileConfiguration langConfig;

    public LanguageManager(CookieClickerZ plugin) {
        this.plugin = plugin;
        loadLanguageConfig();
    }

    public void reload() {
        loadLanguageConfig();
    }

    private void loadLanguageConfig() {
        File languageDirectory = new File(plugin.getDataFolder(), "lang/");
        if (!languageDirectory.exists() || !languageDirectory.isDirectory()) languageDirectory.mkdir();

        for (String langString : defaultLangs) {
            File langFile = new File("lang/", langString + ".yml");
            if (!new File(languageDirectory, langString + ".yml").exists()) {
                plugin.getLogger().info("Saving file " + langFile.getPath());
                plugin.saveResource(langFile.getPath(), false);
            }
        }

        String langOption = plugin.getConfig().getString("lang") != null ? plugin.getConfig().getString("lang") : "en-US";
        File selectedLangFile = new File(languageDirectory, langOption + ".yml");

        if (!selectedLangFile.exists()) {
            selectedLangFile = new File(languageDirectory, "en-US.yml");
            plugin.getLogger().warning("Language file " + langOption + ".yml (" + selectedLangFile.getPath() + ") not found! Using fallback en-US.yml.");
        }

        plugin.getLogger().info("Using language file: " + selectedLangFile.getPath());
        langConfig = YamlConfiguration.loadConfiguration(selectedLangFile);
    }

    public String getString(String key) {
        return langConfig.getString(key);
    }

    public String getString(String key, String fallback) {
        return langConfig.getString(key) != null ? langConfig.getString(key) : fallback;
    }

    public List<String> getStringList(String key) {
        return langConfig.getStringList(key);
    }

    public int getInt(String key) {
        return langConfig.getInt(key);
    }

    public double getDouble(String key) {
        return langConfig.getDouble(key);
    }
}
