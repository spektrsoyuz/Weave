/*
 * Weave
 * Created by SpektrSoyuz
 * All Rights Reserved
 */
package com.spektrsoyuz.weave.storage;

import com.spektrsoyuz.weave.WeavePlugin;
import com.spektrsoyuz.weave.player.WeavePlayer;
import com.spektrsoyuz.weave.storage.query.WeavePlayerQuery;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public final class DatabaseManager {

    private final FileConfiguration config;
    private final Logger logger;
    private HikariDataSource dataSource;

    private static final String WEAVE_PLAYERS = "weave_players";

    public DatabaseManager(final WeavePlugin plugin) {
        this.config = plugin.getConfig();
        this.logger = plugin.getLogger();

        init();
        createTables();
    }

    private void init() {
        HikariConfig hikariConfig = new HikariConfig();

        String host = config.getString("database.host");
        int port = config.getInt("database.port");
        String database = config.getString("database.database");
        String username = config.getString("database.username");
        String password = config.getString("database.password");

        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        hikariConfig.setMaximumPoolSize(10);
        dataSource = new HikariDataSource(hikariConfig);
    }

    private void createTables() {
        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            statement.addBatch("CREATE TABLE IF NOT EXISTS " + WEAVE_PLAYERS + " (id VARCHAR(36) PRIMARY KEY, username VARCHAR(16), display_name VARCHAR(128), nickname VARCHAR(64), vanished BOOLEAN);");
            statement.executeBatch();
        } catch (SQLException e) {
            logger.severe("Error creating tables: " + e.getMessage());
        }
    }

    public CompletableFuture<Void> saveWeavePlayer(final WeavePlayer weavePlayer) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection()) {
                final PreparedStatement upsert = connection.prepareStatement("INSERT INTO " + WEAVE_PLAYERS + " (id, username, display_name, nickname, vanished) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE username = ?, display_name = ?, nickname = ?, vanished = ?;");
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
        });
    }

    public CompletableFuture<WeavePlayerQuery> queryWeavePlayer(final UUID mojangId) {
        return CompletableFuture.supplyAsync(() -> {
            final WeavePlayerQuery query = new WeavePlayerQuery();
            try (Connection connection = getConnection()) {
                final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + WEAVE_PLAYERS + " WHERE id = ?;");
                statement.setString(1, mojangId.toString());

                final ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    final String username = resultSet.getString("username");
                    final String displayName = resultSet.getString("display_name");
                    final String nickname = resultSet.getString("nickname");
                    final boolean vanished = resultSet.getBoolean("vanished");

                    final WeavePlayer weavePlayer = new WeavePlayer(mojangId, username, displayName, nickname, vanished);
                    query.addResult(weavePlayer);
                }
            } catch (SQLException e) {
                logger.severe("Error querying weave player: " + e.getMessage());
            }
            return query;
        });
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
