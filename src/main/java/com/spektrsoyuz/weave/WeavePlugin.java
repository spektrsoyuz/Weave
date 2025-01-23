package com.spektrsoyuz.weave;

import com.spektrsoyuz.weave.hook.PlaceholderAPIHook;
import com.spektrsoyuz.weave.listener.PlayerListener;
import com.spektrsoyuz.weave.player.PlayerManager;
import com.spektrsoyuz.weave.storage.DatabaseManager;
import com.spektrsoyuz.weave.task.UpdateWeavePlayers;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

@SuppressWarnings("UnstableApiUsage")

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

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIHook(this).register();
        }

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
        });

        new PlayerListener(this);

        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.runTaskTimer(this, new UpdateWeavePlayers(this), 10L, 10L);
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
