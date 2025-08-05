package com.zetaplugins.cookieclickerz.util.holograms;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.jetbrains.annotations.NotNull;
import com.zetaplugins.cookieclickerz.CookieClickerZ;
import com.zetaplugins.cookieclickerz.util.ClickerManager;

/**
 * A concrete implementation of HologramManager
 * that uses the DecentHolograms API to manage holograms in the CookieClickerZ plugin.
 */
public final class DecentHologramManager extends HologramManager {
    public DecentHologramManager(CookieClickerZ plugin) {
        super(plugin);
    }

    @Override
    public void spawnHologram(@NotNull ClickerManager.Clicker clicker) {
        if (!getPlugin().getConfig().getBoolean("hologram")) return;
        if (DHAPI.getHologram(getHologramName(clicker)) != null) return;

        Hologram hologram = DHAPI.createHologram(getHologramName(clicker), getHologramLocation(clicker));
        for (String line : getHologramLines(clicker)) {
            DHAPI.addHologramLine(hologram, line);
        }
    }

    @Override
    public void removeHologram(@NotNull String clickerName) {
        Hologram hologram = DHAPI.getHologram("cc_" + clickerName);
        if (hologram != null) DHAPI.removeHologram("cc_" + clickerName);
    }
}
