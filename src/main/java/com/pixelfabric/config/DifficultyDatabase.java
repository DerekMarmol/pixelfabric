package com.pixelfabric.config;

import java.io.File;
import java.sql.*;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DifficultyDatabase {
    private static final Logger LOGGER = LoggerFactory.getLogger("PixelFabric");
    private static final String DB_PATH = "config/difficulty_settings.db";
    private static Connection connection;
    private static boolean isInitialized = false;

    public static void initDatabase() {
        if (isInitialized) {
            LOGGER.info("Database already initialized");
            return;
        }

        try {
            // Asegurar que existe el directorio config usando el path del mod
            File configDir = FabricLoader.getInstance().getConfigDir().resolve("pixelfabric").toFile();
            if (!configDir.exists() && !configDir.mkdirs()) {
                LOGGER.error("Could not create config directory");
                return;
            }

            // Construir la ruta completa de la base de datos
            File dbFile = new File(configDir, "difficulty_settings.db");
            String dbPath = dbFile.getAbsolutePath();

            // Cargar el driver SQLite explícitamente
            Class.forName("org.sqlite.JDBC");

            // Establecer la conexión
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            connection.setAutoCommit(true);

            // Crear la tabla si no existe
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS difficulty_commands (
                        command_name TEXT PRIMARY KEY,
                        is_active INTEGER DEFAULT 0
                    )
                """);
            }

            isInitialized = true;
            LOGGER.info("Database initialized successfully at: " + dbPath);

        } catch (SQLException e) {
            LOGGER.error("SQL error initializing database", e);
        } catch (ClassNotFoundException e) {
            LOGGER.error("SQLite driver not found", e);
        } catch (Exception e) {
            LOGGER.error("Unexpected error initializing database", e);
        }
    }

    public static boolean isCommandActive(String commandName) {
        if (!isInitialized || connection == null) {
            LOGGER.info("Database not initialized, initializing now...");
            initDatabase();
        }

        if (connection == null) {
            LOGGER.error("Database connection could not be established");
            return false;
        }

        try {
            if (connection.isClosed()) {
                LOGGER.warn("Database connection was closed, reopening...");
                initDatabase();
                if (connection == null || connection.isClosed()) {
                    return false;
                }
            }

            try (PreparedStatement stmt = connection.prepareStatement(
                    "SELECT is_active FROM difficulty_commands WHERE command_name = ?"
            )) {
                stmt.setString(1, commandName);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    boolean isActive = rs.getInt("is_active") == 1;
                    LOGGER.debug("Command {} is {}", commandName, isActive ? "active" : "inactive");
                    return isActive;
                } else {
                    // Si no existe, lo insertamos como desactivado
                    try (PreparedStatement insertStmt = connection.prepareStatement(
                            "INSERT INTO difficulty_commands (command_name, is_active) VALUES (?, 0)"
                    )) {
                        insertStmt.setString(1, commandName);
                        insertStmt.execute();
                        LOGGER.debug("Inserted new command {} as inactive", commandName);
                    }
                    return false;
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error checking command status: " + commandName, e);
            return false;
        }
    }

    public static void toggleCommand(String commandName) {
        if (!isInitialized || connection == null) {
            initDatabase();
        }

        try {
            if (connection == null || connection.isClosed()) {
                LOGGER.error("No valid database connection available");
                return;
            }

            try (PreparedStatement stmt = connection.prepareStatement("""
                INSERT INTO difficulty_commands (command_name, is_active)
                VALUES (?, 1)
                ON CONFLICT(command_name) DO UPDATE SET
                is_active = ((is_active + 1) % 2)
                WHERE command_name = ?
            """)) {
                stmt.setString(1, commandName);
                stmt.setString(2, commandName);
                stmt.execute();
                LOGGER.info("Toggled command status: {}", commandName);
            }
        } catch (SQLException e) {
            LOGGER.error("Error toggling command status: " + commandName, e);
        }
    }

    public static void closeDatabase() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
                isInitialized = false;
                LOGGER.info("Database closed successfully");
            }
        } catch (SQLException e) {
            LOGGER.error("Error closing database", e);
        }
    }
}