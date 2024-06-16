package org.strassburger.cookieclickerz;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.strassburger.cookieclickerz.util.CommandManager;
import org.strassburger.cookieclickerz.util.EventManager;
import org.strassburger.cookieclickerz.util.LanguageManager;
import org.strassburger.cookieclickerz.util.VersionChecker;
import org.strassburger.cookieclickerz.util.storage.MySQLPlayerDataStorage;
import org.strassburger.cookieclickerz.util.storage.PlayerDataStorage;
import org.strassburger.cookieclickerz.util.storage.SQLitePlayerDataStorage;

public final class CookieClickerZ extends JavaPlugin {
    private static CookieClickerZ instance;
    private VersionChecker versionChecker;
    private PlayerDataStorage playerDataStorage;
    private LanguageManager languageManager;

    private final boolean hasPlaceholderApi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;

    @Override
    public void onEnable() {
        instance = this;

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        languageManager = new LanguageManager();

        CommandManager.registerCommands();

        EventManager.registerListeners();

        playerDataStorage = createPlayerDataStorage();
        playerDataStorage.init();

        versionChecker = new VersionChecker();

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

    public PlayerDataStorage getPlayerDataStorage() {
        return playerDataStorage;
    }

    public boolean hasPlaceholderApi() {
        return hasPlaceholderApi;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    private PlayerDataStorage createPlayerDataStorage() {
        String option = getConfig().getString("storage.type");

        if (option.equalsIgnoreCase("mysql")) {
            getLogger().info("Using MySQL storage");
            return new MySQLPlayerDataStorage();
        }

        getLogger().info("Using SQLite storage");
        return new SQLitePlayerDataStorage();
    }

    public static String locationToString(Location location) {
        return location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ();
    }

    public static Location stringToLocation(String string, World world) throws IllegalArgumentException {
        String[] parts = string.split(",");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid location string: " + string);
        }

        try {
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);

            return new Location(world, x, y, z);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in location string: " + string, e);
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
