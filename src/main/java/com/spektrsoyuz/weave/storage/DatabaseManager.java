package com.spektrsoyuz.weave.storage;

import com.spektrsoyuz.weave.WeavePlugin;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public final class DatabaseManager {

    private final FileConfiguration config;
    private final Logger logger;
    private HikariDataSource dataSource;

    public DatabaseManager(final WeavePlugin plugin) {
        this.config = plugin.getConfig();
        this.logger = plugin.getLogger();

        init();
        createTables();
    }

    private void init() {

    }

    private void createTables() {

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
