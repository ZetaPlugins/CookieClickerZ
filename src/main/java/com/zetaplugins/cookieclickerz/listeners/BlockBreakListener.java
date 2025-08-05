package com.zetaplugins.cookieclickerz.listeners;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import com.zetaplugins.cookieclickerz.CookieClickerZ;

public class BlockBreakListener implements Listener {
    private final CookieClickerZ plugin;

    public BlockBreakListener(CookieClickerZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (plugin.getClickerManager().isClicker(block.getLocation())) event.setCancelled(true);
    }
}
