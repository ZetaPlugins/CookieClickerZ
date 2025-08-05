package com.zetaplugins.cookieclickerz.util.holograms;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import com.zetaplugins.cookieclickerz.CookieClickerZ;
import com.zetaplugins.cookieclickerz.util.ClickerManager;

/**
 * A concrete implementation of HologramManager
 * that uses the FancyHolograms API to manage holograms in the CookieClickerZ plugin.
 */
public final class FancyHologramManager extends HologramManager {
    private final de.oliver.fancyholograms.api.HologramManager fancyHologramManager;

    public FancyHologramManager(CookieClickerZ plugin) {
        super(plugin);
        fancyHologramManager = FancyHologramsPlugin.get().getHologramManager();
    }

    @Override
    public void spawnHologram(ClickerManager.@NotNull Clicker clicker) {
        if (!getPlugin().getConfig().getBoolean("hologram")) return;
        if (fancyHologramManager.getHologram(getHologramName(clicker)).isPresent()) return;

        TextHologramData hologramData = new TextHologramData(getHologramName(clicker), getHologramLocation(clicker));
        hologramData.setText(getHologramLines(clicker));
        Hologram hologram = fancyHologramManager.create(hologramData);
        fancyHologramManager.addHologram(hologram);
    }

    @Override
    public void removeHologram(@NotNull String clickerName) {
        fancyHologramManager
                .getHologram(getHologramName(clickerName))
                .ifPresent(fancyHologramManager::removeHologram);
    }

    @Override
    protected Location getHologramLocation(@NotNull ClickerManager.Clicker clicker) {
        double offset = getPlugin().getLanguageManager().getDouble("clickerHologramOffset");
        return clicker.getLocation().clone().add(0.5, offset - 0.5, 0.5);
    }
}
