package org.strassburger.cookieclickerz;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.strassburger.cookieclickerz.util.*;
import org.strassburger.cookieclickerz.util.storage.Storage;
import org.strassburger.cookieclickerz.util.storage.SQLiteStorage;

import java.util.List;

public final class CookieClickerZ extends JavaPlugin {
    private static CookieClickerZ instance;
    private VersionChecker versionChecker;
    private Storage storage;
    private LanguageManager languageManager;
    private ConfigManager configManager;
    private AntiCheat antiCheat;
    private HologramManager hologramManager;

    private final boolean hasPlaceholderApi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    private final boolean hasDecentHolograms = Bukkit.getPluginManager().getPlugin("DecentHolograms") != null;

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

        if (hasDecentHolograms) {
            hologramManager = new HologramManager(this);
            hologramManager.spawnAllHolograms();
        }

        versionChecker = new VersionChecker(this);

        getLogger().info("LifeStealZ enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("LifeStealZ disabled!");
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

    private Storage createStorage() {
        String option = getConfig().getString("storage.type");

        if (option.equalsIgnoreCase("mysql")) {
            getLogger().info("Using MySQL storage");
            // TODO: Implement MySQLPlayerDataStorage
            return null;
        }

        getLogger().info("Using SQLite storage");
        return new SQLiteStorage();
    }

    private void initConfig() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        configManager = new ConfigManager(this);

        for (String file : List.of("upgrades", "prestige", "clicker")) {
            configManager.getCustomConfig(file);
        }
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
