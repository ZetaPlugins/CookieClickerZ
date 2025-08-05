package org.strassburger.cookieclickerz;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.strassburger.cookieclickerz.storage.MySQLStorage;
import org.strassburger.cookieclickerz.util.*;
import org.strassburger.cookieclickerz.util.achievements.AchievementManager;
import org.strassburger.cookieclickerz.util.cookieevents.CookieEventManager;
import org.strassburger.cookieclickerz.storage.Storage;
import org.strassburger.cookieclickerz.storage.SQLiteStorage;
import org.strassburger.cookieclickerz.util.holograms.DecentHologramManager;
import org.strassburger.cookieclickerz.util.holograms.FancyHologramManager;
import org.strassburger.cookieclickerz.util.holograms.HologramManager;

import java.util.List;

public final class CookieClickerZ extends JavaPlugin {
    private static CookieClickerZ instance;
    private VersionChecker versionChecker;
    private Storage storage;
    private LanguageManager languageManager;
    private ConfigManager configManager;
    private AntiCheat antiCheat;
    private HologramManager hologramManager;
    private ClickerManager clickerManager;
    private CookieEventManager cookieEventManager;
    private AchievementManager achievementManager;

    private final boolean hasPlaceholderApi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    private final boolean hasDecentHolograms = Bukkit.getPluginManager().getPlugin("DecentHolograms") != null;
    private final boolean hasFancyHolograms = Bukkit.getPluginManager().getPlugin("FancyHolograms") != null;

    @Override
    public void onEnable() {
        instance = this;

        initConfig();

        languageManager = new LanguageManager(this);

        new CommandManager(this).registerCommands();
        new EventManager(this).registerListeners();

        storage = createStorage();
        storage.init();

        antiCheat = new AntiCheat(this);
        clickerManager = new ClickerManager(this);
        cookieEventManager = new CookieEventManager(this);
        achievementManager = new AchievementManager(this);

        if (hasFancyHolograms) {
            getLogger().info("FancyHolograms found! Using FancyHolograms for holograms.");
            hologramManager = new FancyHologramManager(this);
            hologramManager.spawnAllHolograms();
        } else if (hasDecentHolograms) {
            getLogger().info("DecentHolograms found! Using DecentHolograms for holograms.");
            hologramManager = new DecentHologramManager(this);
            hologramManager.spawnAllHolograms();
        } else {
            getLogger().warning("No hologram plugin found! Holograms will not be displayed.");
        }

        if (hasPlaceholderApi()) {
            PapiExpansion papiExpansion = new PapiExpansion(this);
            if (papiExpansion.canRegister()) {
                papiExpansion.register();
                getLogger().info("PlaceholderAPI found! Enabled PlaceholderAPI support!");
            }
        }

        versionChecker = new VersionChecker(this);

        initializeBStats();

        getLogger().info("CookieClickerZ enabled!");
    }

    @Override
    public void onDisable() {
        storage.saveAllCachedData();
        if (hologramManager != null) hologramManager.removeAllHolograms();
        getLogger().info("CookieClickerZ disabled!");
    }

    public static CookieClickerZ getInstance() {
        return instance;
    }

    public VersionChecker getVersionChecker() {
        return versionChecker;
    }

    public Storage getStorage() {
        return storage;
    }

    public boolean hasPlaceholderApi() {
        return hasPlaceholderApi;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public AntiCheat getAntiCheat() {
        return antiCheat;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    public ClickerManager getClickerManager() {
        return clickerManager;
    }

    public CookieEventManager getCookieEventManager() {
        return cookieEventManager;
    }

    public AchievementManager getAchievementManager() {
        return achievementManager;
    }

    private Storage createStorage() {
        switch (getConfig().getString("storage.type", "sqlite").toLowerCase()) {
            case "mysql":
                getLogger().info("Using MySQL storage");
                return new MySQLStorage(this);
            case "sqlite":
            default:
                getLogger().info("Using SQLite storage");
                return new SQLiteStorage(this);
        }
    }

    private void initConfig() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        configManager = new ConfigManager(this);

        for (String file : List.of("upgrades", "prestige", "clicker")) {
            configManager.getCustomConfig(file);
        }
    }

    private void initializeBStats() {
        int pluginId = 25442;
        Metrics metrics = new Metrics(this, pluginId);

        metrics.addCustomChart(new Metrics.SimplePie("storage_type", () -> getConfig().getString("storage.type")));
        metrics.addCustomChart(new Metrics.SimplePie("language", () -> getConfig().getString("lang")));
    }

    public static String locationToString(Location location) {
        return location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ();
    }

    public static Location stringToLocation(String string, World world) {
        String[] parts = string.split(",");
        if (parts.length != 4) return null;

        try {
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);

            return new Location(world, x, y, z);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Location stringToLocation(String string) throws IllegalArgumentException {
        String[] parts = string.split(",");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid location string: " + string);
        }
        String worldName = parts[0];
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new IllegalArgumentException("World not found: " + worldName);
        }

        return stringToLocation(string, world);
    }
}
