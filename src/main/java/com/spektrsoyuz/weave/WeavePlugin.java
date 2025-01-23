package com.spektrsoyuz.weave;

import com.spektrsoyuz.weave.listener.PlayerListener;
import com.spektrsoyuz.weave.player.PlayerManager;
import com.spektrsoyuz.weave.storage.DatabaseManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class WeavePlugin extends JavaPlugin {

    private DatabaseManager databaseManager;
    private PlayerManager playerManager;

    @Override
    public void onLoad() {
        // Plugin load logic
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        databaseManager = new DatabaseManager(this);
        playerManager = new PlayerManager(this);

        new PlayerListener(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (databaseManager != null) {
            databaseManager.close();
        }
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }
}
