package com.zetaplugins.cookieclickerz.util.leaderboard;

import java.math.BigInteger;
import java.util.UUID;

public final class LeaderBoardEntry {
    public final UUID uuid;
    public final String name;
    public final BigInteger totalCookies;
    public final BigInteger cookiesPerClick;

    public LeaderBoardEntry(UUID uuid, String name, BigInteger totalCookies, BigInteger cookiesPerClick) {
        this.uuid = uuid;
        this.name = name;
        this.totalCookies = totalCookies;
        this.cookiesPerClick = cookiesPerClick;
    }
}
