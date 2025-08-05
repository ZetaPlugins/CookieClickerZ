package com.zetaplugins.cookieclickerz.util;

import org.bukkit.configuration.file.FileConfiguration;
import com.zetaplugins.cookieclickerz.CookieClickerZ;

import java.math.BigInteger;

public class PrestigeData {
    private final int level;
    private final String name;
    private final BigInteger cost;
    private final int multiplier;

    public PrestigeData(CookieClickerZ plugin, int level) {
        FileConfiguration config = plugin.getConfigManager().getPrestigeConfig();

        if (level == 0) {
            this.level = 0;
            this.name = "None";
            this.cost = BigInteger.ZERO;
            this.multiplier = 1;
            return;
        }

        this.level = level;
        this.name = config.getString("levels." + level + ".name");
        this.cost = NumFormatter.stringToBigInteger(config.getString("levels." + level + ".cost", "0"));
        this.multiplier = config.getInt("levels." + level + ".multiplier");
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
}
