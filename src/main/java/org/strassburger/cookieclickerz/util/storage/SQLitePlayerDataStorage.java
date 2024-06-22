package org.strassburger.cookieclickerz.util.storage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.strassburger.cookieclickerz.CookieClickerZ;

import java.io.*;
import java.math.BigInteger;
import java.sql.*;
import java.util.Map;
import java.util.UUID;

public class SQLitePlayerDataStorage implements PlayerDataStorage {
    private static final String CSV_SEPARATOR = ",";

    @Override
    public void init() {
        try (Connection connection = createConnection()) {
            if (connection == null) return;
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS player_data (" +
                        "uuid TEXT PRIMARY KEY, " +
                        "name TEXT, " +
                        "totalCookies TEXT, " +
                        "totalClicks INTEGER, " +
                        "lastLogoutTime INTEGER, " +
                        "cookiesPerClick TEXT, " +
                        "offlineCookies TEXT)");
            } catch (SQLException e) {
                CookieClickerZ.getInstance().getLogger().severe("Failed to initialize SQLite database: " + e.getMessage());
            }

            // Create upgrades table if not exists
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS upgrades (" +
                        "uuid TEXT, " +
                        "upgrade_name TEXT, " +
                        "level INTEGER, " +
                        "PRIMARY KEY (uuid, upgrade_name), " +
                        "FOREIGN KEY (uuid) REFERENCES player_data(uuid))");
            } catch (SQLException e) {
                CookieClickerZ.getInstance().getLogger().severe("Failed to initialize upgrades table in SQLite database: " + e.getMessage());
            }
        } catch (SQLException e) {
            CookieClickerZ.getInstance().getLogger().severe("Failed to initialize SQLite database: " + e.getMessage());
        }
    }

    private Connection createConnection() {
        try {
            CookieClickerZ plugin = CookieClickerZ.getInstance();
            String pluginFolderPath = plugin.getDataFolder().getPath();
            return DriverManager.getConnection("jdbc:sqlite:" + pluginFolderPath + "/userData.db");
        } catch (SQLException e) {
            CookieClickerZ.getInstance().getLogger().severe("Failed to create connection to SQLite database: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void save(PlayerData playerData) {
        try (Connection connection = createConnection()) {
            if (connection == null) return;
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT OR REPLACE INTO player_data (uuid, name, totalCookies, totalClicks, lastLogoutTime, cookiesPerClick, offlineCookies) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                statement.setString(1, playerData.getUuid());
                statement.setString(2, playerData.getName());
                statement.setString(3, playerData.getTotalCookies().toString());
                statement.setInt(4, playerData.getTotalClicks());
                statement.setLong(5, playerData.getLastLogoutTime());
                statement.setString(6, playerData.getCookiesPerClick().toString());
                statement.setString(7, playerData.getOfflineCookies().toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                CookieClickerZ.getInstance().getLogger().severe("Failed to save player data to SQLite database: " + e.getMessage());
            }

            // Save upgrades
            saveUpgrades(connection, playerData);
        } catch (SQLException e) {
            CookieClickerZ.getInstance().getLogger().severe("Failed to save player data to SQLite database: " + e.getMessage());
        }
    }

    private void saveUpgrades(Connection connection, PlayerData playerData) throws SQLException {
        // Clear existing upgrades for the player
        try (PreparedStatement deleteStatement = connection.prepareStatement(
                "DELETE FROM upgrades WHERE uuid = ?")) {
            deleteStatement.setString(1, playerData.getUuid());
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            CookieClickerZ.getInstance().getLogger().severe("Failed to clear existing upgrades for player: " + e.getMessage());
            throw e;
        }

        // Save current upgrades
        try (PreparedStatement insertStatement = connection.prepareStatement(
                "INSERT INTO upgrades (uuid, upgrade_name, level) VALUES (?, ?, ?)")) {
            for (Map.Entry<String, Integer> entry : playerData.getUpgrades().entrySet()) {
                insertStatement.setString(1, playerData.getUuid());
                insertStatement.setString(2, entry.getKey());
                insertStatement.setInt(3, entry.getValue());
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            CookieClickerZ.getInstance().getLogger().severe("Failed to save upgrades for player: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public PlayerData load(UUID uuid) {
        PlayerData playerData = loadPlayerData(uuid);
        if (playerData != null) {
            loadUpgrades(uuid, playerData);
        }
        return playerData;
    }

    private PlayerData loadPlayerData(UUID uuid) {
        try (Connection connection = createConnection()) {
            if (connection == null) return null;
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM player_data WHERE uuid = ?")) {
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

                return playerData;
            } catch (SQLException e) {
                CookieClickerZ.getInstance().getLogger().severe("Failed to load player data from SQLite database: " + e.getMessage());
                return null;
            }
        } catch (SQLException e) {
            CookieClickerZ.getInstance().getLogger().severe("Failed to load player data from SQLite database: " + e.getMessage());
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
                CookieClickerZ.getInstance().getLogger().severe("Failed to load upgrades from SQLite database: " + e.getMessage());
            }
        } catch (SQLException e) {
            CookieClickerZ.getInstance().getLogger().severe("Failed to load upgrades from SQLite database: " + e.getMessage());
        }
    }

    @Override
    public PlayerData load(String uuid) {
        return load(UUID.fromString(uuid));
    }

    @Override
    public String export(String fileName) {
        String filePath = CookieClickerZ.getInstance().getDataFolder().getPath() + "/" + fileName + ".csv";
        try (Connection connection = createConnection()) {
            if (connection == null) return null;

            try (Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery("SELECT * FROM player_data");

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                    while (resultSet.next()) {
                        StringBuilder line = new StringBuilder();
                        line.append(resultSet.getString("uuid")).append(CSV_SEPARATOR)
                                .append(resultSet.getString("name")).append(CSV_SEPARATOR)
                                .append(resultSet.getString("totalCookies")).append(CSV_SEPARATOR)
                                .append(resultSet.getInt("totalClicks")).append(CSV_SEPARATOR)
                                .append(resultSet.getLong("lastLogoutTime")).append(CSV_SEPARATOR)
                                .append(resultSet.getString("cookiesPerClick")).append(CSV_SEPARATOR)
                                .append(resultSet.getString("offlineCookies"));
                        writer.write(line.toString());
                        writer.newLine();
                    }
                }
            } catch (SQLException | IOException e) {
                CookieClickerZ.getInstance().getLogger().severe("Failed to export player data to CSV file: " + e.getMessage());
                return null;
            }
        } catch (SQLException e) {
            CookieClickerZ.getInstance().getLogger().severe("Failed to export player data to CSV file: " + e.getMessage());
            return null;
        }
        return filePath;
    }

    @Override
    public void importData(String fileName) {
        String filePath = CookieClickerZ.getInstance().getDataFolder().getPath() + "/" + fileName;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(CSV_SEPARATOR);

                if (data.length != 7) {
                    CookieClickerZ.getInstance().getLogger().severe("Invalid CSV format.");
                    continue;
                }

                try (Connection connection = createConnection()) {
                    if (connection == null) return;
                    try (PreparedStatement statement = connection.prepareStatement(
                            "INSERT OR REPLACE INTO player_data (uuid, name, totalCookies, totalClicks, lastLogoutTime, cookiesPerClick, offlineCookies) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                        statement.setString(1, data[0]);
                        statement.setString(2, data[1]);
                        statement.setString(3, data[2]);
                        statement.setInt(4, Integer.parseInt(data[3]));
                        statement.setLong(5, Long.parseLong(data[4]));
                        statement.setString(6, data[5]);
                        statement.setString(7, data[6]);
                        statement.executeUpdate();
                    } catch (SQLException e) {
                        CookieClickerZ.getInstance().getLogger().severe("Failed to import player data from CSV file: " + e.getMessage());
                    }
                } catch (SQLException e) {
                    CookieClickerZ.getInstance().getLogger().severe("Failed to import player data from CSV file: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            CookieClickerZ.getInstance().getLogger().severe("Failed to read CSV file: " + e.getMessage());
        }
    }
}
