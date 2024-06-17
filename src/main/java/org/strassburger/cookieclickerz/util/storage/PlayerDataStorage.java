package org.strassburger.cookieclickerz.util.storage;

import java.util.List;
import java.util.UUID;

public interface PlayerDataStorage {
    void init();

    void save(PlayerData playerData);

    PlayerData load(String uuid);

    PlayerData load(UUID uuid);

    String export(String fileName);

    void importData(String fileName);
}