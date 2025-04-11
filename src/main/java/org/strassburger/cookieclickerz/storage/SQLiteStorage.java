package org.strassburger.cookieclickerz.storage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.strassburger.cookieclickerz.CookieClickerZ;
import org.strassburger.cookieclickerz.util.achievements.Achievement;

import java.io.*;
import java.math.BigInteger;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class SQLiteStorage extends Storage {
    private final Map<UUID , PlayerData> playerDataCache = new ConcurrentHashMap<>();
    private static final String CSV_SEPARATOR = ",";

    public SQLiteStorage(CookieClickerZ plugin) {
        super(plugin);

        // Multiply by 20 to convert seconds to ticks
        long saveInterval = plugin.getConfig().getInt("enabled.saveInterval", 60) * 20L;

        new BukkitRunnable() {
            @Override
            public void run() {
                saveAllCachedData();
            }
        }.runTaskTimerAsynchronously(plugin, saveInterval, saveInterval);
    }

    private boolean shouldUsePlayerCache() {
        return getPlugin().getConfig().getBoolean("playerCache.enabled", true);
    }

    private int getMaxCacheSize() {
        return getPlugin().getConfig().getInt("playerCache.maxSize", 1000);
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

    private Connection createConnection() {
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

        PlayerData playerData = loadPlayerData(uuid);
        if (playerData != null) {
            loadUpgrades(uuid, playerData);
            loadAchievements(uuid, playerData);
        }

        if (shouldUsePlayerCache()) {
            playerDataCache.put(uuid, playerData);
            if (playerDataCache.size() > getMaxCacheSize()) saveAllCachedData();
        }
        return playerData;
    }

    private PlayerData loadPlayerData(UUID uuid) {
        final String query = "SELECT * FROM players WHERE uuid = ?";

        try (Connection connection = createConnection()) {
            if (connection == null) return null;

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, uuid.toString());
                ResultSet resultSet = statement.executeQuery();

                // If no player data is found, create a new PlayerData object
                if (!resultSet.next()) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null) return null;
                    PlayerData newPlayerData = new PlayerData(player.getName(), uuid);
                    save(newPlayerData); // Save new player data
                    return newPlayerData;
                }

                PlayerData playerData = new PlayerData(resultSet.getString("name"), uuid);
                playerData.setTotalCookies(new BigInteger(resultSet.getString("totalCookies")));
                playerData.setTotalClicks(resultSet.getInt("totalClicks"));
                playerData.setLastLogoutTime(resultSet.getLong("lastLogoutTime"));
                playerData.setCookiesPerClick(new BigInteger(resultSet.getString("cookiesPerClick")));
                playerData.setOfflineCookies(new BigInteger(resultSet.getString("offlineCookies")));
                playerData.setPrestige(resultSet.getInt("prestige"));

                return playerData;
            } catch (SQLException e) {
                getPlugin().getLogger().severe("Failed to load player data from SQLite database: " + e.getMessage());
                return null;
            }
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to load player data from SQLite database: " + e.getMessage());
            return null;
        }
    }

    private void loadUpgrades(UUID uuid, PlayerData playerData) {
        final String query = "SELECT * FROM upgrades WHERE uuid = ?";

        try (Connection connection = createConnection()) {
            if (connection == null) return;

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, uuid.toString());
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    String upgradeName = resultSet.getString("upgrade_name");
                    int level = resultSet.getInt("level");
                    playerData.addUpgrade(upgradeName, level);
                }
            } catch (SQLException e) {
                getPlugin().getLogger().severe("Failed to load upgrades from SQLite database: " + e.getMessage());
            }
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to load upgrades from SQLite database: " + e.getMessage());
        }
    }

    private void loadAchievements(UUID uuid, PlayerData playerData) {
        final String query = "SELECT * FROM achievements WHERE uuid = ?";

        try (Connection connection = createConnection()) {
            if (connection == null) return;

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, uuid.toString());
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    String achievementSlug = resultSet.getString("achievement_name");
                    int progress = resultSet.getInt("progress");
                    playerData.setAchievementProgress(achievementSlug, progress);
                }
            } catch (SQLException e) {
                getPlugin().getLogger().severe("Failed to load achievements from SQLite database: " + e.getMessage());
            }
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to load achievements from SQLite database: " + e.getMessage());
        }
    }

    @Override
    public PlayerData load(String uuid) {
        return load(UUID.fromString(uuid));
    }

    @Override
    public String export(String fileName) {
        String filePath = getPlugin().getDataFolder().getPath() + "/" + fileName + ".csv";
        try (Connection connection = createConnection()) {
            if (connection == null) return null;

            try (Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery("SELECT * FROM players");

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                    while (resultSet.next()) {
                        String line = resultSet.getString("uuid") + CSV_SEPARATOR +
                                resultSet.getString("name") + CSV_SEPARATOR +
                                resultSet.getString("totalCookies") + CSV_SEPARATOR +
                                resultSet.getInt("totalClicks") + CSV_SEPARATOR +
                                resultSet.getLong("lastLogoutTime") + CSV_SEPARATOR +
                                resultSet.getString("cookiesPerClick") + CSV_SEPARATOR +
                                resultSet.getString("offlineCookies") + CSV_SEPARATOR +
                                resultSet.getInt("prestige");
                        writer.write(line);
                        writer.newLine();
                    }
                }
            } catch (SQLException | IOException e) {
                getPlugin().getLogger().severe("Failed to export player data to CSV file: " + e.getMessage());
                return null;
            }
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to export player data to CSV file: " + e.getMessage());
            return null;
        }
        return filePath;
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

    /**
     * Get all players from the database. The playerdata does not include upgrades or achievements.
     * @return A list of PlayerData objects representing all players in the database.
     */
    public List<PlayerData> getAllPlayers() {
        List<PlayerData> players = new ArrayList<>();

        try (Connection connection = createConnection()) {
            if (connection == null) return players;

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM players");

            // Process each result and create PlayerData objects
            while (rs.next()) {
                PlayerData player = new PlayerData(rs.getString("name"), UUID.fromString(rs.getString("uuid")));
                player.setTotalCookies(new BigInteger(rs.getString("totalCookies")));
                player.setTotalClicks(rs.getInt("totalClicks"));
                player.setLastLogoutTime(rs.getLong("lastLogoutTime"));
                player.setCookiesPerClick(new BigInteger(rs.getString("cookiesPerClick")));
                player.setOfflineCookies(new BigInteger(rs.getString("offlineCookies")));
                player.setPrestige(rs.getInt("prestige"));
                players.add(player);
            }
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to load upgrades from SQLite database: " + e.getMessage());
        }

        return players;
    }

    @Override
    public void saveAllCachedData() {
        if (playerDataCache.isEmpty()) return;

        Map<UUID, PlayerData> snapshot = new HashMap<>(playerDataCache);
        playerDataCache.clear();

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
            try (PreparedStatement psUpgrades = connection.prepareStatement(
                    "INSERT INTO upgrades (uuid, upgrade_name, level) " +
                            "VALUES (?, ?, ?) " +
                            "ON CONFLICT(uuid, upgrade_name) DO UPDATE SET level = excluded.level")) {
                for (PlayerData data : snapshot.values()) {
                    for (Map.Entry<String, Integer> upgrade : data.getUpgrades().entrySet()) {
                        psUpgrades.setString(1, data.getUuid().toString());
                        psUpgrades.setString(2, upgrade.getKey());
                        psUpgrades.setInt(3, upgrade.getValue());
                        psUpgrades.addBatch();
                    }
                }
                psUpgrades.executeBatch();
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
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to flush player data cache: " + e.getMessage());
        }
    }
}
