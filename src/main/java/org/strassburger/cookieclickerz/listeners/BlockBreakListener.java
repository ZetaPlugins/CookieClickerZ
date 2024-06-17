package org.strassburger.cookieclickerz.listeners;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.strassburger.cookieclickerz.util.ClickerManager;

public class BlockBreakListener implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (ClickerManager.isClicker(block.getLocation())) event.setCancelled(true);
    }
}
