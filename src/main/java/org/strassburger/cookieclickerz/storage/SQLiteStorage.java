package org.strassburger.cookieclickerz.storage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.strassburger.cookieclickerz.CookieClickerZ;
import org.strassburger.cookieclickerz.util.achievements.Achievement;

import java.io.*;
import java.math.BigInteger;
import java.sql.*;
import java.util.*;

public final class SQLiteStorage extends Storage {
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
                getPlugin().getLogger().severe("Failed to initialize SQLite database: " + e.getMessage());
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
                getPlugin().getLogger().severe("Failed to initialize upgrades table in SQLite database: " + e.getMessage());
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
                getPlugin().getLogger().severe("Failed to initialize achievements table in SQLite database: " + e.getMessage());
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
        try (Connection connection = createConnection()) {
            if (connection == null) return;
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT OR REPLACE INTO players (uuid, name, totalCookies, totalClicks, lastLogoutTime, cookiesPerClick, offlineCookies, prestige) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
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
        // Clear existing upgrades for the player
        try (PreparedStatement deleteStatement = connection.prepareStatement(
                "DELETE FROM upgrades WHERE uuid = ?")) {
            deleteStatement.setString(1, playerData.getUuid().toString());
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to clear existing upgrades for player: " + e.getMessage());
            throw e;
        }

        // Save current upgrades
        try (PreparedStatement insertStatement = connection.prepareStatement(
                "INSERT INTO upgrades (uuid, upgrade_name, level) VALUES (?, ?, ?)")) {
            for (Map.Entry<String, Integer> entry : playerData.getUpgrades().entrySet()) {
                insertStatement.setString(1, playerData.getUuid().toString());
                insertStatement.setString(2, entry.getKey());
                insertStatement.setInt(3, entry.getValue());
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to save upgrades for player: " + e.getMessage());
            throw e;
        }
    }

    private void saveAchievements(Connection connection, PlayerData playerData) throws SQLException {
        // Clear existing achievements for the player
        try (PreparedStatement deleteStatement = connection.prepareStatement(
                "DELETE FROM achievements WHERE uuid = ?")) {
            deleteStatement.setString(1, playerData.getUuid().toString());
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to clear existing achievements for player: " + e.getMessage());
            throw e;
        }

        // Save current achievements
        try (PreparedStatement insertStatement = connection.prepareStatement(
                "INSERT INTO achievements (uuid, achievement_name, progress) VALUES (?, ?, ?)")) {
            for (Achievement achievement : playerData.getAchievements()) {
                insertStatement.setString(1, playerData.getUuid().toString());
                insertStatement.setString(2, achievement.getType().getSlug());
                insertStatement.setInt(3, achievement.getProgress());
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to save achievements for player: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public PlayerData load(UUID uuid) {
        PlayerData playerData = loadPlayerData(uuid);
        if (playerData != null) {
            loadUpgrades(uuid, playerData);
            loadAchievements(uuid, playerData);
        }
        return playerData;
    }

    private PlayerData loadPlayerData(UUID uuid) {
        try (Connection connection = createConnection()) {
            if (connection == null) return null;
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM players WHERE uuid = ?")) {
                statement.setString(1, uuid.toString());
                ResultSet resultSet = statement.executeQuery();

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
        try (Connection connection = createConnection()) {
            if (connection == null) return;
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM upgrades WHERE uuid = ?")) {
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
        try (Connection connection = createConnection()) {
            if (connection == null) return;
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM achievements WHERE uuid = ?")) {
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
}
