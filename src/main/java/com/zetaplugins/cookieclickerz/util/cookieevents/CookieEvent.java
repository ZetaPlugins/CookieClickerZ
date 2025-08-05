package com.zetaplugins.cookieclickerz.util.cookieevents;

import org.jetbrains.annotations.NotNull;
import com.zetaplugins.cookieclickerz.CookieClickerZ;

import java.util.UUID;

public class CookieEvent {
    private final CookieEventType type;
    private final UUID uuid;
    private final long startTime;

    public CookieEvent(@NotNull CookieEventType type, @NotNull UUID uuid, long startTime) {
        this.type = type;
        this.startTime = startTime;
        this.uuid = uuid;
    }

    public CookieEvent(@NotNull CookieEventType type, @NotNull UUID uuid) {
        this(type, uuid, System.currentTimeMillis());
    }

    public CookieEventType getType() {
        return type;
    }

    public long getStartTime() {
        return startTime;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void runEvent(CookieClickerZ plugin) {
        type.run(plugin, uuid);
    }
}
