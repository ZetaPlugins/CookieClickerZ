package com.zetaplugins.cookieclickerz.util.leaderboard;

import com.zetaplugins.cookieclickerz.CookieClickerZ;

import java.util.*;

public final class LeaderBoardService {
    private final CookieClickerZ plugin;
    private volatile List<LeaderBoardEntry> topCookiesPerClick;
    private volatile List<LeaderBoardEntry> topOfflineCookies;
    private volatile List<LeaderBoardEntry> topPrestige;
    private volatile List<LeaderBoardEntry> topTotalClicks;
    private volatile List<LeaderBoardEntry> topTotalCookies;

    public LeaderBoardService(CookieClickerZ plugin) {
        this.plugin = plugin;
        this.topCookiesPerClick = Collections.emptyList();
        this.topOfflineCookies = Collections.emptyList();
        this.topPrestige = Collections.emptyList();
        this.topTotalClicks = Collections.emptyList();
        this.topTotalCookies = Collections.emptyList();
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
            topCookiesPerClick = plugin.getStorage().getTopCookiesPerClick(leaderboardSize);
            topOfflineCookies = plugin.getStorage().getTopOfflineCookies(leaderboardSize);
            topPrestige = plugin.getStorage().getTopPrestige(leaderboardSize);
            topTotalClicks = plugin.getStorage().getTopTotalClicks(leaderboardSize);
            topTotalCookies = plugin.getStorage().getTopTotalCookies(leaderboardSize);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to refresh leaderboard: " + e.getMessage());
        }
    }

    public List<LeaderBoardEntry> getTopCookiesPerClick() {
        return topCookiesPerClick;
    }

    public List<LeaderBoardEntry> getTopOfflineCookies() {
        return topOfflineCookies;
    }

    public List<LeaderBoardEntry> getTopPrestige() {
        return topPrestige;
    }

    public List<LeaderBoardEntry> getTopTotalClicks() {
        return topTotalClicks;
    }

    public List<LeaderBoardEntry> getTopTotalCookies() {
        return topTotalCookies;
    }
}
