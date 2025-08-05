package com.zetaplugins.cookieclickerz.storage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.zetaplugins.cookieclickerz.CookieClickerZ;

import java.io.*;
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class SQLStorage extends Storage {
    private static final String CSV_SEPARATOR = ",";

    public SQLStorage(CookieClickerZ plugin) {
        super(plugin);
    }

    abstract Connection createConnection() throws SQLException;

    protected PlayerData loadPlayerData(Connection connection, UUID uuid) {
        final String query = "SELECT * FROM players WHERE uuid = ?";

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
    }

    protected void loadUpgrades(Connection connection, UUID uuid, PlayerData playerData) {
        final String query = "SELECT * FROM upgrades WHERE uuid = ?";

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
    }

    protected void loadAchievements(Connection connection, UUID uuid, PlayerData playerData) {
        final String query = "SELECT * FROM achievements WHERE uuid = ?";

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
