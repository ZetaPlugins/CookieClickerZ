package com.zetaplugins.cookieclickerz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import com.zetaplugins.cookieclickerz.CookieClickerZ;
import com.zetaplugins.cookieclickerz.storage.PlayerData;
import com.zetaplugins.cookieclickerz.storage.Storage;

public class PlayerQuitListener implements Listener {
    private final CookieClickerZ plugin;

    public PlayerQuitListener(CookieClickerZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Storage storage = plugin.getStorage();

        PlayerData playerData = storage.load(player.getUniqueId());

        if (playerData == null) return;

        playerData.setLastLogoutTime(System.currentTimeMillis());
        storage.save(playerData);
    }
}
