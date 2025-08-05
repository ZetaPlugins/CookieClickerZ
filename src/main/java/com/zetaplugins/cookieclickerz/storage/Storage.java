package com.zetaplugins.cookieclickerz.storage;

import org.bukkit.scheduler.BukkitRunnable;
import com.zetaplugins.cookieclickerz.CookieClickerZ;

import java.util.List;
import java.util.UUID;

public abstract class Storage {
    private final CookieClickerZ plugin;

    public Storage(CookieClickerZ plugin) {
        this.plugin = plugin;

        // Multiply by 20 to convert seconds to ticks
        long saveInterval = plugin.getConfig().getInt("enabled.saveInterval", 60) * 20L;

        new BukkitRunnable() {
            @Override
            public void run() {
                saveAllCachedData();
            }
        }.runTaskTimerAsynchronously(plugin, saveInterval, saveInterval);
    }

    protected CookieClickerZ getPlugin() {
        return plugin;
    }

    protected boolean shouldUsePlayerCache() {
        return getPlugin().getConfig().getBoolean("playerCache.enabled", true);
    }

    protected int getMaxCacheSize() {
        return getPlugin().getConfig().getInt("playerCache.maxSize", 1000);
    }

    /**
     * Initialize the storage system.
     */
    abstract public void init();

    /**
     * Save a player data object to the storage system.
     * @param playerData The player data object to save.
     */
    abstract public void save(PlayerData playerData);

    /**
     * Load a player data object from the storage system.
     * @param uuid The UUID of the player.
     * @return The player data object.
     */
    public PlayerData load(String uuid) {
        return load(UUID.fromString(uuid));
    }

    /**
     * Load a player data object from the storage system.
     * @param uuid The UUID of the player.
     * @return The player data object.
     */
    abstract public PlayerData load(UUID uuid);

    /**
     * Export the player data to a file.
     */
    abstract public String export(String fileName);

    /**
     * Import player data from a file.
     * @param fileName The name of the file to import from.
     */
    abstract public void importData(String fileName);

    /**
     * Get a List of all players in the storage system.
     * @return A List of all players. PlayerData does not contain achievements and upgrades.
     */
    abstract public List<PlayerData> getAllPlayers();

    /**
     * Save all cached data to the storage system.
     */
    abstract public void saveAllCachedData();
}