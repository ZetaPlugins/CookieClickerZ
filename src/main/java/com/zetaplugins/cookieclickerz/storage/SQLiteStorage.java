package com.zetaplugins.cookieclickerz.storage;

import com.zetaplugins.cookieclickerz.CookieClickerZ;
import com.zetaplugins.cookieclickerz.util.achievements.Achievement;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public final class SQLiteStorage extends SQLStorage {
    private final Map<UUID , PlayerData> playerDataCache = new ConcurrentHashMap<>();
    private static final String CSV_SEPARATOR = ",";

    public SQLiteStorage(CookieClickerZ plugin) {
        super(plugin);
    }

    @Override
    public void init() {
        try (Connection connection = createConnection()) {
            if (connection == null) return;

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS players (" +
                        "uuid TEXT PRIMARY KEY, " +
                        "name TEXT, " +
                        "totalCookies TEXT, " +
                        "totalClicks INTEGER, " +
                        "lastLogoutTime INTEGER, " +
                        "cookiesPerClick TEXT, " +
                        "offlineCookies TEXT," +
                        "prestige INTEGER DEFAULT 0)");
            } catch (SQLException e) {
                getPlugin().getLogger().severe("Failed to create players table in SQLite database: " + e.getMessage());
            }

            // Create upgrades table if not exists
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS upgrades (" +
                        "uuid TEXT, " +
                        "upgrade_name TEXT, " +
                        "level INTEGER, " +
                        "PRIMARY KEY (uuid, upgrade_name), " +
                        "FOREIGN KEY (uuid) REFERENCES players(uuid))");
            } catch (SQLException e) {
                getPlugin().getLogger().severe("Failed to create upgrades table in SQLite database: " + e.getMessage());
            }

            // Create achievements table if not exists
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS achievements (" +
                        "uuid TEXT, " +
                        "achievement_name TEXT, " +
                        "progress INTEGER, " +
                        "PRIMARY KEY (uuid, achievement_name), " +
                        "FOREIGN KEY (uuid) REFERENCES players(uuid))");
            } catch (SQLException e) {
                getPlugin().getLogger().severe("Failed to create achievements table in SQLite database: " + e.getMessage());
            }
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to initialize SQLite database: " + e.getMessage());
        }
    }

    Connection createConnection() {
        try {
            CookieClickerZ plugin = getPlugin();
            String pluginFolderPath = plugin.getDataFolder().getPath();
            return DriverManager.getConnection("jdbc:sqlite:" + pluginFolderPath + "/userData.db");
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to create connection to SQLite database: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void save(PlayerData playerData) {
        if (playerData == null) return;

        if (shouldUsePlayerCache() && playerDataCache.containsKey(playerData.getUuid())) {
            playerDataCache.put(playerData.getUuid(), playerData);
            return;
        }

        final String query = "INSERT OR REPLACE INTO players (uuid, name, totalCookies, totalClicks, lastLogoutTime, cookiesPerClick, offlineCookies, prestige) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = createConnection()) {
            if (connection == null) return;
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, playerData.getUuid().toString());
                statement.setString(2, playerData.getName());
                statement.setString(3, playerData.getTotalCookies().toString());
                statement.setInt(4, playerData.getTotalClicks());
                statement.setLong(5, playerData.getLastLogoutTime());
                statement.setString(6, playerData.getCookiesPerClick().toString());
                statement.setString(7, playerData.getOfflineCookies().toString());
                statement.setInt(8, playerData.getPrestige());
                statement.executeUpdate();
            } catch (SQLException e) {
                getPlugin().getLogger().severe("Failed to save player data to SQLite database: " + e.getMessage());
            }

            // Save upgrades
            saveUpgrades(connection, playerData);

            // Save achievements
            saveAchievements(connection, playerData);
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to save player data to SQLite database: " + e.getMessage());
        }
    }

    private void saveUpgrades(Connection connection, PlayerData playerData) throws SQLException {
        if (playerData.hasRemovedUpgrades()) {
            final String deleteQuery = "DELETE FROM upgrades WHERE uuid = ?";
            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                deleteStatement.setString(1, playerData.getUuid().toString());
                deleteStatement.executeUpdate();
            } catch (SQLException e) {
                getPlugin().getLogger().severe("Failed to delete upgrades for player: " + e.getMessage());
                throw e;
            }
        }

        final String query = "INSERT INTO upgrades (uuid, upgrade_name, level) " +
                "VALUES (?, ?, ?) " +
                "ON CONFLICT(uuid, upgrade_name) DO UPDATE SET level = excluded.level";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (Map.Entry<String, Integer> entry : playerData.getUpgrades().entrySet()) {
                statement.setString(1, playerData.getUuid().toString());
                statement.setString(2, entry.getKey());
                statement.setInt(3, entry.getValue());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to save upgrades for player: " + e.getMessage());
            throw e;
        }
    }

    private void saveAchievements(Connection connection, PlayerData playerData) throws SQLException {
        final String query = "INSERT INTO achievements (uuid, achievement_name, progress) " +
                "VALUES (?, ?, ?) " +
                "ON CONFLICT(uuid, achievement_name) DO UPDATE SET progress = excluded.progress";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (Achievement achievement : playerData.getAchievements()) {
                statement.setString(1, playerData.getUuid().toString());
                statement.setString(2, achievement.getType().getSlug());
                statement.setInt(3, achievement.getProgress());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to save achievements for player: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public PlayerData load(UUID uuid) {
        if (shouldUsePlayerCache() && playerDataCache.containsKey(uuid)) {
            return playerDataCache.get(uuid);
        }

        try (Connection connection = createConnection()) {
            PlayerData playerData = loadPlayerData(connection, uuid);
            if (playerData != null) {
                loadUpgrades(connection, uuid, playerData);
                loadAchievements(connection, uuid, playerData);

                if (shouldUsePlayerCache()) {
                    playerDataCache.put(uuid, playerData);
                    if (playerDataCache.size() > getMaxCacheSize()) saveAllCachedData();
                }
            }
            return playerData;
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to load player data from SQLite database: " + e.getMessage());
            return null;
        }
    }

    @Override
    public PlayerData load(String uuid) {
        return load(UUID.fromString(uuid));
    }

    @Override
    public void importData(String fileName) {
        String filePath = getPlugin().getDataFolder().getPath() + "/" + fileName;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(CSV_SEPARATOR);

                if (data.length != 8) {
                    getPlugin().getLogger().severe("Invalid CSV format.");
                    continue;
                }

                try (Connection connection = createConnection()) {
                    if (connection == null) return;
                    try (PreparedStatement statement = connection.prepareStatement(
                            "INSERT OR REPLACE INTO players (uuid, name, totalCookies, totalClicks, lastLogoutTime, cookiesPerClick, offlineCookies, prestige) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                        statement.setString(1, data[0]);
                        statement.setString(2, data[1]);
                        statement.setString(3, data[2]);
                        statement.setInt(4, Integer.parseInt(data[3]));
                        statement.setLong(5, Long.parseLong(data[4]));
                        statement.setString(6, data[5]);
                        statement.setString(7, data[6]);
                        statement.setInt(8, Integer.parseInt(data[7]));
                        statement.executeUpdate();
                    } catch (SQLException e) {
                        getPlugin().getLogger().severe("Failed to import player data from CSV file: " + e.getMessage());
                    }
                } catch (SQLException e) {
                    getPlugin().getLogger().severe("Failed to import player data from CSV file: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            getPlugin().getLogger().severe("Failed to read CSV file: " + e.getMessage());
        }
    }

    @Override
    public void saveAllCachedData() {
        if (playerDataCache.isEmpty()) return;

        Map<UUID, PlayerData> snapshot = new HashMap<>(playerDataCache);

        try (Connection connection = createConnection()) {
            if (connection == null) return;

            connection.setAutoCommit(false); // For better batch performance

            // Save all players
            try (PreparedStatement psPlayers = connection.prepareStatement(
                    "INSERT OR REPLACE INTO players (uuid, name, totalCookies, totalClicks, lastLogoutTime, cookiesPerClick, offlineCookies, prestige) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                for (PlayerData data : snapshot.values()) {
                    psPlayers.setString(1, data.getUuid().toString());
                    psPlayers.setString(2, data.getName());
                    psPlayers.setString(3, data.getTotalCookies().toString());
                    psPlayers.setInt(4, data.getTotalClicks());
                    psPlayers.setLong(5, data.getLastLogoutTime());
                    psPlayers.setString(6, data.getCookiesPerClick().toString());
                    psPlayers.setString(7, data.getOfflineCookies().toString());
                    psPlayers.setInt(8, data.getPrestige());
                    psPlayers.addBatch();
                }
                psPlayers.executeBatch();
            }

            // Save upgrades
            for (PlayerData data : snapshot.values()) {
                try {
                    saveUpgrades(connection, data);
                } catch (SQLException e) {
                    getPlugin().getLogger().log(Level.WARNING, "Failed to save upgrades for player " + data.getUuid() + ": " + e.getMessage(), e);
                }
            }

            // Save achievements
            try (PreparedStatement psAchievements = connection.prepareStatement(
                    "INSERT INTO achievements (uuid, achievement_name, progress) " +
                            "VALUES (?, ?, ?) " +
                            "ON CONFLICT(uuid, achievement_name) DO UPDATE SET progress = excluded.progress")) {
                for (PlayerData data : snapshot.values()) {
                    for (Achievement achievement : data.getAchievements()) {
                        psAchievements.setString(1, data.getUuid().toString());
                        psAchievements.setString(2, achievement.getType().getSlug());
                        psAchievements.setInt(3, achievement.getProgress());
                        psAchievements.addBatch();
                    }
                }
                psAchievements.executeBatch();
            }

            connection.commit();

            playerDataCache.keySet().removeAll(snapshot.keySet()); // Clear the cache for saved players
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to flush player data cache: " + e.getMessage());
        }
    }
}
