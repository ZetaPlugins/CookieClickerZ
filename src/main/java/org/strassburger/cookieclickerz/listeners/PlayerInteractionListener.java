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
import org.bukkit.plugin.Plugin;
import org.strassburger.cookieclickerz.CookieClickerZ;
import org.strassburger.cookieclickerz.util.ClickerManager;
import org.strassburger.cookieclickerz.util.MessageUtils;
import org.strassburger.cookieclickerz.util.NumFormatter;
import org.strassburger.cookieclickerz.util.PrestigeData;
import org.strassburger.cookieclickerz.util.achievements.Achievement;
import org.strassburger.cookieclickerz.util.achievements.AchievementCategory;
import org.strassburger.cookieclickerz.util.achievements.AchievementManager;
import org.strassburger.cookieclickerz.util.achievements.AchievementType;
import org.strassburger.cookieclickerz.util.cookieevents.CookieEventManager;
import org.strassburger.cookieclickerz.util.cookieevents.CookieEventType;
import org.strassburger.cookieclickerz.util.gui.MainGUI;
import org.strassburger.cookieclickerz.util.storage.PlayerData;
import org.strassburger.cookieclickerz.util.storage.Storage;

import java.math.BigInteger;
import java.util.List;

public class PlayerInteractionListener implements Listener {
    private final CookieClickerZ plugin;

    public PlayerInteractionListener(CookieClickerZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null) return;

        Location clickedLocation = clickedBlock.getLocation();

        if (plugin.getClickerManager().isClicker(clickedLocation)) {
            if (event.getAction().isLeftClick() && event.getAction() == Action.LEFT_CLICK_BLOCK) {
                handleClickerClick(player);
            }

            if (event.getAction().isRightClick() && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                handleGuiOpen(player);
            }
        }
    }

    private void handleGuiOpen(Player player) {
        if (!player.hasPermission("cookieclickerz.openshop")) {
            throwPermissionError(player);
            return;
        }

        if (!MainGUI.isOpen(player)) {
            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, .3f, 1f);
            MainGUI.open(player);
        }
    }

    private void handleClickerClick(Player player) {
        FileConfiguration config = plugin.getConfig();
        Storage storage = plugin.getStorage();

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

        PlayerData playerData = storage.load(player.getUniqueId());

        BigInteger cookiesPerClick = playerData.getCookiesPerClick();

        int prestigeMultiplier = new PrestigeData(plugin, playerData.getPrestige()).getMultiplier();
        cookiesPerClick = cookiesPerClick.multiply(BigInteger.valueOf(prestigeMultiplier));

        BigInteger originalCookiesPerClick = new BigInteger(cookiesPerClick.toByteArray());

        CookieEventManager cookieEventManager = plugin.getCookieEventManager();
        if (cookieEventManager.hasEvent(player, CookieEventType.COOKIE_FRENZY)) {
            cookiesPerClick = cookiesPerClick.multiply(BigInteger.valueOf(7));
        }
        if (cookieEventManager.hasEvent(player, CookieEventType.CLICK_FRENZY)) {
            cookiesPerClick = cookiesPerClick.multiply(BigInteger.valueOf(777));
        }
        if (cookieEventManager.hasEvent(player, CookieEventType.CURSED_FINGER)) {
            cookiesPerClick = cookiesPerClick.divide(BigInteger.valueOf(2));
        }

        playerData.setTotalCookies(playerData.getTotalCookies().add(cookiesPerClick));
        playerData.setTotalClicks(playerData.getTotalClicks() + 1);
        storage.save(playerData);

        String addedCookiesDisplay = cookieEventManager.hasEvent(player, CookieEventType.CURSED_FINGER)
                ? "<red><st>" + NumFormatter.formatBigInt(originalCookiesPerClick) + "</st></red> " + NumFormatter.formatBigInt(cookiesPerClick)
                : NumFormatter.formatBigInt(cookiesPerClick);

        progressAchievements(plugin, player, playerData, cookiesPerClick);

        player.sendActionBar(
                MessageUtils.getAndFormatMsg(
                        false,
                        "getCookieActionbar",
                        "%ac%+%num% &7%cookieName%&7 &8| %ac%%total% &7%cookieName%&7",
                        new MessageUtils.Replaceable<>("%num%", addedCookiesDisplay),
                        new MessageUtils.Replaceable<>("%cookieName%", CookieClickerZ.getInstance().getConfig().getString("cookieName", "<#D2691E>Cookies")),
                        new MessageUtils.Replaceable<>("%total%", NumFormatter.formatBigInt(playerData.getTotalCookies()))                        )
        );

        if (plugin.getConfig().getBoolean("events.enabled", true) && plugin.getCookieEventManager().getEvents(player).isEmpty()) {
            plugin.getCookieEventManager().startRandomEvent(player);
        }
    }

    private void throwPermissionError(Player player) {
        Component msg = MessageUtils.getAndFormatMsg(false, "noPermissionError", "&cYou don't have permission to use this!");
        player.sendMessage(msg);
    }

    private void progressAchievements(CookieClickerZ plugin, Player player, PlayerData playerData, BigInteger cookiesGained) {
        for (AchievementType achievementType : AchievementType.getByCategory(AchievementCategory.CLICKS)) {
            playerData.progressAchievement(achievementType, 1, plugin);
            plugin.getStorage().save(playerData);
        }

        for (AchievementType achievementType : AchievementType.getByCategory(AchievementCategory.COOKIES)) {
            playerData.progressCookiesAchievement(achievementType, playerData.getTotalCookies(), plugin);
            plugin.getStorage().save(playerData);
        }
    }
}
