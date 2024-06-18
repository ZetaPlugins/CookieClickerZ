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
import org.strassburger.cookieclickerz.util.NumFormatter;
import org.strassburger.cookieclickerz.util.Replaceable;
import org.strassburger.cookieclickerz.util.gui.MainGUI;
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

                BigInteger cookiesPerClick = playerData.getCookiesPerClick();

                playerData.setTotalCookies(playerData.getTotalCookies().add(cookiesPerClick));
                playerData.setTotalClicks(playerData.getTotalClicks() + 1);
                playerDataStorage.save(playerData);

                player.sendActionBar(
                        MessageUtils.getAndFormatMsg(
                                false,
                                "getCookieActionbar",
                                "%ac%+%num% %cookieName%&7 &8| %ac%%total% %cookieName%&7",
                                new Replaceable("%num%", NumFormatter.formatBigInt(cookiesPerClick)),
                                new Replaceable("%cookieName%", CookieClickerZ.getInstance().getConfig().getString("cookieName", "<#D2691E>Cookies")),
                                new Replaceable("%total%", NumFormatter.formatBigInt(playerData.getTotalCookies()))                        )
                );
            }

            if (event.getAction().isRightClick() && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (!player.hasPermission("cookieclickerz.openshop")) {
                    throwPermissionError(player);
                    return;
                }

                if (!MainGUI.isOpen(player)) MainGUI.open(player);
            }
        }
    }

    private void throwPermissionError(Player player) {
        Component msg = MessageUtils.getAndFormatMsg(false, "noPermissionError", "&cYou don't have permission to use this!");
        player.sendMessage(msg);
    }
}
