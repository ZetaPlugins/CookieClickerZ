package org.strassburger.cookieclickerz.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.strassburger.cookieclickerz.CookieClickerZ;
import org.strassburger.cookieclickerz.util.ClickerManager;
import org.strassburger.cookieclickerz.util.MessageUtils;
import org.strassburger.cookieclickerz.util.storage.PlayerData;
import org.strassburger.cookieclickerz.util.storage.PlayerDataStorage;

import java.math.BigInteger;

public class PlayerInteractionListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        PlayerDataStorage playerDataStorage = CookieClickerZ.getInstance().getPlayerDataStorage();

        if (clickedBlock == null) return;

        Location clickedLocation = clickedBlock.getLocation();

        if (ClickerManager.isClicker(clickedLocation)) {
            if (event.getAction().isLeftClick() && event.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (!player.hasPermission("cookieclickerz.useclicker")) {
                    throwPermissionError(player);
                    return;
                }

                player.playSound(player.getLocation(), Sound.valueOf(CookieClickerZ.getInstance().getConfig().getString("clickSound", "BLOCK_WOODEN_BUTTON_CLICK_ON")), 1, 1);

                PlayerData playerData = playerDataStorage.load(player.getUniqueId());
                playerData.setTotalCookies(playerData.getTotalCookies().add(new BigInteger("1")));
                playerDataStorage.save(playerData);
                player.sendMessage(MessageUtils.formatMsg("&7You got a cookie! You now have " + MessageUtils.getAccentColor() + playerData.getTotalCookies() + " &7cookies!"));
            }

            if (event.getAction().isRightClick() && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (!player.hasPermission("cookieclickerz.openshop")) {
                    throwPermissionError(player);
                    return;
                }

                player.sendMessage("You opened the clicker menu!");
            }
        }
    }

    private void throwPermissionError(Player player) {
        Component msg = MessageUtils.getAndFormatMsg(false, "noPermissionError", "&cYou don't have permission to use this!");
        player.sendMessage(msg);
    }
}
