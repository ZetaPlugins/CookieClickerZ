package com.zetaplugins.cookieclickerz.util.leaderboard;

import com.zetaplugins.cookieclickerz.CookieClickerZ;

import java.util.*;

public final class LeaderBoardService {
    private final CookieClickerZ plugin;
    private volatile List<LeaderBoardEntry> topTotalCookies;
    private volatile List<LeaderBoardEntry> topCpc;

    public LeaderBoardService(CookieClickerZ plugin) {
        this.plugin = plugin;
        this.topTotalCookies = Collections.emptyList();
        this.topCpc = Collections.emptyList();
    }

    public void start() {
        if (!plugin.getConfig().getBoolean("leaderboard.enabled", true)) return;
        final int refreshInterval = plugin.getConfig().getInt("leaderboard.updateInterval", 60);
        refreshAsync();
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(
                plugin,
                this::refreshSafe,
                20L * refreshInterval,
                20L * refreshInterval
        );
    }

    private void refreshAsync() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, this::refreshSafe);
    }

    private void refreshSafe() {
        try {
            final int leaderboardSize = plugin.getConfig().getInt("leaderboard.size", 10);
            List<LeaderBoardEntry> newTopTotalCookies = plugin.getStorage().getTopCookiesPlayers(leaderboardSize);
            List<LeaderBoardEntry> newTopCpc = plugin.getStorage().getTopCpcPlayers(leaderboardSize);

            this.topTotalCookies = newTopTotalCookies;
            this.topCpc = newTopCpc;
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to refresh leaderboard: " + e.getMessage());
        }
    }

    public List<LeaderBoardEntry> getTopTotalCookies() {
        return topTotalCookies;
    }

    public List<LeaderBoardEntry> getTopCpc() {
        return topCpc;
    }
}
