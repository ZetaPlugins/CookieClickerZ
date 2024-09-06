package org.strassburger.cookieclickerz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.strassburger.cookieclickerz.CookieClickerZ;
import org.strassburger.cookieclickerz.util.MessageUtils;
import org.strassburger.cookieclickerz.util.storage.PlayerData;
import org.strassburger.cookieclickerz.util.storage.Storage;

import java.util.List;

public class PlayerJoinListener implements Listener {
    private final CookieClickerZ plugin;

    public PlayerJoinListener(CookieClickerZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Storage storage = plugin.getStorage();

        List<String> worldWhitelisted = plugin.getConfig().getStringList("worlds");
        if (!worldWhitelisted.contains(player.getLocation().getWorld().getName())) return;

        PlayerData playerData = storage.load(player.getUniqueId());

        if (playerData == null) {
            PlayerData newPlayerData = new PlayerData(player.getName(), player.getUniqueId());
            storage.save(newPlayerData);
            playerData = newPlayerData;
        }

        if (player.isOp() && plugin.getConfig().getBoolean("checkForUpdates") && plugin.getVersionChecker().NEW_VERSION_AVAILABLE) {
            player.sendMessage(MessageUtils.getAndFormatMsg(true, "newVersionAvailable", "&7A new version of CookieClickerZ is available!\\n%ac%<click:OPEN_URL:https://modrinth.com/plugin/lifestealz/versions>https://modrinth.com/plugin/lifestealz/versions</click>"));
        }
    }
}
