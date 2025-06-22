package org.strassburger.cookieclickerz.util.holograms;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.strassburger.cookieclickerz.CookieClickerZ;
import org.strassburger.cookieclickerz.util.ClickerManager;
import org.strassburger.cookieclickerz.util.MessageUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Abstract class for managing holograms in the CookieClickerZ plugin.
 * This class provides methods to spawn and remove holograms for clickers.
 * It is designed to be extended by specific hologram implementations.
 */
public abstract class HologramManager {
    private final CookieClickerZ plugin;

    public HologramManager(CookieClickerZ plugin) {
        this.plugin = plugin;
    }

    protected CookieClickerZ getPlugin() {
        return plugin;
    }

    /**
     * Spawns holograms for all clickers currently managed by the ClickerManager.
     */
    public void spawnAllHolograms() {
        getPlugin().getClickerManager().getClickers().forEach(this::spawnHologram);
    }

    /**
     * Spawns a hologram for the given clicker.
     *
     * @param clicker the Clicker object
     */
    public abstract void spawnHologram(@NotNull ClickerManager.Clicker clicker);

    /**
     * Removes the hologram associated with the given clicker name.
     *
     * @param clickerName the name of the clicker
     */
    public abstract void removeHologram(@NotNull String clickerName);

    /**
     * Removes the hologram associated with the given clicker.
     *
     * @param clicker the Clicker object
     */
    public void removeHologram(@NotNull ClickerManager.Clicker clicker) {
        removeHologram(clicker.getName());
    }

    /**
     * Removes all holograms for all clickers currently managed by the ClickerManager.
     */
    public void removeAllHolograms() {
        getPlugin().getClickerManager().getClickers().forEach(this::removeHologram);
    }

    /**
     * Generates a unique hologram name based on the clicker's name.
     * The format is "cc_[clickerName]".
     *
     * @param clicker the Clicker object
     * @return the hologram name
     */
    protected String getHologramName(@NotNull ClickerManager.Clicker clicker) {
        return getHologramName(clicker.getName());
    }

    /**
     * Generates a unique hologram name based on the clicker's name.
     * The format is "cc_[clickerName]".
     *
     * @param clickerName the name of the clicker
     * @return the hologram name
     */
    protected String getHologramName(@NotNull String clickerName) {
        return "cc_" + clickerName;
    }

    /**
     * Gets the location where the hologram should be spawned for the given clicker.
     *
     * @param clicker the Clicker object
     * @return the location for the hologram
     */
    protected Location getHologramLocation(@NotNull ClickerManager.Clicker clicker) {
        double offset = getPlugin().getLanguageManager().getDouble("clickerHologramOffset");
        return clicker.getLocation().clone().add(0.5, offset, 0.5);
    }

    /**
     * Retrieves the lines to be displayed in the hologram for the given clicker.
     *
     * @param clicker the Clicker object
     * @return a list of strings representing the hologram lines
     */
    protected List<String> getHologramLines(@NotNull ClickerManager.Clicker clicker) {
        return getPlugin().getLanguageManager().getStringList("clickerHologram").stream()
                .map(MessageUtils::replacePlaceholders)
                .map(MessageUtils::convertToLegacy)
                .collect(Collectors.toList());
    }
}
