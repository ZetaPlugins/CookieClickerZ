package org.strassburger.cookieclickerz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.strassburger.cookieclickerz.CookieClickerZ;
import org.strassburger.cookieclickerz.util.MessageUtils;
import org.strassburger.cookieclickerz.util.NumFormatter;
import org.strassburger.cookieclickerz.storage.PlayerData;
import org.strassburger.cookieclickerz.storage.Storage;

import java.math.BigInteger;

public class PlayerJoinListener implements Listener {
    private final CookieClickerZ plugin;

    public PlayerJoinListener(CookieClickerZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (plugin.getConfig().getBoolean("offlineCookies.enabled", true)) grantOfflineCookies(player);

        if (player.isOp() && plugin.getConfig().getBoolean("checkForUpdates") && plugin.getVersionChecker().NEW_VERSION_AVAILABLE) {
            player.sendMessage(MessageUtils.getAndFormatMsg(true, "newVersionAvailable", "&7A new version of CookieClickerZ is available!\\n%ac%<click:OPEN_URL:https://modrinth.com/plugin/cookieclickerz/versions>https://modrinth.com/plugin/cookieclickerz/versions</click>"));
        }
    }

    private void grantOfflineCookies(Player player) {
        Storage storage = plugin.getStorage();

        PlayerData playerData = storage.load(player.getUniqueId());

        if (playerData == null) {
            PlayerData newPlayerData = new PlayerData(player.getName(), player.getUniqueId());
            storage.save(newPlayerData);
            playerData = newPlayerData;
        }

        long lastLogoutTime = playerData.getLastLogoutTime();
        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - lastLogoutTime;
        int timeDifferenceMinutes = (int) (timeDifference / 1000 / 60);

        if (timeDifferenceMinutes > 0) {
            BigInteger offlineCookies = playerData.getOfflineCookies();
            BigInteger cookiesToAdd = offlineCookies.multiply(BigInteger.valueOf(timeDifferenceMinutes));
            playerData.setTotalCookies(playerData.getTotalCookies().add(cookiesToAdd));
            storage.save(playerData);

            plugin.getLogger().info("Player " + player.getName() + " earned " + cookiesToAdd + " cookies for being offline for " + timeDifferenceMinutes + " minutes.");

            if (plugin.getConfig().getBoolean("offlineCookies.joinMessage", true)) {
                player.sendMessage(MessageUtils.getAndFormatMsg(true, "offlineCookies", "&7You earned &e%ac%%amount%&7 cookies while you were offline!", new MessageUtils.Replaceable<>("%amount%", NumFormatter.formatBigInt(cookiesToAdd))));
            }
        }
    }
}
