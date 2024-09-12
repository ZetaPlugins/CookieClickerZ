package org.strassburger.cookieclickerz.util;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.strassburger.cookieclickerz.CookieClickerZ;

import java.util.List;
import java.util.stream.Collectors;

public class HologramManager {
    private final CookieClickerZ plugin;

    public HologramManager(CookieClickerZ plugin) {
        this.plugin = plugin;
    }

    public void spawnAllHolograms() {
        plugin.getClickerManager().getClickers().forEach(this::spawnHologram);
    }

    public void spawnHologram(@NotNull ClickerManager.Clicker clicker) {
        if (!plugin.getConfig().getBoolean("hologram")) return;
        if (DHAPI.getHologram("cc_" + clicker.getName()) != null) return;

        double offset = plugin.getLanguageManager().getDouble("clickerHologramOffset");

        Location location = clicker.getLocation().clone().add(0.5, offset, 0.5);
        List<String> lines = plugin.getLanguageManager().getStringList("clickerHologram").stream()
                        .map(MessageUtils::replacePlaceholders)
                        .map(MessageUtils::convertToLegacy)
                        .collect(Collectors.toList());

        Hologram hologram = DHAPI.createHologram("cc_" + clicker.getName(), location);
        for (String line : lines) {
            DHAPI.addHologramLine(hologram, line);
        }
    }

    public void removeHologram(@NotNull String clickerName) {
        Hologram hologram = DHAPI.getHologram("cc_" + clickerName);
        if (hologram != null) DHAPI.removeHologram("cc_" + clickerName);
    }

    public void removeHologram(@NotNull ClickerManager.Clicker clicker) {
        removeHologram(clicker.getName());
    }
}
