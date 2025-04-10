package org.strassburger.cookieclickerz.storage;

import org.strassburger.cookieclickerz.CookieClickerZ;

import java.util.List;
import java.util.UUID;

public abstract class Storage {
    private final CookieClickerZ plugin;

    public Storage(CookieClickerZ plugin) {
        this.plugin = plugin;
    }

    protected CookieClickerZ getPlugin() {
        return plugin;
    }

    abstract public void init();

    abstract public void save(PlayerData playerData);

    abstract public PlayerData load(String uuid);

    abstract public PlayerData load(UUID uuid);

    abstract public String export(String fileName);

    abstract public void importData(String fileName);

    abstract public List<PlayerData> getAllPlayers();
}