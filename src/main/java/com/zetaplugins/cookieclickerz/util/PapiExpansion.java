package com.zetaplugins.cookieclickerz.util;

import com.zetaplugins.cookieclickerz.util.leaderboard.LeaderBoardEntry;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import com.zetaplugins.cookieclickerz.CookieClickerZ;
import com.zetaplugins.cookieclickerz.storage.PlayerData;

import java.util.List;

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

        if (identifier.startsWith("cookies_top_")) {
            String[] parts = identifier.split("_");
            if (parts.length >= 4) {
                try {
                    int index = Integer.parseInt(parts[2]);
                    String field = parts[3].toLowerCase();

                    List<LeaderBoardEntry> top = plugin.getLeaderBoardService().getTopTotalCookies();
                    if (index <= 0 || index > top.size()) return "N/A";

                    LeaderBoardEntry e = top.get(index - 1);
                    switch (field) {
                        case "name": return e.name;
                        case "amount": return e.totalCookies.toString();
                        case "formattedamount": return NumFormatter.formatBigInt(e.totalCookies);
                        default: return "InvalidField";
                    }
                } catch (NumberFormatException ignored) {
                    return "InvalidIndex";
                }
            }
            return "InvalidPlaceholder";
        }

        if (identifier.startsWith("cpc_top_")) {
            String[] parts = identifier.split("_");
            if (parts.length >= 4) {
                try {
                    int index = Integer.parseInt(parts[2]);
                    String field = parts[3].toLowerCase();

                    List<LeaderBoardEntry> top = plugin.getLeaderBoardService().getTopCpc();
                    if (index <= 0 || index > top.size()) return "N/A";

                    LeaderBoardEntry e = top.get(index - 1);
                    switch (field) {
                        case "name": return e.name;
                        case "amount": return e.cookiesPerClick.toString();
                        case "formattedamount": return NumFormatter.formatBigInt(e.cookiesPerClick);
                        default: return "InvalidField";
                    }
                } catch (NumberFormatException ignored) {
                    return "InvalidIndex";
                }
            }
            return "InvalidPlaceholder";
        }

        return "InvalidPlaceholder";
    }
}
