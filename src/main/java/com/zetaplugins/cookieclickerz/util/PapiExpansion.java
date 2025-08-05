package com.zetaplugins.cookieclickerz.util;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import com.zetaplugins.cookieclickerz.CookieClickerZ;
import com.zetaplugins.cookieclickerz.storage.PlayerData;

public class PapiExpansion extends PlaceholderExpansion {
    private final CookieClickerZ plugin;

    public PapiExpansion(CookieClickerZ plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getAuthor() {
        return "Kartoffelchipss";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "cookieclickerz";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        if (player == null || player.getPlayer() == null) return "PlayerNotFound";

        switch (identifier) {
            case "name": {
                return player.getName();
            }
            case "totalcookies": {
                PlayerData playerData = plugin.getStorage().load(player.getUniqueId());
                return playerData.getTotalCookies().toString();
            }
            case "totalcookies_formatted": {
                PlayerData playerData = plugin.getStorage().load(player.getUniqueId());
                return NumFormatter.formatBigInt(playerData.getTotalCookies());
            }
            case "cookiesperclick": {
                PlayerData playerData = plugin.getStorage().load(player.getUniqueId());
                return NumFormatter.formatBigInt(playerData.getCookiesPerClick());
            }
            case "offlinecookies": {
                PlayerData playerData = plugin.getStorage().load(player.getUniqueId());
                return NumFormatter.formatBigInt(playerData.getOfflineCookies());
            }
            case "prestige": {
                PlayerData playerData = plugin.getStorage().load(player.getUniqueId());
                return String.valueOf(playerData.getPrestige());
            }
            case "totalclicks": {
                PlayerData playerData = plugin.getStorage().load(player.getUniqueId());
                return String.valueOf(playerData.getTotalClicks());
            }
        }

        return "InvalidPlaceholder";
    }
}
