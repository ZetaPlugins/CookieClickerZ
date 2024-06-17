package org.strassburger.cookieclickerz.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.strassburger.cookieclickerz.util.ClickerManager;
import org.strassburger.cookieclickerz.util.MessageUtils;

public class PlayerInteractionListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null) return;

        Location clickedLocation = clickedBlock.getLocation();

        if (ClickerManager.isClicker(clickedLocation)) {
            if (event.getAction().isLeftClick() && event.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (!player.hasPermission("cookieclickerz.useclicker")) {
                    throwPermissionError(player);
                    return;
                }

                player.sendMessage("You got a cookie!");
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
