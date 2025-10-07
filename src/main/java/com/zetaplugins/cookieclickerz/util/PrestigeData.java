package com.zetaplugins.cookieclickerz.util;

import org.bukkit.configuration.file.FileConfiguration;
import com.zetaplugins.cookieclickerz.CookieClickerZ;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * A class representing the data for a prestige level in the CookieClickerZ plugin.
 * It loads the prestige level data from the configuration file based on the provided level.
 */
public class PrestigeData {
    private final int level;
    private final String name;
    private final BigInteger cost;
    private final int multiplier;
    private final List<String> commands;
    private final List<String> additionalLore;
    private final boolean hideOriginalLore;

    /**
     * Constructor that initializes the PrestigeData object by loading data from the configuration file.
     * @param plugin The main plugin instance.
     * @param level The prestige level to load data for.
     */
    public PrestigeData(CookieClickerZ plugin, int level) {
        FileConfiguration config = plugin.getConfigManager().getPrestigeConfig();

        if (level == 0) {
            this.level = 0;
            this.name = "None";
            this.cost = BigInteger.ZERO;
            this.multiplier = 1;
            this.commands = new ArrayList<>();
            this.additionalLore = new ArrayList<>();
            this.hideOriginalLore = false;
            return;
        }

        this.level = level;
        this.name = config.getString("levels." + level + ".name");
        this.cost = NumFormatter.stringToBigInteger(config.getString("levels." + level + ".cost", "0"));
        this.multiplier = config.getInt("levels." + level + ".multiplier");
        this.commands = config.getStringList("levels." + level + ".commands");
        this.additionalLore = config.getStringList("levels." + level + ".lore");
        this.hideOriginalLore = config.getBoolean("levels." + level + ".hideOriginalLore", false);
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public BigInteger getCost() {
        return cost;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public List<String> getCommands() {
        return commands;
    }

    public List<String> getAdditionalLore() {
        return additionalLore;
    }

    public boolean shouldHideOriginalLore() {
        return hideOriginalLore;
    }
}
