package com.pixelfabric.mission;

import java.sql.*;
import java.util.UUID;

public class MissionDatabase {
    private static final String DB_URL = "jdbc:sqlite:mission.db";
    private static MissionDatabase instance;
    private Connection connection;

    private MissionDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            initializeTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static MissionDatabase getInstance() {
        if (instance == null) {
            instance = new MissionDatabase();
        }
        return instance;
    }

    private void initializeTables() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS active_mission (
                    id INTEGER PRIMARY KEY,
                    mission_id TEXT NOT NULL,
                    activated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS kill_progress (
                    player_uuid TEXT NOT NULL,
                    kills INTEGER DEFAULT 0,
                    blue_coin_given BOOLEAN DEFAULT FALSE,
                    PRIMARY KEY (player_uuid)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS completed_missions (
                    player_uuid TEXT NOT NULL,
                    completed BOOLEAN DEFAULT FALSE,
                    PRIMARY KEY (player_uuid)
                )
            """);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveActiveMission(String missionId) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT OR REPLACE INTO active_mission (id, mission_id) VALUES (1, ?)")) {
            stmt.setString(1, missionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getActiveMission() {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT mission_id FROM active_mission WHERE id = 1")) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("mission_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveKillProgress(UUID playerUuid, int kills, boolean blueCoinGiven) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT OR REPLACE INTO kill_progress (player_uuid, kills, blue_coin_given) VALUES (?, ?, ?)")) {
            stmt.setString(1, playerUuid.toString());
            stmt.setInt(2, kills);
            stmt.setBoolean(3, blueCoinGiven);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getKillProgress(UUID playerUuid) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT kills FROM kill_progress WHERE player_uuid = ?")) {
            stmt.setString(1, playerUuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("kills");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setMissionCompleted(UUID playerUuid, boolean completed) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT OR REPLACE INTO completed_missions (player_uuid, completed) VALUES (?, ?)")) {
            stmt.setString(1, playerUuid.toString());
            stmt.setBoolean(2, completed);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isMissionCompleted(UUID playerUuid) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT completed FROM completed_missions WHERE player_uuid = ?")) {
            stmt.setString(1, playerUuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("completed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void clearAllProgress() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM kill_progress");
            stmt.execute("DELETE FROM completed_missions");
            stmt.execute("DELETE FROM active_mission");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
