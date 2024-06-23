package org.strassburger.cookieclickerz.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
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
import java.util.List;

public class PlayerInteractionListener implements Listener {
    private final CookieClickerZ plugin = CookieClickerZ.getInstance();
    private final FileConfiguration config = plugin.getConfig();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        PlayerDataStorage playerDataStorage = plugin.getPlayerDataStorage();

        if (clickedBlock == null) return;

        Location clickedLocation = clickedBlock.getLocation();

        if (ClickerManager.isClicker(clickedLocation)) {
            if (event.getAction().isLeftClick() && event.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (!player.hasPermission("cookieclickerz.useclicker")) {
                    throwPermissionError(player);
                    return;
                }

                if (config.getBoolean("anticheat.cps.enabled", true) && plugin.getAntiCheat().getCps(player.getUniqueId()) >= config.getInt("anticheat.cps.max", 15)) {
                    player.sendMessage(MessageUtils.formatMsg(config.getString("anticheat.cps.message", "&cYou are clicking too fast!")));
                    List<String> commands = config.getStringList("anticheat.cps.commands");
                    for (String command : commands) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                    }
                    return;
                }

                Long lastPlayerMove = plugin.getAntiCheat().getLastMove(player.getUniqueId());
                long maxTime = (config.getInt("anticheat.nomovement.max", 15) * 1000L);
                if (config.getBoolean("anticheat.nomovement.enabled", true) && lastPlayerMove != null && System.currentTimeMillis() - lastPlayerMove > maxTime) {
                    player.sendMessage(MessageUtils.formatMsg(config.getString("anticheat.nomovement.message", "&cYou are not moving!")));
                    player.playSound(player.getLocation(), Sound.valueOf(config.getString("errorSound", "ENTITY_VILLAGER_NO")), 1, 1);
                    List<String> commands = config.getStringList("anticheat.nomovement.commands");
                    for (String command : commands) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                    }
                    return;
                }

                player.playSound(player.getLocation(), Sound.valueOf(config.getString("clickSound", "BLOCK_WOODEN_BUTTON_CLICK_ON")), 1, 1);

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

                if (!MainGUI.isOpen(player)) {
                    player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, .3f, 1f);
                    MainGUI.open(player);
                }
            }
        }
    }

    private void throwPermissionError(Player player) {
        Component msg = MessageUtils.getAndFormatMsg(false, "noPermissionError", "&cYou don't have permission to use this!");
        player.sendMessage(msg);
    }
}
