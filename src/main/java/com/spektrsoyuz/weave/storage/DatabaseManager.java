/*
 * Weave
 * Created by SpektrSoyuz
 * All Rights Reserved
 */
package com.spektrsoyuz.weave.storage;

import com.spektrsoyuz.weave.WeavePlugin;
import com.spektrsoyuz.weave.player.WeavePlayer;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.sql.*;
import java.util.UUID;
import java.util.logging.Logger;

public final class DatabaseManager {

    private final FileConfiguration config;
    private final File dataFolder;
    private final Logger logger;
    private HikariDataSource dataSource;
    private String playersTable;

    public DatabaseManager(final WeavePlugin plugin) {
        this.config = plugin.getConfig();
        this.dataFolder = plugin.getDataFolder();
        this.logger = plugin.getLogger();

        init();
        createTables();
    }

    private void init() {
        final HikariConfig hikariConfig = new HikariConfig();
        final String tablePrefix = config.getString("database.table_prefix");
        this.playersTable = tablePrefix + "_players";

        final String host = config.getString("database.host");
        final int port = config.getInt("database.port");
        final String database = config.getString("database.database");
        final String username = config.getString("database.username");
        final String password = config.getString("database.password");

        final String dbType = config.getString("database.type");
        final boolean isMySQL = "mysql".equalsIgnoreCase(dbType);

        if (isMySQL) {
            hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
            hikariConfig.setUsername(username);
            hikariConfig.setPassword(password);
            hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
            hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
            hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        } else {
            hikariConfig.setJdbcUrl("jdbc:sqlite:" + dataFolder.getAbsolutePath() + "/weave.db");
            hikariConfig.setDriverClassName("org.sqlite.JDBC");
        }

        hikariConfig.setMaximumPoolSize(10);
        dataSource = new HikariDataSource(hikariConfig);
    }

    private void createTables() {
        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            statement.addBatch("CREATE TABLE IF NOT EXISTS " + playersTable + " (id VARCHAR(36) PRIMARY KEY, username VARCHAR(16), display_name VARCHAR(128), nickname VARCHAR(64), vanished BOOLEAN);");
            statement.executeBatch();
        } catch (SQLException e) {
            logger.severe("Error creating tables: " + e.getMessage());
        }
    }

    public void saveWeavePlayer(final WeavePlayer weavePlayer) {
        try (Connection connection = getConnection()) {
            final PreparedStatement upsert = connection.prepareStatement("INSERT INTO " + playersTable + " (id, username, display_name, nickname, vanished) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE username = ?, display_name = ?, nickname = ?, vanished = ?;");
            // insert
            upsert.setString(1, weavePlayer.getMojangId().toString());
            upsert.setString(2, weavePlayer.getUsername());
            upsert.setString(3, weavePlayer.getDisplayName());
            upsert.setString(4, weavePlayer.getNickname());
            upsert.setBoolean(5, weavePlayer.isVanished());
            // update
            upsert.setString(6, weavePlayer.getUsername());
            upsert.setString(7, weavePlayer.getDisplayName());
            upsert.setString(8, weavePlayer.getNickname());
            upsert.setBoolean(9, weavePlayer.isVanished());
            upsert.execute();
        } catch (SQLException e) {
            logger.severe("Error saving weave player: " + e.getMessage());
        }
    }

    public WeavePlayer queryWeavePlayer(final UUID mojangId) {
        WeavePlayer weavePlayer = null;
        try (Connection connection = getConnection()) {
            final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + playersTable + " WHERE id = ?;");
            statement.setString(1, mojangId.toString());

            final ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                final String username = resultSet.getString("username");
                final String displayName = resultSet.getString("display_name");
                final String nickname = resultSet.getString("nickname");
                final boolean vanished = resultSet.getBoolean("vanished");

                weavePlayer = new WeavePlayer(mojangId, username, displayName, nickname, vanished);
            }
        } catch (SQLException e) {
            logger.severe("Error querying weave player: " + e.getMessage());
        }
        return weavePlayer;
    }

    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
