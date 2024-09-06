package org.strassburger.cookieclickerz.util;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class AntiCheat implements Listener {
    private final JavaPlugin plugin;
    private final HashMap<UUID, ClickData> playerClicks = new HashMap<>();
    private final HashMap<UUID, Long> lastMove = new HashMap<>();

    public AntiCheat(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        startCpsCalculationTask();
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        playerClicks.putIfAbsent(playerId, new ClickData());
        ClickData clickData = playerClicks.get(playerId);
        clickData.incrementClicks();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        lastMove.put(playerId, System.currentTimeMillis());
    }

    private void startCpsCalculationTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID playerId : playerClicks.keySet()) {
                    ClickData clickData = playerClicks.get(playerId);
                    clickData.updateCps();
                }
            }
        }.runTaskTimer(plugin, 20, 20); // Run every second (20 ticks)
    }

    public double getCps(UUID playerId) {
        ClickData clickData = playerClicks.get(playerId);
        return clickData != null ? clickData.getCps() : 0.0;
    }

    public Long getLastMove(UUID playerId) {
        return lastMove.get(playerId);
    }

    private static class ClickData {
        private int clicks = 0;
        private double cps = 0.0;

        public void incrementClicks() {
            clicks++;
        }

        public void updateCps() {
            cps = clicks;
            clicks = 0;
        }

        public double getCps() {
            return cps;
        }
    }
}
