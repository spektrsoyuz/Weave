/*
 * Weave
 * Created by SpektrSoyuz
 * All Rights Reserved
 */
package com.spektrsoyuz.weave;

import com.spektrsoyuz.weave.command.DisplayNameCommand;
import com.spektrsoyuz.weave.command.NicknameCommand;
import com.spektrsoyuz.weave.command.VanishCommand;
import com.spektrsoyuz.weave.hook.PlaceholderAPIHook;
import com.spektrsoyuz.weave.listener.PlayerListener;
import com.spektrsoyuz.weave.player.PlayerManager;
import com.spektrsoyuz.weave.storage.DatabaseManager;
import com.spektrsoyuz.weave.task.UpdatePlayersTask;
import com.spektrsoyuz.weave.task.UpdateVanishTask;
import com.spektrsoyuz.weave.task.UpdateRedisTask;
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

            new DisplayNameCommand(this, commands);
            new NicknameCommand(this, commands);
            new VanishCommand(this, commands);
        });

        new PlayerListener(this);

        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.runTaskTimer(this, new UpdatePlayersTask(this), 60000L, 60000L); // update every minute
        scheduler.runTaskTimer(this, new UpdateVanishTask(this), 10L, 10L); // update every 10ms
        scheduler.runTaskTimer(this, new UpdateRedisTask(this), 10L, 10L); // update every 10ms
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
